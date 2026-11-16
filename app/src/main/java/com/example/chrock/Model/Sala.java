package com.example.chrock.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Sala {
    private String id;
    private String codigo;
    private String ADMId;
    private String titulo;
    private String descricao;
    private String img;
    private Map<String, Papel> membros = new HashMap<>();

    public Sala(String id, String codigo, String ADMId, String titulo, String descricao) {
        this.id = id;
        this.codigo = codigo;
        this.ADMId = ADMId;
        this.titulo = titulo;
        this.descricao = descricao;
        membros.put(ADMId, Papel.ADM);
    }

    public void setMembrosPapel(String usuarioId, Papel papel) {
        if (!membros.containsKey(usuarioId)) {
            throw new IllegalArgumentException("Essa pessoa não está na sala.");
        }
        if (usuarioId == ADMId) {
            throw new IllegalArgumentException("O ADM não pode ter o papel alterado.");
        }
        membros.put(usuarioId, papel);
    }

    public Papel getPapel(String usuarioId){
        if (membros.containsKey(usuarioId)){
            return membros.get(usuarioId);
        }
        throw new IllegalArgumentException("Usuário não encontrado");
    }

    public boolean isMembro(String usuarioId){
        if (membros.containsKey(usuarioId)){
            return true;
        } else {
            return false;
        }
    }

    public void adicionarMembro(String usuarioId){
        if (membros.containsKey(usuarioId)){
            throw new IllegalArgumentException("essa pessoa já está na sala.");
        } else {
            membros.put(usuarioId, Papel.PARTICIPANTE);
        }
    }

    public Map<String, Papel> getMembros(){

        return Collections.unmodifiableMap(membros);
    }

    public String getADMId() {
        return ADMId;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getId() {
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
