package com.example.chrock;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.Adapter.ParticipantesAdapter;
import com.example.chrock.data.Sessao;
import com.example.chrock.util.Acesso;
import com.example.chrock.util.Imagem;
import com.example.chrock.util.ImagemSala;
import com.example.chrock.model.Papel;
import com.example.chrock.model.Sala;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class criarSala extends AppCompatActivity {
    private final RepositorioSupabase repo = RepositorioSupabase.get();

    private String salaId;
    private Uri imagem;
    private EditText campoTitulo;
    private EditText campoDescricao;
    private ImageButton botaoImagem;
    private Button botaoSalvar;
    private TextView tituloModeradores;
    private RecyclerView listaModeradores;
    private ProgressBar carregando;
    private ParticipantesAdapter adapter;

    private final ActivityResultLauncher<String> seletorImagem =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imagem = uri;
                    botaoImagem.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_criar_sala);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.criarSala), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!Acesso.exigirLogin(this)) return;

        campoTitulo = findViewById(R.id.editTextText);
        campoDescricao = findViewById(R.id.editDescricao);
        botaoImagem = findViewById(R.id.imageButton);
        botaoSalvar = findViewById(R.id.btnSalvar);
        tituloModeradores = findViewById(R.id.tituloModeradores);
        listaModeradores = findViewById(R.id.listaModeradores);
        carregando = findViewById(R.id.carregando);

        salaId = getIntent().getStringExtra("salaId");

        if (salaId == null) {
            prepararCriacao();
        } else {
            prepararEdicao();
        }
    }

    private void prepararCriacao() {
        botaoSalvar.setText("Criar");
        tituloModeradores.setVisibility(View.GONE);
        listaModeradores.setVisibility(View.GONE);

        botaoImagem.setOnClickListener(v -> seletorImagem.launch("image/*"));
        botaoSalvar.setOnClickListener(v -> criar());
    }

    private void criar() {
        String titulo = campoTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            campoTitulo.setError("Dê um título à sala.");
            return;
        }
        String descricao = campoDescricao.getText().toString().trim();

        botaoSalvar.setEnabled(false);
        carregando.setVisibility(View.VISIBLE);

        if (imagem == null) {
            enviarCriacao(titulo, descricao, null);
            return;
        }

        Uri escolhida = imagem;
        new Thread(() -> {
            try {
                byte[] jpeg = Imagem.prepararJpeg(getContentResolver(), escolhida);
                runOnUiThread(() -> enviarCriacao(titulo, descricao, jpeg));
            } catch (IOException | RuntimeException e) {
                runOnUiThread(() -> falharAoSalvar("Não consegui ler essa imagem."));
            }
        }).start();
    }

    private void enviarCriacao(String titulo, String descricao, byte[] jpeg) {
        repo.criarSalaComImagem(titulo, descricao, jpeg,
                new RepositorioSupabase.Resposta<String>() {
                    @Override
                    public void sucesso(String id) {
                        Toast.makeText(criarSala.this, "Sala criada!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void erro(String mensagem) {
                        falharAoSalvar(mensagem);
                    }
                });
    }

    private void prepararEdicao() {
        botaoSalvar.setText("Salvar");
        botaoSalvar.setEnabled(false);
        botaoImagem.setEnabled(false);
        carregando.setVisibility(View.VISIBLE);

        repo.carregarSala(salaId, new RepositorioSupabase.Resposta<Sala>() {
            @Override
            public void sucesso(Sala sala) {
                buscarNomes(sala);
            }

            @Override
            public void erro(String mensagem) {
                falharAoCarregar(mensagem);
            }
        });
    }

    private void buscarNomes(Sala sala) {
        repo.carregarMembros(salaId, new RepositorioSupabase.Resposta<Map<String, Usuario>>() {
            @Override
            public void sucesso(Map<String, Usuario> usuarios) {
                mostrarSala(sala, usuarios);
            }

            @Override
            public void erro(String mensagem) {
                falharAoCarregar(mensagem);
            }
        });
    }

    private void mostrarSala(Sala sala, Map<String, Usuario> usuarios) {
        if (isFinishing() || isDestroyed()) return;

        String eu = Sessao.getUsuarioId();
        boolean souAdm = sala.isMembro(eu) && sala.getPapel(eu) == Papel.ADM;
        if (!souAdm) {
            Toast.makeText(this, "Só o ADM edita a sala.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        campoTitulo.setText(sala.getTitulo());
        campoDescricao.setText(sala.getDescricao());

        ImagemSala.mostrar(botaoImagem, sala.getImg());

        carregando.setVisibility(View.GONE);

        List<Usuario> candidatos = new ArrayList<>();
        Set<String> moderadoresAtuais = new HashSet<>();
        for (Map.Entry<String, Papel> e : sala.getMembros().entrySet()) {
            if (e.getValue() == Papel.ADM) continue;
            Usuario u = usuarios.get(e.getKey());
            if (u == null) continue;
            candidatos.add(u);
            if (e.getValue() == Papel.MODERADOR) moderadoresAtuais.add(u.getId());
        }

        if (!candidatos.isEmpty()) {
            adapter = new ParticipantesAdapter(candidatos, moderadoresAtuais);
            listaModeradores.setAdapter(adapter);
            tituloModeradores.setVisibility(View.VISIBLE);
            listaModeradores.setVisibility(View.VISIBLE);
        }

        botaoSalvar.setEnabled(true);
        botaoSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {
        String titulo = campoTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            campoTitulo.setError("A sala precisa de um título.");
            return;
        }
        String descricao = campoDescricao.getText().toString().trim();
        List<String> moderadores = adapter != null ? adapter.getSelecionados() : new ArrayList<>();

        botaoSalvar.setEnabled(false);
        carregando.setVisibility(View.VISIBLE);

        repo.salvarSala(salaId, titulo, descricao, moderadores,
                new RepositorioSupabase.Resposta<Void>() {
                    @Override
                    public void sucesso(Void nada) {
                        Toast.makeText(criarSala.this, "Sala salva!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void erro(String mensagem) {
                        falharAoSalvar(mensagem);
                    }
                });
    }

    private void falharAoCarregar(String mensagem) {
        if (isFinishing() || isDestroyed()) return;
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
        finish();
    }

    private void falharAoSalvar(String mensagem) {
        if (isFinishing() || isDestroyed()) return;
        carregando.setVisibility(View.GONE);
        botaoSalvar.setEnabled(true);
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }
}