package com.example.chrock.repositorio;

import android.os.Handler;
import android.os.Looper;

import com.example.chrock.data.Api;
import com.example.chrock.data.Conversor;
import com.example.chrock.data.Sessao;
import com.example.chrock.data.SupabaseConfig;
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
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class RepositorioSupabase {

    public interface Resposta<T> {
        void sucesso(T dado);
        void erro(String mensagem);
    }

    public static final String SEM_ACESSO = "Você não faz parte desta sala.";
    public static final String SALA_NAO_ENCONTRADA = "Sala não encontrada.";

    private static final String BUCKET_SALAS = "salas";

    private static final Handler TELA = new Handler(Looper.getMainLooper());
    private static RepositorioSupabase instancia;

    private final Map<String, Usuario> usuariosEmCache = new HashMap<>();
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
                            String eu = Sessao.getUsuarioId();
                            List<Sala> salas = new ArrayList<>();
                            for (int i = 0; i < lista.length(); i++) {
                                Sala sala = Conversor.sala(lista.getJSONObject(i));
                                if (sala.isMembro(eu)) salas.add(sala);
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
                                JSONObject linha = lista.getJSONObject(i);
                                if (linha.isNull("profiles")) continue;
                                Usuario u = Conversor.usuario(linha.getJSONObject("profiles"));
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

    public void carregarDespesa(String despesaId, Resposta<Despesa> resposta) {
        Api.get("/rest/v1/despesas?select=id,sala_id,responsavel_id,titulo,total_centavos,"
                        + "vencimento,comprovante,contribuicoes(usuario_id,status)"
                        + "&id=eq." + despesaId,
                new Api.Resultado() {
                    @Override
                    public void sucesso(String corpo) {
                        try {
                            JSONArray lista = new JSONArray(corpo);
                            if (lista.length() == 0) {
                                falhar(resposta, "Despesa não encontrada.");
                                return;
                            }
                            Despesa d = Conversor.despesa(lista.getJSONObject(0));
                            TELA.post(() -> {
                                despesasEmCache.put(d.getDespesaId(), d);
                                resposta.sucesso(d);
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

    public void carregarSala(String salaId, Resposta<Sala> resposta) {
        Api.get("/rest/v1/salas?select=id,titulo,descricao,codigo,img,adm_id,"
                        + "sala_membros(usuario_id,papel)&id=eq." + salaId,
                new Api.Resultado() {
                    @Override
                    public void sucesso(String corpo) {
                        try {
                            JSONArray lista = new JSONArray(corpo);
                            if (lista.length() == 0) {
                                falhar(resposta, SALA_NAO_ENCONTRADA);
                                return;
                            }
                            Sala sala = Conversor.sala(lista.getJSONObject(0));
                            if (!sala.isMembro(Sessao.getUsuarioId())) {
                                falhar(resposta, SEM_ACESSO);
                                return;
                            }
                            entregar(resposta, sala);
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

    public void criarSalaComImagem(String titulo, String descricao, byte[] imagemJpeg,
                                   Resposta<String> resposta) {
        if (imagemJpeg == null) {
            criarSala(titulo, descricao, null, resposta);
            return;
        }

        String caminho = Sessao.getUsuarioId() + "/" + UUID.randomUUID() + ".jpg";

        Api.enviarArquivo(BUCKET_SALAS, caminho, imagemJpeg, "image/jpeg", new Api.Resultado() {
            @Override
            public void sucesso(String corpo) {
                String endereco = SupabaseConfig.URL + "/storage/v1/object/public/"
                        + BUCKET_SALAS + "/" + caminho;
                criarSala(titulo, descricao, endereco, resposta);
            }

            @Override
            public void erro(String mensagem) {
                String m = mensagem.toLowerCase(Locale.ROOT);
                if (m.contains("bucket not found")) {
                    falhar(resposta, "O bucket \"salas\" não existe no Supabase (rode o supabase_imagens.sql).");
                } else if (m.contains("row-level security")) {
                    falhar(resposta, "O Supabase não deixou enviar a imagem (falta a política do bucket \"salas\").");
                } else {
                    falhar(resposta, mensagem);
                }
            }
        });
    }

    public void criarSala(String titulo, String descricao, String img, Resposta<String> resposta) {
        JSONObject args = new JSONObject();
        try {
            args.put("p_titulo", titulo);
            args.put("p_descricao", descricao);
            args.put("p_img", img == null ? JSONObject.NULL : img);
        } catch (JSONException e) {
            falhar(resposta, "Dados inválidos.");
            return;
        }

        Api.rpc("criar_sala", args, new Api.Resultado() {
            @Override
            public void sucesso(String corpo) {
                entregar(resposta, corpo.replace("\"", "").trim());
            }

            @Override
            public void erro(String mensagem) {
                falhar(resposta, mensagem);
            }
        });
    }

    public void salvarSala(String salaId, String titulo, String descricao,
                           List<String> moderadores, Resposta<Void> resposta) {
        JSONObject args = new JSONObject();
        try {
            JSONArray ids = new JSONArray();
            for (String id : moderadores) ids.put(id);

            args.put("p_sala", salaId);
            args.put("p_titulo", titulo);
            args.put("p_descricao", descricao);
            args.put("p_moderadores", ids);
        } catch (JSONException e) {
            falhar(resposta, "Dados inválidos.");
            return;
        }

        Api.rpc("salvar_sala", args, new Api.Resultado() {
            @Override
            public void sucesso(String corpo) {
                entregar(resposta, null);
            }

            @Override
            public void erro(String mensagem) {
                falhar(resposta, mensagem);
            }
        });
    }

    public void entrarComCodigo(String codigo, Resposta<String> resposta) {
        JSONObject args = new JSONObject();
        try {
            args.put("p_codigo", codigo);
        } catch (JSONException e) {
            falhar(resposta, "Dados inválidos.");
            return;
        }

        Api.rpc("entrar_na_sala", args, new Api.Resultado() {
            @Override
            public void sucesso(String corpo) {
                entregar(resposta, corpo.replace("\"", "").trim());
            }

            @Override
            public void erro(String mensagem) {
                falhar(resposta, mensagem);
            }
        });
    }

    public void registrarPagamento(Despesa despesa, byte[] fotoJpeg, Resposta<Void> resposta) {
        String caminho = despesa.getSalaId() + "/" + despesa.getDespesaId() + "/comprovante.jpg";

        Api.enviarArquivo("comprovantes", caminho, fotoJpeg, "image/jpeg", new Api.Resultado() {
            @Override
            public void sucesso(String corpo) {
                JSONObject args = new JSONObject();
                try {
                    args.put("p_despesa", despesa.getDespesaId());
                    args.put("p_comprovante", caminho);
                } catch (JSONException e) {
                    falhar(resposta, "Dados inválidos.");
                    return;
                }

                Api.rpc("registrar_pagamento", args, new Api.Resultado() {
                    @Override
                    public void sucesso(String corpo) {
                        entregar(resposta, null);
                    }

                    @Override
                    public void erro(String mensagem) {
                        falhar(resposta, mensagem);
                    }
                });
            }

            @Override
            public void erro(String mensagem) {
                falhar(resposta, mensagem);
            }
        });
    }

    public void sinalizarTransferencia(String despesaId, Resposta<Void> resposta) {
        JSONObject args = new JSONObject();
        try {
            args.put("p_despesa", despesaId);
        } catch (JSONException e) {
            falhar(resposta, "Dados inválidos.");
            return;
        }

        Api.rpc("sinalizar_transferencia", args, new Api.Resultado() {
            @Override
            public void sucesso(String corpo) {
                entregar(resposta, null);
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

    public void criarDespesa(
            String salaId,
            String responsavelId,
            String titulo,
            int totalCentavos,
            String vencimento,
            Set<String> contribuintes,
            Resposta<String> resposta
    ) {
        JSONObject args = new JSONObject();

        try {
            args.put("p_sala", salaId);
            args.put("p_responsavel", responsavelId);
            args.put("p_titulo", titulo);
            args.put("p_total_centavos", totalCentavos);
            args.put("p_vencimento", vencimento);

            JSONArray listaContribuintes = new JSONArray();

            for (String id : contribuintes) {
                listaContribuintes.put(id);
            }

            args.put("p_contribuintes", listaContribuintes);

        } catch (JSONException e) {
            falhar(resposta, "Dados inválidos.");
            return;
        }

        Api.rpc("criar_despesa", args, new Api.Resultado() {
            @Override
            public void sucesso(String corpo) {
                entregar(resposta, corpo.replace("\"", "").trim());
            }

            @Override
            public void erro(String mensagem) {
                falhar(resposta, mensagem);
            }
        });
    }
}