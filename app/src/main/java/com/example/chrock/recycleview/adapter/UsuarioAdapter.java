package com.example.chrock.recycleview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chrock.R;
import com.example.chrock.Usuario;

import java.util.List;
//Classe adapter permite que os dados sejam adaptados para algo visual. Para isso é necessário métodos relacionados ao recyclerview
public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> listaUsuarios;
    public UsuarioAdapter (List<Usuario> lista) {
        this.listaUsuarios = lista;
    }

    @NonNull
    @Override
    //método responsável pela criação da ViewHolder, criação de um item
    //ViewHolder é um objeto que irá conter todas as views do xml de referência
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.molde_usuario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    //Método responsável pela manipulação do conteúdo das views através do ViewHolder

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //o parâmetro holder referencia o ViewHolder criado no onCreateViewHolder
        // Através dele é possível alterar o conteúdo do conjunto de views do ViewHolder
        //a partir da variável responsável pela view específica
        Usuario usuarioAtual = listaUsuarios.get(position);
        holder.textNome.setText(usuarioAtual.getNomeUsuario());
    }

    @Override
    public int getItemCount() {

        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    //É criado uma classe ViewHolder que herdará as propriedades ViewHolder do RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Variável criada para referenciar a view do tipo TextView
        TextView textNome;

        public ViewHolder(View itemView) {
            super(itemView);

            textNome = itemView.findViewById(R.id.textView);



        }
    }
}