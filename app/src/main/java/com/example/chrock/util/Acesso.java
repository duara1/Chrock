package com.example.chrock.util;

import android.app.Activity;
import android.content.Intent;

import com.example.chrock.Login;
import com.example.chrock.data.Sessao;

public class Acesso {

    public static boolean exigirLogin(Activity tela) {
        Sessao.carregar(tela);
        if (Sessao.estaLogado()) return true;
        irParaLogin(tela);
        return false;
    }

    public static void irParaLogin(Activity tela) {
        Intent intent = new Intent(tela, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        tela.startActivity(intent);
        tela.finish();
    }
}