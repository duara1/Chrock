package com.example.chrock.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.model.Usuario;
import com.example.chrock.util.Animacao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParticipantesAdapter extends RecyclerView.Adapter<ParticipantesAdapter.ViewHolder> {

    private final List<Usuario> itens;
    private final Set<String> selecionados;

    public ParticipantesAdapter(List<Usuario> itens, Set<String> selecionadosIniciais) {
        this.itens = itens;
        this.selecionados = new HashSet<>(selecionadosIniciais);
    }

    public List<String> getSelecionados() {
        return new ArrayList<>(selecionados);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome;

        ViewHolder(View card) {
            super(card);
            nome = card.findViewById(R.id.textView);
            Animacao.efeitoApertar(card);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup pai, int viewType) {
        View card = LayoutInflater.from(pai.getContext())
                .inflate(R.layout.item_participantes, pai, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int posicao) {
        Usuario u = itens.get(posicao);
        boolean marcado = selecionados.contains(u.getId());

        h.nome.setText(u.getNome());
        h.itemView.setBackgroundResource(marcado ? R.drawable.card_selecionado : R.drawable.borda_card);
        h.itemView.setContentDescription(u.getNome() + (marcado ? ", moderador" : ""));

        h.itemView.setOnClickListener(v -> {
            int pos = h.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            String id = itens.get(pos).getId();
            if (!selecionados.add(id)) selecionados.remove(id);
            notifyItemChanged(pos);
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}