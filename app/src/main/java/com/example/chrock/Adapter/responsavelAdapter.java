package com.example.chrock.Adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
        TextView recebido;
        TextView contagem;
        ProgressBar progresso;
        ImageView bola;

        public ViewHolder(View card){
            super(card);
            titulo = card.findViewById(R.id.nomeDespesaResponsavel);
            vencimento = card.findViewById(R.id.venceEm);
            total = card.findViewById(R.id.total);
            suaParte = card.findViewById(R.id.suaParte);
            recebido = card.findViewById(R.id.recebido);
            contagem = card.findViewById(R.id.valor);
            progresso = card.findViewById(R.id.barraProgresso);
            bola = card.findViewById(R.id.bola);
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

        int diasAte = Formatar.diasAte(item.getVencimento());
        if (diasAte <= 10 && diasAte > 5) {
            h.bola.setVisibility(View.VISIBLE);
            h.bola.setImageResource(R.drawable.bola_amarela);
        } else if (diasAte <= 5 && diasAte > 0){
            h.bola.setVisibility(View.VISIBLE);
            h.bola.setImageResource(R.drawable.bola_vermelha);
        } else if (diasAte == 0){
            h.vencimento.setText("Vence HOJE");
            h.vencimento.setTextColor(Color.RED);
        } else if (diasAte < 0) {
            h.vencimento.setText("Despesa vencida");
            h.vencimento.setTextColor(Color.RED);
        }

        if (diasAte == 1) h.vencimento.setText("Vence em " + diasAte + " dia");
        if (diasAte > 1) h.vencimento.setText("Vence em " + diasAte + " dias");


        h.total.setText("Total da conta: " + Formatar.reais(item.getTotalEmInt()));
        h.suaParte.setText("Sua parte: " + Formatar.reais(item.getValorATransferirEmInt(item.getResponsavelId())));

        int meta = item.getMetaDeRecebimento();
        if (meta > 0){
            h.recebido.setVisibility(View.VISIBLE);
            h.contagem.setVisibility(View.VISIBLE);
            h.contagem.setText(Formatar.reais(item.getRecebidoEmInt()) + " de " + Formatar.reais(item.getTotalEmInt()));
            h.progresso.setVisibility(View.VISIBLE);
            h.progresso.setMax(item.getTotalEmInt());
            h.progresso.setProgress(item.getRecebidoEmInt());
        }else {
            h.recebido.setVisibility(View.VISIBLE);
            h.recebido.setText("Sem contribuições");
            h.recebido.setGravity(Gravity.CENTER_HORIZONTAL);
            h.recebido.setTypeface(null, Typeface.BOLD);
            h.contagem.setVisibility(View.GONE);
            h.progresso.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
