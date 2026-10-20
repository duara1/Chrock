
package com.example.chrock;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HistoricoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        TextView botaoVoltar = findViewById(R.id.botaoVoltar);

        botaoVoltar.setOnClickListener(view -> finish());
    }
}
