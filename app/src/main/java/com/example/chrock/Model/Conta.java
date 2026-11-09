
package com.example.chrock.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Conta implements Serializable {

    private String id;
    private String nome;
    private String responsavel;
    private double valor;
    private String vencimento;
    private boolean paga;
    private ArrayList<String> participantes;

    public Conta(String id, String nome, String responsavel,
                 double valor, String vencimento, boolean paga) {

        this.id = id;
        this.nome = nome;
        this.responsavel = responsavel;
        this.valor = valor;
        this.vencimento = vencimento;
        this.paga = paga;
        this.participantes = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public double getValor() {
        return valor;
    }

    public String getVencimento() {
        return vencimento;
    }

    public boolean isPaga() {
        return paga;
    }

    public ArrayList<String> getParticipantes() {
        return participantes;
    }

    public void setPaga(boolean paga) {
        this.paga = paga;
    }

    public void adicionarParticipante(String participante) {
        participantes.add(participante);
    }
}
