package com.example.chrock;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.Adapter.ConfirmacaoAdapter;
import com.example.chrock.Adapter.responsavelAdapter;
import com.example.chrock.Adapter.transferenciaAdapter;
import com.example.chrock.data.Sessao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.ItemConfirmacao;
import com.example.chrock.model.Papel;
import com.example.chrock.model.Sala;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Secoes;

import java.util.List;
import java.util.Map;

public class SalaInicio extends AppCompatActivity {
    // A sala que a MainActivity mandou abrir
    private String salaId;
    private responsavelAdapter adapter;
    private ProgressBar carregando;
    private Sala salaAtual;
    private Despesa item;
    private Button btnAnexar;
    private  Button paga;
    private ActivityResultLauncher<String> pegarImg = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            caminho ->{
                if (caminho != null){
                    if (btnAnexar != null && paga != null && adapter != null){
                        btnAnexar.setText("Imagem anexada");
                        paga.setTextColor(Color.rgb(99,77,244));
                        adapter.receberUri(caminho);
                    }
                }
            }
    );

    private void atualizarEngrenagem(Sala sala) {
        String eu = Sessao.getUsuarioId();
        boolean souAdm = sala.isMembro(eu) && sala.getPapel(eu) == Papel.ADM;
        findViewById(R.id.btnConfig).setVisibility(souAdm ? View.VISIBLE : View.GONE);
    }

    private final ActivityResultLauncher<Intent> abrirSala =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), r -> {
                if (r.getResultCode() == RESULT_OK) carregarHome();
            });

    private void carregarHome() {
        carregando.setVisibility(View.VISIBLE);

        RepositorioSupabase.get().carregarSala(salaId, new RepositorioSupabase.Resposta<Sala>() {
            @Override
            public void sucesso(Sala sala) {
                salaAtual = sala;
                atualizarEngrenagem(sala);
                carregarDadosDaSala(sala);
            }

            @Override
            public void erro(String mensagem) {
                falhaNaHome(mensagem);
            }
        });
    }

    private void carregarDadosDaSala(Sala sala) {
        RepositorioSupabase repo = RepositorioSupabase.get();

        repo.carregarDespesas(sala.getId(), new RepositorioSupabase.Resposta<List<Despesa>>() {
            @Override
            public void sucesso(List<Despesa> despesas) {
                repo.carregarMembros(sala.getId(),
                        new RepositorioSupabase.Resposta<Map<String, Usuario>>() {
                            @Override
                            public void sucesso(Map<String, Usuario> usuarios) {
                                montarHome(despesas);
                            }

                            @Override
                            public void erro(String mensagem) {
                                falhaNaHome(mensagem);
                            }
                        });
            }

            @Override
            public void erro(String mensagem) {
                falhaNaHome(mensagem);
            }
        });
    }

    private void montarHome(List<Despesa> despesas) {
        String eu = Sessao.getUsuarioId();

        List<ItemConfirmacao> aConfirmar = Secoes.aConfirmar(despesas, eu);
        List<Despesa> aTransferir = Secoes.aTransferir(despesas, eu);
        List<Despesa> responsavel = Secoes.souResponsavel(despesas, eu);

        ((RecyclerView) findViewById(R.id.listaConfirmar))
                .setAdapter(new ConfirmacaoAdapter(aConfirmar));
        ((RecyclerView) findViewById(R.id.listaTransferir))
                .setAdapter(new transferenciaAdapter(aTransferir));
        // Guardado no campo "adapter" pra o pegarImg conseguir entregar a imagem a ele
        adapter = new responsavelAdapter(responsavel, (item, btnAnexar, paga) -> {
            this.item = item;
            this.btnAnexar = btnAnexar;
            this.paga = paga;
            pegarImg.launch("image/*");
        });
        ((RecyclerView) findViewById(R.id.listaResponsavel)).setAdapter(adapter);

        mostrar(!aConfirmar.isEmpty(),
                R.id.tituloConfirmar, R.id.subTituloConfirmar, R.id.listaConfirmar);
        mostrar(!aTransferir.isEmpty(),
                R.id.tituloTransferir, R.id.subTituloTransferir, R.id.listaTransferir);
        mostrar(!responsavel.isEmpty(),
                R.id.tituloResponsavel, R.id.subTituloResponsavel, R.id.listaResponsavel);

        carregando.setVisibility(View.GONE);
    }

    private void mostrar(boolean visivel, int... ids) {
        for (int id : ids) {
            findViewById(id).setVisibility(visivel ? View.VISIBLE : View.GONE);
        }
    }

    private void falhaNaHome(String mensagem) {
        carregando.setVisibility(View.GONE);
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sala);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Sala), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnConfig).setOnClickListener(v -> {
            if (salaAtual == null) return;
            Intent i = new Intent(this, criarSala.class);
            i.putExtra("salaId", salaAtual.getId());
            abrirSala.launch(i);
        });

        carregando = findViewById(R.id.carregando);

        // O login agora acontece na MainActivity, antes de chegar aqui
        salaId = getIntent().getStringExtra("salaId");
        if (salaId == null || !Sessao.estaLogado()) {
            finish();
            return;
        }

        carregarHome();
    }
}