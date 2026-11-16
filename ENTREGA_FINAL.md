# Chrock - entrega consolidada da branch Yasmin

## Funcionalidades
- Histórico pessoal com filtro por mês/ano e status, consultando o repositório compartilhado.
- Histórico por conta com participantes, status individuais e referência de comprovante.
- Histórico da sala com contribuições e contagem de status.
- Menu temporário de testes com usuário u1 e sala s1.
- Resumos e notificações legados permanecem dependentes de DadosManager e precisam de integração futura.

## Limitações importantes
- `RepositorioFalso` guarda dados demonstrativos apenas em memória, não é banco persistente.
- Status CONFIRMADO não comprova data ou valor efetivamente pago.
- O nome de arquivo de comprovante é apenas uma referência, não garante arquivo disponível.
- Esta entrega NÃO foi validada em emulador nem por compilação Gradle neste ambiente.
- Não foi feito merge com a main.

## Verificação no Windows
1. Abra a pasta do projeto no Android Studio.
2. Execute `gradlew.bat :app:assembleDebug`.
3. Execute `gradlew.bat :app:testDebugUnitTest`.
4. Teste o menu temporário e abra os históricos pessoal, por conta e da sala.

## Commits sugeridos
1. `refactor(historico): conecta detalhes de contas ao repositorio compartilhado`
2. `feat(historico): exibe status das contribuicoes da sala`
3. `docs(chrock): documenta integracao e limitacoes da entrega`

Faça cada commit selecionando somente os arquivos correspondentes.
