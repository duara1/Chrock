
package com.example.chrock.model;

import java.io.Serializable;

public class Pagamento implements Serializable {

    private String id;
    private String contaId;
    private String pagador;
    private double valor;
    private String data;
    private boolean confirmado;
    private String comprovante;

    public Pagamento(String id, String contaId, String pagador,
                     double valor, String data, boolean confirmado,
                     String comprovante) {

        this.id = id;
        this.contaId = contaId;
        this.pagador = pagador;
        this.valor = valor;
        this.data = data;
        this.confirmado = confirmado;
        this.comprovante = comprovante;
    }

    public String getId() {
        return id;
    }

    public String getContaId() {
        return contaId;
    }

    public String getPagador() {
        return pagador;
    }

    public double getValor() {
        return valor;
    }

    public String getData() {
        return data;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getComprovante() {
        return comprovante;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }
}
