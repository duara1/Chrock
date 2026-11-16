package com.example.chrock.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioFalso;
import com.example.chrock.util.Formatar;

import java.util.List;

public class ConfirmacaoAdapter extends RecyclerView.Adapter<ConfirmacaoAdapter.ViewHolder> {
    private List<RepositorioFalso.ItemConfirmacao> itens;
    private RepositorioFalso repo = RepositorioFalso.get();
    public ConfirmacaoAdapter(List<RepositorioFalso.ItemConfirmacao> itens){
        this.itens = itens;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome;
        TextView valor;
        TextView despesa;

        public ViewHolder(View card){
            super(card);
            nome = card.findViewById(R.id.nomeTransferencia);
            valor = card.findViewById(R.id.transferiu);
            despesa = card.findViewById(R.id.despesa);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup pai, int viewType){
        View card = LayoutInflater.from(pai.getContext()).inflate(R.layout.item_confirmacao, pai, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int posicao){
        RepositorioFalso.ItemConfirmacao item = itens.get(posicao);

        h.nome.setText(repo.getUsuario(item.usuarioId).getNome());
        h.valor.setText("Transferiu: " + Formatar.reais(item.despesa.getValorTransferirAConfirmar()));
        h.despesa.setText("De: " + item.despesa.getTitulo());
    }

    @Override
    public int getItemCount(){
        return itens.size();
    }

}
