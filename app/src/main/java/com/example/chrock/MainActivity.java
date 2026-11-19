package com.example.chrock;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import com.example.chrock.Adapter.ConfirmacaoAdapter;
import com.example.chrock.Adapter.responsavelAdapter;
import com.example.chrock.Adapter.transferenciaAdapter;
import com.example.chrock.data.AuthApi;
import com.example.chrock.data.Sessao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Geral;
import com.example.chrock.model.ItemConfirmacao;
import com.example.chrock.model.Sala;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioFalso;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Formatar;
import com.example.chrock.util.Secoes;

import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private responsavelAdapter adapter;
    private ProgressBar carregando;
    private Despesa item;
    private Button btnAnexar;
    private  Button paga;
    private ActivityResultLauncher<String> pegarImg = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            caminho ->{
                    if (caminho != null){
                        if (btnAnexar != null){
                            btnAnexar.setText("Imagem anexada");
                            paga.setTextColor(Color.rgb(99,77,244));
                            adapter.receberUri(caminho);
                        }
                    }
            }
    );

    private void carregarHome() {
        RepositorioSupabase repo = RepositorioSupabase.get();

        repo.carregarSalas(new RepositorioSupabase.Resposta<List<Sala>>() {
            @Override
            public void sucesso(List<Sala> salas) {
                if (salas.isEmpty()) {
                    carregando.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Você ainda não está em nenhuma sala.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                carregarDadosDaSala(salas.get(0));
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
        ((RecyclerView) findViewById(R.id.listaResponsavel))
                .setAdapter(new responsavelAdapter(responsavel, (item, btnAnexar, paga) ->{
                    this.item = item;
                    this.btnAnexar = btnAnexar;
                    this.paga = paga;
                    pegarImg.launch("image/*");
                }));

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
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        carregando = findViewById(R.id.carregando);

        carregando.setVisibility(View.VISIBLE);

        AuthApi.entrar("teste2@exemplo.com", "senha12345", new AuthApi.Retorno() {
            @Override
            public void sucesso() {
                runOnUiThread(() -> carregarHome());
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
}