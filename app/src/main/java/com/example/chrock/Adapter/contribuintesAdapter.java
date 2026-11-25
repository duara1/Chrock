package com.example.chrock.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chrock.R;
import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;

import java.util.List;

public class contribuintesAdapter extends RecyclerView.Adapter<contribuintesAdapter.ViewHolder> {
    List<Contribuicao> itens;
    private final RepositorioSupabase repo = RepositorioSupabase.get();

    public contribuintesAdapter(List<Contribuicao> cont){
        itens = cont;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView foto;
        TextView nome;
        TextView status;
        int corStatus;

        public ViewHolder(View card){
            super(card);
            foto = card.findViewById(R.id.perfil);
            nome = card.findViewById(R.id.nomeContribuente);
            status = card.findViewById(R.id.status);
            corStatus = status.getCurrentTextColor();
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

        Usuario u = repo.getUsuario(item.getUsuarioId());
        h.nome.setText(u != null ? u.getNome() : "Alguém");

        String img = u != null ? u.getImg() : null;
        if (img != null && !img.trim().isEmpty()) {
            Glide.with(h.foto)
                    .load(img)
                    .placeholder(R.drawable.fundo_avatar)
                    .error(R.drawable.fundo_avatar)
                    .centerCrop()
                    .into(h.foto);
        } else {
            Glide.with(h.foto).clear(h.foto);
            h.foto.setImageResource(R.drawable.fundo_avatar);
        }

        h.status.setVisibility(View.VISIBLE);
        h.status.setTextColor(h.corStatus);
        switch (item.getStatus()) {
            case CONFIRMADO:
                h.status.setText("Concluído");
                h.status.setTextColor(Color.GREEN);
                break;
            case CONTESTADO:
                h.status.setText("Contestado");
                h.status.setTextColor(Color.RED);
                break;
            case SINALIZADO:
                h.status.setText("Aguardando confirmação");
                break;
            default:
                h.status.setText("Pendente");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}