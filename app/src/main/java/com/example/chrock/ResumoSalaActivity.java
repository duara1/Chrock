
package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.model.Despesa;
import com.example.chrock.model.Sala;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Acesso;
import com.example.chrock.util.Formatar;
import java.util.List;
import java.util.Locale;

public class ResumoSalaActivity extends AppCompatActivity {

    private LinearLayout conteudo;
    private String salaId;

    private final RepositorioSupabase repo =
            RepositorioSupabase.get();


@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!Acesso.exigirLogin(this)) return;

    setContentView(R.layout.activity_resumo_sala);

    salaId = getIntent().getStringExtra("salaId");

    if (salaId == null || salaId.trim().isEmpty()) {
        finish();
        return;
    }

    findViewById(R.id.botaoVoltar)
            .setOnClickListener(v -> finish());

    conteudo = findViewById(R.id.conteudoResumoSala);
}

@Override
protected void onResume() {
    super.onResume();

    if (conteudo != null && Acesso.exigirLogin(this)) {
        carregarResumo();
    }
}



private void carregarResumo() {

    conteudo.removeAllViews();
    adicionarTexto("Carregando resumo da sala...");

    repo.carregarSala(
            salaId,
            new RepositorioSupabase.Resposta<Sala>() {

        @Override
        public void sucesso(Sala sala) {

            if (isFinishing() || isDestroyed()) return;

            repo.carregarDespesas(
                    salaId,
                    new RepositorioSupabase.Resposta<List<Despesa>>() {

                @Override
                public void sucesso(List<Despesa> despesas) {

                    if (isFinishing() || isDestroyed()) return;

                    montarResumo(despesas);
                }

                @Override
                public void erro(String mensagem) {
                    mostrarErro(mensagem);
                }
            });
        }

        @Override
        public void erro(String mensagem) {
            mostrarErro(mensagem);
        }
    });
}

private void mostrarErro(String mensagem) {

    if (isFinishing() || isDestroyed()) return;

    conteudo.removeAllViews();
    adicionarTexto(mensagem);
}

private void montarResumo(List<Despesa> despesas) {

    conteudo.removeAllViews();

    int total = 0;
    int pago = 0;
    int contasPagas = 0;

    for (Despesa d : despesas) {

        total += d.getTotalEmInt();

        if (d.isConcluida()) {
            pago += d.getTotalEmInt();
            contasPagas++;
        }
    }

    adicionarTitulo("Resumo financeiro da sala");

    adicionarIndicador(
            "Total de despesas",
            Formatar.reais(total)
    );

    adicionarIndicador(
            "Total pago",
            Formatar.reais(pago)
    );

    adicionarIndicador(
            "Total pendente",
            Formatar.reais(total - pago)
    );

    adicionarIndicador(
            "Contas cadastradas",
            despesas.size()
    );

    adicionarIndicador(
            "Contas pagas",
            contasPagas
    );

    adicionarIndicador(
            "Contas pendentes",
            despesas.size() - contasPagas
    );

    adicionarTitulo("Contas da sala");

    if (despesas.isEmpty()) {
        adicionarTexto("Nenhuma conta cadastrada.");
        return;
    }

    for (Despesa d : despesas) {
        adicionarCard(d);
    }
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

    private void adicionarIndicador(String titulo, double valor) {

        adicionarIndicador(
                titulo,
                formatarValor(valor)
        );
    }

    private void adicionarIndicador(String titulo, int quantidade) {

        adicionarIndicador(
                titulo,
                String.valueOf(quantidade)
        );
    }

    private void adicionarIndicador(String titulo, String valor) {

        LinearLayout card = criarCard();

        card.addView(criarTexto(
                titulo, 14, "#666666", false
        ));

        card.addView(criarTexto(
                valor, 22, "#478B37", true
        ));

        conteudo.addView(card);
    }


private void adicionarCard(Despesa despesa) {

    LinearLayout card = criarCard();

    card.addView(criarTexto(
            despesa.getTitulo(),
            18, "#222222", true
    ));

    card.addView(criarTexto(
            "Vencimento: " + despesa.getVencimento(),
            14, "#555555", false
    ));

    card.addView(criarTexto(
            "Participantes: "
                    + despesa.getContribuintes().size(),
            14, "#555555", false
    ));

    card.addView(criarTexto(
            Formatar.reais(despesa.getTotalEmInt()),
            16, "#222222", true
    ));

    card.addView(criarTexto(
            despesa.isConcluida() ? "Paga" : "Pendente",
            14,
            despesa.isConcluida()
                    ? "#478B37"
                    : "#C77B22",
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
