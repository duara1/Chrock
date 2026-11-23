package com.example.chrock;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chrock.data.Sessao;
import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Acesso;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class historico extends AppCompatActivity {
    private String salaId;
    private LinearLayout conteudo;
    private Spinner modo;
    private final RepositorioSupabase repo = RepositorioSupabase.get();
    private List<Despesa> despesas;
    private Map<String, Usuario> usuarios;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!Acesso.exigirLogin(this)) return;
        setContentView(R.layout.activity_historico);
        salaId = getIntent().getStringExtra("salaId");
        if (salaId == null || salaId.trim().isEmpty()) { finish(); return; }
        conteudo = findViewById(R.id.conteudoHistorico);
        modo = findViewById(R.id.modoHistorico);
        modo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Meu histórico", "Histórico da sala", "Histórico por conta"}));
        modo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { mostrar(); }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        findViewById(R.id.abaInicio).setOnClickListener(v -> finish());
        carregar();
    }
    @Override protected void onResume() { super.onResume(); if (conteudo != null) carregar(); }
    private void carregar() {
        mensagem("Carregando histórico...");
        repo.carregarSala(salaId, new RepositorioSupabase.Resposta<com.example.chrock.model.Sala>() {
            @Override public void sucesso(com.example.chrock.model.Sala sala) {
                repo.carregarDespesas(salaId, new RepositorioSupabase.Resposta<List<Despesa>>() {
                    @Override public void sucesso(List<Despesa> dados) {
                        despesas = dados;
                        repo.carregarMembros(salaId, new RepositorioSupabase.Resposta<Map<String, Usuario>>() {
                            @Override public void sucesso(Map<String, Usuario> membros) { usuarios = membros; mostrar(); }
                            @Override public void erro(String erro) { mensagem(erro); }
                        });
                    }
                    @Override public void erro(String erro) { mensagem(erro); }
                });
            }
            @Override public void erro(String erro) { mensagem(erro); }
        });
    }
    private void mensagem(String msg) { if (conteudo == null || isFinishing()) return; conteudo.removeAllViews(); linha(msg, 16); }
    private void linha(String msg, int tamanho) {
        TextView t = new TextView(this);
        t.setText(msg); t.setTextSize(tamanho); t.setTextColor(0xff242424);
        t.setPadding(20, 18, 20, 18);
        conteudo.addView(t);
    }
    private void mostrar() {
        if (conteudo == null || despesas == null || usuarios == null || isFinishing()) return;
        conteudo.removeAllViews();
        String eu = Sessao.getUsuarioId();
        int modoAtual = modo.getSelectedItemPosition();
        int quantidade = 0;
        for (Despesa d : despesas) {
            boolean participo = d.isContribuente(eu);
            if (modoAtual == 0 && !participo) continue;
            quantidade++;
            linha(d.getTitulo(), 20);
            linha("Valor total: R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", d.getTotalEmInt()/100.0), 15);
            linha("Vencimento: " + d.getVencimento(), 14);
            Usuario responsavel = usuarios.get(d.getResponsavelId());
            linha("Responsável: " + (responsavel == null ? "Não identificado" : responsavel.getNome()), 14);
            if (modoAtual == 0) {
                linha("Minha contribuição: " + d.getStatus(eu).name(), 15);
            } else {
                for (Contribuicao c : d.getContribuintes()) {
                    Usuario u = usuarios.get(c.getUsuarioId());
                    linha((u == null ? "Participante" : u.getNome()) + ": " + c.getStatus().name(), 14);
                }
            }
            linha("────────────────────────", 14);
        }
        if (quantidade == 0) linha(modoAtual == 0 ? "Você não participa de nenhuma despesa nesta sala." : "Nenhuma despesa encontrada nesta sala.", 16);
        if (modoAtual == 2) linha("Exibição por conta. Selecione a despesa na lista para consultar seus participantes e status.", 13);
    }
}
