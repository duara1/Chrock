package com.example.chrock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

/** Menu temporário de teste. Será substituído pela navegação da tela principal. */
public class ModulosYasminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        int margem = (int) (20 * getResources().getDisplayMetrics().density);
        menu.setPadding(margem, margem, margem, margem);
        setContentView(menu);
        adicionar(menu, "Resumo pessoal", ResumoPessoalActivity.class);
        adicionar(menu, "Resumo da sala", ResumoSalaActivity.class);
        adicionar(menu, "Histórico pessoal", HistoricoActivity.class);
        adicionar(menu, "Histórico por conta", HistoricoContaActivity.class);
        adicionar(menu, "Histórico da sala", HistoricoSalaActivity.class);
        adicionar(menu, "Notificações", NotificacoesActivity.class);
    }

    private void adicionar(LinearLayout menu, String titulo, Class<?> destino) {
        Button botao = new Button(this);
        botao.setText(titulo);
        botao.setOnClickListener(v -> {
            Intent intent = new Intent(this, destino);
            if (destino == HistoricoActivity.class || destino == HistoricoContaActivity.class
                    || destino == HistoricoSalaActivity.class) {
                // Identidade de demonstração existente no RepositorioFalso.
                // A navegação definitiva deverá fornecer os IDs da sessão e da sala.
                intent.putExtra(HistoricoActivity.EXTRA_USUARIO_ID, "u1");
                intent.putExtra(HistoricoActivity.EXTRA_SALA_ID, "s1");
            }
            startActivity(intent);
        });
        menu.addView(botao);
    }
}
