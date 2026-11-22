package com.example.chrock.data;

public class Sessao {

    private static String accessToken;
    private static String refreshToken;
    private static String usuarioId;

    public static void iniciar(String access, String refresh, String id) {
        accessToken = access;
        refreshToken = refresh;
        usuarioId = id;
    }

    public static void encerrar() {
        accessToken = null;
        refreshToken = null;
        usuarioId = null;
    }

    public static boolean estaLogado() {
        return accessToken != null;
    }

    public static String getAccessToken() { return accessToken; }
    public static String getRefreshToken() { return refreshToken; }
    public static String getUsuarioId() { return usuarioId; }
}