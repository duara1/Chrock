package com.example.chrock.data;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {

    public interface Resultado {
        void sucesso(String corpo);
        void erro(String mensagem);
    }

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void get(String caminho, Resultado resultado) {
        if (!Sessao.estaLogado()) {
            resultado.erro("Faça login primeiro.");
            return;
        }
        Request pedido = comCabecalhos(caminho).get().build();
        executar(pedido, resultado);
    }

    public static void rpc(String funcao, JSONObject argumentos, Resultado resultado) {
        if (!Sessao.estaLogado()) {
            resultado.erro("Faça login primeiro.");
            return;
        }
        Request pedido = comCabecalhos("/rest/v1/rpc/" + funcao)
                .post(RequestBody.create(argumentos.toString(), JSON))
                .build();
        executar(pedido, resultado);
    }

    // Envia um arquivo (ex.: a foto do comprovante) pro Storage do Supabase.
    // x-upsert = true: se já existir um arquivo nesse caminho, ele é trocado
    // (útil se a pessoa tentar de novo depois de uma falha).
    public static void enviarArquivo(String bucket, String caminho, byte[] dados,
                                     String tipo, Resultado resultado) {
        if (!Sessao.estaLogado()) {
            resultado.erro("Faça login primeiro.");
            return;
        }
        Request pedido = comCabecalhos("/storage/v1/object/" + bucket + "/" + caminho)
                .addHeader("x-upsert", "true")
                .post(RequestBody.create(dados, MediaType.get(tipo)))
                .build();
        executar(pedido, resultado);
    }

    public interface ResultadoArquivo {
        void sucesso(byte[] dados);
        void erro(String mensagem);
    }

    public static void baixarArquivo(String bucket, String caminho, ResultadoArquivo resultado) {
        if (!Sessao.estaLogado()) {
            resultado.erro("Faça login primeiro.");
            return;
        }
        Request pedido = comCabecalhos("/storage/v1/object/authenticated/" + bucket + "/" + caminho)
                .get()
                .build();

        CLIENT.newCall(pedido).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                resultado.erro("Sem conexão. Verifique a internet.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resposta) throws IOException {
                if (resposta.isSuccessful()) {
                    resultado.sucesso(resposta.body().bytes());
                } else if (resposta.code() == 401) {
                    Sessao.encerrar();
                    resultado.erro("Sua sessão expirou. Entre de novo.");
                } else {
                    resultado.erro("Não foi possível abrir o comprovante.");
                }
            }
        });
    }

    private static Request.Builder comCabecalhos(String caminho) {
        return new Request.Builder()
                .url(SupabaseConfig.URL + caminho)
                .addHeader("apikey", SupabaseConfig.CHAVE_PUBLICA)
                .addHeader("Authorization", "Bearer " + Sessao.getAccessToken());
    }

    private static void executar(Request pedido, Resultado resultado) {
        executar(pedido, resultado, true);
    }

    private static void executar(Request pedido, Resultado resultado, boolean podeRenovar) {
        CLIENT.newCall(pedido).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                resultado.erro(AuthApi.MSG_SEM_CONEXAO);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resposta) throws IOException {
                String texto = resposta.body().string();
                if (resposta.isSuccessful()) {
                    resultado.sucesso(texto);
                } else if (resposta.code() == 401 && podeRenovar) {
                    renovarETentarDeNovo(pedido, resultado);
                } else if (resposta.code() == 401) {
                    Sessao.encerrar();
                    resultado.erro("Sua sessão expirou. Entre de novo.");
                } else {
                    resultado.erro(mensagemDeErro(texto));
                }
            }
        });
    }

    private static void renovarETentarDeNovo(Request pedido, Resultado resultado) {
        AuthApi.renovar(new AuthApi.Retorno() {
            @Override
            public void sucesso() {
                Request novo = pedido.newBuilder()
                        .header("Authorization", "Bearer " + Sessao.getAccessToken())
                        .build();
                executar(novo, resultado, false);
            }

            @Override
            public void erro(String mensagem) {
                if (AuthApi.MSG_SEM_CONEXAO.equals(mensagem)) {
                    resultado.erro(mensagem);
                    return;
                }
                Sessao.encerrar();
                resultado.erro("Sua sessão expirou. Entre de novo.");
            }
        });
    }

    public static void atualizar(String caminho, JSONObject dados, Resultado resultado) {
        if (!Sessao.estaLogado()) {
            resultado.erro("Faça login primeiro.");
            return;
        }
        Request pedido = comCabecalhos(caminho)
                .addHeader("Prefer", "return=minimal")
                .patch(RequestBody.create(dados.toString(), JSON))
                .build();
        executar(pedido, resultado);
    }

    private static String mensagemDeErro(String texto) {
        try {
            JSONObject json = new JSONObject(texto);
            if (json.has("message")) return json.getString("message");
        } catch (JSONException e) {
        }
        return "Não foi possível concluir. Tente de novo.";
    }
}