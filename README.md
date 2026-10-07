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
> ⚠️ Render는 관리형 **MySQL을 제공하지 않습니다**(PostgreSQL만). 무료 MySQL은 Aiven 또는 TiDB Cloud Serverless 사용을 권장합니다.

1. **DB**: Aiven/TiDB에서 MySQL 생성 → 접속 정보 확인 (SSL 필요 시 `DB_URL`에 `useSSL=true` 추가)
2. **Backend (Render Web Service)**: 저장소 연결 → Runtime `Docker`, Root Directory `backend` → 환경변수 `DB_URL`, `DB_USER`, `DB_PASSWORD`, `CORS_ORIGINS` 입력. Health Check Path: `/actuator/health`
3. **Frontend (Vercel)**: 저장소 연결 → Root Directory `frontend` → 환경변수 `VITE_API_URL=https://<render-주소>`
4. Vercel 배포 주소를 Render의 `CORS_ORIGINS`에 넣고 재배포

무료 Render는 15분 무요청 시 잠들어 첫 접속이 30초~1분 걸립니다.
