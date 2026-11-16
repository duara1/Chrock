# Integração 05: histórico pessoal

- `HistoricoActivity` consulta `RepositorioFalso` por meio de `HistoricoRepositorioAdapter`.
- A tela recebe `usuario_id` e `sala_id` via Intent; valida a existência do usuário, sala e associação.
- Cada conta mostra o status da contribuição do usuário, e não o status global da despesa.
- Não são criados registros de pagamentos fictícios: o repositório compartilhado ainda não armazena data/valor individual pago.
- O menu temporário abre a demonstração com `u1` e `s1`; substituir pelos IDs reais ao integrar a navegação.

## Testes manuais
1. Abrir `Testar módulos > Histórico pessoal`: devem aparecer Água, Internet e Mercado para u1.
2. Água: contribuição pendente; Internet: pagamento sinalizado; Mercado: contribuição confirmada.
3. Abrir a Activity sem extras: deve aparecer mensagem de seleção de sala/usuário.
4. Confirmar que nenhuma tela mostra pagamentos individuais inventados.

## Validação
Código inspecionado; build Android e execução em emulador ainda não realizados.
