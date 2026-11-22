package com.example.chrock.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.despesa_informacoes;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Animacao;
import com.example.chrock.util.Formatar;
import com.example.chrock.util.Vencimento;

import java.util.List;

public class transferenciaAdapter extends RecyclerView.Adapter<transferenciaAdapter.ViewHolder> {
    private List<Despesa> itens;
    private final RepositorioSupabase repo = RepositorioSupabase.get();

    public transferenciaAdapter(List<Despesa> itens){
        this.itens = itens;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView titulo;
        TextView para;
        TextView vencimento;
        TextView valor;
        ImageView bola;
        int corVencimento;
        public ViewHolder(View card){
            super(card);
            titulo = card.findViewById(R.id.nomeDespesa);
            para = card.findViewById(R.id.para);
            vencimento = card.findViewById(R.id.venceEm);
            valor = card.findViewById(R.id.valor);
            bola = card.findViewById(R.id.bola);
            corVencimento = vencimento.getCurrentTextColor();
            Animacao.efeitoApertar(card);
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

        Usuario resp = repo.getUsuario(item.getResponsavelId());
        h.para.setText("Para: " + (resp != null ? resp.getNome() : "responsável"));

        Vencimento.aplicar(h.vencimento, h.bola, h.corVencimento, item.getVencimento());
        h.valor.setText(Formatar.reais(item.getValorTransferirAConfirmar()));

        h.itemView.setOnClickListener(v ->{
            Context contexto = v.getContext();

            Intent intent = new Intent(contexto, despesa_informacoes.class);
            intent.putExtra("id", item.getDespesaId());


            contexto.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}

