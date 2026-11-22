package com.example.chrock.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.data.Sessao;
import com.example.chrock.model.Sala;
import com.example.chrock.util.Animacao;
import com.example.chrock.util.ImagemSala;

import java.util.List;

public class SalasAdapter extends RecyclerView.Adapter<SalasAdapter.ViewHolder> {

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
        ImageView sapo;
        TextView titulo;
        TextView descricao;

        ViewHolder(View card) {
            super(card);
            imagem = card.findViewById(R.id.imgSala);
            sapo = card.findViewById(R.id.imgSapo);
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

        ImagemSala.mostrarOuEsconder(h.imagem, sala.getImg());
        mostrarSapoSeAdm(h, sala);

        h.itemView.setContentDescription("Sala " + sala.getTitulo());
        h.itemView.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            ouvinte.abrir(itens.get(pos));
        });
    }

    private void mostrarSapoSeAdm(ViewHolder h, Sala sala) {
        String eu = Sessao.getUsuarioId();
        boolean souAdm = eu != null && eu.equals(sala.getADMId());

        h.sapo.setVisibility(souAdm ? View.VISIBLE : View.GONE);

        float densidade = h.descricao.getResources().getDisplayMetrics().density;
        int lado = (int) (10 * densidade);
        int fim = souAdm ? (int) (64 * densidade) : lado;
        h.descricao.setPadding(lado, lado, fim, lado);
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}