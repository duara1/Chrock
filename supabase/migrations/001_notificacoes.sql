
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


-- ==========================================
-- FUNCOES DO MODULO DE NOTIFICACOES
-- ==========================================

CREATE OR REPLACE FUNCTION public.gerar_lembretes_vencimento(p_sala uuid)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO ''
AS $function$
DECLARE
    v_usuario UUID := auth.uid();
    v_hoje DATE := (NOW() AT TIME ZONE 'America/Manaus')::DATE;
BEGIN
    IF v_usuario IS NULL THEN
        RAISE EXCEPTION 'Usuário não autenticado.';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM public.sala_membros sm
        WHERE sm.sala_id = p_sala
        AND sm.usuario_id = v_usuario
    ) THEN
        RAISE EXCEPTION 'Sem acesso à sala.';
    END IF;

    INSERT INTO public.notificacoes (
        usuario_id,
        sala_id,
        despesa_id,
        titulo,
        mensagem,
        tipo,
        chave_evento
    )
    SELECT
        v_usuario,
        d.sala_id,
        d.id,

        CASE
            WHEN d.vencimento < v_hoje
                THEN 'Despesa vencida'
            ELSE 'Vencimento próximo'
        END,

        CASE
            WHEN d.vencimento < v_hoje
                THEN 'A despesa ' || d.titulo
                     || ' está vencida.'
            ELSE 'A despesa ' || d.titulo
                 || ' vence em '
                 || TO_CHAR(d.vencimento, 'DD/MM/YYYY')
                 || '.'
        END,

        CASE
            WHEN d.vencimento < v_hoje
                THEN 'conta_vencida'
            ELSE 'vencimento_proximo'
        END,

        CONCAT(
            v_usuario::TEXT,
            ':',
            d.id::TEXT,
            ':',
            d.vencimento::TEXT,
            ':',
            CASE
                WHEN d.vencimento < v_hoje
                    THEN 'vencida'
                ELSE 'proxima'
            END
        )

    FROM public.despesas d
    JOIN public.contribuicoes c
        ON c.despesa_id = d.id

    WHERE d.sala_id = p_sala
    AND c.usuario_id = v_usuario
    AND c.status IN ('PENDENTE', 'CONTESTADO')
    AND d.vencimento <= v_hoje + 3
    AND (
        d.comprovante IS NULL
        OR BTRIM(d.comprovante) = ''
    )

    ON CONFLICT (chave_evento)
    DO NOTHING;
END;
$function$;


CREATE OR REPLACE FUNCTION public.gerar_notificacao_contribuicao()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path TO ''
AS $function$
DECLARE
    v_sala UUID;
    v_responsavel UUID;
    v_titulo_despesa TEXT;

    v_destinatario UUID;
    v_titulo TEXT;
    v_mensagem TEXT;
    v_tipo TEXT;
BEGIN
    SELECT
        d.sala_id,
        d.responsavel_id,
        d.titulo
    INTO
        v_sala,
        v_responsavel,
        v_titulo_despesa
    FROM public.despesas d
    WHERE d.id = NEW.despesa_id;

    IF NOT FOUND THEN
        RETURN NEW;
    END IF;

    IF TG_OP = 'INSERT' THEN

        v_destinatario := NEW.usuario_id;
        v_titulo := 'Nova despesa';
        v_mensagem :=
            'Você foi incluído na despesa '
            || v_titulo_despesa || '.';
        v_tipo := 'conta_criada';

    ELSIF TG_OP = 'UPDATE' THEN

        IF OLD.status IS NOT DISTINCT FROM NEW.status THEN
            RETURN NEW;
        END IF;

        CASE NEW.status

            WHEN 'SINALIZADO' THEN

                v_destinatario := v_responsavel;
                v_titulo := 'Transferência sinalizada';
                v_mensagem :=
                    'Um participante sinalizou uma transferência para '
                    || v_titulo_despesa || '.';
                v_tipo := 'transferencia_sinalizada';

            WHEN 'CONFIRMADO' THEN

                v_destinatario := NEW.usuario_id;
                v_titulo := 'Transferência confirmada';
                v_mensagem :=
                    'Sua contribuição para '
                    || v_titulo_despesa
                    || ' foi confirmada.';
                v_tipo := 'transferencia_confirmada';

            WHEN 'CONTESTADO' THEN

                v_destinatario := NEW.usuario_id;
                v_titulo := 'Transferência contestada';
                v_mensagem :=
                    'Sua contribuição para '
                    || v_titulo_despesa
                    || ' foi contestada.';
                v_tipo := 'transferencia_contestada';

            ELSE
                RETURN NEW;

        END CASE;

    ELSE
        RETURN NEW;
    END IF;

    IF v_destinatario IS NULL THEN
        RETURN NEW;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM public.sala_membros sm
        WHERE sm.sala_id = v_sala
        AND sm.usuario_id = v_destinatario
    ) THEN
        RETURN NEW;
    END IF;

    INSERT INTO public.notificacoes (
        usuario_id,
        sala_id,
        despesa_id,
        titulo,
        mensagem,
        tipo
    )
    VALUES (
        v_destinatario,
        v_sala,
        NEW.despesa_id,
        v_titulo,
        v_mensagem,
        v_tipo
    );

    RETURN NEW;
END;
$function$;


CREATE OR REPLACE FUNCTION public.marcar_notificacao_lida(p_notificacao uuid)
RETURNS void
LANGUAGE plpgsql
SET search_path TO ''
AS $function$
BEGIN
    UPDATE public.notificacoes
    SET lida = TRUE
    WHERE id = p_notificacao
    AND usuario_id = auth.uid();
END;
$function$;


CREATE OR REPLACE FUNCTION public.marcar_todas_notificacoes_lidas(p_sala uuid)
RETURNS void
LANGUAGE plpgsql
SET search_path TO ''
AS $function$
BEGIN
    UPDATE public.notificacoes
    SET lida = TRUE
    WHERE usuario_id = auth.uid()
    AND sala_id = p_sala
    AND lida = FALSE;
END;
$function$;




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

