# Sistema de Gestão de Pizzaria

Projeto web desenvolvido em **Java 17** com **Spring Boot**, voltado para o gerenciamento de uma pizzaria. O sistema permite que clientes realizem pedidos pela plataforma e que administradores acompanhem pedidos, gerenciem pizzas, ingredientes, usuários e visualizem informações gerenciais por meio de dashboard.

## Sobre o projeto

O Sistema de Gestão de Pizzaria foi criado para centralizar o fluxo de pedidos de uma pizzaria, evitando problemas comuns como pedidos descentralizados, falhas de comunicação, divergências de preços, falta de controle de estoque e ausência de dados para tomada de decisão.

A aplicação possui dois tipos principais de usuários:

- **CLIENTE**: pode visualizar pizzas, escolher tamanho, adicionar itens ao carrinho, finalizar pedidos e acompanhar o histórico/status dos pedidos.
- **ADMIN**: pode gerenciar pizzas, ingredientes, usuários, pedidos e visualizar dashboards com informações importantes do negócio.

## Funcionalidades

### Cliente

- Cadastro e login de usuário.
- Visualização do cardápio de pizzas.
- Consulta dos ingredientes de cada pizza.
- Escolha do tamanho da pizza.
- Adição de pizzas ao carrinho.
- Alteração de quantidade no carrinho.
- Escolha da forma de pagamento.
- Finalização do pedido.
- Consulta do histórico de pedidos e seus status.

### Administrador

- Login com perfil administrativo.
- Cadastro de novos administradores com código secreto de validação.
- Gerenciamento de pizzas.
- Gerenciamento de ingredientes.
- Gerenciamento de usuários.
- Visualização de todos os pedidos realizados.
- Aceite ou rejeição de pedidos.
- Dashboard com gráficos gerenciais, como:
  - pizzas mais vendidas;
  - clientes com mais pedidos;
  - ingredientes mais utilizados.

## Tecnologias utilizadas

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring MVC**
- **Spring Data JPA**
- **Spring Security**
- **Thymeleaf**
- **Bootstrap 5**
- **H2 Database**
- **BCrypt**
- **Maven**
- **Chart.js**

## Arquitetura do projeto

O projeto segue uma arquitetura em camadas, separando responsabilidades para facilitar manutenção e organização do código.

```text
src/main/java
 └── ...
     ├── config        # Configurações do sistema e segurança
     ├── controller    # Controllers MVC e REST
     ├── model         # Entidades JPA
     ├── repository    # Interfaces de acesso ao banco de dados
     ├── service       # Regras de negócio
     └── SistemaPizzariaApplication.java

src/main/resources
 ├── templates         # Páginas HTML com Thymeleaf
 ├── static            # Arquivos CSS, JS e imagens
 └── application.properties
```

## Principais entidades

- **Usuario**: representa os usuários do sistema, podendo ser CLIENTE ou ADMIN.
- **Pedido**: representa os pedidos realizados pelos clientes.
- **ItemPedido**: representa os itens dentro de um pedido, armazenando quantidade e preço no momento da compra.
- **Pizza**: representa as pizzas disponíveis no cardápio.
- **Ingrediente**: representa os ingredientes utilizados nas pizzas.
- **PizzaTamanho**: representa os tamanhos disponíveis para cada pizza e seus respectivos preços.

## Regras de negócio importantes

- O sistema possui controle de acesso por perfil: **ADMIN** e **CLIENTE**.
- Apenas usuários com perfil **CLIENTE** podem acessar o carrinho e finalizar pedidos.
- Apenas usuários com perfil **ADMIN** podem acessar o dashboard e áreas de gerenciamento.
- O cadastro de administradores exige um código secreto.
- As senhas são armazenadas com criptografia usando **BCrypt**.
- Pedidos possuem controle de status, como **PENDENTE**, **CONFIRMADO** e **REJEITADO**.
- Pizzas que já possuem histórico de pedidos não devem ser excluídas fisicamente, garantindo a integridade dos dados.

## Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

- Java 17 ou superior
- Maven
- Git

## Como executar o projeto

1. Clone o repositório:

```bash
git clone https://github.com/BiSousa/sistema-pizzaria-java.git
```

2. Acesse a pasta do projeto:

```bash
cd sistema-pizzaria-java
```

3. Execute a aplicação com Maven:

```bash
mvn spring-boot:run
```

Caso o projeto possua o Maven Wrapper, também é possível executar com:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

4. Acesse a aplicação no navegador:

```text
http://localhost:8080/
```

## Banco de dados

O projeto utiliza o banco de dados **H2** em arquivo local, configurado no arquivo:

```text
src/main/resources/application.properties
```

Caso queira acessar o console do H2, verifique no `application.properties` se o console está habilitado e utilize as credenciais configuradas no próprio arquivo.

## Segurança

O sistema utiliza **Spring Security** para proteger as rotas da aplicação.

Exemplos de regras aplicadas:

- Rotas de carrinho e criação de pedidos são acessíveis apenas por usuários com perfil **CLIENTE**.
- Rotas de dashboard, gerenciamento e API administrativa são acessíveis apenas por usuários com perfil **ADMIN**.
- Senhas são criptografadas antes de serem salvas no banco de dados.

## Testes

O projeto contempla testes para validar regras importantes do sistema, como:

- Fechamento de pedido com sucesso.
- Registro de pedido com status inicial correto.
- Integridade dos itens do pedido.
- Bloqueio de exclusão de pizzas que já possuem histórico de pedidos.

Para executar os testes, utilize:

```bash
mvn test
```

## Status do projeto

Projeto desenvolvido como atividade acadêmica, com foco em:

- aplicação web com Java e Spring Boot;
- autenticação e autorização por perfil;
- persistência de dados com JPA/H2;
- interface responsiva com Thymeleaf e Bootstrap;
- gerenciamento de pedidos, pizzas, ingredientes e usuários;
- dashboard administrativo.

## Integrantes

- Beatriz Carvalho Sousa — RA: 082230027
- Yuri Villaço de Souza — RA: 082230036

## Repositório

Acesse o projeto no GitHub:

```text
https://github.com/BiSousa/sistema-pizzaria-java/tree/main
```

