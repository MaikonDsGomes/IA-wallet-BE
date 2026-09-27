# InvestWallet API - Mapeamento de camadas

## 1. Visão geral

O `investwallet-api` é uma API REST em Spring Boot 3.3.6, compilada com Java 21. O projeto implementa funcionalidades de autenticação, usuários, ativos e carteira de investimentos, seguindo uma separação inspirada em arquitetura limpa/hexagonal:

```text
Cliente HTTP
    |
    v
Presentation (controllers, advice, documentação)
    |
    v
Application (casos de uso, DTOs e mapper)
    |
    v
Domain (modelos, contratos de repositório, serviços e exceções)
    |
    v
Infrastructure (JPA/MySQL, Redis, segurança e configurações)
```

A aplicação é inicializada por `com.wallet.md.MdApplication`. A configuração principal está em `src/main/resources/application.yml`.

## 2. Camada de apresentação

Localização: `src/main/java/com/wallet/md/presentation`

É a porta HTTP da aplicação. Recebe requisições, valida payloads, extrai o usuário autenticado, delega para casos de uso e padroniza as respostas.

### Controllers

| Controller | Base | Responsabilidade |
|---|---|---|
| `AuthController` | `/api/v1/auth` | Registro, login, logout e refresh token |
| `UsuarioController` | `/api/v1/usuarios` | Consulta, listagem paginada, atualização e exclusão de usuários |
| `CarteiraController` | `/api/v1/carteira` | Score, recomendações, posições, venda, ordens e movimentações |
| `AtivoController` | `/api/v1/ativos` | Catálogo, detalhe e histórico de cotações por código/ticker |
| `PublicController` | `/api/v1/public` | Health check e informações públicas da API |

`presentation/advice/GlobalExceptionHandler` converte exceções de domínio, autenticação, autorização, validação e integridade de dados em respostas HTTP padronizadas com `ApiResponse`.

`presentation/docs` contém documentação complementar para endpoints e `presentation/utils/AuthUtils` concentra a extração do identificador do usuário autenticado.

### Catálogo de ativos

- `GET /api/v1/ativos`: lista ativos ativos, com filtros opcionais `tipo` e `busca` por nome/código.
- `GET /api/v1/ativos/{simbolo}`: retorna os dados do ativo e sua cotação mais recente.
- `GET /api/v1/ativos/{simbolo}/cotacoes`: retorna o histórico de cotações ordenado da mais recente para a mais antiga.

### Recomendações

- `GET /api/v1/carteira/recomendacoes`: consulta as recomendações ativas persistidas para a carteira autenticada.
- `POST /api/v1/carteira/recomendacoes/atualizacao`: solicita a regeneração assíncrona e retorna `202 Accepted`; o frontend continua usando a última versão disponível durante o processamento.
- `POST /api/v1/carteira/posicoes/venda`: executa uma venda imediatamente, reduz a posição, registra ordem `VENDA/EXECUTADA`, credita a conta e registra uma movimentação `VENDA` na mesma transação.
- `POST /api/v1/carteira/posicoes/compra`: executa uma compra imediatamente, valida e debita o saldo, cria/aumenta a posição, registra ordem `COMPRA/EXECUTADA` e movimentação `COMPRA`.
- `GET /api/v1/carteira/movimentacoes`: lista o extrato da conta com o saldo acumulado após cada movimentação.
- `GET /api/v1/carteira/ordens`: lista as ordens da conta, incluindo ativo, tipo, quantidade, preço e status.

O primeiro motor de recomendações é determinístico e usa o score da carteira, o perfil de risco, a volatilidade, a concentração e a diversificação. Isso evita que o caminho de leitura dependa de um modelo generativo. A tabela `recomendacao` mantém o histórico, enquanto somente os registros ativos são exibidos.

`RecomendacoesScheduler` dispara a atualização semanal para todas as carteiras. O desenho permite adicionar eventos de alteração de posição/perfil e um adaptador de IA posteriormente, sem alterar o contrato de consulta. Recomendações são educacionais e não executam ordens nem representam indicação direta de compra ou venda.

#### Personalização opcional com Gemini

O `GerarRecomendacoesCarteiraUseCase` primeiro cria recomendações determinísticas e depois delega somente a melhoria textual para `RecommendationTextProvider`. O adapter atual é `GeminiRecommendationTextProvider`; outro provedor pode ser adicionado implementando essa interface, sem alterar controller, persistência ou regras de negócio.

O Gemini é configurado por variáveis de ambiente:

```powershell
$env:GEMINI_ENABLED="true"
$env:GEMINI_API_KEY="sua-chave"
$env:GEMINI_MODEL="gemini-2.0-flash"
```

O padrão é desabilitado (`GEMINI_ENABLED=false`), mantendo os textos determinísticos. A chamada usa `responseMimeType=application/json` e um schema com `titulo`, `descricao`, `categoria` e `prioridade`. A resposta é validada, rejeita indicação direta de compra/venda e, em caso de timeout, erro ou JSON inválido, o sistema persiste o texto determinístico original.

## 3. Camada de aplicação

Localização: `src/main/java/com/wallet/md/application`

Orquestra os casos de uso sem expor detalhes de HTTP ou de implementação do banco.

### Casos de uso

- **Usuário/autenticação:** `RegisterUsuarioUseCase`, `AuthenticateUsuarioUseCase`, `RefreshTokenUseCase`, `LogoutUseCase`, `GetUsuarioUseCase`, `ListUsuariosUseCase`, `UpdateUsuarioUseCase` e `DeleteUsuarioUseCase`.
- **Carteira:** `PostPosicaoUseCase`, `PatchPosicaoUseCase`, `DeletePosicaoUseCase`, `VenderPosicaoUseCase`, `GetPosicoesCarteiraUseCase`, `GetMovimentacoesContaUseCase`, `GetOrdensContaUseCase`, `GetCarteiraKpisUseCase`, `GetScoreCarteiraUseCase`, `GetRecomendacoesCarteiraUseCase`, `GerarRecomendacoesCarteiraUseCase` e `SolicitarAtualizacaoRecomendacoesUseCase`.
- **Ativos:** `GetAtivosPorTicket`.

### DTOs e conversão

- `dto/request`: contratos de entrada de autenticação, usuário, carteira e análise de IA.
- `dto/response`: contratos de saída, incluindo respostas de autenticação, usuários, posições, KPIs, score e análise de IA.
- `mapper/UsuarioMapper`: conversão entre o modelo de domínio e os DTOs de usuário, usando MapStruct.

Os casos de uso são registrados manualmente em `infrastructure/config/UseCaseConfig`, permitindo que dependam das interfaces do domínio.

## 4. Camada de domínio

Localização: `src/main/java/com/wallet/md/domain`

Representa o vocabulário e as regras centrais do negócio, mantendo contratos independentes dos frameworks de persistência.

### Modelos

Os principais modelos são `Usuario`, `Conta`, `Carteira`, `Ativo`, `Posicao`, `Cotacao`, `Ordem`, `Movimentacao`, `PerfilRisco`, `ScoreCarteira`, `RiscoAlerta`, `Recomendacao`, `Noticia`, `NoticiaAtivo` e `NoticiaCacheUsuario`.

### Enums

`TipoAtivo`, `TipoMovimentacao`, `TipoOrdem`, `StatusOrdem`, `NivelRisco`, `Volatilidade`, `CategoriaRecomendacao`, `Prioridade`, `Sentimento` e `Role` formalizam estados e classificações do negócio.

### Contratos e serviços

- `domain/repository`: interfaces de acesso a usuários, tokens, contas, carteiras, ativos, cotações, posições, perfis de risco e scores.
- `domain/service/PasswordEncoderService`: abstração para hash e verificação de senhas.
- `domain/service/TokenProvider`: abstração para geração, validação e leitura de tokens.
- `domain/exception`: exceções de negócio e de infraestrutura expostas de forma controlada pela camada de apresentação.

## 5. Camada de infraestrutura

Localização: `src/main/java/com/wallet/md/infrastructure`

Implementa as dependências externas e conecta os contratos do domínio aos frameworks e serviços executáveis.

### Persistência

- `persistence/entity`: entidades JPA correspondentes às tabelas do banco.
- `persistence/repository`: interfaces Spring Data JPA e consultas JPQL, incluindo projections para posições detalhadas.
- `persistence/adapter`: adaptadores que implementam as interfaces de `domain/repository`, fazem a conversão Entity/Domain e encapsulam transações.
- `persistence/projection`: projeções específicas para consultas de carteira.

O banco principal é MySQL. O Hibernate está configurado com `ddl-auto: none`; a criação/evolução do schema fica a cargo do Flyway:

- `V1__create_schema_investwallet.sql`: schema inicial e relacionamentos.
- `V2__mock_data.sql`: dados de demonstração.
- `V3__add_codigo_ativo.sql`: inclusão do código/ticker em `ativo`.

### Segurança

- `SecurityConfig`: API stateless, CSRF desabilitado, rotas públicas e regras de autorização.
- `JwtAuthenticationFilter`: lê o Bearer token, consulta a blacklist e popula o `SecurityContext`.
- `JwtTokenProvider`: implementação do contrato de tokens JWT.
- `CustomUserDetailsService`: carrega usuários para o Spring Security.
- `BcryptPasswordEncoderAdapter`: implementação de hash de senha.
- `RedisTokenBlacklistRepository`: persiste tokens revogados no Redis com TTL.
- `JwtAuthenticationEntryPoint`: resposta para falhas de autenticação.

### Configurações

- `OpenApiConfig`: Swagger/OpenAPI.
- `RedisConfig`: integração com Redis.
- `WebConfig`: configurações web, como CORS.
- `UseCaseConfig`: composição dos casos de uso.

## 6. Dados e dependências externas

| Componente | Uso | Configuração |
|---|---|---|
| MySQL | Persistência transacional | `spring.datasource` em `application.yml` |
| Flyway | Migrações versionadas | `classpath:db/migration` |
| Redis 7 Alpine | Blacklist de tokens JWT | `compose.yaml`, porta `6379` |
| Spring Security + JWT | Autenticação/autorização stateless | `SecurityConfig` e pacote `security` |
| Swagger/OpenAPI | Documentação interativa | `/swagger-ui.html` e `/api-docs` |

O `compose.yaml` provisiona somente o Redis. O MySQL precisa estar disponível separadamente no endereço configurado.

## 7. Fluxos principais

### Autenticação

1. `AuthController` recebe registro ou login.
2. O caso de uso consulta `UsuarioRepository` e usa `PasswordEncoderService`.
3. `TokenProvider` gera access token e refresh token.
4. No logout, o token é colocado na blacklist do Redis até expirar.
5. Nas requisições protegidas, `JwtAuthenticationFilter` valida o token, verifica a blacklist e carrega o usuário.

### Consulta/alteração de carteira

1. `CarteiraController` extrai o usuário via `AuthUtils`.
2. O caso de uso localiza `Conta` e `Carteira` pelos repositórios de domínio.
3. O adaptador chama o repositório JPA e converte o resultado para domínio/projection.
4. Operações de posição recalculam quantidade e preço médio quando aplicável.
5. A resposta é convertida para DTO e encapsulada em `ApiResponse`.

### Venda e histórico financeiro

1. `VenderPosicaoUseCase` valida conta, carteira, posição e quantidade disponível.
2. A posição é reduzida ou removida sem alterar o preço médio remanescente.
3. A ordem executada é registrada, o saldo é creditado pelo valor bruto e a movimentação é persistida como `VENDA`.
4. A operação é transacional; falhas interrompem a persistência do conjunto.
5. O extrato ordenado por data calcula `saldoApos` acumulando depósitos e vendas como créditos, e compras e saques como débitos. Esse histórico permite construir gráficos de saldo sem depender do estado atual da conta.

## 8. Organização de diretórios

```text
src/
├── main/java/com/wallet/md/
│   ├── application/       # casos de uso, DTOs e mappers
│   ├── domain/            # regras, modelos, contratos e exceções
│   ├── infrastructure/   # JPA, MySQL, Redis, JWT e configurações
│   └── presentation/      # API HTTP, tratamento de erros e docs
├── main/resources/
│   ├── application.yml
│   └── db/migration/
└── test/java/             # testes automatizados
```

## 9. Estado observado e pontos de atenção

- O projeto possui um teste de contexto em `MdApplicationTests`; `mvnw verify` valida a suíte e gera o relatório JaCoCo.
- A configuração versionada contém credenciais e segredo JWT diretamente em `application.yml`; para ambientes compartilhados/produção, esses valores devem ser externalizados por variáveis de ambiente ou secret manager.
- Há casos de uso e DTOs de análise de IA, recomendações e KPIs presentes na estrutura, mas nem todos possuem controller/integração externa correspondente no recorte atual.
- Existem repositórios de `ativo` em mais de um subpacote (`domain/repository/ativo` e `domain/repository/carteira`, com adapters/JPA correspondentes). Essa separação deve ser preservada apenas se representar contextos distintos; caso contrário, é candidata a padronização futura.

## 10. Comandos úteis

```bash
# Subir o Redis
docker compose up -d redis

# Compilar e executar testes
./mvnw test

# Executar a aplicação
./mvnw spring-boot:run
```

## 11. Use cases core e cobertura

Os casos de uso core concentram as regras que sustentam as funções críticas da API:

| Contexto | Use cases core | Funções cobertas |
|---|---|---|
| Autenticação | `AuthenticateUsuarioUseCase`, `RefreshTokenUseCase`, `LogoutUseCase` | Login, validação de conta/token, renovação e blacklist |
| Usuários | `RegisterUsuarioUseCase`, `GetUsuarioUseCase`, `ListUsuariosUseCase`, `UpdateUsuarioUseCase`, `DeleteUsuarioUseCase` | Registro, consulta, paginação, alteração de perfil/senha e exclusão |
| Carteira | `PostPosicaoUseCase`, `PatchPosicaoUseCase`, `DeletePosicaoUseCase`, `GetScoreCarteiraUseCase`, `GetCarteiraKpisUseCase`, `GerarRecomendacoesCarteiraUseCase` | Inclusão/venda/exclusão de posição, score, KPIs, distribuição por classe e recomendações |
| Ativos | `GetAtivosPorTicket` | Catálogo, detalhe e histórico de cotações |

São consideradas funções core as operações públicas dos use cases acima. A suíte unitária cobre as 18 funções públicas identificadas (100% de cobertura funcional), sempre com cenários de sucesso e de falha para regras críticas. A cobertura estrutural consolidada pelo JaCoCo nos use cases é de 91,15% de linhas e 60% de branches.

O relatório técnico de linhas/branches é gerado pelo JaCoCo durante `mvnw verify` e fica disponível em `target/site/jacoco/index.html`.
