package com.example.chrock;

public class Usuario {
    private String nomeUsuario;
    private String statusUsuario;
    private String chavePix;


    public Usuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getStatusUsuario() {
        return statusUsuario;
    }

    public String getChavePix() {
        return chavePix;
    }
    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }


}
