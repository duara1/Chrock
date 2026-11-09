
package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.data.DadosManager;
import com.example.chrock.model.Conta;
import com.example.chrock.model.Pagamento;

import java.util.ArrayList;
import java.util.Locale;

public class HistoricoActivity extends AppCompatActivity {

    private LinearLayout conteudo;
    private final String usuario = "Você";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        DadosManager.inicializar(this);

        findViewById(R.id.botaoVoltar)
                .setOnClickListener(v -> finish());

        conteudo = findViewById(R.id.conteudoHistorico);

        carregarHistorico();
    }

    private void carregarHistorico() {

        conteudo.removeAllViews();

        adicionarTitulo("Contas com sua participação");

        ArrayList<Conta> contas =
                DadosManager.getHistoricoPessoal(this, usuario);

        if (contas.isEmpty()) {
            adicionarVazio("Você ainda não participa de nenhuma conta.");
        } else {
            for (Conta conta : contas) {
                adicionarCardConta(conta);
            }
        }

        adicionarTitulo("Seus pagamentos");

        ArrayList<Pagamento> pagamentos =
                DadosManager.getPagamentosPessoais(this, usuario);

        if (pagamentos.isEmpty()) {
            adicionarVazio("Nenhum pagamento registrado.");
        } else {
            for (Pagamento pagamento : pagamentos) {
                adicionarCardPagamento(pagamento);
            }
        }
    }

    private void adicionarTitulo(String titulo) {

        TextView texto = criarTexto(titulo, 19, "#222222", true);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1, -2
                );

        params.setMargins(0, 22, 0, 12);

        texto.setLayoutParams(params);

        conteudo.addView(texto);
    }

    private void adicionarCardConta(Conta conta) {

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
                "Valor: R$ " + String.format(
                        Locale.forLanguageTag("pt-BR"),
                        "%,.2f",
                        conta.getValor()
                ),
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

    private void adicionarCardPagamento(Pagamento pagamento) {

        LinearLayout card = criarCard();

        Conta conta = DadosManager.buscarConta(
                this, pagamento.getContaId()
        );

        String nomeConta = conta != null
                ? conta.getNome()
                : "Conta não encontrada";

        card.addView(criarTexto(
                nomeConta, 17, "#222222", true
        ));

        card.addView(criarTexto(
                "Data: " + pagamento.getData(),
                14, "#555555", false
        ));

        card.addView(criarTexto(
                "Valor pago: R$ " + String.format(
                        Locale.forLanguageTag("pt-BR"),
                        "%,.2f",
                        pagamento.getValor()
                ),
                15, "#222222", true
        ));

        card.addView(criarTexto(
                pagamento.isConfirmado()
                        ? "Pagamento confirmado"
                        : "Aguardando confirmação",
                14,
                pagamento.isConfirmado() ? "#478B37" : "#C77B22",
                true
        ));

        conteudo.addView(card);
    }

    private LinearLayout criarCard() {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 18, 18, 18);
        card.setBackgroundColor(Color.WHITE);
        card.setElevation(4);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(3, 5, 3, 12);

        card.setLayoutParams(params);

        return card;
    }

    private TextView criarTexto(
            String texto,
            int tamanho,
            String cor,
            boolean negrito) {

        TextView view = new TextView(this);

        view.setText(texto);
        view.setTextSize(tamanho);
        view.setTextColor(Color.parseColor(cor));
        view.setPadding(0, 5, 0, 5);

        if (negrito) {
            view.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        return view;
    }

    private void adicionarVazio(String mensagem) {

        TextView texto = criarTexto(
                mensagem, 14, "#777777", false
        );

        conteudo.addView(texto);
    }
}
