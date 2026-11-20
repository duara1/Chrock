package com.example.chrock.model;

import android.net.Uri;

import java.util.List;
import java.util.ArrayList;

public class  Despesa {
    private String despesaId;
    private String salaId;
    private String responsavelId;
    private String titulo;
    private int totalEmInt;
    private String vencimento;
    private List<Contribuicao> contribuintes;
    private Uri comprovante;

    public Despesa(String despesaId, String salaId, String responsavelId, String titulo, int totalEmInt, String vencimento , List<String> contribuentes) {
        this.despesaId = despesaId;
        this.salaId = salaId;
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.totalEmInt = totalEmInt;
        this.vencimento = vencimento;

        if (contribuentes == null || contribuentes.isEmpty()){
            throw new IllegalArgumentException("Escolha pelo menos 1 contribuente");
        }
        this.contribuintes = new ArrayList<>();
        for (String id:contribuentes) {
            this.contribuintes.add(new Contribuicao(id));
        }
    }

    private Despesa(String despesaId, String salaId, String responsavelId, String titulo,
                    int totalEmInt, String vencimento, String comprovante,
                    List<Contribuicao> contribuintes) {
        this.despesaId = despesaId;
        this.salaId = salaId;
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.totalEmInt = totalEmInt;
        this.vencimento = vencimento;
        this.comprovante = (comprovante == null || comprovante.trim().isEmpty())
                ? null
                : Uri.parse(comprovante.trim());
        this.contribuintes = new ArrayList<>(contribuintes);
    }

    public static Despesa doServidor(String despesaId, String salaId, String responsavelId,
                                     String titulo, int totalEmInt, String vencimento,
                                     String comprovante, List<Contribuicao> contribuintes) {
        if (contribuintes == null || contribuintes.isEmpty()) {
            throw new IllegalArgumentException("Despesa sem contribuintes");
        }
        return new Despesa(despesaId, salaId, responsavelId, titulo, totalEmInt,
                vencimento, comprovante, contribuintes);
    }

    public int getValorATransferirEmInt(String usuarioId){
        int pessoas = contribuintes.size();
        if (pessoas < 1) throw new IllegalArgumentException("ERRO: Ningem em contribuentes. - getValorATransferirEmInt");
        if (!isContribuente(usuarioId)) return 0;
        return (totalEmInt + pessoas - 1) / pessoas;
    }

    public int getValorTransferirAConfirmar(){
        int pessoas = contribuintes.size();
        return (totalEmInt + pessoas - 1) / pessoas;
    }

    public boolean isResponsavelContribui() {
        for (Contribuicao c : contribuintes) {
            if (c.getUsuarioId().equals(responsavelId)) return true;
        }
        return false;
    }

    public int getMetaDeRecebimento() {
        int outros = contribuintes.size() - (isResponsavelContribui() ? 1 : 0);
        return ((totalEmInt + contribuintes.size() - 1) / contribuintes.size()) * outros;
    }

    public void mudarStatus(String usuarioId, StatusContribuicao status){
        if(isContribuente(usuarioId)){
            for (Contribuicao c: contribuintes) {
                if (c.getUsuarioId().equals(usuarioId)){
                    c.setStatus(status);
                    return;
                }
            }
        } else {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
    }

    public StatusContribuicao getStatus(String usuarioId){
        if (isContribuente(usuarioId)){
            for (Contribuicao c: contribuintes) {
                if (c.getUsuarioId().equals(usuarioId)){
                    return c.getStatus();
                }
            }
        }
        throw new IllegalArgumentException("Erro: getStatus - Despesa");
    }

    public Boolean isContribuente(String usuarioId){
        for (int i = 0;  i < contribuintes.size() ; i++){
            if (contribuintes.get(i).getUsuarioId().equals(usuarioId))
                return true;
        }
        return false;
    }

    public boolean isConcluida(){
        if (comprovante != null && !comprovante.toString().trim().isEmpty()){
            return true;
        } else {
            return false;
        }
    }

    public int getRecebidoEmInt() {
        int porPessoa = getValorTransferirAConfirmar();
        int soma = 0;
        for (Contribuicao c : contribuintes) {
            if (!c.getUsuarioId().equals(responsavelId)
                    && c.getStatus() == StatusContribuicao.CONFIRMADO) {
                soma += porPessoa;
            }
        }
        return soma;
    }

    public Uri getComprovante() {
        return comprovante;
    }

    public List<Contribuicao> getContribuintes() {
        return contribuintes;
    }

    public String getDespesaId() {
        return despesaId;
    }

    public String getResponsavelId() {
        return responsavelId;
    }

    public String getSalaId() {
        return salaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getTotalEmInt() {
        return totalEmInt;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setComprovante(Uri comprovante) {
        this.comprovante = comprovante;
    }
}
