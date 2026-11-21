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

    private static Request.Builder comCabecalhos(String caminho) {
        return new Request.Builder()
                .url(SupabaseConfig.URL + caminho)
                .addHeader("apikey", SupabaseConfig.CHAVE_PUBLICA)
                .addHeader("Authorization", "Bearer " + Sessao.getAccessToken());
    }

    private static void executar(Request pedido, Resultado resultado) {
        CLIENT.newCall(pedido).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                resultado.erro("Sem conexão. Verifique a internet.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resposta) throws IOException {
                String texto = resposta.body().string();
                if (resposta.isSuccessful()) {
                    resultado.sucesso(texto);
                } else if (resposta.code() == 401) {
                    resultado.erro("Sua sessão expirou. Entre de novo.");
                } else {
                    resultado.erro(mensagemDeErro(texto));
                }
            }
        });
    }

    private static String mensagemDeErro(String texto) {
        try {
            JSONObject json = new JSONObject(texto);
            if (json.has("message")) return json.getString("message");
        } catch (JSONException e) {
            // o corpo não era um JSON: cai na mensagem genérica abaixo
        }
        return "Não foi possível concluir. Tente de novo.";
    }
}