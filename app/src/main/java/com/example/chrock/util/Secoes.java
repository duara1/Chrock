package com.example.chrock.util;

import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.ItemConfirmacao;
import com.example.chrock.model.StatusContribuicao;

import java.util.ArrayList;
import java.util.List;

public class Secoes {

    public static List<Despesa> aTransferir(List<Despesa> despesas, String usuarioId) {
        List<Despesa> resultado = new ArrayList<>();
        for (Despesa d : despesas) {
            if (d.isConcluida()) continue;
            if (d.getResponsavelId().equals(usuarioId)) continue;
            if (!d.isContribuente(usuarioId)) continue;

            StatusContribuicao status = d.getStatus(usuarioId);
            if (status == StatusContribuicao.PENDENTE || status == StatusContribuicao.CONTESTADO) {
                resultado.add(d);
            }
        }
        return resultado;
    }

    public static List<Despesa> souResponsavel(List<Despesa> despesas, String usuarioId) {
        List<Despesa> resultado = new ArrayList<>();
        for (Despesa d : despesas) {
            if (d.getResponsavelId().equals(usuarioId) && !d.isConcluida()) {
                resultado.add(d);
            }
        }
        return resultado;
    }

    public static List<ItemConfirmacao> aConfirmar(List<Despesa> despesas, String usuarioId) {
        List<ItemConfirmacao> resultado = new ArrayList<>();
        for (Despesa d : despesas) {
            if (!d.getResponsavelId().equals(usuarioId)) continue;
            for (Contribuicao c : d.getContribuintes()) {
                if (c.getStatus() == StatusContribuicao.SINALIZADO) {
                    resultado.add(new ItemConfirmacao(d, c.getUsuarioId()));
                }
            }
        }
        return resultado;
    }
}