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

    public Despesa(List<Contribuicao> contribuentes, String chavePix, String despesaId, String responsavelId, String salaId, String titulo, int totalEmInt, String vencimento) {
        this.contribuintes = new ArrayList<>(contribuentes);
        this.chavePix = chavePix;
        this.despesaId = despesaId;
        this.responsavelId = responsavelId;
        this.salaId = salaId;
        this.titulo = titulo;
        this.totalEmInt = totalEmInt;
        this.vencimento = vencimento;
    }

    public int getValorATransferirEmInt(){
        int pessoas = contribuintes.size();
        if (pessoas < 1) return 0;
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
        return getValorATransferirEmInt() * outros;
    }

    public String getChavePix() {
        return chavePix;
    }

    public String getComprovante() {
        return comprovante;
    }

    public List<Contribuicao> getContribuentes() {
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
