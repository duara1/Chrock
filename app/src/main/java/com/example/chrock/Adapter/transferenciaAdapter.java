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

public class transferenciaAdapter extends RecyclerView.Adapter<transferenciaAdapter.ViewHolder> {
    private List<Despesa> itens;
    RepositorioFalso repo = RepositorioFalso.get();

    public transferenciaAdapter(List<Despesa> itens){
        this.itens = itens;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView titulo;
        TextView para;
        TextView vencimento;
        TextView valor;
        public ViewHolder(View card){
            super(card);
            titulo = card.findViewById(R.id.nomeDespesa);
            para = card.findViewById(R.id.para);
            vencimento = card.findViewById(R.id.venceEm);
            valor = card.findViewById(R.id.valor);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup pai, int viewType) {
        View card = LayoutInflater.from(pai.getContext()).inflate(R.layout.item_transferir, pai, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int posicao) {
        Despesa item = itens.get(posicao);

        h.titulo.setText(item.getTitulo());
        h.para.setText("Para:" + repo.getUsuario(item.getResponsavelId()).getNome());
        h.vencimento.setText(item.getVencimento());
        h.valor.setText(Formatar.reais(item.getValorTransferirAConfirmar()));
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}

