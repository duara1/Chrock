
package com.example.chrock;

import android.os.Bundle;
import android.view.View;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.example.chrock.data.Sessao;
import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Sala;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Acesso;
import com.example.chrock.util.Formatar;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class historico extends AppCompatActivity {

    private String salaId;
    private LinearLayout conteudo;

    private Spinner modo;
    private Spinner filtroPeriodo;
    private Spinner filtroStatus;

    private List<Despesa> despesas;
    private Map<String, Usuario> usuarios;

    private boolean carregando = false;

    private final RepositorioSupabase repo =
            RepositorioSupabase.get();

    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);

        if (!Acesso.exigirLogin(this)) return;

        setContentView(R.layout.activity_historico);

        salaId = getIntent().getStringExtra("salaId");

        if (salaId == null || salaId.trim().isEmpty()) {
            finish();
            return;
        }

        conteudo = findViewById(R.id.conteudoHistorico);
        modo = findViewById(R.id.modoHistorico);
        filtroPeriodo = findViewById(R.id.filtroPeriodo);
        filtroStatus = findViewById(R.id.filtroStatus);

        configurarFiltros();

        findViewById(R.id.abaInicio)
                .setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (conteudo != null && Acesso.exigirLogin(this)) {
            carregar();
        }
    }

    private void configurarFiltros() {

        modo.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{
                        "Meu histórico",
                        "Histórico da sala",
                        "Histórico por conta"
                }
        ));

        filtroPeriodo.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Todos os meses"}
        ));

        filtroStatus.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{
                        "Todos os status",
                        "Pendente",
                        "Sinalizado",
                        "Confirmado",
                        "Contestado"
                }
        ));

        AdapterView.OnItemSelectedListener listener =
                new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id) {

                mostrar();
            }

            @Override
            public void onNothingSelected(
                    AdapterView<?> parent) {
            }
        };

        modo.setOnItemSelectedListener(listener);
        filtroPeriodo.setOnItemSelectedListener(listener);
        filtroStatus.setOnItemSelectedListener(listener);
    }

    private void carregar() {

        if (carregando) return;

        carregando = true;
        despesas = null;
        usuarios = null;

        mensagem("Carregando histórico...");

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
                    public void sucesso(List<Despesa> dados) {

                        if (isFinishing() || isDestroyed()) return;

                        despesas = dados;

                        repo.carregarMembros(
                                salaId,
                                new RepositorioSupabase.Resposta<Map<String, Usuario>>() {

                            @Override
                            public void sucesso(
                                    Map<String, Usuario> membros) {

                                if (isFinishing() || isDestroyed()) return;

                                usuarios = membros;

                                atualizarPeriodos();

                                carregando = false;

                                mostrar();
                            }

                            @Override
                            public void erro(String erro) {
                                falha(erro);
                            }
                        });
                    }

                    @Override
                    public void erro(String erro) {
                        falha(erro);
                    }
                });
            }

            @Override
            public void erro(String erro) {
                falha(erro);
            }
        });
    }

    private void falha(String erro) {

        carregando = false;
        despesas = null;
        usuarios = null;

        mensagem(erro);
    }

    private String obterPeriodo(String data) {

        if (data == null) return "";

        // Formato utilizado pelo Supabase.
        if (data.matches("\\d{4}-\\d{2}-\\d{2}")) {

            return data.substring(5, 7)
                    + "/"
                    + data.substring(0, 4);
        }

        // Formato brasileiro.
        if (data.matches("\\d{2}/\\d{2}/\\d{4}")) {

            return data.substring(3);
        }

        return "";
    }

    private void atualizarPeriodos() {

        Object anterior = filtroPeriodo.getSelectedItem();

        Set<String> meses = new LinkedHashSet<>();

        meses.add("Todos os meses");

        for (Despesa d : despesas) {

            String periodo = obterPeriodo(d.getVencimento());

            if (!periodo.isEmpty()) {
                meses.add(periodo);
            }
        }

        List<String> opcoes = new ArrayList<>(meses);

        filtroPeriodo.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                opcoes
        ));

        int posicao = opcoes.indexOf(anterior);

        filtroPeriodo.setSelection(
                posicao >= 0 ? posicao : 0
        );
    }

    private boolean correspondePeriodo(Despesa d) {

        Object selecionado = filtroPeriodo.getSelectedItem();

        if (selecionado == null
                || filtroPeriodo.getSelectedItemPosition() == 0) {

            return true;
        }

        return selecionado.toString()
                .equals(obterPeriodo(d.getVencimento()));
    }

    private boolean correspondeStatus(
            Despesa d,
            String usuarioId,
            int modoAtual) {

        int posicao = filtroStatus.getSelectedItemPosition();

        if (posicao <= 0) return true;

        String[] status = {
                "",
                "PENDENTE",
                "SINALIZADO",
                "CONFIRMADO",
                "CONTESTADO"
        };

        String selecionado = status[posicao];

        if (modoAtual == 0) {

            return d.isContribuente(usuarioId)
                    && d.getStatus(usuarioId)
                    .name().equals(selecionado);
        }

        for (Contribuicao c : d.getContribuintes()) {

            if (c.getStatus().name().equals(selecionado)) {
                return true;
            }
        }

        return false;
    }

    private void mostrar() {

        if (conteudo == null
                || despesas == null
                || usuarios == null
                || carregando
                || isFinishing()
                || isDestroyed()) {

            return;
        }

        conteudo.removeAllViews();

        String eu = Sessao.getUsuarioId();

        int modoAtual = modo.getSelectedItemPosition();

        int quantidade = 0;

        for (Despesa d : despesas) {

            if (modoAtual == 0 && !d.isContribuente(eu)) {
                continue;
            }

            if (!correspondePeriodo(d)) continue;

            if (!correspondeStatus(d, eu, modoAtual)) {
                continue;
            }

            adicionarCard(d, eu, modoAtual);

            quantidade++;
        }

        if (quantidade == 0) {
            mensagem("Nenhuma despesa encontrada para os filtros selecionados.");
        }
    }

    private void adicionarCard(
            Despesa d,
            String eu,
            int modoAtual) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.WHITE);

        card.setPadding(
                dp(16), dp(16),
                dp(16), dp(16)
        );

        card.setElevation(dp(3));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                dp(4), dp(6),
                dp(4), dp(12)
        );

        card.setLayoutParams(params);

        card.addView(criarTexto(
                d.getTitulo(), 20, true
        ));

        card.addView(criarTexto(
                "Valor total: "
                        + Formatar.reais(d.getTotalEmInt()),
                16, true
        ));

        card.addView(criarTexto(
                "Vencimento: " + d.getVencimento(),
                14, false
        ));

        card.addView(criarTexto(
                "Responsável: "
                        + nomeUsuario(d.getResponsavelId()),
                14, false
        ));

        TextView situacao = criarTexto(
                d.isConcluida()
                        ? "Conta paga"
                        : "Conta em aberto",
                14, true
        );

        situacao.setTextColor(Color.parseColor(
                d.isConcluida()
                        ? "#478B37"
                        : "#C77B22"
        ));

        card.addView(situacao);

        if (modoAtual == 0) {

            card.addView(criarTexto(
                    "Minha parte: "
                            + Formatar.reais(
                                d.getValorATransferirEmInt(eu)
                            ),
                    16, true
            ));

            card.addView(criarTexto(
                    "Minha contribuição: "
                            + d.getStatus(eu).name(),
                    14, false
            ));

        } else if (modoAtual == 1) {

            card.addView(criarTexto(
                    "Participantes",
                    16, true
            ));

            for (Contribuicao c : d.getContribuintes()) {

                card.addView(criarTexto(
                        nomeUsuario(c.getUsuarioId())
                                + ": "
                                + c.getStatus().name(),
                        14, false
                ));
            }
        }

        TextView detalhes = criarTexto(
                "Consultar detalhes da conta",
                14, true
        );

        detalhes.setTextColor(
                Color.parseColor("#7467CA")
        );

        card.addView(detalhes);

        card.setClickable(true);
        card.setFocusable(true);

        card.setOnClickListener(v -> abrirDetalhes(d));

        conteudo.addView(card);
    }

    private void abrirDetalhes(Despesa d) {

        StringBuilder texto = new StringBuilder();

        texto.append("Valor total: ")
                .append(Formatar.reais(d.getTotalEmInt()))
                .append("\n\n");

        texto.append("Vencimento: ")
                .append(d.getVencimento())
                .append("\n\n");

        texto.append("Responsável: ")
                .append(nomeUsuario(d.getResponsavelId()))
                .append("\n\n");

        texto.append("Situação: ")
                .append(d.isConcluida()
                        ? "Paga"
                        : "Em aberto")
                .append("\n\n");

        texto.append("PARTICIPANTES\n\n");

        for (Contribuicao c : d.getContribuintes()) {

            texto.append(nomeUsuario(c.getUsuarioId()))
                    .append("\n");

            texto.append("Valor: ")
                    .append(Formatar.reais(
                            d.getValorATransferirEmInt(
                                    c.getUsuarioId()
                            )
                    ))
                    .append("\n");

            texto.append("Status: ")
                    .append(c.getStatus().name())
                    .append("\n\n");
        }

        new AlertDialog.Builder(this)
                .setTitle(d.getTitulo())
                .setMessage(texto.toString())
                .setPositiveButton("Fechar", null)
                .show();
    }

    private String nomeUsuario(String id) {

        Usuario usuario = usuarios.get(id);

        return usuario == null
                ? "Não identificado"
                : usuario.getNome();
    }

    private TextView criarTexto(
            String mensagem,
            int tamanho,
            boolean negrito) {

        TextView texto = new TextView(this);

        texto.setText(mensagem);
        texto.setTextSize(tamanho);

        texto.setTextColor(
                Color.parseColor("#252525")
        );

        texto.setPadding(
                0, dp(5), 0, dp(5)
        );

        if (negrito) {

            texto.setTypeface(
                    null, Typeface.BOLD
            );
        }

        return texto;
    }

    private void mensagem(String msg) {

        if (conteudo == null
                || isFinishing()
                || isDestroyed()) {

            return;
        }

        conteudo.removeAllViews();

        conteudo.addView(
                criarTexto(msg, 16, false)
        );
    }

    private int dp(int valor) {

        return Math.round(
                valor * getResources()
                        .getDisplayMetrics().density
        );
    }
}
