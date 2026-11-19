package com.example.chrock.repositorio;

import android.os.Handler;
import android.os.Looper;

import com.example.chrock.data.Api;
import com.example.chrock.data.Conversor;
import com.example.chrock.model.Despesa;
import com.example.chrock.model.Sala;
import com.example.chrock.model.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioSupabase {

    public interface Resposta<T> {
        void sucesso(T dado);
        void erro(String mensagem);
    }

    private static final Handler TELA = new Handler(Looper.getMainLooper());
    private static RepositorioSupabase instancia;private final Map<String, Usuario> usuariosEmCache = new HashMap<>();
    private final Map<String, Despesa> despesasEmCache = new HashMap<>();



    public static RepositorioSupabase get() {
        if (instancia == null) {
            instancia = new RepositorioSupabase();
        }
        return instancia;
    }

    private RepositorioSupabase() {}

    public void carregarSalas(Resposta<List<Sala>> resposta) {
        Api.get("/rest/v1/salas?select=id,titulo,descricao,codigo,img,adm_id,"
                        + "sala_membros(usuario_id,papel)&order=titulo.asc",
                new Api.Resultado() {
                    @Override
                    public void sucesso(String corpo) {
                        try {
                            JSONArray lista = new JSONArray(corpo);
                            List<Sala> salas = new ArrayList<>();
                            for (int i = 0; i < lista.length(); i++) {
                                salas.add(Conversor.sala(lista.getJSONObject(i)));
                            }
                            entregar(resposta, salas);
                        } catch (JSONException | RuntimeException e) {
                            falhar(resposta, "Resposta inesperada do servidor.");
                        }
                    }

                    @Override
                    public void erro(String mensagem) {
                        falhar(resposta, mensagem);
                    }
                });
    }

    public void carregarDespesas(String salaId, Resposta<List<Despesa>> resposta) {
        Api.get("/rest/v1/despesas?select=id,sala_id,responsavel_id,titulo,total_centavos,"
                        + "vencimento,comprovante,contribuicoes(usuario_id,status)"
                        + "&sala_id=eq." + salaId + "&order=vencimento.asc",
                new Api.Resultado() {
                    @Override
                    public void sucesso(String corpo) {
                        try {
                            JSONArray lista = new JSONArray(corpo);
                            List<Despesa> despesas = new ArrayList<>();
                            for (int i = 0; i < lista.length(); i++) {
                                despesas.add(Conversor.despesa(lista.getJSONObject(i)));
                            }
                            TELA.post(() -> {
                                for (Despesa d : despesas) despesasEmCache.put(d.getDespesaId(), d);
                                resposta.sucesso(despesas);
                            });
                        } catch (JSONException | RuntimeException e) {
                            falhar(resposta, "Resposta inesperada do servidor.");
                        }
                    }

                    @Override
                    public void erro(String mensagem) {
                        falhar(resposta, mensagem);
                    }
                });
    }

    public void carregarMembros(String salaId, Resposta<Map<String, Usuario>> resposta) {
        Api.get("/rest/v1/sala_membros?select=usuario_id,profiles(id,nome,pix,img)"
                        + "&sala_id=eq." + salaId,
                new Api.Resultado() {
                    @Override
                    public void sucesso(String corpo) {
                        try {
                            JSONArray lista = new JSONArray(corpo);
                            Map<String, Usuario> usuarios = new LinkedHashMap<>();
                            for (int i = 0; i < lista.length(); i++) {
                                JSONObject perfil = lista.getJSONObject(i).getJSONObject("profiles");
                                Usuario u = Conversor.usuario(perfil);
                                usuarios.put(u.getId(), u);
                            }
                            TELA.post(() -> {
                                usuariosEmCache.putAll(usuarios);
                                resposta.sucesso(usuarios);
                            });
                        } catch (JSONException | RuntimeException e) {
                            falhar(resposta, "Resposta inesperada do servidor.");
                        }
                    }

                    @Override
                    public void erro(String mensagem) {
                        falhar(resposta, mensagem);
                    }
                });
    }

    private static <T> void entregar(Resposta<T> resposta, T dado) {
        TELA.post(() -> resposta.sucesso(dado));
    }

    private static void falhar(Resposta<?> resposta, String mensagem) {
        TELA.post(() -> resposta.erro(mensagem));
    }

    public Usuario getUsuario(String id) {
        return usuariosEmCache.get(id);
    }

    public Despesa getDespesa(String id) {
        return despesasEmCache.get(id);
    }
}