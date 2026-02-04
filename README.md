# GameVault

API REST para cadastro de catálogo de Genero e Plataformas de Jogos, desenvolvida com Java e Spring Boot.

## Sumário

- [Sobre o projeto](#sobre-o-projeto)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Configuração](#configuração)
- [Passo a Passo](#passo-a-passo)
- [Documentação da API](#documentação-da-api)
- [Endpoints](#endpoints)

## Sobre o projeto

Plataforma que permite aos usuários descobrir jogos disponíveis em diferentes plataformas. O projeto foi desenvolvido com foco em:

- Organização de conteúdo: categorização eficiente de Jogos
- Múltiplos serviços: integração com diversas plataformas de jogos
- Segurança: autenticação JWT para proteção dos endpoints

## Tecnologias

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-%23007ACC.svg?style=for-the-badge&logo=flyway&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=fff)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity&logoColor=fff)](#)


## Arquitetura

O projeto segue arquitetura em camadas:

`src/main/java/com/gameVault/`
- `config/` — Configurações do Spring e Security
- `controller/` — Controllers REST
- `entity/` — Entidades JPA
- `repository/` — Repositórios Spring Data
- `service/` — Regras de negócio
- `exception/` — Exceções customizadas
- `mapper/` — Conversão entre DTOs e entidades

## Funcionalidades

Autenticação e autorização
- Login e registro de usuários
- Autenticação JWT
- Proteção de rotas por perfil de usuário

Gerenciamento de generos
- CRUD completo de generos de jogos
- Validação de dados
- Tratamento de dependências

Serviços de plataformas
- Cadastro e gestão de provedores
- Associação com jogos
- Validações de integridade

Catálogo de Jogos
- Cadastro detalhado de Jogos
- Busca por múltiplos critérios
- Associação com  generos e plataformas
- Sistema de avaliação

## Configuração

Scripts disponíveis:

- `build.sh` — Compila o projeto e gera o arquivo JAR
- `start.sh` — Inicia a aplicação usando o JAR gerado

### Passo a passo

1. Clone o repositório:

```
git clone [url-do-repositorio]
```

2. Configuração do banco de dados (exemplo PostgreSQL):

```
spring.datasource.url=jdbc:postgresql://localhost:5432/gamevault
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

3. Build do projeto (Unix):

```
./build.sh
```

ou, em Windows (PowerShell/CMD):

```
./mvnw clean package
```

4. Iniciar a aplicação (Unix):

```
./start.sh
```

ou, em Windows:

```
java -jar target/<arquivo>.jar
```

A API ficará disponível em `http://localhost:8080`.

## Documentação da API

Para testar a API, acesse o Swagger em:

```
http://localhost:8080/swagger-ui/index.html
```

### Endpoints

Autenticação

- `POST /gamevault/auth/registrar` — Registrar novo usuário
- `POST /gamevault/auth/login` — Login de usuário

Generos

- `POST /gamevault/genero` — Criar genero
- `GET /gamevault/genero` — Listar generos
- `GET /gamevault/genero/{id}` — Buscar genero por ID
- `DELETE /gamevault/genero/{id}` — Deletar genero

Serviços de Plataformas

- `POST /gamevault/plataforma` — Criar serviço de plataforma
- `GET /gamevault/plataforma` — Listar serviços de plataformas
- `GET /gamevault/plataforma/{id}` — Buscar plataforma por ID
- `DELETE /gamevault/plataforma/{id}` — Deletar plataforma

Jogos

- `POST /gamevault/jogo` — Criar jogo
- `GET /gamevault/jogo` — Listar jogos
- `GET /gamevault/jogo/{id}` — Buscar jogo por ID
- `GET /gamevault/jogo/search?genero={id}` — Buscar jogos por genero
- `PUT /gamevault/jogo` — Atualizar jogo
- `DELETE /gamevault/jogo/{id}` — Deletar jogo