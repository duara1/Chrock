package com.example.chrock;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chrock.data.Sessao;

public class historico extends AppCompatActivity {
    String salaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historico), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        salaId = getIntent().getStringExtra("salaId");
        if (salaId == null || !Sessao.estaLogado()) {
            finish();
            return;
        }
        findViewById(R.id.abaInicio).setOnClickListener(v -> finish());

        // O Histórico não faz nada aqui, porque a pessoa já está nele.
    }
}