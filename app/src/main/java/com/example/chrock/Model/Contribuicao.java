package com.example.chrock.model;

public class Contribuicao {
    private String usuarioId;
    private StatusContribuicao status;

    public Contribuicao(String usuarioId) {
        this.usuarioId = usuarioId;
        this.status = StatusContribuicao.PENDENTE;
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
}
