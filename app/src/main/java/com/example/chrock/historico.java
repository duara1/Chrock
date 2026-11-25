package com.example.chrock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chrock.data.Sessao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Acesso;
import com.example.chrock.util.Formatar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class historico extends AppCompatActivity {

    private static final int MEU_HISTORICO = 0;
    private static final int HISTORICO_DA_SALA = 1;

    private final RepositorioSupabase repo = RepositorioSupabase.get();

    private String salaId;
    private LinearLayout conteudo;

    private Spinner modo;

    private List<Despesa> despesas;
    private Map<String, Usuario> usuarios;
    private boolean carregando = false;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        if (!Acesso.exigirLogin(this)) return;

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historico), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        salaId = getIntent().getStringExtra("salaId");
        if (salaId == null || salaId.trim().isEmpty()) {
            finish();
            return;
        }

        conteudo = findViewById(R.id.conteudoHistorico);
        modo = findViewById(R.id.modoHistorico);

        configurarFiltros();
        configurarMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (conteudo != null && Acesso.exigirLogin(this)) carregar();
    }

    private void configurarMenu() {
        findViewById(R.id.abaInicio).setOnClickListener(v -> finish());
    }

    private void configurarFiltros() {
        modo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Meu histórico", "Histórico da sala"}));

        modo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> pai, View view, int posicao, long id) {
                mostrar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> pai) {
            }
        });
    }

    private void carregar() {
        if (carregando) return;
        carregando = true;
        mensagem("Carregando histórico...");

        repo.carregarDespesas(salaId, new RepositorioSupabase.Resposta<List<Despesa>>() {
            @Override
            public void sucesso(List<Despesa> dados) {
                if (isFinishing() || isDestroyed()) return;

                repo.carregarMembros(salaId, new RepositorioSupabase.Resposta<Map<String, Usuario>>() {
                    @Override
                    public void sucesso(Map<String, Usuario> membros) {
                        if (isFinishing() || isDestroyed()) return;

                        despesas = new ArrayList<>(dados);
                        Collections.reverse(despesas);
                        usuarios = membros;
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

    private void falha(String erro) {
        carregando = false;
        mensagem(erro);
    }

    private boolean entraNoMeuHistorico(Despesa d, String eu) {
        if (!d.isContribuente(eu)) return false;
        if (d.getResponsavelId().equals(eu)) return false;

        StatusContribuicao status = d.getStatus(eu);
        return status == StatusContribuicao.SINALIZADO
                || status == StatusContribuicao.CONFIRMADO;
    }

    private boolean entraNoHistoricoDaSala(Despesa d) {
        return d.isConcluida();
    }

    private void mostrar() {
        if (conteudo == null || despesas == null || usuarios == null || carregando
                || isFinishing() || isDestroyed()) {
            return;
        }

        conteudo.removeAllViews();
        String eu = Sessao.getUsuarioId();
        int modoAtual = modo.getSelectedItemPosition();
        int quantidade = 0;

        for (Despesa d : despesas) {
            if (modoAtual == MEU_HISTORICO) {
                if (!entraNoMeuHistorico(d, eu)) continue;
                adicionarCardPessoal(d, eu);
            } else {
                if (!entraNoHistoricoDaSala(d)) continue;
                adicionarCardDaSala(d);
            }
            quantidade++;
        }

        if (quantidade == 0) {
            mensagem(modoAtual == MEU_HISTORICO
                    ? "Você ainda não tem transferências aqui."
                    : "Nenhuma conta paga ainda.");
        }
    }

    private void adicionarCardPessoal(Despesa d, String eu) {
        LinearLayout card = novoCard();

        card.addView(criarTexto(d.getTitulo(), 18, true));
        card.addView(criarTexto("Minha parte: R$ " + Formatar.reais(d.getValorATransferirEmInt(eu)), 16, true));
        card.addView(criarTexto("Para: " + nomeUsuario(d.getResponsavelId()), 14, false));
        card.addView(criarTexto("Vencimento: " + d.getVencimento(), 14, false));

        boolean confirmado = d.getStatus(eu) == StatusContribuicao.CONFIRMADO;
        TextView status = criarTexto(confirmado ? "Confirmado" : "Aguardando confirmação", 14, true);
        status.setTextColor(Color.parseColor(confirmado ? "#478B37" : "#C77B22"));
        card.addView(status);

        card.setContentDescription(d.getTitulo() + ", " + (confirmado ? "confirmado" : "aguardando confirmação"));
        card.setOnClickListener(v -> {
            Intent i = new Intent(this, despesa_informacoes.class);
            i.putExtra("id", d.getDespesaId());
            startActivity(i);
        });

        conteudo.addView(card);
    }

    private void adicionarCardDaSala(Despesa d) {
        LinearLayout card = novoCard();

        card.addView(criarTexto(d.getTitulo(), 18, true));
        card.addView(criarTexto("Total: R$ " + Formatar.reais(d.getTotalEmInt()), 16, true));
        card.addView(criarTexto("Paga por: " + nomeUsuario(d.getResponsavelId()), 14, false));
        card.addView(criarTexto("Vencimento: " + d.getVencimento(), 14, false));

        TextView verComprovante = criarTexto("Ver comprovante", 14, true);
        verComprovante.setTextColor(getColor(R.color.roxoBtn));
        card.addView(verComprovante);

        card.setContentDescription(d.getTitulo() + ", paga. Toque para ver o comprovante");
        card.setOnClickListener(v -> mostrarComprovante(d));

        conteudo.addView(card);
    }

    private void mostrarComprovante(Despesa d) {
        if (d.getComprovante() == null) return;

        Toast.makeText(this, "Abrindo comprovante...", Toast.LENGTH_SHORT).show();

        repo.baixarComprovante(d.getComprovante().toString(), new RepositorioSupabase.Resposta<byte[]>() {
            @Override
            public void sucesso(byte[] dados) {
                if (isFinishing() || isDestroyed()) return;

                Bitmap foto = BitmapFactory.decodeByteArray(dados, 0, dados.length);
                if (foto == null) {
                    Toast.makeText(historico.this, "Não foi possível abrir o comprovante.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                ImageView imagem = new ImageView(historico.this);
                imagem.setImageBitmap(foto);
                imagem.setAdjustViewBounds(true);
                imagem.setPadding(dp(8), dp(8), dp(8), dp(8));
                imagem.setContentDescription("Comprovante de " + d.getTitulo());

                new AlertDialog.Builder(historico.this)
                        .setTitle(d.getTitulo())
                        .setView(imagem)
                        .setPositiveButton("Fechar", null)
                        .show();
            }

            @Override
            public void erro(String mensagem) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(historico.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private LinearLayout novoCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.WHITE);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setElevation(dp(3));
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp(4), dp(6), dp(4), dp(10));
        card.setLayoutParams(params);
        return card;
    }

    private String nomeUsuario(String id) {
        Usuario usuario = usuarios.get(id);
        return usuario == null ? "Alguém" : usuario.getNome();
    }

    private TextView criarTexto(String texto, int tamanho, boolean negrito) {
        TextView t = new TextView(this);
        t.setText(texto);
        t.setTextSize(tamanho);
        t.setTextColor(Color.parseColor("#252525"));
        t.setPadding(0, dp(3), 0, dp(3));
        if (negrito) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private void mensagem(String msg) {
        if (conteudo == null || isFinishing() || isDestroyed()) return;
        conteudo.removeAllViews();
        conteudo.addView(criarTexto(msg, 16, false));
    }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }
}