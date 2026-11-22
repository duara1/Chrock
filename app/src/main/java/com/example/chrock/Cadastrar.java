package com.example.chrock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chrock.data.AuthApi;

public class Cadastrar extends AppCompatActivity {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private EditText campoNome;
    private EditText campoEmail;
    private EditText campoSenha;
    private EditText campoChave;
    private Button botaoCadastrar;

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

        campoNome = findViewById(R.id.cadastroNome);
        campoEmail = findViewById(R.id.cadastroEmail);
        campoSenha = findViewById(R.id.cadastroSenha);
        campoChave = findViewById(R.id.cadastroChave);
        botaoCadastrar = findViewById(R.id.botaoCadastrar);

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
            return;
        }

        botaoCadastrar.setEnabled(false);
        botaoCadastrar.setText("Cadastrando...");

        AuthApi.cadastrar(nome, chavePix, email, senha, new AuthApi.Retorno() {
            @Override
            public void sucesso() {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    Intent intent = new Intent(Cadastrar.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
                    Toast.makeText(Cadastrar.this, mensagem, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
