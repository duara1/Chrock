package com.example.chrock;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrock.Adapter.UsuarioAdapter;
import com.example.chrock.data.Sessao;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioSupabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class criar_despesa extends AppCompatActivity {

    // A sala onde a despesa vai ser criada (o SalaInicio manda pelo Intent)
    private String salaId;

    private RecyclerView recyclerUsuarios;
    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> listaUsuario;
    private EditText editTitulo;

    private EditText editValor;
    private EditText editData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_criar_despesa);
        editTitulo = findViewById(R.id.editTextTituloConta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.telaCriarDespesa), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        salaId = getIntent().getStringExtra("salaId");
        if (salaId == null || !Sessao.estaLogado()) {
            finish();
            return;
        }

        editData = findViewById(R.id.editTextDataVencimento);
        editValor = findViewById(R.id.editTextValorConta);
        RecyclerView rvUsuario = findViewById(R.id.rv_usuario);
        listaUsuario = new ArrayList<>();

        usuarioAdapter = new UsuarioAdapter(listaUsuario);

        rvUsuario.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        rvUsuario.setAdapter((usuarioAdapter));

        carregarUsuarios();

        findViewById(R.id.buttonCadastrar).setOnClickListener(v -> {
            verificarSelecionados();
        });

        // Setinha: fecha esta tela e mostra a SalaInicio, que continua aberta por baixo
        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());
    }

    private void carregarUsuarios() {
        RepositorioSupabase.get().carregarMembros(
                salaId,
                new RepositorioSupabase.Resposta<Map<String, Usuario>>() {
                    @Override
                    public void sucesso(Map<String, Usuario> dado) {
                        listaUsuario.clear();
                        listaUsuario.addAll(dado.values());

                        usuarioAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void erro(String mensagem) {

                    }
                }
        );
    }

    private void verificarSelecionados() {

        String titulo = editTitulo.getText().toString().trim();
        String valor = editValor.getText().toString().trim();
        String data = editData.getText().toString().trim();

        Set<String> contribuintes = usuarioAdapter.getUsuariosSelecionados();

        String responsavelId = Sessao.getUsuarioId();

        String dataSQL;

        try {
            dataSQL = converterData(data);
        } catch (ParseException e) {
            Toast.makeText(this, "Data inválida. Use dd/mm/aaaa.", Toast.LENGTH_SHORT).show();
            return;
        }

        int centavos;

        try {
            centavos = converterParaCentavos(valor);
        } catch (NumberFormatException | ArithmeticException e) {
            Toast.makeText(this, "Valor inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("CRIAR_DESPESA", "Título: " + titulo);
        Log.d("CRIAR_DESPESA", "Data SQL: " + dataSQL);
        Log.d("CRIAR_DESPESA", "Centavos: " + centavos);
        Log.d("CRIAR_DESPESA", "Responsável: " + responsavelId);
        Log.d("CRIAR_DESPESA", "Contribuintes: " + contribuintes);

        RepositorioSupabase.get().criarDespesa(
                salaId,
                responsavelId,
                titulo,
                centavos,
                dataSQL,
                contribuintes,
                new RepositorioSupabase.Resposta<String>() {
                    @Override
                    public void sucesso(String despesaId) {
                        Toast.makeText(
                                criar_despesa.this,
                                "Despesa criada com sucesso!",
                                Toast.LENGTH_SHORT
                        ).show();

                        Log.d("CRIAR_DESPESA", "ID da despesa: " + despesaId);

                        finish();
                    }

                    @Override
                    public void erro(String mensagem) {
                        Toast.makeText(
                                criar_despesa.this,
                                mensagem,
                                Toast.LENGTH_LONG
                        ).show();

                        Log.e("CRIAR_DESPESA", "Erro: " + mensagem);
                    }
                }
        );
    }

    private int converterParaCentavos(String valor) {

        valor = valor.replace(",", ".");

        BigDecimal decimal = new BigDecimal(valor);

        return decimal
                .multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.UNNECESSARY)
                .intValueExact();
    }

    private String converterData(String data) throws ParseException {

        SimpleDateFormat formatoEntrada =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        formatoEntrada.setLenient(false);

        Date dataConvertida = formatoEntrada.parse(data);

        SimpleDateFormat formatoSaida =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return formatoSaida.format(dataConvertida);
    }
}