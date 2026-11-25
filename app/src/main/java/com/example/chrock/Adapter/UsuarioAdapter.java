package com.example.chrock.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chrock.R;
import com.example.chrock.model.Usuario;
import com.example.chrock.util.Animacao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private final List<Usuario> listaUsuarios;
    private final Set<String> usuariosSelecionados = new HashSet<>();

    public UsuarioAdapter(List<Usuario> lista) {
        this.listaUsuarios = lista;
    }

    public Set<String> getUsuariosSelecionados() {
        return usuariosSelecionados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participantes, parent, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Usuario usuario = listaUsuarios.get(position);
        boolean marcado = usuariosSelecionados.contains(usuario.getId());

        h.nome.setText(usuario.getNome());

        String img = usuario.getImg();
        if (img != null && !img.trim().isEmpty()) {
            Glide.with(h.foto)
                    .load(img)
                    .placeholder(R.drawable.fundo_avatar)
                    .error(R.drawable.fundo_avatar)
                    .centerCrop()
                    .into(h.foto);
        } else {
            Glide.with(h.foto).clear(h.foto);
            h.foto.setImageResource(R.drawable.fundo_avatar);
        }

        h.itemView.setBackgroundResource(marcado ? R.drawable.card_selecionado : R.drawable.borda_card);
        h.itemView.setContentDescription(usuario.getNome() + (marcado ? ", vai contribuir" : ""));

        h.itemView.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            String id = listaUsuarios.get(pos).getId();
            if (!usuariosSelecionados.add(id)) usuariosSelecionados.remove(id);
            notifyItemChanged(pos);
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foto;
        TextView nome;

        public ViewHolder(@NonNull View card) {
            super(card);
            foto = card.findViewById(R.id.foto);
            nome = card.findViewById(R.id.textView);
            Animacao.efeitoApertar(card);
        }
    }
}