package com.example.chrock.data;

import com.example.chrock.model.Contribuicao;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Papel;
import com.example.chrock.model.Sala;
import com.example.chrock.model.StatusContribuicao;
import com.example.chrock.model.Usuario;
import com.example.chrock.util.Formatar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Conversor {

    private static String texto(JSONObject j, String chave) throws JSONException {
        return j.isNull(chave) ? "" : j.getString(chave);
    }

    public static Sala sala(JSONObject j) throws JSONException {
        Map<String, Papel> membros = new LinkedHashMap<>();
        JSONArray lista = j.getJSONArray("sala_membros");
        for (int i = 0; i < lista.length(); i++) {
            JSONObject m = lista.getJSONObject(i);
            // toUpperCase: aceita "moderador" ou "MODERADOR" vindo do banco
            membros.put(m.getString("usuario_id"),
                    Papel.valueOf(m.getString("papel").trim().toUpperCase(Locale.ROOT)));
        }
        return Sala.doServidor(j.getString("id"), texto(j, "titulo"), texto(j, "descricao"),
                texto(j, "codigo"), texto(j, "img"), j.getString("adm_id"), membros);
    }

    public static Despesa despesa(JSONObject j) throws JSONException {
        List<Contribuicao> contribuicoes = new ArrayList<>();
        JSONArray lista = j.getJSONArray("contribuicoes");
        for (int i = 0; i < lista.length(); i++) {
            JSONObject c = lista.getJSONObject(i);
            contribuicoes.add(new Contribuicao(
                    c.getString("usuario_id"),
                    StatusContribuicao.valueOf(c.getString("status").trim().toUpperCase(Locale.ROOT))));
        }
        String comprovante = j.isNull("comprovante") ? null : j.getString("comprovante");
        return Despesa.doServidor(j.getString("id"), j.getString("sala_id"),
                j.getString("responsavel_id"), texto(j, "titulo"), j.getInt("total_centavos"),
                Formatar.deIso(j.getString("vencimento")), comprovante, contribuicoes);
    }

    public static Usuario usuario(JSONObject j) throws JSONException {
        return new Usuario(j.getString("id"), texto(j, "nome"), texto(j, "pix"), texto(j, "img"));
    }
}