
-- Chrock - Modulo de notificacoes
-- Dependencias: profiles, salas, despesas, contribuicoes,
-- sala_membros e private.e_membro(uuid).

BEGIN;

CREATE TABLE IF NOT EXISTS public.notificacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    usuario_id UUID NOT NULL
        REFERENCES public.profiles(id) ON DELETE CASCADE,

    sala_id UUID NOT NULL
        REFERENCES public.salas(id) ON DELETE CASCADE,

    despesa_id UUID
        REFERENCES public.despesas(id) ON DELETE SET NULL,

    titulo TEXT NOT NULL,
    mensagem TEXT NOT NULL,
    tipo TEXT NOT NULL,

    lida BOOLEAN NOT NULL DEFAULT FALSE,
    criada_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    chave_evento TEXT UNIQUE,

    CONSTRAINT notificacoes_tipo_check CHECK (
        tipo IN (
            'conta_criada',
            'vencimento_proximo',
            'conta_vencida',
            'transferencia_sinalizada',
            'transferencia_confirmada',
            'transferencia_contestada'
        )
    )
);

CREATE INDEX IF NOT EXISTS idx_notificacoes_usuario_sala
ON public.notificacoes (
    usuario_id,
    sala_id,
    criada_em DESC
);

CREATE INDEX IF NOT EXISTS idx_notificacoes_nao_lidas
ON public.notificacoes (
    usuario_id,
    sala_id
)
WHERE lida = FALSE;

ALTER TABLE public.notificacoes
ENABLE ROW LEVEL SECURITY;

REVOKE ALL ON public.notificacoes
FROM PUBLIC, anon, authenticated;

GRANT SELECT ON public.notificacoes
TO authenticated;

GRANT UPDATE (lida) ON public.notificacoes
TO authenticated;

DROP POLICY IF EXISTS notificacoes_consultar_proprias
ON public.notificacoes;

CREATE POLICY notificacoes_consultar_proprias
ON public.notificacoes
FOR SELECT
TO authenticated
USING (
    usuario_id = (SELECT auth.uid())
    AND private.e_membro(sala_id)
);

DROP POLICY IF EXISTS notificacoes_atualizar_proprias
ON public.notificacoes;

CREATE POLICY notificacoes_atualizar_proprias
ON public.notificacoes
FOR UPDATE
TO authenticated
USING (
    usuario_id = (SELECT auth.uid())
    AND private.e_membro(sala_id)
)
WITH CHECK (
    usuario_id = (SELECT auth.uid())
    AND private.e_membro(sala_id)
);


REVOKE ALL ON FUNCTION
public.gerar_notificacao_contribuicao()
FROM PUBLIC, anon, authenticated;

REVOKE ALL ON FUNCTION
public.gerar_lembretes_vencimento(UUID)
FROM PUBLIC, anon;

REVOKE ALL ON FUNCTION
public.marcar_notificacao_lida(UUID)
FROM PUBLIC, anon;

REVOKE ALL ON FUNCTION
public.marcar_todas_notificacoes_lidas(UUID)
FROM PUBLIC, anon;

GRANT EXECUTE ON FUNCTION
public.gerar_lembretes_vencimento(UUID)
TO authenticated;

GRANT EXECUTE ON FUNCTION
public.marcar_notificacao_lida(UUID)
TO authenticated;

GRANT EXECUTE ON FUNCTION
public.marcar_todas_notificacoes_lidas(UUID)
TO authenticated;

DROP TRIGGER IF EXISTS trg_notificacao_contribuicao
ON public.contribuicoes;

CREATE TRIGGER trg_notificacao_contribuicao
AFTER INSERT OR UPDATE OF status
ON public.contribuicoes
FOR EACH ROW
EXECUTE FUNCTION public.gerar_notificacao_contribuicao();

COMMIT;

