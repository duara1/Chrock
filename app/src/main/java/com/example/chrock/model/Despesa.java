package com.example.chrock.model;

import java.util.List;
import java.util.ArrayList;

public class  Despesa {
    private String despesaId;
    private String salaId;
    private String responsavelId;
    private String titulo;
    private int totalEmInt;
    private String vencimento;
    private String chavePix;
    private List<Contribuicao> contribuintes;
    private String comprovante;

    public Despesa(String despesaId, String salaId, String responsavelId, String titulo, int totalEmInt, String vencimento, String chavePix, List<String> contribuentes) {
        this.despesaId = despesaId;
        this.salaId = salaId;
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.totalEmInt = totalEmInt;
        this.vencimento = vencimento;
        this.chavePix = chavePix;

        if (contribuentes == null || contribuentes.isEmpty()){
            throw new IllegalArgumentException("Escolha pelo menos 1 contribuente");
        }
        this.contribuintes = new ArrayList<>();
        for (String id:contribuentes) {
            this.contribuintes.add(new Contribuicao(id));
        }
    }

    public int getValorATransferirEmInt(String usuarioId){
        int pessoas = contribuintes.size();
        if (pessoas < 1) throw new IllegalArgumentException("ERRO: Ningem em contribuentes. - getValorATransferirEmInt");
        if (usuarioId.equals(responsavelId) && !isContribuente(usuarioId)) return 0;
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
        if (comprovante != null && !comprovante.trim().isEmpty()){
            return true;
        } else {
            return false;
        }
    }

    public String getChavePix() {
        return chavePix;
    }

    public String getComprovante() {
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

    public void setComprovante(String comprovante) {
        this.comprovante = comprovante;
    }
}
