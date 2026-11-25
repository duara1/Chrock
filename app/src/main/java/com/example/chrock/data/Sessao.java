package com.example.chrock.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Sessao {

    private static final String ARQUIVO = "sessao";
    private static final String CHAVE_ACCESS = "access_token";
    private static final String CHAVE_REFRESH = "refresh_token";
    private static final String CHAVE_USUARIO = "usuario_id";

    private static SharedPreferences preferencias;

    private static String accessToken;
    private static String refreshToken;
    private static String usuarioId;

    public static void carregar(Context contexto) {
        if (preferencias == null) {
            preferencias = contexto.getApplicationContext()
                    .getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        }
        if (accessToken == null) {
            accessToken = preferencias.getString(CHAVE_ACCESS, null);
            refreshToken = preferencias.getString(CHAVE_REFRESH, null);
            usuarioId = preferencias.getString(CHAVE_USUARIO, null);
        }
    }

    public static void iniciar(String access, String refresh, String id) {
        accessToken = access;
        refreshToken = refresh;
        usuarioId = id;

        if (preferencias != null) {
            preferencias.edit()
                    .putString(CHAVE_ACCESS, access)
                    .putString(CHAVE_REFRESH, refresh)
                    .putString(CHAVE_USUARIO, id)
                    .apply();
        }
    }

    public static void encerrar() {
        accessToken = null;
        refreshToken = null;
        usuarioId = null;

        if (preferencias != null) {
            preferencias.edit().clear().apply();
        }
    }

    public static boolean estaLogado() {
        return accessToken != null;
    }

    public static String getAccessToken() { return accessToken; }
    public static String getRefreshToken() { return refreshToken; }
    public static String getUsuarioId() { return usuarioId; }
}