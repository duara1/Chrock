package com.example.chrock;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chrock.model.Despesa;
import com.example.chrock.repositorio.RepositorioFalso;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RepositorioFalso repo = RepositorioFalso.get();
        String[] pessoas = {"u1", "u2", "u3"};

        for (String id: pessoas) {
            Log.d("Teste", "======" + repo.getUsuario(id).getNome() + "======");

            for (RepositorioFalso.ItemConfirmacao c: repo.aConfirmar(id, "s1")) {
                Log.d("aConfirmar", repo.getUsuario(c.usuarioId).getNome() + "\nTranferiu: " + c.despesa.getValorTransferirAConfirmar() + "\nDe:" + c.despesa.getTitulo());
            }

            for (Despesa t: repo.aTransferir(id, "s1")) {
                Log.d("aTransferir", t.getTitulo() + "\nTransferir: " + t.getValorATransferirEmInt(id) + "\nPara: " + t.getResponsavelId());
            }

            for (Despesa r: repo.souResponsavel(id, "s1")) {
                Log.d("souResponsavel", r.getTitulo() + "\nTotal: " + r.getTotalEmInt() + "\nSua parte: " + r.getValorATransferirEmInt(id) + "\nMeta de recebimento: " + r.getMetaDeRecebimento() + "\n" + r.getContribuintes().size() + "contribuentes");
            }
        }
    }
}