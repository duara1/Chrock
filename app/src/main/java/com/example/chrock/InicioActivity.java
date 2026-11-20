package com.example.chrock;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity {

    private RecyclerView listaSalas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaSalas = findViewById(R.id.listaSalas);

        findViewById(R.id.btnEntrarComCodigo).setOnClickListener(v -> {
            Dialog popup = new Dialog(this);
            popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popup.setContentView(R.layout.dialog_entrar_sala);

            EditText editCodigo = popup.findViewById(R.id.editCodigo);
            AppCompatButton btnEntrar = popup.findViewById(R.id.btnConfirmarCodigo);

            btnEntrar.setOnClickListener(view -> {
                String codigo = editCodigo.getText().toString().trim();
                if (codigo.isEmpty()) {
                    editCodigo.setError("Digite o código da sala");
                    return;
                }
                Toast.makeText(this, "Código informado: " + codigo, Toast.LENGTH_SHORT).show();
                popup.dismiss();
            });

            popup.show();

            Window janela = popup.getWindow();
            if (janela != null) {
                janela.setBackgroundDrawableResource(android.R.color.transparent);
                janela.setDimAmount(0.25f);
                int largura = (int) (getResources().getDisplayMetrics().widthPixels * 0.88f);
                janela.setLayout(largura, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });

        findViewById(R.id.btnCriarSala).setOnClickListener(v ->
                startActivity(new Intent(InicioActivity.this, CriarSalaActivity.class)));
        List<SalaCard> salas = new ArrayList<>();
        salas.add(new SalaCard("Casa", "Divisão das principais contas do apê."));
        salas.add(new SalaCard("Chácara", "Divisão do rolê na chácara."));

        listaSalas.setAdapter(new SalaCardAdapter(salas, sala ->
                Toast.makeText(this, "Abrir sala: " + sala.titulo, Toast.LENGTH_SHORT).show()));
    }

    private static class SalaCard {
        final String titulo;
        final String descricao;

        SalaCard(String titulo, String descricao) {
            this.titulo = titulo;
            this.descricao = descricao;
        }
    }

    private interface AoClicarNaSala {
        void aoClicar(SalaCard sala);
    }

    private static class SalaCardAdapter extends RecyclerView.Adapter<SalaCardAdapter.Holder> {

        private final List<SalaCard> salas;
        private final AoClicarNaSala ouvinte;

        SalaCardAdapter(List<SalaCard> salas, AoClicarNaSala ouvinte) {
            this.salas = salas;
            this.ouvinte = ouvinte;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sala, parent, false);
            return new Holder(item);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            SalaCard sala = salas.get(position);
            holder.titulo.setText(sala.titulo);
            holder.descricao.setText(sala.descricao);
            holder.itemView.setOnClickListener(v -> ouvinte.aoClicar(sala));
        }

        @Override
        public int getItemCount() {
            return salas.size();
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView titulo;
            final TextView descricao;

            Holder(View itemView) {
                super(itemView);
                titulo = itemView.findViewById(R.id.txtTitulo);
                descricao = itemView.findViewById(R.id.txtDescricao);
            }
        }
    }
}
