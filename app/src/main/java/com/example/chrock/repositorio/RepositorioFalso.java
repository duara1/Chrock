package com.example.chrock.repositorio;

import android.net.Uri;

import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Geral;
import com.example.chrock.model.ItemConfirmacao;
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

        Despesa agua = criarDespesa("d1", "s1", "u1", "Água", 10000, "18/11/2026", usuarios.get("u1").getPix(), new ArrayList<>(List.of("u1", "u2", "u3")));
        Despesa luz = criarDespesa("d2", "s1", "u1", "Luz", 20000, "27/11/2026", usuarios.get("u1").getPix(), new ArrayList<>(List.of("u2")));
        Despesa internet = criarDespesa("d3", "s1", "u2", "Internet", 12000, "16/11/2026", usuarios.get("u1").getPix(), new ArrayList<>(List.of("u2", "u3")));
        Despesa mercado = criarDespesa("d4", "s1", "u1", "Mercado", 150000, "20/10/2026", usuarios.get("u1").getPix(), new ArrayList<>(List.of("u1", "u2", "u3")));
        Despesa netflix = criarDespesa("d5", "s1", "u2", "Netflix", 5000, "28/11/2026", usuarios.get("u3").getPix(), new ArrayList<>(List.of("u3")));

        internet.mudarStatus("u3", StatusContribuicao.SINALIZADO);
        mercado.mudarStatus("u1", StatusContribuicao.CONFIRMADO);
        mercado.mudarStatus("u2", StatusContribuicao.CONFIRMADO);
        mercado.mudarStatus("u3", StatusContribuicao.CONFIRMADO);
        mercado.setComprovante(Uri.parse("https://exemplo.com"));

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

    public static class cardsInfo{
        public int pendente;
        public int pago;
        public int total;

        public cardsInfo(int pendente, int pago, int total){
            this.pendente = pendente;
            this.pago = pago;
            this.total = total;
        }
    }
    public cardsInfo getInfosCard(String usuarioId, String salaId, Geral card){
        int pendente = 0;
        int pago = 0;
        int total = 0;
        if (card.equals(Geral.PESSOAL)) {
            for (Despesa d : despesas) {
                if (d.isContribuente(usuarioId) && d.getSalaId().equals(salaId)) {
                    total = total + d.getValorTransferirAConfirmar();
                }

                if (d.isContribuente(usuarioId) && d.getSalaId().equals(salaId) && d.getStatus(usuarioId).equals(StatusContribuicao.CONFIRMADO)) {
                    pago = pago + d.getValorTransferirAConfirmar();
                }

                if (d.isContribuente(usuarioId) && d.getSalaId().equals(salaId) && !d.getStatus(usuarioId).equals(StatusContribuicao.CONFIRMADO)) {
                    pendente = pendente + d.getValorTransferirAConfirmar();
                }
            }
            return new cardsInfo(pendente, pago, total);
        }

        if (card.equals(Geral.SALA)){
            for (Despesa d: despesas) {
                if (d.getSalaId().equals(salaId)){
                    total = total + d.getTotalEmInt();
                }

                if (d.getSalaId().equals(salaId) && d.isConcluida()){
                    pago = pago + d.getTotalEmInt();
                }

                if (d.getSalaId().equals(salaId) && !d.isConcluida()){
                    pendente = pendente + d.getTotalEmInt();
                }
            }
            return new cardsInfo(pendente, pago, total);
        }
        return new cardsInfo(pendente, pago, total);
    }


    public Despesa criarDespesa(String despesaId, String salaId, String responsavelId, String titulo, int totalEmInt, String vencimento, String chavePix, List<String> contribuentes){
        Despesa d = new Despesa(despesaId, salaId, responsavelId, titulo, totalEmInt, vencimento, contribuentes);
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
