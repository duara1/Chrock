# CHROK 📱

Aplicativo Android para gerenciamento de despesas compartilhadas, desenvolvido como projeto acadêmico de Engenharia de Software da FUCAPI.

O CHROK busca facilitar a organização financeira entre pessoas que dividem despesas, como colegas de apartamento, permitindo criar contas compartilhadas, organizar pagamentos e acompanhar as obrigações financeiras de cada participante.

## 🎯 Objetivo

Desenvolver um aplicativo móvel que simplifique a divisão e o acompanhamento de despesas compartilhadas, oferecendo mais transparência sobre os valores devidos, os pagamentos realizados e as responsabilidades de cada participante.

## ✨ Funcionalidades

O projeto está organizado em quatro módulos principais:

### 1. Acesso e gerenciamento de salas
- Cadastro e login de usuários.
- Cadastro e edição de chave Pix.
- Criação de salas compartilhadas.
- Geração e utilização de códigos únicos para entrada nas salas.
- Gerenciamento de participantes.
- Promoção de participantes a moderadores.
- Consulta de participantes e códigos de acesso conforme as permissões.

### 2. Gerenciamento de contas
- Visualização das contas cadastradas.
- Criação e edição de contas.
- Definição de responsáveis e colaboradores.
- Divisão igualitária de despesas.
- Cálculo do valor individual de cada participante.
- Consulta da chave Pix do responsável.
- Controle de permissões para edição.
- Notificações sobre adição e edição de contas.

### 3. Acompanhamento de pagamentos
- Visualização de pagamentos pendentes.
- Sinalização de pagamentos realizados.
- Confirmação de pagamentos.
- Contestação de pagamentos.
- Acompanhamento dos estados de cada pagamento.
- Registro de comprovantes e contas originais.
- Notificações relacionadas a pagamentos, contestações e prazos.

### 4. Histórico e acompanhamento financeiro
- Resumo financeiro pessoal.
- Resumo financeiro da sala.
- Histórico de pagamentos por conta.
- Histórico financeiro pessoal.
- Histórico financeiro da sala.
- Central de notificações.

## 🛠️ Tecnologias utilizadas

- **Linguagem:** Java
- **Plataforma:** Android
- **Interface:** XML para layouts Android
- **Ambiente de desenvolvimento:** Android Studio
- **Sistema de build:** Gradle

## 📱 Interface do aplicativo

A interface foi planejada para permitir que os usuários visualizem suas despesas individuais e as despesas da sala, identifiquem pagamentos pendentes e acompanhem os valores envolvidos nas transferências.

As telas do projeto incluem acesso à conta, criação e gerenciamento de salas, contas compartilhadas, acompanhamento de pagamentos e histórico financeiro.

## 🚀 Como executar o projeto

### Pré-requisitos

- Android Studio instalado.
- JDK compatível com a versão do Gradle utilizada pelo projeto.
- Android SDK correspondente à configuração do projeto.
- Emulador Android ou dispositivo físico com depuração USB habilitada.

### Instalação

1. Clone o repositório:

   ```bash
   git clone <URL_DO_REPOSITORIO>
   ```

2. Abra o Android Studio.

3. Selecione **Open** e escolha a pasta do projeto clonado.

4. Aguarde a sincronização do Gradle e a instalação das dependências necessárias.

5. Selecione um emulador ou conecte um dispositivo Android.

6. Execute o aplicativo pelo botão **Run** do Android Studio.

> Substitua `<URL_DO_REPOSITORIO>` pelo endereço real do repositório Git.

## 📂 Estrutura do projeto

A estrutura principal segue a organização convencional de projetos Android:

```text
CHROK/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/chrock/
│           │       ├── MainActivity.java
│           │       └── ...
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   └── ...
│           └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
└── README.md
```

*Observação: a estrutura acima é ilustrativa. Os arquivos e diretórios podem variar conforme a organização atual do repositório.*

## 🧪 Testes

O projeto prevê a verificação das funcionalidades de cadastro e autenticação, gerenciamento de salas, criação e divisão de contas, transições de estados de pagamento, permissões de usuários, histórico e notificações.

Os testes devem verificar se os resultados apresentados correspondem às regras de negócio definidas para cada funcionalidade.

## 🎓 Contexto acadêmico

**Instituição:** FUCAPI  
**Curso:** Engenharia de Software  
**Evento:** 13º Encontro Regional de Engenharia (ENCOENG 2026)  
**Área temática:** Programação para dispositivos móveis

### Integrantes

- Luara Duarte
- Isaque Marinho
- Marcos Paulo
- Yasmin Rocha

## 📌 Status do projeto

Projeto acadêmico em desenvolvimento, com funcionalidades organizadas por módulos e evolução incremental da aplicação Android.

---

**CHROK**: mais organização e transparência na divisão de despesas compartilhadas.
