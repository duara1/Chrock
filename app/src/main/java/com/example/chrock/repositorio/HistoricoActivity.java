
package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.integracao.HistoricoRepositorioAdapter;
import com.example.chrock.repositorio.RepositorioFalso;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.model.Conta;


import java.util.List;
import java.util.Locale;

public class HistoricoActivity extends AppCompatActivity {

    private LinearLayout conteudo;
    public static final String EXTRA_USUARIO_ID = "usuario_id";
    public static final String EXTRA_SALA_ID = "sala_id";
    private String usuarioId;
    private String salaId;
    private RepositorioFalso repositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        usuarioId = getIntent().getStringExtra(EXTRA_USUARIO_ID);
        salaId = getIntent().getStringExtra(EXTRA_SALA_ID);
        repositorio = RepositorioFalso.get();

        findViewById(R.id.botaoVoltar)
                .setOnClickListener(v -> finish());

        conteudo = findViewById(R.id.conteudoHistorico);

        carregarHistorico();
    }

    private void carregarHistorico() {
        conteudo.removeAllViews();
        if (usuarioId == null || salaId == null) {
            adicionarVazio("Selecione uma sala e um usuário para consultar o histórico.");
            return;
        }
        if (repositorio.getUsuario(usuarioId) == null || repositorio.getSala(salaId) == null
                || !repositorio.getSala(salaId).isMembro(usuarioId)) {
            adicionarVazio("Usuário ou sala inválidos.");
            return;
        }

        adicionarTitulo("Contas com sua participação");
        List<Conta> contas = HistoricoRepositorioAdapter.contasDoUsuario(
                repositorio, salaId, usuarioId);
        if (contas.isEmpty()) {
            adicionarVazio("Você ainda não participa de nenhuma conta.");
        } else {
            for (Conta conta : contas) {
                Despesa despesa = repositorio.getDespesa(conta.getId());
                adicionarCardConta(conta, despesa.getStatus(usuarioId));
            }
        }
        adicionarTitulo("Pagamentos");
        adicionarVazio("Datas, valores pagos e confirmações individuais serão exibidos "
                + "quando houver registros de pagamento integrados. "
                + "Os status das contribuições aparecem nas contas acima.");
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

    private void adicionarCardConta(Conta conta, StatusContribuicao status) {

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

        String descricao;
        switch (status) {
            case CONFIRMADO: descricao = "Contribuição confirmada"; break;
            case SINALIZADO: descricao = "Pagamento sinalizado"; break;
            case CONTESTADO: descricao = "Contribuição contestada"; break;
            default: descricao = "Contribuição pendente";
        }
        card.addView(criarTexto(descricao, 14,
                status == StatusContribuicao.CONFIRMADO ? "#478B37" : "#C77B22", true));

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
