package com.example.chrock.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.R;
import com.example.chrock.model.ItemConfirmacao;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Formatar;

import java.util.List;

public class ConfirmacaoAdapter extends RecyclerView.Adapter<ConfirmacaoAdapter.ViewHolder> {
    private final List<ItemConfirmacao> itens;
    private final Runnable aoResponder;
    private final RepositorioSupabase repo = RepositorioSupabase.get();

    public ConfirmacaoAdapter(List<ItemConfirmacao> itens, Runnable aoResponder) {
        this.itens = itens;
        this.aoResponder = aoResponder;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome;
        TextView valor;
        TextView despesa;
        Button confirmar;
        Button contestar;

        public ViewHolder(View card){
            super(card);
            nome = card.findViewById(R.id.nomeTransferencia);
            valor = card.findViewById(R.id.transferiu);
            despesa = card.findViewById(R.id.despesa);
            confirmar = card.findViewById(R.id.confirmar);
            contestar = card.findViewById(R.id.contestar);
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
        ItemConfirmacao item = itens.get(posicao);

        Usuario u = repo.getUsuario(item.usuarioId);
        h.nome.setText(u != null ? u.getNome() : "Alguém");
        h.valor.setText("Transferiu: " + Formatar.reais(item.despesa.getValorTransferirAConfirmar()));
        h.despesa.setText("De: " + item.despesa.getTitulo());

        h.confirmar.setEnabled(true);
        h.contestar.setEnabled(true);

        h.confirmar.setOnClickListener(v -> responder(h, true));
        h.contestar.setOnClickListener(v -> responder(h, false));
    }

    private void responder(ViewHolder h, boolean confirmar) {
        int pos = h.getBindingAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) return;
        ItemConfirmacao item = itens.get(pos);

        h.confirmar.setEnabled(false);
        h.contestar.setEnabled(false);

        RepositorioSupabase.Resposta<Void> resposta = new RepositorioSupabase.Resposta<Void>() {
            @Override
            public void sucesso(Void nada) {
                int atual = itens.indexOf(item);
                if (atual >= 0) {
                    itens.remove(atual);
                    notifyItemRemoved(atual);
                }
                if (aoResponder != null) aoResponder.run();
            }

            @Override
            public void erro(String mensagem) {
                h.confirmar.setEnabled(true);
                h.contestar.setEnabled(true);
                Toast.makeText(h.itemView.getContext(), mensagem, Toast.LENGTH_LONG).show();
            }
        };

        String despesaId = item.despesa.getDespesaId();
        if (confirmar) {
            repo.confirmarTransferencia(despesaId, item.usuarioId, resposta);
        } else {
            repo.contestarTransferencia(despesaId, item.usuarioId, resposta);
        }
    }

    @Override
    public int getItemCount(){
        return itens.size();
    }
}