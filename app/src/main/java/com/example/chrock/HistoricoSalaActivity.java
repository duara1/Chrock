
package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.data.DadosManager;
import com.example.chrock.model.Conta;
import com.example.chrock.model.Pagamento;

import java.util.ArrayList;
import java.util.Locale;

public class HistoricoSalaActivity extends AppCompatActivity {

    private LinearLayout conteudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_sala);

        DadosManager.inicializar(this);

        findViewById(R.id.botaoVoltar)
                .setOnClickListener(v -> finish());

        conteudo = findViewById(R.id.conteudoHistoricoSala);

        carregarHistorico();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (conteudo != null) {
            carregarHistorico();
        }
    }

    private void carregarHistorico() {

        conteudo.removeAllViews();

        adicionarTitulo("Histórico de pagamentos da sala");

        ArrayList<Pagamento> pagamentos =
                DadosManager.getHistoricoPagamentosSala(this);

        if (pagamentos.isEmpty()) {
            adicionarTexto("Nenhum pagamento registrado.");
            return;
        }

        double totalConfirmado = 0;
        double totalAguardando = 0;

        for (Pagamento pagamento : pagamentos) {

            if (pagamento.isConfirmado()) {
                totalConfirmado += pagamento.getValor();
            } else {
                totalAguardando += pagamento.getValor();
            }

            adicionarCard(pagamento);
        }

        adicionarTitulo("Resumo");

        adicionarInfo(
                "Pagamentos confirmados",
                formatarValor(totalConfirmado)
        );

        adicionarInfo(
                "Aguardando confirmação",
                formatarValor(totalAguardando)
        );
    }

    private void adicionarCard(Pagamento pagamento) {

        LinearLayout card = criarCard();

        Conta conta = DadosManager.buscarConta(
                this, pagamento.getContaId()
        );

        String nomeConta = conta != null
                ? conta.getNome()
                : "Conta não encontrada";

        card.addView(criarTexto(
                nomeConta, 18, "#222222", true
        ));

        card.addView(criarTexto(
                "Pago por: " + pagamento.getPagador(),
                14, "#555555", false
        ));

        card.addView(criarTexto(
                "Data: " + pagamento.getData(),
                14, "#555555", false
        ));

        card.addView(criarTexto(
                "Valor: " + formatarValor(pagamento.getValor()),
                16, "#222222", true
        ));

        card.addView(criarTexto(
                pagamento.isConfirmado()
                        ? "Confirmado"
                        : "Aguardando confirmação",
                14,
                pagamento.isConfirmado() ? "#478B37" : "#C77B22",
                true
        ));

        conteudo.addView(card);
    }

    private void adicionarTitulo(String titulo) {

        TextView texto = criarTexto(
                titulo, 19, "#222222", true
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 20, 0, 12);

        texto.setLayoutParams(params);

        conteudo.addView(texto);
    }

    private void adicionarInfo(String titulo, String valor) {

        LinearLayout card = criarCard();

        card.addView(criarTexto(
                titulo, 14, "#666666", false
        ));

        card.addView(criarTexto(
                valor, 20, "#478B37", true
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

        conteudo.addView(
                criarTexto(mensagem, 14, "#777777", false)
        );
    }

    private String formatarValor(double valor) {

        return String.format(
                Locale.forLanguageTag("pt-BR"),
                "R$ %,.2f",
                valor
        );
    }
}
