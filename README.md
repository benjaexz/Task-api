# 📋 Task API

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot 3.2.5](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg?style=flat-square&logo=docker)](https://www.docker.com/)
[![OpenAPI / Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D.svg?style=flat-square&logo=swagger)](http://localhost:8080/swagger-ui.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](https://opensource.org/licenses/MIT)

API RESTful para gerenciamento de tarefas desenvolvida com foco em boas práticas de arquitetura de software, desacoplamento por camadas (Controller, Service, Repository, DTO), persistência relacional com PostgreSQL conteinerizado e documentação viva com Swagger/OpenAPI.

---

## 🚀 Tecnologias e Ferramentas

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.2.5
- **Persistência & Dados:** Spring Data JPA, Hibernate, Driver PostgreSQL
- **Banco de Dados:** PostgreSQL 16 (via Docker Compose)
- **Validação de Dados:** Bean Validation (`jakarta.validation`)
- **Documentação de API:** SpringDoc OpenAPI 2.6.0 (Swagger UI)
- **Gerenciador de Dependências:** Maven Wrapper (`mvnw`)
- **Containerização:** Docker & Docker Compose

---

## 🏛️ Arquitetura e Padrões Adotados

- **Arquitetura em Camadas:** Separação estrita entre manipulação de requisições (`Controller`), regras de negócio (`Service`) e acesso a dados (`Repository`).
- **DTO Pattern (Data Transfer Object):** Desacoplamento entre entidades de banco de dados (`Tarefa`) e contratos de entrada/saída (`TarefaRequestDTO`, `TarefaResponseDTO`), prevenindo *mass assignment vulnerabilities*.
- **Paginação e Ordenação Dinâmica:** Uso nativo do `Pageable` do Spring Data para otimização de consultas e redução de payload de rede.
- **Tratamento Centralizado de Exceções:** `@RestControllerAdvice` e `ProblemDetail` (RFC 7807) para formatação consistente de respostas de erro e falhas de validação.
- **Persistência Segura e Isolada:** Ambiente de banco de dados executado em container isolado com volume persistente local.

---

## 📋 Pré-requisitos

Antes de iniciar, certifique-se de possuir instalado em sua máquina:

- **JDK 17** ou superior instalado e configurado no `PATH`
- **Docker** e **Docker Compose** instalados e em execução
- **Git**

---

## 🛠️ Como Executar a Aplicação

### 1. Clonar o repositório

```bash
git clone [https://github.com/benjaexz/Task-api.git](https://github.com/benjaexz/Task-api.git)
cd Tarefa-api
2. Subir o container do banco de dados (PostgreSQL)Execute o Docker Compose para inicializar a instância do PostgreSQL:Bashdocker compose up -d
Verifique se o contêiner está em execução: Bashdocker ps
3. Executar a aplicação Spring BootUtilizar o Maven Wrapper para compilar e iniciar o servidor:Bash./mvnw spring-boot:run
A aplicação está disponível em http://localhost:8080.📖 Documentação Interativa (Swagger UI)Com a aplicação em execução, acesse a documentação interativa e execute requisições de teste diretamente pelo navegador:📍 URL do Swagger UI: http://localhost:8080/swagger-ui.html📍 OpenAPI JSON: http://localhost:8080/v3/api-docs🛣️ Endpoints da APIMétodoEndpointDescriçãoGET/tarefasLista tarefas com página e ordenação (página, tamanho, classificação)GET/tarefas/{id}Busca os detalhes de uma tarifa por IDPOST/tarefasCria uma nova tarifa com validações nos camposPUT/tarefas/{id}Atualiza integralmente os dados de uma tarifa existenteDELETE/tarefas/{id}Remove uma tarifa da base de dados📬 Exemplos de RequisiçãoCriar Tarefa (POST/tarefas)Bashcurl -X POST http://localhost:8080/tarefas \
  -H "Tipo de conteúdo: application/json" \
  -d '{
    "titulo": "Estudar Spring Data JPA",
    "descricao": "Explorar mapas avançados e paginação",
    "concluido": falso
  }'
Resposta (201 Criado):JSON{
  "id": 1,
  "titulo": "Estudar Spring Data JPA",
  "descricao": "Explorar mapas avançados e paginação",
  "concluida": falso
}
Listar com Página e Ordenação (GET /tarefas)Bashcurl -s "http://localhost:8080/tarefas?page=0&size=5&sort=id,desc"
Resposta (200 OK):JSON{
  "conteúdo": [
    {
      "id": 1,
      "titulo": "Estudar Spring Data JPA",
      "descricao": "Explorar mapeamentos avançados e paginação",
      "concluida": false
    }
  ],
  "pageável": {
    "número da página": 0,
    "tamanho da página": 5,
    "classificar": {
      "classificado": verdadeiro,
      "vazio": falso,
      "não classificado": falso
    },
    "deslocamento": 0,
    "paginado": verdadeiro,
    "não paginado": falso
  },
  "totalPáginas": 1,
  "totalElementos": 1,
  "último": verdadeiro,
  "tamanho": 5,
  "número": 0,
  "classificar": {
    "classificado": verdadeiro,
    "vazio": falso,
    "não classificado": falso
  },
  "númeroDeElementos": 1,
  "primeiro": verdade,
  "vazio": falso
}
🧪 Estrutura de DiretóriosPlaintexttask-api/
├── origem/
│ ├── principal/
│ │ ├── java/com/exemplo/task_api/
│ │ │ ├── controlador/ # Endpoints REST e anotações do Swagger
│ │ │ ├── dto/ # Objetos de transferência de dados (Solicitação / Resposta)
│ │ │ ├── exceção/ # Manipulador global de exceções (@RestControllerAdvice)
│ │ │ ├── modelo/ # Entidades JPA mapeadas no banco relacional
│ │ │ ├── repositório/ # Interfaces Spring Data JPA
│ │ │ └── serviço/ # Regras de negócio e organização de operações
│ │ └── recursos/
│ │ └── application.properties # Parâmetros de conexão e dialeto PostgreSQL
│ └── teste/ # Estrutura preparada para testes de integração
├── docker-compose.yml # Definição do serviço PostgreSQL e volumes persistentes
├── pom.xml # Dependências do projeto Maven
└── LEIA-ME.md
    "não classificado": falso
