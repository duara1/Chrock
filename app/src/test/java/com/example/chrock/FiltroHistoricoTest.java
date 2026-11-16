package com.example.chrock;

import com.example.chrock.integracao.FiltroHistorico;
import com.example.chrock.integracao.HistoricoRepositorioAdapter;
import com.example.chrock.model.Conta;
import com.example.chrock.repositorio.RepositorioFalso;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class FiltroHistoricoTest {
    private final RepositorioFalso repo = RepositorioFalso.get();
    private List<Conta> contas() { return HistoricoRepositorioAdapter.contasDoUsuario(repo, "s1", "u1"); }

    @Test public void filtraPorMesDoVencimento() {
        List<Conta> resultado = FiltroHistorico.filtrar(contas(), repo, "u1", "10/2026", "Todos os status");
        assertEquals(1, resultado.size());
        assertEquals("Mercado", resultado.get(0).getNome());
    }
    @Test public void filtraPorStatusConfirmado() {
        List<Conta> resultado = FiltroHistorico.filtrar(contas(), repo, "u1", "Todos os meses", "Confirmado");
        assertEquals(1, resultado.size());
        assertEquals("Mercado", resultado.get(0).getNome());
    }
    @Test public void filtrosSemCorrespondenciaRetornamVazio() {
        assertTrue(FiltroHistorico.filtrar(contas(), repo, "u1", "10/2026", "Pendente").isEmpty());
    }
}
