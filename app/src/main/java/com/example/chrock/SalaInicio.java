package com.example.chrock;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.Adapter.ConfirmacaoAdapter;
import com.example.chrock.Adapter.responsavelAdapter;
import com.example.chrock.Adapter.transferenciaAdapter;
import com.example.chrock.data.Sessao;
import com.example.chrock.util.Acesso;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.ItemConfirmacao;
import com.example.chrock.model.Papel;
import com.example.chrock.model.Sala;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;
import com.example.chrock.util.Formatar;
import com.example.chrock.util.Imagem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.example.chrock.util.Secoes;

import java.util.List;
import java.util.Map;

public class SalaInicio extends AppCompatActivity {
    private String salaId;
    private responsavelAdapter adapter;
    private ProgressBar carregando;
    private Sala salaAtual;
    private Despesa item;
    private Button btnAnexar;
    private  Button paga;
    private boolean escolhendoImagem = false;
    private ActivityResultLauncher<String> pegarImg = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            caminho ->{
                escolhendoImagem = false;
                if (caminho != null){
                    if (btnAnexar != null && paga != null && adapter != null){
                        btnAnexar.setText("Imagem anexada");
                        paga.setTextColor(Color.rgb(99,77,244));
                        adapter.receberUri(caminho);
                    }
                }
            }
    );

    private void atualizarBotoesDoPapel(Sala sala) {
        String eu = Sessao.getUsuarioId();
        Papel papel = sala.isMembro(eu) ? sala.getPapel(eu) : Papel.PARTICIPANTE;

        boolean souAdm = papel == Papel.ADM;
        boolean souAdmOuModerador = papel == Papel.ADM || papel == Papel.MODERADOR;

        findViewById(R.id.btnConfig).setVisibility(souAdm ? View.VISIBLE : View.GONE);
        findViewById(R.id.codigoSala).setVisibility(souAdmOuModerador ? View.VISIBLE : View.GONE);
        findViewById(R.id.criarDespesa).setVisibility(souAdmOuModerador ? View.VISIBLE : View.GONE);
    }

    private void copiarCodigo() {
        if (salaAtual == null) return;

        ClipboardManager areaDeTransferencia = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (areaDeTransferencia == null) return;

        areaDeTransferencia.setPrimaryClip(
                ClipData.newPlainText("Código da sala", salaAtual.getCodigo()));
        Toast.makeText(this, "Código copiado", Toast.LENGTH_SHORT).show();
    }

    private void preencherResumo(List<Despesa> despesas) {
        String eu = Sessao.getUsuarioId();

        int totalPessoal = 0;
        int pagoPessoal = 0;
        int totalSala = 0;
        int pagoSala = 0;

        for (Despesa d : despesas) {
            totalSala += d.getTotalEmInt();
            if (d.isConcluida()) {
                pagoSala += d.getTotalEmInt();
            }

            if (d.isContribuente(eu)) {
                int minhaParte = d.getValorATransferirEmInt(eu);
                totalPessoal += minhaParte;
                if (d.isConcluida() || d.getStatus(eu) == StatusContribuicao.CONFIRMADO) {
                    pagoPessoal += minhaParte;
                }
            }
        }

        escreverValor(R.id.totalValorPessoal, totalPessoal);
        escreverValor(R.id.pagValorPessoal, pagoPessoal);
        escreverValor(R.id.pendValorPessoal, totalPessoal - pagoPessoal);

        escreverValor(R.id.pagoValorSala, totalSala);
        escreverValor(R.id.pagValorSala, pagoSala);
        escreverValor(R.id.pendValorSala, totalSala - pagoSala);
    }

    private void escreverValor(int id, int centavos) {
        ((TextView) findViewById(id)).setText(Formatar.reais(centavos));
    }

    private final ActivityResultLauncher<Intent> abrirSala =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), r -> {
                if (r.getResultCode() == RESULT_OK) carregarHome();
            });

    private void carregarHome() {
        carregando.setVisibility(View.VISIBLE);

        RepositorioSupabase.get().carregarSala(salaId, new RepositorioSupabase.Resposta<Sala>() {
            @Override
            public void sucesso(Sala sala) {
                salaAtual = sala;
                atualizarBotoesDoPapel(sala);
                carregarDadosDaSala(sala);
            }

            @Override
            public void erro(String mensagem) {
                falhaNaHome(mensagem);
                if (RepositorioSupabase.SEM_ACESSO.equals(mensagem)
                        || RepositorioSupabase.SALA_NAO_ENCONTRADA.equals(mensagem)) {
                    finish();
                }
            }
        });
    }

    private void carregarDadosDaSala(Sala sala) {
        RepositorioSupabase repo = RepositorioSupabase.get();

        repo.carregarDespesas(sala.getId(), new RepositorioSupabase.Resposta<List<Despesa>>() {
            @Override
            public void sucesso(List<Despesa> despesas) {
                repo.carregarMembros(sala.getId(),
                        new RepositorioSupabase.Resposta<Map<String, Usuario>>() {
                            @Override
                            public void sucesso(Map<String, Usuario> usuarios) {
                                montarHome(despesas);
                            }

                            @Override
                            public void erro(String mensagem) {
                                falhaNaHome(mensagem);
                            }
                        });
            }

            @Override
            public void erro(String mensagem) {
                falhaNaHome(mensagem);
            }
        });
    }

    private void montarHome(List<Despesa> despesas) {
        String eu = Sessao.getUsuarioId();

        preencherResumo(despesas);

        List<ItemConfirmacao> aConfirmar = Secoes.aConfirmar(despesas, eu);
        List<Despesa> aTransferir = Secoes.aTransferir(despesas, eu);
        List<Despesa> responsavel = Secoes.souResponsavel(despesas, eu);

        ((RecyclerView) findViewById(R.id.listaConfirmar))
                .setAdapter(new ConfirmacaoAdapter(aConfirmar));
        ((RecyclerView) findViewById(R.id.listaTransferir))
                .setAdapter(new transferenciaAdapter(aTransferir));
        adapter = new responsavelAdapter(responsavel,
                (item, btnAnexar, paga) -> {
                    this.item = item;
                    this.btnAnexar = btnAnexar;
                    this.paga = paga;
                    escolhendoImagem = true;
                    pegarImg.launch("image/*");
                },
                (item, comprovante, popUp, botaoPaga) -> pagarConta(item, comprovante, popUp, botaoPaga));
        ((RecyclerView) findViewById(R.id.listaResponsavel)).setAdapter(adapter);

        mostrar(!aConfirmar.isEmpty(),
                R.id.tituloConfirmar, R.id.subTituloConfirmar, R.id.listaConfirmar);
        mostrar(!aTransferir.isEmpty(),
                R.id.tituloTransferir, R.id.subTituloTransferir, R.id.listaTransferir);
        mostrar(!responsavel.isEmpty(),
                R.id.tituloResponsavel, R.id.subTituloResponsavel, R.id.listaResponsavel);

        carregando.setVisibility(View.GONE);
    }

    private void pagarConta(Despesa item, Uri comprovante, Dialog popUp, Button botaoPaga) {
        new Thread(() -> {
            try {
                byte[] foto = Imagem.prepararJpeg(getContentResolver(), comprovante);
                runOnUiThread(() -> enviarPagamento(item, foto, popUp, botaoPaga));
            } catch (IOException | RuntimeException e) {
                runOnUiThread(() -> falhaAoPagar("Não consegui ler essa imagem.", botaoPaga));
            }
        }).start();
    }

    private void enviarPagamento(Despesa item, byte[] foto, Dialog popUp, Button botaoPaga) {
        RepositorioSupabase.get().registrarPagamento(item, foto,
                new RepositorioSupabase.Resposta<Void>() {
                    @Override
                    public void sucesso(Void nada) {
                        if (isFinishing() || isDestroyed()) return;
                        popUp.dismiss();
                        carregarHome();
                    }

                    @Override
                    public void erro(String mensagem) {
                        falhaAoPagar(mensagem, botaoPaga);
                    }
                });
    }

    private void falhaAoPagar(String mensagem, Button botaoPaga) {
        if (isFinishing() || isDestroyed()) return;
        botaoPaga.setEnabled(true);
        botaoPaga.setText("Conta paga");
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (escolhendoImagem || salaId == null) return;
        if (Acesso.exigirLogin(this)) carregarHome();
    }

    private void mostrar(boolean visivel, int... ids) {
        for (int id : ids) {
            findViewById(id).setVisibility(visivel ? View.VISIBLE : View.GONE);
        }
    }

    private void falhaNaHome(String mensagem) {
        carregando.setVisibility(View.GONE);
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sala);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Sala), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnConfig).setOnClickListener(v -> {
            if (salaAtual == null) return;
            Intent i = new Intent(this, criarSala.class);
            i.putExtra("salaId", salaAtual.getId());
            abrirSala.launch(i);
        });

        findViewById(R.id.codigoSala).setOnClickListener(v -> copiarCodigo());

        findViewById(R.id.criarDespesa).setOnClickListener(v -> {
            Intent i = new Intent(this, criar_despesa.class);
            i.putExtra("salaId", salaId);
            startActivity(i);
        });


        // Navegacao dos modulos integrados
        findViewById(R.id.cardPessoal).setOnClickListener(v ->
                abrirModulo(ResumoPessoalActivity.class));

        findViewById(R.id.cardSala).setOnClickListener(v ->
                abrirModulo(ResumoSalaActivity.class));

        findViewById(R.id.abaHistorico).setOnClickListener(v -> {
            Intent i = new Intent(this, historico.class);
            i.putExtra("salaId", salaId);
            startActivity(i);
        });

        ((TextView) findViewById(R.id.data)).setText(
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        carregando = findViewById(R.id.carregando);

        if (!Acesso.exigirLogin(this)) return;

        salaId = getIntent().getStringExtra("salaId");
        if (salaId == null) {
            finish();
            return;
        }






        carregarHome();
    }


    private void abrirModulo(Class<?> activity) {

        if (salaId == null || salaAtual == null) {
            Toast.makeText(
                    this,
                    "Aguarde o carregamento da sala.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!Acesso.exigirLogin(this)) return;

        Intent intent = new Intent(this, activity);
        intent.putExtra("salaId", salaId);
        startActivity(intent);
    }
}