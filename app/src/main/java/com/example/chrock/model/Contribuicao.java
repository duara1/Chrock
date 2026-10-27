package com.example.chrock.model;

public class Contribuicao {
    private int usuarioId;
    private StatusContribuicao status;

    public Contribuicao(int usuarioId) {
        this.usuarioId = usuarioId;
        this.status = StatusContribuicao.PENDENTE;
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
