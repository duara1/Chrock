
package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.repositorio.RepositorioFalso;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Usuario;
import com.example.chrock.model.StatusContribuicao;
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
        RepositorioFalso repo = RepositorioFalso.get();
        String salaId = getIntent().getStringExtra("sala_id");
        adicionarTitulo("Contribuições da sala");
        if (salaId == null || repo.getSala(salaId) == null) {
            adicionarTexto("Selecione uma sala válida."); return;
        }
        java.util.List<Despesa> despesas = repo.getDespesasDaSala(salaId);
        if (despesas.isEmpty()) { adicionarTexto("Nenhuma despesa nesta sala."); return; }
        int confirmadas = 0, sinalizadas = 0, pendentes = 0, contestadas = 0;
        for (Despesa despesa : despesas) {
            LinearLayout card = criarCard();
            card.addView(criarTexto(despesa.getTitulo(), 18, "#222222", true));
            card.addView(criarTexto("Total: " + formatarValor(despesa.getTotalEmInt() / 100.0), 15, "#555555", false));
            for (Contribuicao contribuicao : despesa.getContribuintes()) {
                StatusContribuicao status = contribuicao.getStatus();
                if (status == StatusContribuicao.CONFIRMADO) confirmadas++;
                else if (status == StatusContribuicao.SINALIZADO) sinalizadas++;
                else if (status == StatusContribuicao.CONTESTADO) contestadas++;
                else pendentes++;
                Usuario usuario = repo.getUsuario(contribuicao.getUsuarioId());
                String nome = usuario == null ? contribuicao.getUsuarioId() : usuario.getNome();
                card.addView(criarTexto(nome + ": " + status.name(), 14, "#555555", false));
            }
            conteudo.addView(card);
        }
        adicionarTitulo("Resumo dos status");
        adicionarInfo("Confirmadas", String.valueOf(confirmadas));
        adicionarInfo("Sinalizadas", String.valueOf(sinalizadas));
        adicionarInfo("Pendentes", String.valueOf(pendentes));
        adicionarInfo("Contestadas", String.valueOf(contestadas));
        adicionarTexto("Contagens de contribuições, não valores efetivamente pagos.");
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
