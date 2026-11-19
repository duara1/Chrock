package com.example.chrock.model;

public class Contribuicao {
    private String usuarioId;
    private StatusContribuicao status;
    private String dataPagmento;

    public Contribuicao(String usuarioId) {
        this.usuarioId = usuarioId;
        status = StatusContribuicao.PENDENTE;
        dataPagmento = "00/00/0000";
    }

    public StatusContribuicao getStatus() {
        return status;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setStatus(StatusContribuicao status) {
        this.status = status;
    }

    public String getDataPagmento() {
        return dataPagmento;
    }

    public void setDataPagmento(String dataPagmento) {
        this.dataPagmento = dataPagmento;
    }
}
