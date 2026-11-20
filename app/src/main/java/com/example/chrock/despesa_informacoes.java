package com.example.chrock;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.Adapter.contribuintesAdapter;
import com.example.chrock.data.Sessao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Formatar;

import java.util.Map;

public class despesa_informacoes extends AppCompatActivity {

    private final RepositorioSupabase repo = RepositorioSupabase.get();
    private ProgressBar carregando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_despesa_informacoes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.despesaInformacoes), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String despesaId = getIntent().getStringExtra("id");
        if (despesaId == null || !Sessao.estaLogado()) {
            finish();
            return;
        }

        carregando = findViewById(R.id.carregando);
        carregando.setVisibility(View.VISIBLE);

        repo.carregarDespesa(despesaId, new RepositorioSupabase.Resposta<Despesa>() {
            @Override
            public void sucesso(Despesa despesa) {
                buscarMembros(despesa);
            }

            @Override
            public void erro(String mensagem) {
                falhar(mensagem);
            }
        });
    }

    private void buscarMembros(Despesa despesa) {
        repo.carregarMembros(despesa.getSalaId(),
                new RepositorioSupabase.Resposta<Map<String, Usuario>>() {
                    @Override
                    public void sucesso(Map<String, Usuario> usuarios) {
                        mostrar(despesa);
                    }

                    @Override
                    public void erro(String mensagem) {
                        falhar(mensagem);
                    }
                });
    }

    private void mostrar(Despesa item) {
        if (isFinishing() || isDestroyed()) return;

        TextView titulo = findViewById(R.id.tituloDespesa);
        TextView total = findViewById(R.id.subTituloDespesa);
        TextView responsavel = findViewById(R.id.nomeResponsavel);
        TextView vencimento = findViewById(R.id.dataVencimento);
        TextView aTransferir = findViewById(R.id.valor);

        Usuario resp = repo.getUsuario(item.getResponsavelId());

        titulo.setText(item.getTitulo());
        total.setText(Formatar.reais(item.getTotalEmInt()));
        responsavel.setText(resp != null ? resp.getNome() : "Responsável");
        vencimento.setText(item.getVencimento());
        aTransferir.setText(Formatar.reais(item.getValorATransferirEmInt(Sessao.getUsuarioId())));

        RecyclerView listaContribuintes = findViewById(R.id.listaContribuintes);
        listaContribuintes.setAdapter(new contribuintesAdapter(item.getContribuintes()));

        carregando.setVisibility(View.GONE);
    }

    private void falhar(String mensagem) {
        if (isFinishing() || isDestroyed()) return;
        carregando.setVisibility(View.GONE);
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }
}