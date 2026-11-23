
package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.data.DadosManager;
import com.example.chrock.model.Notificacao;

import java.util.ArrayList;

public class NotificacoesActivity extends AppCompatActivity {

    private LinearLayout conteudo;
    private TextView contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        DadosManager.inicializar(this);

        findViewById(R.id.botaoVoltar)
                .setOnClickListener(v -> finish());

        conteudo = findViewById(R.id.conteudoNotificacoes);
        contador = findViewById(R.id.contadorNotificacoes);

        Button marcarTodas = findViewById(R.id.botaoMarcarTodas);

        marcarTodas.setOnClickListener(v -> {

            DadosManager.marcarTodasComoLidas(this);

            carregarNotificacoes();
        });

        carregarNotificacoes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (conteudo != null) {
            carregarNotificacoes();
        }
    }

    private void carregarNotificacoes() {

        conteudo.removeAllViews();

        ArrayList<Notificacao> notificacoes =
                DadosManager.getNotificacoes(this);

        int naoLidas =
                DadosManager.getQuantidadeNaoLidas(this);

        contador.setText(
                naoLidas + " notificações não lidas"
        );

        if (notificacoes.isEmpty()) {

            adicionarTexto("Você não possui notificações.");

            return;
        }

        for (Notificacao notificacao : notificacoes) {

            adicionarCard(notificacao);
        }
    }

    private void adicionarCard(Notificacao notificacao) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);

        card.setBackgroundColor(
                notificacao.isLida()
                        ? Color.WHITE
                        : Color.rgb(237, 248, 233)
        );

        card.setElevation(3);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(3, 5, 3, 12);

        card.setLayoutParams(params);

        TextView titulo = criarTexto(
                notificacao.getTitulo(),
                17,
                "#222222",
                true
        );

        TextView mensagem = criarTexto(
                notificacao.getMensagem(),
                14,
                "#555555",
                false
        );

        TextView data = criarTexto(
                notificacao.getData(),
                12,
                "#777777",
                false
        );

        TextView status = criarTexto(
                notificacao.isLida()
                        ? "Lida"
                        : "Não lida",
                13,
                notificacao.isLida()
                        ? "#777777"
                        : "#478B37",
                true
        );

        card.addView(titulo);
        card.addView(mensagem);
        card.addView(data);
        card.addView(status);

        card.setOnClickListener(v -> {

            DadosManager.marcarComoLida(
                    this,
                    notificacao.getId()
            );

            carregarNotificacoes();
        });

        conteudo.addView(card);
    }

    private TextView criarTexto(
            String mensagem,
            int tamanho,
            String cor,
            boolean negrito) {

        TextView texto = new TextView(this);

        texto.setText(mensagem);
        texto.setTextSize(tamanho);
        texto.setTextColor(Color.parseColor(cor));
        texto.setPadding(0, 5, 0, 5);

        if (negrito) {
            texto.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );
        }

        return texto;
    }

    private void adicionarTexto(String mensagem) {

        TextView texto = criarTexto(
                mensagem, 15, "#777777", false
        );

        texto.setGravity(Gravity.CENTER);
        texto.setPadding(15, 30, 15, 30);

        conteudo.addView(texto);
    }
}
