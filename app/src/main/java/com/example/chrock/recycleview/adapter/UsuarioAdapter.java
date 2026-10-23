package com.example.chrock.recycleview.adapter;

import static android.view.View.inflate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chrock.R;
import com.example.chrock.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> listaUsuarios;
    public UsuarioAdapter (List<Usuario> lista) {
        this.listaUsuarios = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teste, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Usuario usuarioAtual = listaUsuarios.get(position);
        holder.textNome.setText(usuarioAtual.getNomeUsuario());
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome;

        public ViewHolder(View itemView) {
            super(itemView);

            textNome = itemView.findViewById(R.id.textView);
        }
    }
}