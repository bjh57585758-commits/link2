# 🍚 밀양 맛집 (Miryang Restaurants)

밀양 오프라인 식당을 등록하고 리뷰를 남기는 풀스택 웹앱.
**React(Vite) + Spring Boot(Java 17) + MySQL** · 설계: [docs/DESIGN.md](docs/DESIGN.md)

## 로컬 실행
```bash
docker compose up -d                 # MySQL (localhost:3306, DB: miryang)
cd backend && mvn spring-boot:run    # http://localhost:8080
cd frontend && cp .env.example .env && npm install && npm run dev   # http://localhost:5173
```
테스트: `cd backend && mvn test` (H2 사용, MySQL 불필요)

## 환경변수
**Backend**: `DB_URL`(jdbc:mysql://host:3306/db?...), `DB_USER`, `DB_PASSWORD`, `CORS_ORIGINS`(Vercel 주소, 쉼표 구분), `PORT`(Render가 자동 주입)
**Frontend**: `VITE_API_URL` (백엔드 주소)

## 배포 (무료)
**TiDB Cloud(MySQL 호환 DB) + Render(백엔드) + Vercel(프론트엔드)** — 단계별 설정은 [docs/DEPLOY.md](docs/DEPLOY.md) 참고.

> Render는 관리형 MySQL을 제공하지 않아(PostgreSQL만) DB는 TiDB Cloud Starter를 사용합니다.
> 무료 Render는 15분 무요청 시 잠들어 첫 접속이 30초~1분 걸립니다.
