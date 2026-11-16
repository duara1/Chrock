
package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.integracao.HistoricoRepositorioAdapter;
import com.example.chrock.repositorio.RepositorioFalso;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Usuario;
import com.example.chrock.model.Conta;
import com.example.chrock.model.Pagamento;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Usuario;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.repositorio.RepositorioFalso;

import java.util.ArrayList;
import java.util.Locale;

public class HistoricoContaActivity extends AppCompatActivity {

    private LinearLayout conteudo;
    private String contaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_conta);



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
        if (getIntent().hasExtra(HistoricoActivity.EXTRA_SALA_ID)) {
            carregarDespesaCompartilhada();
            return;
        }

        if (contaId == null || contaId.isEmpty()) {
            listarContas();
        } else {
            mostrarDetalhesConta();
        }
    }

    /** Detalhes do repositório compartilhado, sem fabricar registros de pagamento. */
    private void carregarDespesaCompartilhada() {
        String salaId = getIntent().getStringExtra(HistoricoActivity.EXTRA_SALA_ID);
        String usuarioId = getIntent().getStringExtra(HistoricoActivity.EXTRA_USUARIO_ID);
        RepositorioFalso repositorio = RepositorioFalso.get();
        if (salaId == null || usuarioId == null || contaId == null
                || repositorio.getSala(salaId) == null
                || !repositorio.getSala(salaId).isMembro(usuarioId)) {
            adicionarTexto("Não foi possível identificar a sala, a conta ou o usuário.");
            return;
        }
        Despesa despesa;
        try {
            despesa = repositorio.getDespesa(contaId);
        } catch (IllegalArgumentException erro) {
            adicionarTexto("Despesa não encontrada.");
            return;
        }
        if (!salaId.equals(despesa.getSalaId())) {
            adicionarTexto("Essa despesa não pertence à sala selecionada.");
            return;
        }
        adicionarTitulo(despesa.getTitulo());
        adicionarTexto("Valor total: " + formatarValor(despesa.getTotalEmInt() / 100.0));
        adicionarTexto("Vencimento: " + despesa.getVencimento());
        Usuario responsavel = repositorio.getUsuario(despesa.getResponsavelId());
        adicionarTexto("Responsável: " + (responsavel == null ? "Não identificado" : responsavel.getNome()));
        adicionarTitulo("Participantes e contribuições");
        for (Contribuicao contribuicao : despesa.getContribuintes()) {
            Usuario participante = repositorio.getUsuario(contribuicao.getUsuarioId());
            String nome = participante == null ? contribuicao.getUsuarioId() : participante.getNome();
            adicionarTexto(nome + " • " + formatarValor(despesa.getValorATransferirEmInt(contribuicao.getUsuarioId()) / 100.0)
                    + " • " + descricaoStatus(contribuicao.getStatus()));
        }
        adicionarTitulo("Comprovante");
        if (despesa.getComprovante() == null || despesa.getComprovante().trim().isEmpty()) {
            adicionarTexto("Nenhum comprovante cadastrado.");
        } else {
            adicionarTexto("Referência cadastrada: " + despesa.getComprovante()
                    + " (arquivo não disponível neste módulo)");
        }
    }

    private String descricaoStatus(StatusContribuicao status) {
        switch (status) {
            case CONFIRMADO: return "Confirmado";
            case SINALIZADO: return "Sinalizado";
            case CONTESTADO: return "Contestado";
            default: return "Pendente";
        }
    }

    private void listarContas() {
        adicionarTitulo("Selecione uma conta");
        RepositorioFalso repo = RepositorioFalso.get();
        String salaId = getIntent().getStringExtra("sala_id");
        if (salaId == null || repo.getSala(salaId) == null) {
            adicionarTexto("Selecione uma sala válida.");
            return;
        }
        java.util.List<Despesa> despesas = repo.getDespesasDaSala(salaId);
        if (despesas.isEmpty()) {
            adicionarTexto("Nenhuma conta cadastrada nesta sala.");
            return;
        }
        for (Despesa despesa : despesas) {
            LinearLayout card = criarCard();
            card.addView(criarTexto(despesa.getTitulo(), 18, "#222222", true));
            card.addView(criarTexto("Vencimento: " + despesa.getVencimento(), 14, "#555555", false));
            card.addView(criarTexto(formatarValor(despesa.getTotalEmInt() / 100.0), 17, "#478B37", true));
            card.setOnClickListener(v -> { contaId = despesa.getDespesaId(); carregarTela(); });
            conteudo.addView(card);
        }
    }

    private void mostrarDetalhesConta() {
        RepositorioFalso repo = RepositorioFalso.get();
        String salaId = getIntent().getStringExtra("sala_id");
        if (salaId == null || repo.getSala(salaId) == null) {
            adicionarTexto("Sala inválida."); return;
        }
        Despesa despesa = null;
        for (Despesa item : repo.getDespesasDaSala(salaId)) {
            if (item.getDespesaId().equals(contaId)) { despesa = item; break; }
        }
        if (despesa == null) {
            adicionarTexto("Conta não encontrada nesta sala."); return;
        }
        adicionarTitulo("Informações da conta");
        adicionarInfo("Conta", despesa.getTitulo());
        Usuario responsavel = repo.getUsuario(despesa.getResponsavelId());
        adicionarInfo("Responsável", responsavel == null ? "Não identificado" : responsavel.getNome());
        adicionarInfo("Vencimento", despesa.getVencimento());
        adicionarInfo("Valor total", formatarValor(despesa.getTotalEmInt() / 100.0));
        adicionarInfo("Situação", despesa.isConcluida() ? "Concluída" : "Em andamento");
        adicionarTitulo("Participantes e contribuições");
        for (Contribuicao contribuicao : despesa.getContribuintes()) {
            Usuario participante = repo.getUsuario(contribuicao.getUsuarioId());
            String nome = participante == null ? contribuicao.getUsuarioId() : participante.getNome();
            adicionarTexto("• " + nome + " - " + contribuicao.getStatus().name());
        }
        adicionarTitulo("Comprovante da despesa");
        String comprovante = despesa.getComprovante();
        adicionarTexto(comprovante == null || comprovante.trim().isEmpty()
                ? "Nenhum comprovante da despesa cadastrado."
                : "Referência: " + comprovante + " (arquivo não disponível para abertura neste protótipo)");
        adicionarTitulo("Pagamentos individuais");
        adicionarTexto("O repositório compartilhado registra status das contribuições, "
                + "mas não disponibiliza datas ou valores efetivamente pagos.");
        TextView voltarLista = criarTexto("← Voltar para todas as contas", 15, "#478B37", true);
        voltarLista.setPadding(0, 20, 0, 20);
        voltarLista.setOnClickListener(v -> { contaId = null; carregarTela(); });
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
