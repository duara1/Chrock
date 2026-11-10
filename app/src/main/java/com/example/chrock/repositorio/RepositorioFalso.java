package com.example.chrock.repositorio;

import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Papel;
import com.example.chrock.model.Sala;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.model.Usuario;
import com.example.chrock.model.Despesa;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioFalso {
    private Map<String, Usuario> usuarios = new HashMap<>();
    private Map<String, Sala> salas = new LinkedHashMap<>();
    private List<Despesa> despesas = new ArrayList<>();
    private static RepositorioFalso instancia;

    public static class ItemConfirmacao {
        public final Despesa despesa;
        public final String usuarioId;

        public ItemConfirmacao(Despesa despesa, String usuarioId) {
            this.despesa = despesa;
            this.usuarioId = usuarioId;
        }
    }

    public static RepositorioFalso get(){
        if (instancia == null){
            instancia = new RepositorioFalso();
            return instancia;
        } else{
            return instancia;
        }
    }

    private RepositorioFalso() {
        popular();
    }

    private void popular() {
        usuarios.put("u1", new Usuario("u1", "Fulano Silva", "fulano@email.com", ""));
        usuarios.put("u2", new Usuario("u2", "Ciclano Costa", "ciclano@email.com", ""));
        usuarios.put("u3", new Usuario("u3", "Beltrano Duarte", "(92) 99999-0000", ""));

        Sala casa = new Sala("s1", "AB12C", "u1", "Casa", "Organizar a divisão das despesas do apartamento");
        casa.adicionarMembro("u2");
        casa.adicionarMembro("u3");
        casa.setMembrosPapel("u3", Papel.MODERADOR);
        salas.put(casa.getId(), casa);

        Despesa agua = criarDespesa("d1", "s1", "u1", "Água", 10000, "09/11/2026", usuarios.get("u1").getPix(), new ArrayList<>(List.of("u1", "u2", "u3")));
        Despesa luz = criarDespesa("d2", "s1", "u1", "Luz", 20000, "05/11/2026", usuarios.get("u1").getPix(), new ArrayList<>(List.of("u2")));
        Despesa internet = criarDespesa("d3", "s1", "u2", "Internet", 12000, "17/11/2026", usuarios.get("u1").getPix(), new ArrayList<>(List.of("u1", "u3")));
        Despesa mercado = criarDespesa("d4", "s1", "u1", "Mercado", 150000, "20/10/2026", usuarios.get("u1").getPix(), new ArrayList<>(List.of("u1", "u2", "u3")));
        Despesa netflix = criarDespesa("d5", "s1", "u3", "Netflix", 5000, "22/11/2026", usuarios.get("u3").getPix(), new ArrayList<>(List.of("u3")));


        internet.mudarStatus("u1", StatusContribuicao.SINALIZADO);
        mercado.mudarStatus("u1", StatusContribuicao.CONFIRMADO);
        mercado.mudarStatus("u2", StatusContribuicao.CONFIRMADO);
        mercado.mudarStatus("u3", StatusContribuicao.CONFIRMADO);
        mercado.setComprovante("comprovante-mercado.pdf");

    }

    public Usuario getUsuario(String usuarioId){
        return usuarios.get(usuarioId);
    }

    public Sala getSala(String salaId){
        return salas.get(salaId);
    }

    public Despesa getDespesa(String despesaId) {
        for (Despesa d: despesas) {
            if (d.getDespesaId().equals(despesaId)){
                return d;
            }
        }
        throw new IllegalArgumentException("Despesa não encontrada.");
    }

    public List<Sala> getSalasDoUsuario(String usuarioId){
        List<Sala> resu = new ArrayList<>();
        for (Sala s: salas.values()) {
            if (s.isMembro(usuarioId)){
                resu.add(s);
            }
        }
        return resu;
    }

    public Despesa criarDespesa(String despesaId, String salaId, String responsavelId, String titulo, int totalEmInt, String vencimento, String chavePix, List<String> contribuentes){
        Despesa d = new Despesa(despesaId, salaId, responsavelId, titulo, totalEmInt, vencimento, chavePix, contribuentes);
        despesas.add(d);
        return d;
    }

    public List<Despesa> souResponsavel(String usuarioId, String salaId){
        List<Despesa> resu = new ArrayList<>();
        for (Despesa d : despesas) {
            if (d.getSalaId().equals(salaId)
                    && d.getResponsavelId().equals(usuarioId)
                    && !d.isConcluida()) {
                resu.add(d);
            }
        }
        return resu;
    }

    public List<Despesa> aTransferir(String usuarioId, String salaId){
        List<Despesa> resu = new ArrayList<>();
        for (Despesa d: despesas) {
            if (!d.getSalaId().equals(salaId)) continue;
            if (d.getResponsavelId().equals(usuarioId)) continue;
            if (!d.isContribuente(usuarioId)) continue;
            if (d.isConcluida()) continue;
            StatusContribuicao status = d.getStatus(usuarioId);

            if (status == StatusContribuicao.PENDENTE || status == StatusContribuicao.CONTESTADO){
                resu.add(d);
            }
        }
        return resu;
    }

    public List<ItemConfirmacao> aConfirmar(String usuarioId, String salaId) {
        List<ItemConfirmacao> resultado = new ArrayList<>();
        for (Despesa d : despesas) {
            if (!d.getSalaId().equals(salaId)) continue;
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
