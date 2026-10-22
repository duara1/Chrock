package com.example.chrock;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    @NonNull
    @Override
    public UsuarioAdapter.UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioAdapter.UsuarioViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    // Coloque isso no final do seu UsuarioAdapter, antes da última chave }
    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        // 1. Declaramos as variáveis para guardar os componentes da tela
        TextView txtNome;
        ImageView imgPerfil;

        // Construtor: Ele recebe o layout da linha (itemView)
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            // 2. Fazemos o "findViewById" uma única vez para salvar na memória
            // IMPORTANTE: Verifique se estes IDs em vermelho são os mesmos do seu item_usuario.xml
            txtNome = itemView.findViewById(R.id.textView);
            imgPerfil = itemView.findViewById(R.id.imageView);
        }
    }

}

