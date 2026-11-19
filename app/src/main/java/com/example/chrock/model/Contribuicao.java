package com.example.chrock.model;

public class Contribuicao {
    private String usuarioId;
    private StatusContribuicao status;

    public Contribuicao(String usuarioId) {
        this.usuarioId = usuarioId;
        status = StatusContribuicao.PENDENTE;
    }

    public Contribuicao(String usuarioId, StatusContribuicao status) {
        this.usuarioId = usuarioId;
        this.status = status;
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
