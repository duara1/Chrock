package com.example.chrock.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.despesa_informacoes;
import com.example.chrock.model.Despesa;
import com.example.chrock.util.Animacao;
import com.example.chrock.util.Formatar;
import com.example.chrock.util.Vencimento;

import java.util.List;

public class responsavelAdapter extends RecyclerView.Adapter<responsavelAdapter.ViewHolder> {
    private List<Despesa> itens;
    private Uri img;
    private Button btnAbertoPopUp;
    private Despesa despesaAbertaPopUp;

    public interface clikAnexar{
        void clikAnexar(Despesa item, Button btnAnexar, Button paga);
    }
    private clikAnexar ouvinte;

    public responsavelAdapter(List<Despesa> itens, clikAnexar ouvinte){
        this.itens = itens;
        this.ouvinte = ouvinte;
    }

    public void receberUri(Uri img){
        if (img != null){
            this.img = img;

            if (btnAbertoPopUp != null){
                btnAbertoPopUp.setEnabled(true);
            }
        }
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
        int corVencimento;

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
            corVencimento = vencimento.getCurrentTextColor();
            Animacao.efeitoApertar(card);
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
        Vencimento.aplicar(h.vencimento, h.bola, h.corVencimento, item.getVencimento());

        h.total.setText("Total da conta: " + Formatar.reais(item.getTotalEmInt()));

        h.suaParte.setVisibility(item.isResponsavelContribui() ? View.VISIBLE : View.GONE);
        h.suaParte.setText("Sua parte: " + Formatar.reais(item.getValorATransferirEmInt(item.getResponsavelId())));

        int meta = item.getMetaDeRecebimento();
        if (meta > 0) {
            int recebido = item.getRecebidoEmInt();

            h.recebido.setText("Recebido");
            h.recebido.setGravity(Gravity.START);
            h.recebido.setTypeface(null, Typeface.NORMAL);
            h.recebido.setVisibility(View.VISIBLE);

            h.contagem.setVisibility(View.VISIBLE);
            h.contagem.setText(Formatar.reais(recebido) + " de " + Formatar.reais(meta));

            h.progresso.setVisibility(View.VISIBLE);
            h.progresso.setMax(meta);
            h.progresso.setProgress(recebido);
            h.progresso.setContentDescription("Recebido " + Formatar.reais(recebido) + " de " + Formatar.reais(meta));
        } else {
            h.recebido.setVisibility(View.VISIBLE);
            h.recebido.setText("Sem contribuições");
            h.recebido.setGravity(Gravity.CENTER_HORIZONTAL);
            h.recebido.setTypeface(null, Typeface.BOLD);
            h.contagem.setVisibility(View.GONE);
            h.progresso.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v ->{
            // Cada pop-up começa do zero, pra a imagem de uma despesa não ir pra outra
            img = null;
            btnAbertoPopUp = null;
            despesaAbertaPopUp = null;

            Context contexto = v.getContext();
            Dialog popUp = new Dialog(contexto);
            popUp.setContentView(R.layout.item_pop_up);

            if (popUp.getWindow() != null) {
                popUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            TextView titulo = popUp.findViewById(R.id.titulo);
            TextView valor = popUp.findViewById(R.id.valor);
            Button anexar = popUp.findViewById(R.id.button);
            Button verMais = popUp.findViewById(R.id.verMais);
            Button contaPaga = popUp.findViewById(R.id.paga);
            ImageButton sair = popUp.findViewById(R.id.sair);

            titulo.setText(item.getTitulo());
            valor.setText("Valor: " + Formatar.reais(item.getTotalEmInt()));

            anexar.setOnClickListener(view ->{
                if (ouvinte != null){
                    this.btnAbertoPopUp = contaPaga;
                    this.despesaAbertaPopUp = item;
                    ouvinte.clikAnexar(item, anexar, contaPaga);
                }
            });

            contaPaga.setOnClickListener(vP -> {
                // Só usa a imagem se ela foi anexada NESTA despesa
                if (img != null && despesaAbertaPopUp == item){
                    // Provisório: muda só no celular. Falta salvar no Supabase (Storage).
                    item.setComprovante(img);
                }
                notifyDataSetChanged();
                popUp.dismiss();
            });

            sair.setOnClickListener(vS ->{
                popUp.dismiss();
            });

            verMais.setOnClickListener(view ->{
                Intent intent = new Intent(contexto, despesa_informacoes.class);
                intent.putExtra("id", item.getDespesaId());
                contexto.startActivity(intent);
            });

            popUp.show();
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}