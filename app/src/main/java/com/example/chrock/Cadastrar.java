package com.example.chrock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.chrock.data.AuthApi;
import com.example.chrock.data.Sessao;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Imagem;

import java.io.IOException;

public class Cadastrar extends AppCompatActivity {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private EditText campoNome;
    private EditText campoEmail;
    private EditText campoSenha;
    private EditText campoChave;
    private Button botaoCadastrar;
    private ImageView fotoPerfil;

    private Uri fotoEscolhida;

    private final ActivityResultLauncher<String> escolherFoto =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                fotoEscolhida = uri;
                Glide.with(this).load(uri).circleCrop().into(fotoPerfil);
                fotoPerfil.setContentDescription("Foto de perfil escolhida. Toque para trocar");
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastrar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Sessao.carregar(this);

        campoNome = findViewById(R.id.cadastroNome);
        campoEmail = findViewById(R.id.cadastroEmail);
        campoSenha = findViewById(R.id.cadastroSenha);
        campoChave = findViewById(R.id.cadastroChave);
        botaoCadastrar = findViewById(R.id.botaoCadastrar);
        fotoPerfil = findViewById(R.id.fotoPerfil);

        fotoPerfil.setOnClickListener(v -> escolherFoto.launch("image/*"));
        botaoCadastrar.setOnClickListener(v -> cadastrar());
    }

    private void cadastrar() {
        String nome = campoNome.getText().toString().trim();
        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString();
        String chavePix = campoChave.getText().toString().trim();

        if (nome.isEmpty()) {
            campoNome.setError("Digite seu nome");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            campoEmail.setError("Digite um e-mail válido");
            return;
        }
        if (senha.length() < TAMANHO_MINIMO_SENHA) {
            campoSenha.setError("A senha precisa ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres");
            return;
        }
        if (chavePix.isEmpty()) {
            campoChave.setError("Digite sua chave Pix");
            campoChave.requestFocus();
            return;
        }
        if (fotoEscolhida == null) {
            Toast.makeText(this, "Escolha uma foto de perfil", Toast.LENGTH_SHORT).show();
            return;
        }

        botaoCadastrar.setEnabled(false);
        botaoCadastrar.setText("Cadastrando...");
        fotoPerfil.setEnabled(false);

        AuthApi.cadastrar(nome, chavePix, email, senha, new AuthApi.Retorno() {
            @Override
            public void sucesso() {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    enviarFoto();
                });
            }

            @Override
            public void erro(String mensagem) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;

                    if (AuthApi.MSG_CONFIRMAR_EMAIL.equals(mensagem)) {
                        Toast.makeText(Cadastrar.this,
                                "Cadastro feito! Confirme o e-mail e depois entre.",
                                Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    botaoCadastrar.setEnabled(true);
                    botaoCadastrar.setText("Cadastrar");
                    fotoPerfil.setEnabled(true);
                    Toast.makeText(Cadastrar.this, mensagem, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void enviarFoto() {
        botaoCadastrar.setText("Enviando foto...");

        new Thread(() -> {
            try {
                byte[] foto = Imagem.prepararJpeg(getContentResolver(), fotoEscolhida);
                runOnUiThread(() -> salvarFoto(foto));
            } catch (IOException | RuntimeException e) {
                runOnUiThread(() -> fotoNaoSalva());
            }
        }).start();
    }

    private void salvarFoto(byte[] foto) {
        RepositorioSupabase.get().salvarFotoPerfil(foto, new RepositorioSupabase.Resposta<String>() {
            @Override
            public void sucesso(String endereco) {
                if (isFinishing() || isDestroyed()) return;
                abrirSalas();
            }

            @Override
            public void erro(String mensagem) {
                fotoNaoSalva();
            }
        });
    }

    private void fotoNaoSalva() {
        if (isFinishing() || isDestroyed()) return;
        Toast.makeText(this, "Sua conta foi criada, mas não deu pra salvar a foto.",
                Toast.LENGTH_LONG).show();
        abrirSalas();
    }

    private void abrirSalas() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}