package com.example.chrock.util;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chrock.R;

public class Vencimento {

    public static void aplicar(TextView texto, ImageView bola, int corPadrao, String vencimento) {
        int dias = Formatar.diasAte(vencimento);

        texto.setTextColor(corPadrao);
        bola.setVisibility(View.GONE);

        if (dias < 0) {
            texto.setText("Despesa vencida");
            texto.setTextColor(Color.RED);
        } else if (dias == 0) {
            texto.setText("Vence HOJE");
            texto.setTextColor(Color.RED);
        } else if (dias == 1) {
            texto.setText("Vence em 1 dia");
            mostrarBola(bola, R.drawable.bola_vermelha);
        } else if (dias <= 5) {
            texto.setText("Vence em " + dias + " dias");
            mostrarBola(bola, R.drawable.bola_vermelha);
        } else if (dias <= 10) {
            texto.setText("Vence em " + dias + " dias");
            mostrarBola(bola, R.drawable.bola_amarela);
        } else {
            texto.setText("Vence em " + dias + " dias");
        }
    }

    private static void mostrarBola(ImageView bola, int desenho) {
        bola.setImageResource(desenho);
        bola.setVisibility(View.VISIBLE);
    }
}
