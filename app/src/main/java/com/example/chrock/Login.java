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
import com.example.chrock.data.Sessao;

public class Login extends AppCompatActivity {

    private EditText campoEmail;
    private EditText campoSenha;
    private Button botaoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Sessao.estaLogado()) {
            abrirSalas();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        campoEmail = findViewById(R.id.emailLoginInput);
        campoSenha = findViewById(R.id.senhaLoginInput);
        botaoLogin = findViewById(R.id.botaoLogin);

        botaoLogin.setOnClickListener(v -> entrar());

        findViewById(R.id.textoCadastrar).setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Cadastrar.class);
            startActivity(intent);
        });
    }

    private void entrar() {
        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            campoEmail.setError("Digite um e-mail válido");
            return;
        }
        if (senha.isEmpty()) {
            campoSenha.setError("Digite a senha");
            return;
        }

        botaoLogin.setEnabled(false);
        botaoLogin.setText("Entrando...");

        AuthApi.entrar(email, senha, new AuthApi.Retorno() {
            @Override
            public void sucesso() {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    abrirSalas();
                });
            }

            @Override
            public void erro(String mensagem) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    botaoLogin.setEnabled(true);
                    botaoLogin.setText("Entrar");
                    Toast.makeText(Login.this, mensagem, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void abrirSalas() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
