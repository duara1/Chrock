package com.example.chrock.model;

public  class ItemConfirmacao {
    public final Despesa despesa;
    public final String usuarioId;

    public ItemConfirmacao(Despesa despesa, String usuarioId) {
        this.despesa = despesa;
        this.usuarioId = usuarioId;
    }
}