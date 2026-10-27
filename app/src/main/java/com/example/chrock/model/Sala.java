package com.example.chrock.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Sala {
    private int id;
    private int ADMId;
    private String titulo;
    private String descricao;
    private String codigo;
    private String img;
    private Map<Integer, Papel> membros = new LinkedHashMap<>();

    public Sala(int ADMId, String codigo, int id, String descricao, String titulo) {
        this.ADMId = ADMId;
        this.codigo = codigo;
        this.id = id;
        this.descricao = descricao;
        this.titulo = titulo;
        membros.put(ADMId, Papel.ADM);
    }

    public void setMembrosPapel(int usuarioId, Papel papel){
        if (membros.containsKey(usuarioId)){
            membros.replace(usuarioId, papel);
        } else {
            throw new IllegalArgumentException("essa pessoa já está na sala.");
        }
    }

    public Papel getPapel(int usuarioId){
        if (membros.containsKey(usuarioId)){
            return membros.get(usuarioId);
        }
        throw new IllegalArgumentException("Usuário não encontrado");
    }

    public boolean isMembro(int usuarioId){
        if (membros.containsKey(usuarioId)){
            return true;
        } else {
            return false;
        }
    }

    public void adicionarMembro(int usuarioId){
        if (membros.containsKey(usuarioId)){
            throw new IllegalArgumentException("essa pessoa já está na sala.");
        } else {
            membros.put(usuarioId, Papel.PARTICIPANTE);
        }
    }

    public Map<Integer, Papel> getMembros(){
        return Collections.unmodifiableMap(membros);
    }

    public int getADMId() {
        return ADMId;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getId() {
        return id;
    }

    public String getImg() {
        return img;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
