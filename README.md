# ChessPulse AI

ChessPulse AI is a full-stack chess analysis and coaching platform. It validates every submitted move on the server, saves the resulting FEN/SAN, evaluates each position through Stockfish UCI, and presents replayable win probabilities, position-based game phases, and the five largest probability swings.

## Stack

- Backend: Java 21, Spring Boot, Spring Data JPA, MySQL, springdoc OpenAPI, Actuator, JUnit 5.
- Chess and engine: chesslib for server-authoritative legal move/FEN state; Stockfish through a single synchronized UCI process.
- Frontend: React, TypeScript, Vite, Tailwind, chess.js, react-chessboard, Vitest.
- Delivery: Docker Compose and GitHub Actions.

## Architecture

`controller → service → repository` is the backend boundary. Entities are mapped to immutable REST DTOs. The UCI client is isolated from the probability conversion, phase detection, turning-point ranking, and coaching services. The public UUID is the only game identifier exposed to clients.

The probability model uses a logistic transform of the white-perspective centipawn score with a draw mass that is greatest around equality and falls as the evaluation becomes decisive. Values are rounded to one decimal; draw is omitted below `DRAW_DISPLAY_THRESHOLD` (default `0.5`). The phase detector is deterministic and material/position based: it recognizes sparse queenless/reduced-force positions as endgames, fully populated early positions as opening, and the rest as middlegame.

## Layout

```text
backend/     Spring API, domain, UCI engine integration, tests
frontend/    Vite React client and tests
docker-compose.yml
.github/workflows/ci.yml
```

## Local setup

1. Copy `.env.example` to `.env` and set MySQL credentials.
2. Start MySQL, create the configured database, then set `SPRING_DATASOURCE_URL`.
3. Download an appropriate Stockfish binary. On Windows set `STOCKFISH_PATH` to its executable and `STOCKFISH_ENABLED=true`.
4. Run the API:

```powershell
cd backend
mvn -s maven-settings.xml spring-boot:run
```

5. Run the web client in another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Swagger is available at `http://localhost:8080/swagger-ui/index.html`; health is at `/actuator/health`.

## Environment variables

| Variable | Purpose |
| --- | --- |
| `SPRING_DATASOURCE_URL`, `MYSQL_USER`, `MYSQL_PASSWORD` | MySQL connection |
| `STOCKFISH_ENABLED`, `STOCKFISH_PATH`, `STOCKFISH_DEPTH` | UCI engine configuration |
| `STOCKFISH_TIMEOUT_SECONDS` | Engine operation limit |
| `DRAW_DISPLAY_THRESHOLD` | Hide negligible draw values |
| `VITE_API_BASE_URL` | Browser API endpoint at build time |
| `OPENAI_API_KEY` | Reserved for an external coaching provider integration |

With no Stockfish configured, read/game-management endpoints stay available and analysis requests return a structured `503 ENGINE_UNAVAILABLE`; no synthetic engine evaluation is used.

## Tests and Docker

```powershell
cd backend; mvn test
cd ../frontend; npm test; npm run build
docker compose up --build
```

Docker installs the Linux Stockfish package in the backend image, so it does not rely on a Windows executable. The compose service exposes frontend at `http://localhost:5173`, API at `http://localhost:8080`, and MySQL at `3306`.

## Deployment

Deploy `frontend` to Netlify with `VITE_API_BASE_URL` set to the public API. Deploy `backend` to Render, Railway, or AWS with a managed MySQL URL and Linux Stockfish present at `STOCKFISH_PATH` (the supplied Docker image handles this). Set `CORS_ORIGINS` to the production frontend origin. Run the included CI workflow on each change.

## Future improvements

- Authentication and per-user game ownership.
- An API-backed LLM coaching provider behind the existing coaching boundary.
- PGN import/export and cached asynchronous deep analysis.
- Screenshots: _add product screenshots here_.
