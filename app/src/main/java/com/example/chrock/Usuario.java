package com.example.chrock;

public class Usuario {
    private String nomeUsuario;
    private String status;

    public Usuario(String nomeUsuario) {
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
