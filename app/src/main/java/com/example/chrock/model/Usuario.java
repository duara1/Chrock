package com.example.chrock.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id;
    private String nome;
    private String pix;
    private String img;
    private List<Integer> salas;
    private List<Despesa> contribui;
    private List<Despesa> responsavel;

    public Usuario(int id, String nome, String pix, String img){
        this.id = id;
        this.nome = nome;
        this.pix = pix;
        this.img = img;
        this.salas = new ArrayList<>();
        this.contribui = new ArrayList<>();
        this.responsavel = new ArrayList<>();
    }

    public List<Despesa> getContribui() {
        return contribui;
    }

    public int getId() {
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

    public List<Despesa> getResponsavel() {
        return responsavel;
    }

    public List<Integer> getSalas() {
        return salas;
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
