package com.example.chrock.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.repositorio.RepositorioFalso;

import java.util.List;

public class contribuintesAdapter extends RecyclerView.Adapter<contribuintesAdapter.ViewHolder> {
    List<Contribuicao> itens;
    RepositorioFalso repo = RepositorioFalso.get();

    public contribuintesAdapter(List<Contribuicao> cont){
        itens = cont;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        TextView status;
        TextView dataPagamento;

        public ViewHolder(View card){
            super(card);
            nome = card.findViewById(R.id.nomeContribuente);
            status = card.findViewById(R.id.status);
            dataPagamento = card.findViewById(R.id.data);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup pai, int viewType) {
        View card = LayoutInflater.from(pai.getContext()).inflate(R.layout.item_contribuentes, pai, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int posicao) {
        Contribuicao item = itens.get(posicao);

        h.nome.setText(repo.getUsuario(item.getUsuarioId()).getNome());

        StatusContribuicao status = item.getStatus();
        if (status == StatusContribuicao.CONFIRMADO){
            h.status.setVisibility(View.VISIBLE);
            h.status.setText("Concluído");
            h.status.setTextColor(Color.GREEN);
            h.dataPagamento.setVisibility(View.VISIBLE);
            h.dataPagamento.setText("Em: " + item.getDataPagmento());
        } else if (status == StatusContribuicao.CONTESTADO){
            h.status.setVisibility(View.VISIBLE);
            h.status.setText("Contestado");
            h.status.setTextColor(Color.RED);
        } else if (status == StatusContribuicao.SINALIZADO){
            h.status.setVisibility(View.VISIBLE);
            h.status.setText("Aguardando confirmação");
        } else {
            h.status.setVisibility(View.VISIBLE);
            h.status.setText("Pendente");
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
