package com.example.chrock;

import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.Adapter.ConfirmacaoAdapter;
import com.example.chrock.Adapter.responsavelAdapter;
import com.example.chrock.Adapter.transferenciaAdapter;
import com.example.chrock.model.Despesa;
import com.example.chrock.repositorio.RepositorioFalso;

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
    }
}