package com.example.chrock;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatButton;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class CriarSalaActivity extends AppCompatActivity {

    private ImageView btnVoltar;
    private ImageView cardImagem;
    private ImageView iconeImagem;
    private EditText editTitulo;
    private EditText editDescricao;
    private AppCompatButton btnSalvar;
    private ActivityResultLauncher<String> selecionarImagemLauncher;
    private Uri imagemSelecionadaUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_sala);

        btnVoltar = findViewById(R.id.btnVoltar);
        cardImagem = findViewById(R.id.cardImagem);
        iconeImagem = findViewById(R.id.iconeImagem);
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        btnSalvar = findViewById(R.id.btnSalvar);

        selecionarImagemLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imagemSelecionadaUri = uri;
                        cardImagem.setImageURI(uri);
                        iconeImagem.setVisibility(View.GONE);
                    }
                }
        );

        cardImagem.setOnClickListener(v -> {
            selecionarImagemLauncher.launch("image/*");
        });

        btnVoltar.setOnClickListener(v -> finish());

        btnSalvar.setOnClickListener(v -> {
            String titulo = editTitulo.getText().toString().trim();
            String descricao = editDescricao.getText().toString().trim();

            if (titulo.isEmpty()) {
                Toast.makeText(CriarSalaActivity.this, "Por favor, insira um título.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(CriarSalaActivity.this, "Sala salva com sucesso!", Toast.LENGTH_SHORT).show();
        });
    }
}