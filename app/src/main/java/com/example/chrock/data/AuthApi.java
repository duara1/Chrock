package com.example.chrock.data;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthApi {

    public interface Retorno {
        void sucesso();
        void erro(String mensagem);
    }

    public static final String MSG_CONFIRMAR_EMAIL = "Cadastro feito. Confirme o e-mail antes de entrar.";
    public static final String MSG_SEM_CONEXAO = "Sem conexão. Verifique a internet.";

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void entrar(String email, String senha, Retorno retorno) {
        JSONObject corpo = new JSONObject();
        try {
            corpo.put("email", email);
            corpo.put("password", senha);
        } catch (JSONException e) {
            retorno.erro("Dados inválidos.");
            return;
        }
        chamar("/auth/v1/token?grant_type=password", corpo, retorno);
    }

    public static void cadastrar(String nome, String pix, String email, String senha, Retorno retorno) {
        JSONObject corpo = new JSONObject();
        JSONObject dados = new JSONObject();
        try {
            dados.put("nome", nome);
            dados.put("pix", pix);
            corpo.put("email", email);
            corpo.put("password", senha);
            corpo.put("data", dados);
        } catch (JSONException e) {
            retorno.erro("Dados inválidos.");
            return;
        }
        chamar("/auth/v1/signup", corpo, retorno);
    }

    public static void renovar(Retorno retorno) {
        String refresh = Sessao.getRefreshToken();
        if (refresh == null) {
            retorno.erro("Faça login primeiro.");
            return;
        }
        JSONObject corpo = new JSONObject();
        try {
            corpo.put("refresh_token", refresh);
        } catch (JSONException e) {
            retorno.erro("Dados inválidos.");
            return;
        }
        chamar("/auth/v1/token?grant_type=refresh_token", corpo, retorno);
    }

    private static void chamar(String caminho, JSONObject corpo, Retorno retorno) {
        Request pedido = new Request.Builder()
                .url(SupabaseConfig.URL + caminho)
                .addHeader("apikey", SupabaseConfig.CHAVE_PUBLICA)
                .post(RequestBody.create(corpo.toString(), JSON))
                .build();

        CLIENT.newCall(pedido).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                retorno.erro(MSG_SEM_CONEXAO);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response resposta) throws IOException {
                String texto = resposta.body().string();
                try {
                    JSONObject json = new JSONObject(texto);
                    if (resposta.isSuccessful() && json.has("access_token")) {
                        Sessao.iniciar(
                                json.getString("access_token"),
                                json.getString("refresh_token"),
                                json.getJSONObject("user").getString("id"));
                        retorno.sucesso();
                    } else if (resposta.isSuccessful()) {
                        retorno.erro(MSG_CONFIRMAR_EMAIL);
                    } else {
                        retorno.erro(mensagemDeErro(json));
                    }
                } catch (JSONException e) {
                    retorno.erro("Resposta inesperada do servidor.");
                }
            }
        });
    }

    private static String mensagemDeErro(JSONObject json) {
        String[] campos = {"error_description", "msg", "message"};
        for (String campo : campos) {
            if (json.has(campo)) return traduzir(json.optString(campo));
        }
        return "Não foi possível concluir. Tente de novo.";
    }

    private static String traduzir(String mensagem) {
        String m = mensagem.toLowerCase(Locale.ROOT);
        if (m.contains("invalid login credentials")) return "E-mail ou senha incorretos.";
        if (m.contains("email not confirmed")) return "Confirme seu e-mail antes de entrar.";
        if (m.contains("already registered")) return "Este e-mail já está cadastrado.";
        if (m.contains("password") && m.contains("at least")) return "A senha precisa ter pelo menos 6 caracteres.";
        if (m.contains("rate limit")) return "Muitas tentativas. Espere um pouco e tente de novo.";
        if (m.contains("unable to validate email") || m.contains("invalid format")) return "E-mail inválido.";
        if (m.contains("database error saving new user")) {
            return "O banco não conseguiu salvar o perfil. Avise quem cuida do Supabase.";
        }
        return mensagem;
    }
}