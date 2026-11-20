package com.example.chrock.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.model.Sala;
import com.example.chrock.util.Animacao;

import java.util.List;

public class SalasAdapter extends RecyclerView.Adapter<SalasAdapter.ViewHolder> {

    // Quem usa o adapter (a MainActivity) decide o que fazer quando clicam numa sala
    public interface AoClicar {
        void abrir(Sala sala);
    }

    private final List<Sala> itens;
    private final AoClicar ouvinte;

    public SalasAdapter(List<Sala> itens, AoClicar ouvinte) {
        this.itens = itens;
        this.ouvinte = ouvinte;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagem;
        TextView titulo;
        TextView descricao;

        ViewHolder(View card) {
            super(card);
            imagem = card.findViewById(R.id.imgSala);
            titulo = card.findViewById(R.id.titulo);
            descricao = card.findViewById(R.id.descricao);
            Animacao.efeitoApertar(card);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup pai, int viewType) {
        View card = LayoutInflater.from(pai.getContext()).inflate(R.layout.salas, pai, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int posicao) {
        Sala sala = itens.get(posicao);

        h.titulo.setText(sala.getTitulo());

        String descricao = sala.getDescricao();
        h.descricao.setText(descricao == null || descricao.isEmpty() ? "Sem descrição" : descricao);

        mostrarImagem(h.imagem, sala.getImg());

        h.itemView.setContentDescription("Sala " + sala.getTitulo());
        h.itemView.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            ouvinte.abrir(itens.get(pos));
        });
    }

    // A imagem só aparece se ela abrir neste celular.
    // Se não tiver imagem (ou ela não abrir), o espaço some em vez de ficar um buraco vazio.
    private void mostrarImagem(ImageView imagem, Uri uri) {
        imagem.setImageDrawable(null);
        if (uri != null && !uri.toString().isEmpty()) {
            try {
                imagem.setImageURI(uri);
            } catch (RuntimeException e) {
                imagem.setImageDrawable(null);
            }
        }
        imagem.setVisibility(imagem.getDrawable() != null ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}