package com.example.chrock.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.model.Despesa;
import com.example.chrock.repositorio.RepositorioFalso;
import com.example.chrock.util.Formatar;

import java.util.List;

public class responsavelAdapter extends RecyclerView.Adapter<responsavelAdapter.ViewHolder> {
    private List<Despesa> itens;
    private RepositorioFalso repo = RepositorioFalso.get();

    public responsavelAdapter(List<Despesa> itens){
        this.itens = itens;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView vencimento;
        TextView total;
        TextView suaParte;

        public ViewHolder(View card){
            super(card);
            titulo = card.findViewById(R.id.nomeDespesaResponsavel);
            vencimento = card.findViewById(R.id.venceEm);
            total = card.findViewById(R.id.total);
            suaParte = card.findViewById(R.id.suaParte);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup pai, int viewType) {
        View card = LayoutInflater.from(pai.getContext()).inflate(R.layout.item_responsavel, pai, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int posicao) {
        Despesa item = itens.get(posicao);
        h.titulo.setText(item.getTitulo());
        h.vencimento.setText(item.getVencimento());
        h.total.setText(Formatar.reais(item.getTotalEmInt()));
        h.suaParte.setText(Formatar.reais(item.getValorATransferirEmInt(item.getResponsavelId())));
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
