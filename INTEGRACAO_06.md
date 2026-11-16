# Mini commit 06: filtros do histórico pessoal

- Filtros por mês de **vencimento** (não data de pagamento) e status individual da contribuição.
- Opções de período derivadas das contas do usuário na sala.
- Estado vazio quando nenhum registro corresponde aos filtros.
- Testes unitários para período, status e combinação sem resultados.

## Validação local (Windows)
`gradlew.bat :app:assembleDebug`
`gradlew.bat :app:testDebugUnitTest`

Na tela Histórico pessoal, selecionar Outubro/2026 e Confirmado deve mostrar Mercado no repositório de demonstração. Selecionar Outubro/2026 e Pendente deve mostrar estado vazio.

**Não foi executada compilação Android nesta preparação.**
