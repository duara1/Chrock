package com.example.chrock.model;

public class Usuario {
    private String id;
    private String nome;
    private String pix;
    private String img;
    public Usuario(String id, String nome, String pix, String img){
        this.id = id;
        this.nome = nome;
        this.pix = pix;
        this.img = img;
    }


    public String getId() {
        return id;
    }

    public String getImg() {
        return img;
    }

    public String getNome() {
        return nome;
    }

    public String getPix() {
        return pix;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPix(String pix) {
        this.pix = pix;
    }
}
