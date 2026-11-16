package com.example.chrock.integracao;

import com.example.chrock.model.Conta;
import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.model.Usuario;
import com.example.chrock.repositorio.RepositorioFalso;

import java.util.ArrayList;
import java.util.List;

/** Conversão de leitura para telas legadas; não persiste nem altera o repositório. */
public final class HistoricoRepositorioAdapter {
    private HistoricoRepositorioAdapter() { }

    /** Retorna somente despesas em que o usuário é contribuinte, na sala informada. */
    public static List<Conta> contasDoUsuario(RepositorioFalso repositorio,
                                              String salaId, String usuarioId) {
        if (repositorio == null || salaId == null || usuarioId == null) {
            throw new IllegalArgumentException("Repositório, sala e usuário são obrigatórios");
        }
        List<Conta> resultado = new ArrayList<>();
        for (Despesa despesa : repositorio.getDespesasDaSala(salaId)) {
            if (!despesa.isContribuente(usuarioId)) continue;
            Usuario responsavel = repositorio.getUsuario(despesa.getResponsavelId());
            String nome = responsavel == null ? "Responsável desconhecido" : responsavel.getNome();
            Conta conta = new Conta(despesa.getDespesaId(), despesa.getTitulo(), nome,
                    despesa.getTotalEmInt() / 100.0, despesa.getVencimento(),
                    despesa.isConcluida());
            for (Contribuicao contribuicao : despesa.getContribuintes()) {
                Usuario participante = repositorio.getUsuario(contribuicao.getUsuarioId());
                conta.adicionarParticipante(participante == null
                        ? contribuicao.getUsuarioId() : participante.getNome());
            }
            resultado.add(conta);
        }
        return resultado;
    }

    /** Estado da contribuição, sem inventar data ou comprovante de pagamento. */
    public static StatusContribuicao statusDoUsuario(Despesa despesa, String usuarioId) {
        return despesa.getStatus(usuarioId);
    }
}
