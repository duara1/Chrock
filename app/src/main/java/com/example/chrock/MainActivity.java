package com.example.chrock;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import com.example.chrock.data.DadosManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Resumo pessoal
        findViewById(R.id.btnResumoPessoal)
                .setOnClickListener(v ->
                        abrirTela(ResumoPessoalActivity.class));

// Resumo da sala
        findViewById(R.id.btnResumoSala)
                .setOnClickListener(v ->
                        abrirTela(ResumoSalaActivity.class));

// Histórico pessoal
        findViewById(R.id.btnHistoricoPessoal)
                .setOnClickListener(v ->
                        abrirTela(HistoricoActivity.class));

// Histórico por conta
        findViewById(R.id.btnHistoricoConta)
                .setOnClickListener(v ->
                        abrirTela(HistoricoContaActivity.class));

// Histórico da sala
        findViewById(R.id.btnHistoricoSala)
                .setOnClickListener(v ->
                        abrirTela(HistoricoSalaActivity.class));

// Notificações
        findViewById(R.id.btnNotificacoes)
                .setOnClickListener(v ->
                        abrirTela(NotificacoesActivity.class));
        DadosManager.inicializar(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void abrirTela(Class<?> tela) {
        Intent intent = new Intent(MainActivity.this, tela);
        startActivity(intent);
    }
}