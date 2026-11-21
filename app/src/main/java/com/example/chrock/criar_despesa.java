package com.example.chrock;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chrock.data.Sessao;

public class criar_despesa extends AppCompatActivity {

    // A sala onde a despesa vai ser criada (o SalaInicio manda pelo Intent)
    private String salaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_criar_despesa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaCriarDespesa), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        salaId = getIntent().getStringExtra("salaId");
        if (salaId == null || !Sessao.estaLogado()) {
            finish();
            return;
        }

        // Setinha: fecha esta tela e mostra a SalaInicio, que continua aberta por baixo
        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());
    }
}