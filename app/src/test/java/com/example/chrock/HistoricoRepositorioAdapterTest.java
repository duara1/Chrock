package com.example.chrock;

import com.example.chrock.integracao.HistoricoRepositorioAdapter;
import com.example.chrock.model.Conta;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.repositorio.RepositorioFalso;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class HistoricoRepositorioAdapterTest {
    @Test public void filtraPorParticipacaoESala() {
        List<Conta> contas = HistoricoRepositorioAdapter.contasDoUsuario(RepositorioFalso.get(), "s1", "u1");
        assertEquals(3, contas.size()); // Água, Internet, Mercado
        for (Conta conta : contas) assertNotEquals("Luz", conta.getNome());
    }
    @Test public void converteCentavosParaReais() {
        List<Conta> contas = HistoricoRepositorioAdapter.contasDoUsuario(RepositorioFalso.get(), "s1", "u1");
        Conta agua = null;
        for (Conta conta : contas) if (conta.getId().equals("d1")) agua = conta;
        assertNotNull(agua);
        assertEquals(100.0, agua.getValor(), 0.001);
    }
    @Test public void naoConfundeSinalizadoComConfirmado() {
        assertEquals(StatusContribuicao.SINALIZADO,
            HistoricoRepositorioAdapter.statusDoUsuario(RepositorioFalso.get().getDespesa("d3"), "u1"));
    }
}
