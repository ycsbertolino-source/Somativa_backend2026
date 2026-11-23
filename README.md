# Monitoramento de Máquinas Industriais — CloudTech Solutions

API REST em **Java 17 + Spring Boot 3** para cadastro, consulta, atualização e exclusão de máquinas industriais e de seus dados de produção (consumo em kW, horário de acionamento e peças produzidas).

O projeto aplica práticas de DevOps: versionamento com Git/GitHub, testes automatizados com JUnit 5 + MockMvc, integração contínua com GitHub Actions e deploy na nuvem com Railway.

## Tecnologias

- Java 17
- Spring Boot 3 (Spring Web)
- JUnit 5 + MockMvc
- Maven
- GitHub Actions (CI)
- Docker + Railway (deploy)

## Estrutura

```
monitoramento-maquinas/
├── src/
│   ├── main/java/br/com/cloudtech/monitoramento/
│   │   ├── MonitoramentoApplication.java
│   │   ├── models/        -> Maquina, Producao
│   │   ├── controller/    -> MaquinaController, ProducaoController, HomeController
│   │   └── repository/    -> MonitoramentoRepository (dados em memória)
│   └── test/java/...      -> testes JUnit 5 + MockMvc
├── .github/workflows/pipeline.yml
├── Dockerfile
├── pom.xml
├── README.md
└── .gitignore
```

## Endpoints

| Método | Endpoint | Descrição | Resposta |
|---|---|---|---|
| GET | `/` | Página inicial | 200 |
| GET | `/status` | Status da aplicação | 200 |
| GET | `/versao` | Versão da aplicação | 200 |
| GET | `/maquinas` | Lista as máquinas | 200 |
| GET | `/maquinas/{id}` | Busca uma máquina | 200 / 404 |
| POST | `/maquinas/cadastro` | Cadastra uma máquina | 201 / 400 |
| PUT | `/maquinas/{id}` | Atualiza uma máquina | 200 / 404 |
| DELETE | `/maquinas/{id}` | Exclui uma máquina | 204 / 404 |
| GET | `/producao` | Lista os registros de produção | 200 |
| GET | `/producao/{id}` | Busca um registro | 200 / 404 |
| GET | `/producao/historico/{maquinaId}` | Histórico e totais da máquina | 200 / 404 |
| POST | `/producao` | Registra produção | 201 / 404 |
| PUT | `/producao/{id}` | Atualiza um registro | 200 / 404 |
| DELETE | `/producao/{id}` | Exclui um registro | 204 / 404 |

### Exemplos de corpo (JSON)

Cadastro de máquina — `POST /maquinas/cadastro`
```json
{ "nome": "Máquina CNC 01", "tipo": "CNC", "setor": "Usinagem", "status": "em operação" }
```

Registro de produção — `POST /producao`
```json
{ "maquinaId": 1, "consumoKw": 12.5, "horarioAcionamento": "08:30", "pecasProduzidas": 150 }
```

## Como executar

```bash
mvn spring-boot:run        # sobe em http://localhost:8080
mvn test                   # executa os testes
mvn clean package          # gera target/monitoramento-maquinas.jar
java -jar target/monitoramento-maquinas.jar
```

## Integração contínua

O workflow `.github/workflows/pipeline.yml` roda a cada push/pull request: baixa o código, configura o Java 17, instala as dependências, compila, executa os testes e gera o JAR (disponível como artefato da execução).

## Deploy

Publicado na Railway a partir deste repositório (build pelo `Dockerfile`). A porta é lida da variável `PORT` fornecida pela Railway.

URL pública: `https://SEU-PROJETO.up.railway.app`
