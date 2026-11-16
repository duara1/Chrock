
package com.example.chrock.model;

import java.io.Serializable;

public class Notificacao implements Serializable {

    private String id;
    private String titulo;
    private String mensagem;
    private String data;
    private String tipo;
    private boolean lida;

    public Notificacao(String id, String titulo, String mensagem,
                       String data, String tipo) {

        this.id = id;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.data = data;
        this.tipo = tipo;
        this.lida = false;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }
}
