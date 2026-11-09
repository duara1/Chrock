
package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.data.DadosManager;
import com.example.chrock.model.Conta;
import com.example.chrock.model.Pagamento;

import java.util.ArrayList;
import java.util.Locale;

public class HistoricoContaActivity extends AppCompatActivity {

    private LinearLayout conteudo;
    private String contaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_conta);

        DadosManager.inicializar(this);

        findViewById(R.id.botaoVoltar)
                .setOnClickListener(v -> finish());

        conteudo = findViewById(R.id.conteudoHistoricoConta);

        contaId = getIntent().getStringExtra("contaId");

        carregarTela();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (conteudo != null) {
            carregarTela();
        }
    }

    private void carregarTela() {

        conteudo.removeAllViews();

        if (contaId == null || contaId.isEmpty()) {
            listarContas();
        } else {
            mostrarDetalhesConta();
        }
    }

    private void listarContas() {

        adicionarTitulo("Selecione uma conta");

        ArrayList<Conta> contas = DadosManager.getContas(this);

        if (contas.isEmpty()) {
            adicionarTexto("Nenhuma conta cadastrada.");
            return;
        }

        for (Conta conta : contas) {

            LinearLayout card = criarCard();

            card.addView(criarTexto(
                    conta.getNome(), 18, "#222222", true
            ));

            card.addView(criarTexto(
                    "Responsável: " + conta.getResponsavel(),
                    14, "#555555", false
            ));

            card.addView(criarTexto(
                    formatarValor(conta.getValor()),
                    17, "#478B37", true
            ));

            card.addView(criarTexto(
                    conta.isPaga() ? "Paga" : "Pendente",
                    14,
                    conta.isPaga() ? "#478B37" : "#C77B22",
                    true
            ));

            card.setOnClickListener(v -> {

                contaId = conta.getId();

                carregarTela();
            });

            conteudo.addView(card);
        }
    }

    private void mostrarDetalhesConta() {

        Conta conta = DadosManager.buscarConta(this, contaId);

        if (conta == null) {

            Toast.makeText(
                    this,
                    "Conta não encontrada.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        adicionarTitulo("Informações da conta");

        adicionarInfo("Conta", conta.getNome());
        adicionarInfo("Responsável", conta.getResponsavel());
        adicionarInfo("Vencimento", conta.getVencimento());
        adicionarInfo("Valor total", formatarValor(conta.getValor()));

        adicionarInfo(
                "Situação",
                conta.isPaga() ? "Paga" : "Pendente"
        );

        adicionarTitulo("Participantes");

        if (conta.getParticipantes().isEmpty()) {
            adicionarTexto("Nenhum participante cadastrado.");
        } else {
            for (String participante : conta.getParticipantes()) {
                adicionarTexto("• " + participante);
            }
        }

        adicionarTitulo("Histórico de pagamentos");

        ArrayList<Pagamento> pagamentos =
                DadosManager.getPagamentosPorConta(this, contaId);

        if (pagamentos.isEmpty()) {
            adicionarTexto("Nenhum pagamento registrado.");
        } else {

            double totalPago = 0;

            for (Pagamento pagamento : pagamentos) {

                if (pagamento.isConfirmado()) {
                    totalPago += pagamento.getValor();
                }

                adicionarCardPagamento(pagamento);
            }

            adicionarTitulo("Resumo dos pagamentos");

            adicionarInfo(
                    "Total confirmado",
                    formatarValor(totalPago)
            );

            adicionarInfo(
                    "Valor restante",
                    formatarValor(
                            Math.max(0, conta.getValor() - totalPago)
                    )
            );
        }

        TextView voltarLista = criarTexto(
                "← Voltar para todas as contas",
                15, "#478B37", true
        );

        voltarLista.setPadding(0, 20, 0, 20);

        voltarLista.setOnClickListener(v -> {
            contaId = null;
            carregarTela();
        });

        conteudo.addView(voltarLista);
    }

    private void adicionarCardPagamento(Pagamento pagamento) {

        LinearLayout card = criarCard();

        card.addView(criarTexto(
                "Pagador: " + pagamento.getPagador(),
                16, "#222222", true
        ));

        card.addView(criarTexto(
                "Valor: " + formatarValor(pagamento.getValor()),
                15, "#333333", false
        ));

        card.addView(criarTexto(
                "Data: " + pagamento.getData(),
                14, "#555555", false
        ));

        card.addView(criarTexto(
                pagamento.isConfirmado()
                        ? "Pagamento confirmado"
                        : "Aguardando confirmação",
                14,
                pagamento.isConfirmado() ? "#478B37" : "#C77B22",
                true
        ));

        if (pagamento.getComprovante() != null
                && !pagamento.getComprovante().isEmpty()) {

            card.addView(criarTexto(
                    "Comprovante: " + pagamento.getComprovante(),
                    13, "#478B37", false
            ));
        }

        conteudo.addView(card);
    }

    private void adicionarInfo(String titulo, String valor) {

        LinearLayout card = criarCard();

        card.addView(criarTexto(
                titulo, 13, "#777777", false
        ));

        card.addView(criarTexto(
                valor, 16, "#222222", true
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
                criarTexto(mensagem, 14, "#666666", false)
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
