# PF_zambao_back

API de artigos (Spring Boot 3 / Java 21) protegida por Auth0.

| Rota | Quem acessa | O que faz |
|---|---|---|
| `POST /artigos` | ADMIN | cadastra um artigo (`titulo`, `descricao`, `prioridade` = BAIXA/MEDIA/ALTA, `emailCriador`) |
| `DELETE /artigos/{id}` | ADMIN | remove um artigo (204; 404 se não existir) |
| `GET /artigos` | ADMIN e USER | lista os artigos, do mais novo para o mais antigo |
| `GET /actuator/health` | público | health check |

Corpo do `POST`: `{"titulo": "...", "descricao": "...", "prioridade": "BAIXA|MEDIA|ALTA", "emailCriador": "..."}`

## Auth0

- Issuer: `https://dev-vrz0ftntk7qkj44u.us.auth0.com/` (**com** a barra final)
- Audience (Identifier da API): `https://avaliacoes-api`
- Os papéis vêm da claim `https://avaliacoes-api/roles`, preenchida pela Action `add-roles-to-token`
  (precisa estar **anexada ao fluxo Login / Post Login**).

Variáveis de ambiente (todas têm valor padrão): `AUTH0_ISSUER`, `AUTH0_AUDIENCE`, `AUTH0_ROLES_CLAIM`,
`CORS_ALLOWED_ORIGINS` (separadas por vírgula), `DB_URL`, `DB_USER`, `DB_PASSWORD`.
Usa PostgreSQL (`DB_URL`, `DB_USER`, `DB_PASSWORD`; padrão `localhost:5432/artigosdb`). Nos testes usa H2 em memória. No deploy o container entra na rede `rede` e fala com o container `postgres-db`; a senha vem do secret `DB_PASSWORD`.

## Rodar local

```bash
mvn verify          # testes + relatório JaCoCo em target/site/jacoco
mvn spring-boot:run
```

## Deploy (GitHub Actions → DockerHub → EC2)

O workflow `.github/workflows/deploy.yml` roda `mvn verify` em todo push/PR e, em push na `main`,
publica a imagem no DockerHub e reinicia o container na EC2.

Secrets necessários em *Settings → Secrets and variables → Actions*:

| Secret | Valor |
|---|---|
| `DOCKERHUB_USERNAME` | seu usuário do DockerHub |
| `DOCKERHUB_TOKEN` | access token do DockerHub (não a senha) |
| `EC2_HOST` | IP público ou DNS da EC2 |
| `EC2_USER` | usuário SSH (ex.: `ubuntu` ou `ec2-user`) |
| `EC2_SSH_KEY` | **conteúdo** completo do arquivo `.pem` |

Variáveis opcionais (aba *Variables*): `AUTH0_ISSUER`, `AUTH0_AUDIENCE`, `CORS_ALLOWED_ORIGINS`.

Na EC2: Docker instalado, usuário no grupo `docker`, e porta **8080** liberada no Security Group.
