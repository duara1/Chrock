package com.example.chrock;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.chrock.util.Acesso;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.StatusContribuicao;
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

        if (!Acesso.exigirLogin(this)) return;

        String despesaId = getIntent().getStringExtra("id");
        if (despesaId == null) {
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

        prepararBotoes(item, resp);

        carregando.setVisibility(View.GONE);
    }

    private void prepararBotoes(Despesa item, Usuario resp) {
        String eu = Sessao.getUsuarioId();
        boolean souResponsavel = item.getResponsavelId().equals(eu);

        boolean precisaTransferir = !item.isConcluida()
                && !souResponsavel
                && item.isContribuente(eu)
                && (item.getStatus(eu) == StatusContribuicao.PENDENTE
                || item.getStatus(eu) == StatusContribuicao.CONTESTADO);

        Button btnPix = findViewById(R.id.btnPix);
        boolean temPix = resp != null && !resp.getPix().isEmpty();
        btnPix.setVisibility(precisaTransferir && temPix ? View.VISIBLE : View.GONE);
        btnPix.setOnClickListener(v -> copiarPix(resp.getPix()));

        Button btnSinalizar = findViewById(R.id.sinalizar);
        btnSinalizar.setVisibility(precisaTransferir ? View.VISIBLE : View.GONE);
        btnSinalizar.setOnClickListener(v -> sinalizar(item, btnSinalizar));
    }

    private void copiarPix(String pix) {
        ClipboardManager areaDeTransferencia = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (areaDeTransferencia == null) return;
        areaDeTransferencia.setPrimaryClip(ClipData.newPlainText("Pix", pix));
        Toast.makeText(this, "Pix copiado", Toast.LENGTH_SHORT).show();
    }

    private void sinalizar(Despesa item, Button btnSinalizar) {
        btnSinalizar.setEnabled(false);
        carregando.setVisibility(View.VISIBLE);

        repo.sinalizarTransferencia(item.getDespesaId(), new RepositorioSupabase.Resposta<Void>() {
            @Override
            public void sucesso(Void nada) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(despesa_informacoes.this, "Transferência sinalizada!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void erro(String mensagem) {
                if (isFinishing() || isDestroyed()) return;
                carregando.setVisibility(View.GONE);
                btnSinalizar.setEnabled(true);
                Toast.makeText(despesa_informacoes.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void falhar(String mensagem) {
        if (isFinishing() || isDestroyed()) return;
        carregando.setVisibility(View.GONE);
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }
}