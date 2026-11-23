package com.example.chrock.integracao;

import com.example.chrock.model.Conta;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.repositorio.RepositorioFalso;
import java.util.ArrayList;
import java.util.List;

/** Filtros somente de leitura. Período corresponde ao mês do vencimento (MM/AAAA). */
public final class FiltroHistorico {
    private FiltroHistorico() {}

    public static List<Conta> filtrar(List<Conta> contas, RepositorioFalso repositorio,
                                      String usuarioId, String periodo, String status) {
        List<Conta> resultado = new ArrayList<>();
        if (contas == null || repositorio == null || usuarioId == null) return resultado;
        for (Conta conta : contas) {
            if (conta == null) continue;
            String vencimento = conta.getVencimento();
            if (!"Todos os meses".equals(periodo)) {
                if (vencimento == null || !vencimento.matches("\\d{2}/\\d{2}/\\d{4}")
                        || !vencimento.substring(3).equals(periodo)) continue;
            }
            Despesa despesa = repositorio.getDespesa(conta.getId());
            if (despesa == null || !despesa.isContribuente(usuarioId)) continue;
            StatusContribuicao atual = despesa.getStatus(usuarioId);
            if (!"Todos os status".equals(status) && !rotulo(atual).equals(status)) continue;
            resultado.add(conta);
        }
        return resultado;
    }

    public static String rotulo(StatusContribuicao status) {
        if (status == StatusContribuicao.CONFIRMADO) return "Confirmado";
        if (status == StatusContribuicao.SINALIZADO) return "Sinalizado";
        if (status == StatusContribuicao.CONTESTADO) return "Contestado";
        return "Pendente";
    }
}
