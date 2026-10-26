package com.example.chrock;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.recycleview.adapter.UsuarioAdapter;

import java.util.ArrayList;
import java.util.List;

public class DespesasActivity extends AppCompatActivity {
    private RecyclerView recyclerUsuarios;
    private UsuarioAdapter usuario;
    private List<Usuario> listaUsuario;

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

        recyclerUsuarios = findViewById(R.id.rv_usuario);

        listaUsuario = new ArrayList<>();
        listaUsuario.add(new Usuario("Luara"));
        listaUsuario.add(new Usuario("Marcos"));
        listaUsuario.add(new Usuario("Yasmin"));
        listaUsuario.add(new Usuario("Isaque"));
        usuario = new UsuarioAdapter(listaUsuario);

        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsuarios.setAdapter(usuario);

    }

    public void copiarChavePix(View v) {
        //usuario clica no botão e é copiado o pix
        //usuario precisa interagir e o botão precisa reagir ao evento de clique e retornar um dado específico: chave pix

        Button chavePix = findViewById(R.id.button3);
        chavePix.setOnClickListener(v -> {

        });
    }


}