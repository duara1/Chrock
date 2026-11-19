package com.example.chrock;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.Adapter.contribuintesAdapter;
import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioFalso;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Formatar;

import java.util.List;

public class despesa_informacoes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_despesa_informacoes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.despesaInformacoes), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RepositorioSupabase repo = RepositorioSupabase.get();
        String despesaId = getIntent().getStringExtra("id");

        Despesa item = repo.getDespesa(despesaId);

        TextView titulo = findViewById(R.id.tituloDespesa);
        TextView total = findViewById(R.id.subTituloDespesa);
        TextView responsavel = findViewById(R.id.nomeResponsavel);
        TextView vencimento = findViewById(R.id.dataVencimento);
        TextView aTransferir = findViewById(R.id.valor);

        titulo.setText(item.getTitulo());
        total.setText(Formatar.reais(item.getTotalEmInt()));
        responsavel.setText(repo.getUsuario(item.getResponsavelId()).getNome());
        vencimento.setText(item.getVencimento());
        aTransferir.setText(Formatar.reais(item.getValorTransferirAConfirmar()));

        List<Contribuicao> contribuicoes = repo.getDespesa(despesaId).getContribuintes();
        RecyclerView listaContribuintes = findViewById(R.id.listaContribuintes);
        listaContribuintes.setAdapter(new contribuintesAdapter(contribuicoes));
    }
}