# Integração 04: preparação do histórico

Inclui os modelos e o repositório da branch `feature-tela-principal`, uma consulta de despesas por sala e um adaptador somente de leitura para o modelo legado `Conta`.

**Ainda não altera as Activities**: elas continuam usando `DadosManager` e os dados de demonstração. O próximo commit deve conectar a tela com um `usuarioId` e `salaId` reais, definidos pela navegação/autenticação.

A contribuição `SINALIZADO` não é pagamento confirmado. O modelo compartilhado não fornece data de pagamento individual; portanto o adaptador não fabrica objetos `Pagamento`.

Execute `./gradlew testDebugUnitTest` e `./gradlew assembleDebug` no ambiente Android Studio antes de commitar.
