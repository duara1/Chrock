
package com.example.chrock;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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

        setContentView(R.layout.activity_main);

        recyclerUsuarios = findViewById(R.id.rv_usuario);

        listaUsuario = new ArrayList<>();

        listaUsuario.add(new Usuario("Luara"));
        listaUsuario.add(new Usuario("Marcos"));
        listaUsuario.add(new Usuario("Yasmin"));
        listaUsuario.add(new Usuario("Isaque"));

        usuario = new UsuarioAdapter(listaUsuario);

        recyclerUsuarios.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        recyclerUsuarios.setAdapter(usuario);
    }
}
