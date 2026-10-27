package com.example.chrock.model;

public class Contribuicao {
    private int usuarioId;
    private StatusContribuicao status;

    public Contribuicao(int usuarioId) {
        this.status = StatusContribuicao.PENDENTE;
        this.usuarioId = usuarioId;
    }

    public StatusContribuicao getStatus() {
        return status;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setStatus(StatusContribuicao status) {
        this.status = status;
    }
}
