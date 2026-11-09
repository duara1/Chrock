
package com.example.chrock.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chrock.model.Conta;
import com.example.chrock.model.Pagamento;
import com.example.chrock.model.Notificacao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.UUID;

public class DadosManager {

    private static final String PREFS = "chrock_dados";

    private static final String CONTAS = "contas";
    private static final String PAGAMENTOS = "pagamentos";
    private static final String NOTIFICACOES = "notificacoes";

    private DadosManager() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // =========================================
    // INICIALIZAÇÃO
    // =========================================

    public static void inicializar(Context context) {

        if (!prefs(context).contains(CONTAS)) {
            criarDadosExemplo(context);
        }

        verificarVencimentos(context);
    }

    // =========================================
    // CONTAS
    // =========================================

    public static void adicionarConta(Context context, Conta conta) {

        ArrayList<Conta> contas = getContas(context);

        contas.add(conta);

        salvarContas(context, contas);

        adicionarNotificacao(
                context,
                "Nova conta cadastrada",
                "A conta " + conta.getNome() + " foi adicionada.",
                "conta_criada"
        );
    }

    public static ArrayList<Conta> getContas(Context context) {

        ArrayList<Conta> lista = new ArrayList<>();

        try {
            String json = prefs(context).getString(CONTAS, "[]");

            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                Conta conta = new Conta(
                        obj.getString("id"),
                        obj.getString("nome"),
                        obj.getString("responsavel"),
                        obj.getDouble("valor"),
                        obj.getString("vencimento"),
                        obj.getBoolean("paga")
                );

                JSONArray participantes =
                        obj.optJSONArray("participantes");

                if (participantes != null) {
                    for (int j = 0; j < participantes.length(); j++) {
                        conta.adicionarParticipante(
                                participantes.getString(j)
                        );
                    }
                }

                lista.add(conta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    private static void salvarContas(
            Context context,
            ArrayList<Conta> contas) {

        try {
            JSONArray array = new JSONArray();

            for (Conta conta : contas) {

                JSONObject obj = new JSONObject();

                obj.put("id", conta.getId());
                obj.put("nome", conta.getNome());
                obj.put("responsavel", conta.getResponsavel());
                obj.put("valor", conta.getValor());
                obj.put("vencimento", conta.getVencimento());
                obj.put("paga", conta.isPaga());

                JSONArray participantes = new JSONArray();

                for (String pessoa : conta.getParticipantes()) {
                    participantes.put(pessoa);
                }

                obj.put("participantes", participantes);

                array.put(obj);
            }

            prefs(context).edit()
                    .putString(CONTAS, array.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Conta buscarConta(Context context, String id) {

        for (Conta conta : getContas(context)) {
            if (conta.getId().equals(id)) {
                return conta;
            }
        }

        return null;
    }

    public static void atualizarConta(Context context, Conta atualizada) {

        ArrayList<Conta> contas = getContas(context);

        for (int i = 0; i < contas.size(); i++) {

            if (contas.get(i).getId().equals(atualizada.getId())) {

                contas.set(i, atualizada);

                break;
            }
        }

        salvarContas(context, contas);
    }

    // =========================================
    // PAGAMENTOS
    // =========================================

    public static void registrarPagamento(
            Context context,
            Pagamento pagamento) {

        ArrayList<Pagamento> pagamentos = getPagamentos(context);

        pagamentos.add(pagamento);

        salvarPagamentos(context, pagamentos);

        adicionarNotificacao(
                context,
                "Pagamento realizado",
                pagamento.getPagador() + " realizou um pagamento de R$ "
                        + String.format(
                        Locale.getDefault(),
                        "%.2f",
                        pagamento.getValor()
                ),
                "pagamento_realizado"
        );
    }

    public static ArrayList<Pagamento> getPagamentos(Context context) {

        ArrayList<Pagamento> lista = new ArrayList<>();

        try {

            JSONArray array = new JSONArray(
                    prefs(context).getString(PAGAMENTOS, "[]")
            );

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                Pagamento pagamento = new Pagamento(
                        obj.getString("id"),
                        obj.getString("contaId"),
                        obj.getString("pagador"),
                        obj.getDouble("valor"),
                        obj.getString("data"),
                        obj.getBoolean("confirmado"),
                        obj.optString("comprovante", "")
                );

                lista.add(pagamento);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    private static void salvarPagamentos(
            Context context,
            ArrayList<Pagamento> pagamentos) {

        try {

            JSONArray array = new JSONArray();

            for (Pagamento pagamento : pagamentos) {

                JSONObject obj = new JSONObject();

                obj.put("id", pagamento.getId());
                obj.put("contaId", pagamento.getContaId());
                obj.put("pagador", pagamento.getPagador());
                obj.put("valor", pagamento.getValor());
                obj.put("data", pagamento.getData());
                obj.put("confirmado", pagamento.isConfirmado());
                obj.put("comprovante", pagamento.getComprovante());

                array.put(obj);
            }

            prefs(context).edit()
                    .putString(PAGAMENTOS, array.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void confirmarPagamento(
            Context context,
            String pagamentoId) {

        ArrayList<Pagamento> pagamentos = getPagamentos(context);

        for (Pagamento pagamento : pagamentos) {

            if (pagamento.getId().equals(pagamentoId)
                    && !pagamento.isConfirmado()) {

                pagamento.setConfirmado(true);

                adicionarNotificacao(
                        context,
                        "Pagamento confirmado",
                        "O pagamento de " + pagamento.getPagador()
                                + " foi confirmado.",
                        "pagamento_confirmado"
                );

                break;
            }
        }

        salvarPagamentos(context, pagamentos);

        atualizarStatusContas(context);
    }

    private static void atualizarStatusContas(Context context) {

        ArrayList<Conta> contas = getContas(context);
        ArrayList<Pagamento> pagamentos = getPagamentos(context);

        for (Conta conta : contas) {

            double totalConfirmado = 0;

            for (Pagamento pagamento : pagamentos) {

                if (pagamento.getContaId().equals(conta.getId())
                        && pagamento.isConfirmado()) {

                    totalConfirmado += pagamento.getValor();
                }
            }

            conta.setPaga(totalConfirmado >= conta.getValor());
        }

        salvarContas(context, contas);
    }

    public static ArrayList<Pagamento> getPagamentosPorConta(
            Context context,
            String contaId) {

        ArrayList<Pagamento> resultado = new ArrayList<>();

        for (Pagamento pagamento : getPagamentos(context)) {

            if (pagamento.getContaId().equals(contaId)) {
                resultado.add(pagamento);
            }
        }

        return resultado;
    }

    public static ArrayList<Pagamento> getPagamentosPessoais(
            Context context,
            String usuario) {

        ArrayList<Pagamento> resultado = new ArrayList<>();

        for (Pagamento pagamento : getPagamentos(context)) {

            if (pagamento.getPagador().equalsIgnoreCase(usuario)) {
                resultado.add(pagamento);
            }
        }

        return resultado;
    }

    // =========================================
    // HISTÓRICOS
    // =========================================

    public static ArrayList<Conta> getHistoricoSala(Context context) {
        return getContas(context);
    }

    public static ArrayList<Conta> getHistoricoPessoal(
            Context context,
            String usuario) {

        ArrayList<Conta> resultado = new ArrayList<>();

        for (Conta conta : getContas(context)) {

            if (conta.getParticipantes().contains(usuario)) {
                resultado.add(conta);
            }
        }

        return resultado;
    }

    // =========================================
    // RESUMOS FINANCEIROS
    // =========================================

    public static double getTotalSala(Context context) {

        double total = 0;

        for (Conta conta : getContas(context)) {
            total += conta.getValor();
        }

        return total;
    }

    public static double getTotalPessoal(
            Context context,
            String usuario) {

        double total = 0;

        for (Conta conta : getHistoricoPessoal(context, usuario)) {
            total += conta.getValor();
        }

        return total;
    }

    public static double getTotalPago(Context context) {

        double total = 0;

        for (Pagamento pagamento : getPagamentos(context)) {

            if (pagamento.isConfirmado()) {
                total += pagamento.getValor();
            }
        }

        return total;
    }

    public static double getTotalPagoPessoal(
            Context context,
            String usuario) {

        double total = 0;

        for (Pagamento pagamento : getPagamentosPessoais(context, usuario)) {

            if (pagamento.isConfirmado()) {
                total += pagamento.getValor();
            }
        }

        return total;
    }

    public static double getTotalPendente(Context context) {

        double total = 0;

        for (Conta conta : getContas(context)) {

            double pago = 0;

            for (Pagamento pagamento :
                    getPagamentosPorConta(context, conta.getId())) {

                if (pagamento.isConfirmado()) {
                    pago += pagamento.getValor();
                }
            }

            total += Math.max(0, conta.getValor() - pago);
        }

        return total;
    }
    public static ArrayList<Pagamento> getHistoricoPagamentosSala(
            Context context) {

        return getPagamentos(context);
    }

    public static int getQuantidadeContasPagas(Context context) {

        int quantidade = 0;

        for (Conta conta : getContas(context)) {

            if (conta.isPaga()) {
                quantidade++;
            }
        }

        return quantidade;
    }

    public static int getQuantidadeContasPendentes(Context context) {

        int quantidade = 0;

        for (Conta conta : getContas(context)) {

            if (!conta.isPaga()) {
                quantidade++;
            }
        }

        return quantidade;
    }

    // =========================================
    // NOTIFICAÇÕES
    // =========================================

    public static void adicionarNotificacao(
            Context context,
            String titulo,
            String mensagem,
            String tipo) {

        ArrayList<Notificacao> lista = getNotificacoes(context);

        Notificacao notificacao = new Notificacao(
                UUID.randomUUID().toString(),
                titulo,
                mensagem,
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                ).format(new Date()),
                tipo
        );

        lista.add(0, notificacao);

        salvarNotificacoes(context, lista);
    }

    public static ArrayList<Notificacao> getNotificacoes(
            Context context) {

        ArrayList<Notificacao> lista = new ArrayList<>();

        try {

            JSONArray array = new JSONArray(
                    prefs(context).getString(NOTIFICACOES, "[]")
            );

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                Notificacao notificacao = new Notificacao(
                        obj.getString("id"),
                        obj.getString("titulo"),
                        obj.getString("mensagem"),
                        obj.getString("data"),
                        obj.getString("tipo")
                );

                notificacao.setLida(obj.getBoolean("lida"));

                lista.add(notificacao);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    private static void salvarNotificacoes(
            Context context,
            ArrayList<Notificacao> notificacoes) {

        try {

            JSONArray array = new JSONArray();

            for (Notificacao notificacao : notificacoes) {

                JSONObject obj = new JSONObject();

                obj.put("id", notificacao.getId());
                obj.put("titulo", notificacao.getTitulo());
                obj.put("mensagem", notificacao.getMensagem());
                obj.put("data", notificacao.getData());
                obj.put("tipo", notificacao.getTipo());
                obj.put("lida", notificacao.isLida());

                array.put(obj);
            }

            prefs(context).edit()
                    .putString(NOTIFICACOES, array.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void marcarComoLida(
            Context context,
            String notificacaoId) {

        ArrayList<Notificacao> lista = getNotificacoes(context);

        for (Notificacao notificacao : lista) {

            if (notificacao.getId().equals(notificacaoId)) {
                notificacao.setLida(true);
                break;
            }
        }

        salvarNotificacoes(context, lista);
    }

    public static void marcarTodasComoLidas(Context context) {

        ArrayList<Notificacao> lista = getNotificacoes(context);

        for (Notificacao notificacao : lista) {
            notificacao.setLida(true);
        }

        salvarNotificacoes(context, lista);
    }

    public static int getQuantidadeNaoLidas(Context context) {

        int quantidade = 0;

        for (Notificacao notificacao : getNotificacoes(context)) {

            if (!notificacao.isLida()) {
                quantidade++;
            }
        }

        return quantidade;
    }

    // =========================================
    // VERIFICAÇÃO DE VENCIMENTOS
    // =========================================

    public static void verificarVencimentos(Context context) {

        ArrayList<Conta> contas = getContas(context);

        SimpleDateFormat formato = new SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
        );

        Calendar hoje = Calendar.getInstance();

        hoje.set(Calendar.HOUR_OF_DAY, 0);
        hoje.set(Calendar.MINUTE, 0);
        hoje.set(Calendar.SECOND, 0);
        hoje.set(Calendar.MILLISECOND, 0);

        for (Conta conta : contas) {

            if (conta.isPaga()) {
                continue;
            }

            try {

                Date dataVencimento = formato.parse(conta.getVencimento());

                if (dataVencimento == null) {
                    continue;
                }

                Calendar vencimento = Calendar.getInstance();
                vencimento.setTime(dataVencimento);

                long diferenca = vencimento.getTimeInMillis()
                        - hoje.getTimeInMillis();

                long dias = diferenca / (1000 * 60 * 60 * 24);

                if (dias >= 0 && dias <= 3) {

                    adicionarNotificacaoUnica(
                            context,
                            "vencimento_proximo_" + conta.getId(),
                            "Conta próxima do vencimento",
                            "A conta " + conta.getNome()
                                    + " vence em " + conta.getVencimento(),
                            "vencimento_proximo"
                    );

                } else if (dias < 0) {

                    adicionarNotificacaoUnica(
                            context,
                            "conta_vencida_" + conta.getId(),
                            "Conta vencida",
                            "A conta " + conta.getNome()
                                    + " está atrasada.",
                            "conta_vencida"
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void adicionarNotificacaoUnica(
            Context context,
            String chave,
            String titulo,
            String mensagem,
            String tipo) {

        SharedPreferences preferences = prefs(context);

        if (!preferences.getBoolean(chave, false)) {

            adicionarNotificacao(
                    context,
                    titulo,
                    mensagem,
                    tipo
            );

            preferences.edit().putBoolean(chave, true).apply();
        }
    }

    // =========================================
    // DADOS DEMONSTRATIVOS
    // =========================================

    public static void criarDadosExemplo(Context context) {

        ArrayList<Conta> contas = new ArrayList<>();

        Conta mercado = new Conta(
                "conta01",
                "Mercado",
                "Pedro Silva",
                1550.00,
                "15/11/2026",
                false
        );

        mercado.adicionarParticipante("Você");
        mercado.adicionarParticipante("Pedro Silva");

        Conta netflix = new Conta(
                "conta02",
                "Netflix",
                "Eberlano Duarte",
                55.90,
                "01/11/2026",
                true
        );

        netflix.adicionarParticipante("Você");
        netflix.adicionarParticipante("Eberlano Duarte");

        Conta internet = new Conta(
                "conta03",
                "Internet",
                "Você",
                120.00,
                "12/11/2026",
                false
        );

        internet.adicionarParticipante("Você");
        internet.adicionarParticipante("Pedro Silva");
        internet.adicionarParticipante("Eberlano Duarte");

        contas.add(mercado);
        contas.add(netflix);
        contas.add(internet);

        salvarContas(context, contas);

        ArrayList<Pagamento> pagamentos = new ArrayList<>();

        pagamentos.add(new Pagamento(
                "pag01",
                "conta02",
                "Eberlano Duarte",
                55.90,
                "01/11/2026",
                true,
                ""
        ));

        pagamentos.add(new Pagamento(
                "pag02",
                "conta01",
                "Pedro Silva",
                500.00,
                "08/11/2026",
                false,
                ""
        ));

        salvarPagamentos(context, pagamentos);

        adicionarNotificacao(
                context,
                "Nova conta cadastrada",
                "Uma nova conta foi adicionada à sala.",
                "conta_criada"
        );

        adicionarNotificacao(
                context,
                "Conta próxima do vencimento",
                "A conta Internet está próxima do vencimento.",
                "vencimento_proximo"
        );

        adicionarNotificacao(
                context,
                "Conta vencida",
                "A conta Netflix possui vencimento anterior.",
                "conta_vencida"
        );

        adicionarNotificacao(
                context,
                "Pagamento realizado",
                "Pedro Silva registrou um pagamento.",
                "pagamento_realizado"
        );

        adicionarNotificacao(
                context,
                "Pagamento confirmado",
                "Um pagamento foi confirmado.",
                "pagamento_confirmado"
        );
    }
}
