package com.example.chrock;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.Adapter.SalasAdapter;
import com.example.chrock.data.AuthApi;
import com.example.chrock.data.Sessao;
import com.example.chrock.model.Sala;
import com.example.chrock.repositorio.RepositorioSupabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String EMAIL_TESTE = "teste1@exemplo.com";
    private static final String SENHA_TESTE = "senha12345";

    private final RepositorioSupabase repo = RepositorioSupabase.get();

    private RecyclerView listaSalas;
    private ProgressBar carregando;
    private TextView vazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaSalas = findViewById(R.id.listaSalas);
        carregando = findViewById(R.id.carregando);
        vazio = findViewById(R.id.vazio);

        findViewById(R.id.button3).setOnClickListener(v -> abrirPopUpCodigo());
        findViewById(R.id.button4).setOnClickListener(v ->
                startActivity(new Intent(this, criarSala.class)));

        if (Sessao.estaLogado()) {
            carregarSalas();
        } else {
            entrar();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (Sessao.estaLogado()) carregarSalas();
    }

    private void entrar() {
        carregando.setVisibility(View.VISIBLE);

        AuthApi.entrar(EMAIL_TESTE, SENHA_TESTE, new AuthApi.Retorno() {
            @Override
            public void sucesso() {
                runOnUiThread(() -> carregarSalas());
            }

            @Override
            public void erro(String mensagem) {
                runOnUiThread(() -> {
                    carregando.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void carregarSalas() {
        carregando.setVisibility(View.VISIBLE);
        vazio.setVisibility(View.GONE);

        repo.carregarSalas(new RepositorioSupabase.Resposta<List<Sala>>() {
            @Override
            public void sucesso(List<Sala> salas) {
                if (isFinishing() || isDestroyed()) return;
                carregando.setVisibility(View.GONE);

                listaSalas.setAdapter(new SalasAdapter(salas, sala -> abrirSala(sala)));
                vazio.setVisibility(salas.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void erro(String mensagem) {
                if (isFinishing() || isDestroyed()) return;
                carregando.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void abrirSala(Sala sala) {
        Intent i = new Intent(this, SalaInicio.class);
        i.putExtra("salaId", sala.getId());
        startActivity(i);
    }

    private void abrirPopUpCodigo() {
        Dialog popUp = new Dialog(this);
        popUp.setContentView(R.layout.item_pop_up_sala);

        if (popUp.getWindow() != null) {
            popUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView titulo = popUp.findViewById(R.id.titulo);
        TextView texto = popUp.findViewById(R.id.valor);
        EditText campoCodigo = popUp.findViewById(R.id.editTextText2);
        Button botaoEntrar = popUp.findViewById(R.id.button2);

        titulo.setText("Entrar na sala");
        texto.setText("Para entrar, digite o código da sala");

        botaoEntrar.setOnClickListener(v -> {
            String codigo = campoCodigo.getText().toString().trim();
            if (codigo.isEmpty()) return;
            botaoEntrar.setEnabled(false);

            repo.entrarComCodigo(codigo, new RepositorioSupabase.Resposta<String>() {
                @Override
                public void sucesso(String salaId) {
                    if (isFinishing() || isDestroyed()) return;
                    popUp.dismiss();
                    Toast.makeText(MainActivity.this, "Você entrou na sala!", Toast.LENGTH_SHORT).show();
                    carregarSalas();
                }

                @Override
                public void erro(String mensagem) {
                    if (isFinishing() || isDestroyed()) return;
                    botaoEntrar.setEnabled(true);
                }
            });
        });

        popUp.show();
    }
}