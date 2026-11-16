
package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.data.DadosManager;
import com.example.chrock.model.Conta;

import java.util.ArrayList;
import java.util.Locale;

public class ResumoPessoalActivity extends AppCompatActivity {

    private LinearLayout conteudo;
    private final String usuario = "Você";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_pessoal);

        DadosManager.inicializar(this);

        findViewById(R.id.botaoVoltar)
                .setOnClickListener(v -> finish());

        conteudo = findViewById(R.id.conteudoResumoPessoal);

        carregarResumo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (conteudo != null) {
            carregarResumo();
        }
    }

    private void carregarResumo() {

        conteudo.removeAllViews();

        ArrayList<Conta> contas =
                DadosManager.getHistoricoPessoal(this, usuario);

        double totalContas =
                DadosManager.getTotalPessoal(this, usuario);

        double totalPago =
                DadosManager.getTotalPagoPessoal(this, usuario);

        adicionarTitulo("Visão geral");

        adicionarIndicador(
                "Contas com participação",
                String.valueOf(contas.size())
        );

        adicionarIndicador(
                "Valor total das contas",
                formatarValor(totalContas)
        );

        adicionarIndicador(
                "Seus pagamentos confirmados",
                formatarValor(totalPago)
        );

        adicionarTitulo("Minhas contas");

        if (contas.isEmpty()) {
            adicionarTexto("Nenhuma conta encontrada.");
            return;
        }

        for (Conta conta : contas) {
            adicionarCard(conta);
        }
    }

    private void adicionarTitulo(String titulo) {

        TextView texto = criarTexto(
                titulo, 19, "#222222", true
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 22, 0, 12);

        texto.setLayoutParams(params);

        conteudo.addView(texto);
    }

    private void adicionarIndicador(String titulo, String valor) {

        LinearLayout card = criarCard();

        card.addView(criarTexto(
                titulo, 14, "#666666", false
        ));

        card.addView(criarTexto(
                valor, 23, "#478B37", true
        ));

        conteudo.addView(card);
    }

    private void adicionarCard(Conta conta) {

        LinearLayout card = criarCard();

        card.addView(criarTexto(
                conta.getNome(), 18, "#222222", true
        ));

        card.addView(criarTexto(
                "Responsável: " + conta.getResponsavel(),
                14, "#555555", false
        ));

        card.addView(criarTexto(
                "Vencimento: " + conta.getVencimento(),
                14, "#555555", false
        ));

        card.addView(criarTexto(
                "Valor: " + formatarValor(conta.getValor()),
                16, "#222222", true
        ));

        card.addView(criarTexto(
                conta.isPaga() ? "Paga" : "Pendente",
                14,
                conta.isPaga() ? "#478B37" : "#C77B22",
                true
        ));

        conteudo.addView(card);
    }

    private LinearLayout criarCard() {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 16, 18, 16);
        card.setBackgroundColor(Color.WHITE);
        card.setElevation(3);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(3, 5, 3, 12);

        card.setLayoutParams(params);

        return card;
    }

    private void adicionarTexto(String mensagem) {
        conteudo.addView(
                criarTexto(mensagem, 14, "#777777", false)
        );
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

    private String formatarValor(double valor) {

        return String.format(
                Locale.forLanguageTag("pt-BR"),
                "R$ %,.2f",
                valor
        );
    }
}
