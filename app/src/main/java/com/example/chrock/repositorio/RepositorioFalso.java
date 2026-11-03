package com.example.chrock.repositorio;

import com.example.chrock.model.Papel;
import com.example.chrock.model.Sala;
import com.example.chrock.model.Usuario;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioFalso {
    private Map<String, Usuario> usuarios = new HashMap<>();
    private Map<String, Sala> salas = new LinkedHashMap<>();

    public RepositorioFalso() {
        popular();
    }

    private void popular() {
        usuarios.put("u1", new Usuario("u1", "Fulano Silva", "fulano@email.com", ""));
        usuarios.put("u2", new Usuario("u2", "Ciclano Costa", "ciclano@email.com", ""));
        usuarios.put("u3", new Usuario("u3", "Beltrano Duarte", "(92) 99999-0000", ""));

        Sala casa = new Sala("s1", "AB12C", "u1", "Casa", "Organizar a divisão das despesas do apartamento");
        casa.adicionarMembro("u2");
        casa.adicionarMembro("u3");
        casa.setMembrosPapel("u3", Papel.MODERADOR);
        salas.put(casa.getId(), casa);
    }

    public Usuario getUsuario(String usuarioId){
        return usuarios.get(usuarioId);
    }

    public Sala getSala(String salaId){
        return salas.get(salaId);
    }

    public List<Sala> getSalasDoUsuario(String usuarioId){
        List<Sala> resu = new ArrayList<>();
        for (Sala s: salas.values()) {
            if (s.isMembro(usuarioId)){
                resu.add(s);
            }
        }
        return resu;
    }
}
