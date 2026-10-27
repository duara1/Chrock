package com.example.chrock.model;

import java.util.List;
import java.util.ArrayList;

public class Despesa {
    private String id;
    private String salaId;
    private int responsavelId;
    private String titulo;
    private int totalEmInt;
    private String vencimento;
    private String chavePix;
    private List<Contribuicao> contribuente;
    private String comprovante;


    public Despesa(List<Contribuicao> contribuente, String chavePix, String id, int responsavelId, String salaId, String titulo, int totalEmInt, String vencimento) {
        if (contribuente == null || contribuente.isEmpty()) {
            throw new IllegalArgumentException("A despesa precisa de pelo menos 1 contribuinte");
        }
        this.contribuente = new ArrayList<>(contribuente);
        this.chavePix = chavePix;
        this.id = id;
        this.responsavelId = responsavelId;
        this.salaId = salaId;
        this.titulo = titulo;
        this.totalEmInt = totalEmInt;
        this.vencimento = vencimento;
    }

    private int getValorATransferirEmInt(){
        int pessoas = contribuente.size();
        if (pessoas < 1) return 0;
        return (totalEmInt + pessoas - 1) / pessoas;
    }

    private int getMetaDeRecebimento(){
        int outros = contribuente.size() - ((contribuente.contains(responsavelId)) ? 1 : 0);
        return getValorATransferirEmInt() * outros;
    }

    public String getBoleto() {
        return comprovante;
    }

    public String getChavePix() {
        return chavePix;
    }

    public List<Contribuicao> getContribuente() {
        return contribuente;
    }

    public String getId() {
        return id;
    }

    public int getResponsavelId() {
        return responsavelId;
    }

    public String getSalaId() {
        return salaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public double getTotal() {
        return totalEmInt;
    }
    public String getVencimento() {
        return vencimento;
    }

    public void setBoleto(String boleto) {
        this.comprovante = boleto;
    }
}
