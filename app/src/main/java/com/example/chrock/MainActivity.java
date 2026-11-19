package com.example.chrock;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.example.chrock.repositorio.RepositorioFalso;
import com.example.chrock.util.Formatar;

import java.util.List;


public class MainActivity extends AppCompatActivity {

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

        RepositorioFalso repo = RepositorioFalso.get();
        String usuarioId = "u2";
        String salaId = "s1";

        //PESSOAL
        RepositorioFalso.cardsInfo geralPes = repo.getInfosCard(usuarioId, salaId, Geral.PESSOAL);
        ((TextView) findViewById(R.id.pendValorPessoal)).setText(Formatar.reais(geralPes.pendente));
        ((TextView) findViewById(R.id.pagValorPessoal)).setText(Formatar.reais(geralPes.pago));
        ((TextView) findViewById(R.id.totalValorPessoal)).setText(Formatar.reais(geralPes.total));

        //SALA
        RepositorioFalso.cardsInfo geralSala = repo.getInfosCard(usuarioId, salaId, Geral.SALA);
        ((TextView) findViewById(R.id.pendValorSala)).setText(Formatar.reais(geralSala.pendente));
        ((TextView) findViewById(R.id.pagValorSala)).setText(Formatar.reais(geralSala.pago));
        ((TextView) findViewById(R.id.pagoValorSala)).setText(Formatar.reais(geralSala.total));

        //CONFIRMAR
        List<RepositorioFalso.ItemConfirmacao> aConfirmar = repo.aConfirmar(usuarioId, salaId);
        RecyclerView listaConfirmacao = findViewById(R.id.listaConfirmar);
        listaConfirmacao.setAdapter(new ConfirmacaoAdapter(aConfirmar));

        int visiConfirmar = aConfirmar.isEmpty() ? View.GONE : View.VISIBLE;
        findViewById(R.id.tituloConfirmar).setVisibility(visiConfirmar);
        findViewById(R.id.subTituloConfirmar).setVisibility(visiConfirmar);
        listaConfirmacao.setVisibility(visiConfirmar);

        //TRANSFERIR
        List<Despesa> aTransferir = repo.aTransferir(usuarioId, salaId);
        RecyclerView listaTranferencia = findViewById(R.id.listaTransferir);
        listaTranferencia.setAdapter(new transferenciaAdapter(aTransferir));

        int visiTransferencia = aTransferir.isEmpty() ? View.GONE : View.VISIBLE;
        findViewById(R.id.tituloTransferir).setVisibility(visiTransferencia);
        findViewById(R.id.subTituloTransferir).setVisibility(visiTransferencia);
        listaTranferencia.setVisibility(visiTransferencia);

        //RESPONSAVEL
        List<Despesa> responsavel = repo.souResponsavel(usuarioId, salaId);
        RecyclerView listaResponsavel = findViewById(R.id.listaResponsavel);
        listaResponsavel.setAdapter(new responsavelAdapter(responsavel));

        int visiResponsavel = responsavel.isEmpty() ? View.GONE : View.VISIBLE;
        findViewById(R.id.tituloResponsavel).setVisibility(visiResponsavel);
        findViewById(R.id.subTituloResponsavel).setVisibility(visiResponsavel);
        listaResponsavel.setVisibility(visiResponsavel);



        AuthApi.cadastrar("Fulano Teste", "fulano@email.com", "teste2@exemplo.com", "senha12345",
                new AuthApi.Retorno() {
                    @Override
                    public void sucesso() {
                        Log.d("CHROK", "Logado! id = " + Sessao.getUsuarioId());
                    }

                    @Override
                    public void erro(String mensagem) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_LONG).show());
                    }
                });
    }
}