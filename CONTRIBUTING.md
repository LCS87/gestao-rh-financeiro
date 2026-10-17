# Contributing

Obrigado por contribuir para o projeto!

## 1. Como começar

1. **Clone o repositório**

```bash
git clone <url-do-repositorio>
cd gestao-rh-financeiro
```

2. **Instale dependências**

- Backend (Java/Maven): não há dependências externas além do Maven.
- Frontend (Node):

```bash
cd frontend
npm install
```

## 2. Padrões de formatação e lint

### Frontend
- Use **ESLint** e **Prettier** antes de commitar.
- Execute:

```bash
cd frontend
npm run lint
npm run lint:fix
npm run format
```

### Backend
- A base possui Checkstyle, PMD e SpotBugs configurados via Maven (fase `verify`).
- Execute:

```bash
mvn verify
```

## 3. Workflow de commits

- Use mensagens no formato **imperativo** (ex: "Corrige cálculo de folha").
- Descreva o que foi alterado e por quê.

## 4. Pull Requests

- Baseie seu PR na branch `main`.
- Mantenha PRs pequenos e focados (uma mudança de cada vez).
- Garanta que CI passe antes de enviar o PR.

## 5. Boas práticas gerais

- **Não commit** arquivos de build (por exemplo: `target/`, `frontend/node_modules/`).
- **Não commit** credenciais (`.env`, senhas, chaves, etc.).
- Documente mudanças relevantes na `README.md` ou em `docs/`.
