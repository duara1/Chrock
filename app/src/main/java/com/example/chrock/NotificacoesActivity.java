package com.example.chrock;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chrock.model.Sala;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Acesso;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotificacoesActivity extends AppCompatActivity {

    private LinearLayout conteudo;
    private TextView contador;
    private Button marcarTodas;

    private String salaId;
    private boolean carregando = false;

    private final RepositorioSupabase repo =
            RepositorioSupabase.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Acesso.exigirLogin(this)) return;

        setContentView(R.layout.activity_notificacoes);

        salaId = getIntent().getStringExtra("salaId");

        if (salaId == null || salaId.trim().isEmpty()) {
            Toast.makeText(this,
                    "Sala não identificada.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        conteudo = findViewById(R.id.conteudoNotificacoes);
        contador = findViewById(R.id.contadorNotificacoes);
        marcarTodas = findViewById(R.id.botaoMarcarTodas);

        marcarTodas.setEnabled(false);

        findViewById(R.id.botaoVoltar)
                .setOnClickListener(v -> finish());

        marcarTodas.setOnClickListener(
                v -> marcarTodasComoLidas()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (conteudo != null
                && salaId != null
                && Acesso.exigirLogin(this)) {
            carregarNotificacoes();
        }
    }

    private boolean telaAtiva() {
        return !isFinishing() && !isDestroyed();
    }

    private void carregarNotificacoes() {

        if (carregando || !telaAtiva()) return;

        carregando = true;
        marcarTodas.setEnabled(false);

        conteudo.removeAllViews();
        contador.setText("Carregando...");

        adicionarTexto("Buscando notificações...");

        repo.carregarSala(
                salaId,
                new RepositorioSupabase.Resposta<Sala>() {

                    @Override
                    public void sucesso(Sala sala) {
                        if (telaAtiva()) gerarLembretes();
                    }

                    @Override
                    public void erro(String mensagem) {
                        mostrarErro(mensagem);
                    }
                }
        );
    }

    private void gerarLembretes() {

        repo.gerarLembretesVencimento(
                salaId,
                new RepositorioSupabase.Resposta<Void>() {

                    @Override
                    public void sucesso(Void dado) {
                        if (telaAtiva()) buscarNotificacoes();
                    }

                    @Override
                    public void erro(String mensagem) {

                        if (!telaAtiva()) return;

                        Toast.makeText(
                                NotificacoesActivity.this,
                                "Não foi possível atualizar os lembretes.",
                                Toast.LENGTH_SHORT
                        ).show();

                        buscarNotificacoes();
                    }
                }
        );
    }

    private void buscarNotificacoes() {

        repo.carregarNotificacoes(
                salaId,
                new RepositorioSupabase.Resposta<JSONArray>() {

                    @Override
                    public void sucesso(JSONArray dados) {

                        if (!telaAtiva()) return;

                        carregando = false;
                        mostrarNotificacoes(dados);
                    }

                    @Override
                    public void erro(String mensagem) {
                        mostrarErro(mensagem);
                    }
                }
        );
    }

    private void mostrarNotificacoes(JSONArray dados) {

        conteudo.removeAllViews();

        int naoLidas = 0;

        for (int i = 0; i < dados.length(); i++) {

            JSONObject notificacao = dados.optJSONObject(i);

            if (notificacao == null) continue;

            if (!notificacao.optBoolean("lida", false)) {
                naoLidas++;
            }

            adicionarCard(notificacao);
        }

        contador.setText(
                naoLidas == 1
                        ? "1 notificação não lida"
                        : naoLidas + " notificações não lidas"
        );

        marcarTodas.setEnabled(naoLidas > 0);

        if (dados.length() == 0) {
            adicionarTexto("Você não possui notificações.");
        }
    }

    private void adicionarCard(JSONObject notificacao) {

        String id = notificacao.optString("id");
        String titulo = notificacao.optString("titulo");
        String mensagem = notificacao.optString("mensagem");
        String data = notificacao.optString("criada_em");
        String tipo = notificacao.optString("tipo");

        boolean lida = notificacao.optBoolean("lida", false);

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(16), dp(18), dp(16));

        card.setBackgroundColor(
                lida ? Color.WHITE : Color.rgb(237, 248, 233)
        );

        card.setElevation(dp(3));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(dp(3), dp(5), dp(3), dp(12));

        card.setLayoutParams(params);

        card.addView(criarTexto(
                titulo, 17, corDoTipo(tipo), true
        ));

        card.addView(criarTexto(
                mensagem, 14, "#555555", false
        ));

        card.addView(criarTexto(
                formatarData(data), 12, "#777777", false
        ));

        card.addView(criarTexto(
                lida ? "Lida" : "Não lida",
                13,
                lida ? "#777777" : "#478B37",
                true
        ));

        if (!lida && !id.isEmpty()) {
            card.setOnClickListener(v -> marcarComoLida(id));
        }

        conteudo.addView(card);
    }

    private void marcarComoLida(String id) {

        if (carregando || !telaAtiva()) return;

        carregando = true;
        marcarTodas.setEnabled(false);

        repo.marcarNotificacaoLida(
                id,
                new RepositorioSupabase.Resposta<Void>() {

                    @Override
                    public void sucesso(Void dado) {
                        if (telaAtiva()) buscarNotificacoes();
                    }

                    @Override
                    public void erro(String mensagem) {
                        mostrarErro(mensagem);
                    }
                }
        );
    }

    private void marcarTodasComoLidas() {

        if (carregando || !telaAtiva()) return;

        carregando = true;
        marcarTodas.setEnabled(false);

        repo.marcarTodasNotificacoesLidas(
                salaId,
                new RepositorioSupabase.Resposta<Void>() {

                    @Override
                    public void sucesso(Void dado) {
                        if (telaAtiva()) buscarNotificacoes();
                    }

                    @Override
                    public void erro(String mensagem) {
                        mostrarErro(mensagem);
                    }
                }
        );
    }

    private String corDoTipo(String tipo) {

        switch (tipo) {

            case "conta_vencida":
            case "transferencia_contestada":
                return "#B54A4A";

            case "vencimento_proximo":
                return "#C77B22";

            case "transferencia_confirmada":
                return "#478B37";

            case "transferencia_sinalizada":
            case "conta_criada":
                return "#423270";

            default:
                return "#222222";
        }
    }

    private String formatarData(String data) {

        try {
            java.text.SimpleDateFormat entrada =
                    new java.text.SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            java.util.Locale.US
                    );

            entrada.setTimeZone(
                    java.util.TimeZone.getTimeZone("UTC")
            );

            java.util.Date instante = entrada.parse(data);

            java.text.SimpleDateFormat saida =
                    new java.text.SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            new java.util.Locale("pt", "BR")
                    );

            return instante == null
                    ? data
                    : saida.format(instante);

        } catch (Exception e) {
            return data;
        }
    }

    private void mostrarErro(String mensagem) {

        if (!telaAtiva()) return;

        carregando = false;
        marcarTodas.setEnabled(false);

        conteudo.removeAllViews();

        contador.setText("Não foi possível carregar");

        adicionarTexto(mensagem);

        Button tentarNovamente = new Button(this);

        tentarNovamente.setText("Tentar novamente");

        tentarNovamente.setOnClickListener(
                v -> carregarNotificacoes()
        );

        conteudo.addView(tentarNovamente);
    }

    private TextView criarTexto(
            String mensagem,
            int tamanho,
            String cor,
            boolean negrito) {

        TextView texto = new TextView(this);

        texto.setText(mensagem);
        texto.setTextSize(tamanho);
        texto.setTextColor(Color.parseColor(cor));
        texto.setPadding(0, dp(5), 0, dp(5));

        if (negrito) {
            texto.setTypeface(null, Typeface.BOLD);
        }

        return texto;
    }

    private void adicionarTexto(String mensagem) {

        conteudo.addView(
                criarTexto(mensagem, 15, "#777777", false)
        );
    }

    private int dp(int valor) {

        return Math.round(
                valor * getResources()
                        .getDisplayMetrics().density
        );
    }
}
