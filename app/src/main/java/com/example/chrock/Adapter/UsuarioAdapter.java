package com.example.chrock.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.model.Usuario;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> listaUsuarios;
    private Set<String> usuariosSelecionados = new HashSet<>();

    public UsuarioAdapter(List<Usuario> lista) {
        this.listaUsuarios = lista;
    }
    public Set<String> getUsuariosSelecionados() {
        return usuariosSelecionados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.molde_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Usuario usuarioAtual = listaUsuarios.get(position);

        holder.textNome.setText(usuarioAtual.getNome());
        String idUsuario = usuarioAtual.getId();

        if (usuariosSelecionados.contains(idUsuario)) {
            holder.cardUsuario.setCardBackgroundColor(Color.parseColor("#8059CD3B"));
        } else {
            holder.cardUsuario.setCardBackgroundColor(Color.WHITE);
        }
        holder.itemView.setOnClickListener(v -> {

            //String idUsuario = usuarioAtual.getId();

            if (usuariosSelecionados.contains(idUsuario)) {
                usuariosSelecionados.remove(idUsuario);
            } else {
                usuariosSelecionados.add(idUsuario);
            }

            notifyItemChanged(position);

        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textNome;
        CardView cardUsuario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardUsuario = itemView.findViewById(R.id.cardUsuario);
            textNome = itemView.findViewById(R.id.textView14);
        }
    }
}
