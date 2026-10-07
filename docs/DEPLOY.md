# 배포 가이드 (TiDB Cloud + Render + Vercel)

```
Vercel (React)  ──HTTPS──▶  Render (Spring Boot)  ──TLS:4000──▶  TiDB Cloud (MySQL 호환)
```
배포 순서: **① DB → ② 백엔드 → ③ 프론트 → ④ CORS 반영**

## 1. TiDB Cloud (DB)
1. https://tidbcloud.com 가입 → **Starter(무료)** 클러스터 생성 (리전은 가까운 곳, 예: 서울/도쿄)
2. 클러스터 화면의 **Connect** 클릭 → 연결 정보 확인 및 **비밀번호 생성** (비밀번호는 처음 한 번만 표시되니 바로 저장)
   - Host: `gateway01.<region>.prod.aws.tidbcloud.com`
   - Port: **4000** (MySQL의 3306이 아님)
   - User: `<접두사>.root` (접두사가 붙은 형태 그대로 사용)
3. SQL Editor에서 DB 생성:
   ```sql
   CREATE DATABASE miryang CHARACTER SET utf8mb4;
   ```
4. 아래 값을 조합해 `DB_URL` 준비 (TLS 필수):
   ```
   jdbc:mysql://<HOST>:4000/miryang?sslMode=VERIFY_IDENTITY&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
   ```
테이블은 앱이 첫 실행 때 자동 생성합니다(`DDL_AUTO=update`).

## 2. Render (백엔드)
1. https://render.com → **New + → Web Service** → GitHub 저장소 `link2` 연결
2. 설정
   | 항목 | 값 |
   |---|---|
   | Branch | 배포할 브랜치 (예: `main`) |
   | Root Directory | `backend` |
   | Runtime / Language | **Docker** |
   | Instance Type | **Free** |
   | Health Check Path | `/actuator/health` |
3. **Environment Variables**
   | Key | Value |
   |---|---|
   | `DB_URL` | 1-4에서 만든 JDBC URL |
   | `DB_USER` | `<접두사>.root` |
   | `DB_PASSWORD` | TiDB 비밀번호 |
   | `CORS_ORIGINS` | 일단 `http://localhost:5173` (④에서 Vercel 주소로 교체) |
4. 배포 후 `https://<서비스명>.onrender.com/actuator/health` 에서 `{"status":"UP"}` 확인, `/api/restaurants` 에서 `[]` 확인

> Free 플랜은 15분간 요청이 없으면 잠들고 첫 접속에 30초~1분 걸립니다. 첫 빌드(Docker + Maven)도 몇 분 걸립니다.

## 3. Vercel (프론트엔드)
1. https://vercel.com → **Add New → Project** → 저장소 `link2` 선택
2. 설정
   | 항목 | 값 |
   |---|---|
   | Root Directory | `frontend` |
   | Framework Preset | Vite (자동 감지) |
   | Environment Variable | `VITE_API_URL` = `https://<서비스명>.onrender.com` (끝에 `/` 없이) |
3. Deploy → 발급된 주소(`https://xxx.vercel.app`) 확인

## 4. CORS 반영 (마지막 단계)
Render의 `CORS_ORIGINS` 를 Vercel 주소로 변경하고 저장(자동 재배포):
```
CORS_ORIGINS=https://xxx.vercel.app
```
- 끝에 `/` 를 붙이지 마세요. 여러 개는 쉼표로 구분합니다.
- Vercel의 미리보기(Preview) 주소는 별도 도메인이라 허용 목록에 추가해야 동작합니다.

## 5. 최종 점검 체크리스트
- [ ] Vercel 주소 접속 → 목록 화면 표시 (첫 접속이 느리면 정상)
- [ ] 식당 등록 → 상세 화면 이동
- [ ] 리뷰 작성 → 목록에 표시
- [ ] 리뷰 삭제 (틀린 비밀번호는 거부, 맞으면 삭제)
- [ ] 새로고침해도 데이터 유지 (TiDB 저장 확인)
- [ ] 브라우저 새로고침으로 `/restaurants/1` 직접 접속해도 404가 안 남 (`vercel.json` rewrite)

## 문제 해결
| 증상 | 원인 / 조치 |
|---|---|
| 화면에 "Failed to fetch" | `CORS_ORIGINS` 불일치(오타, 끝의 `/`, http/https) 또는 `VITE_API_URL` 오류. 변경 후 Vercel은 **재배포** 필요 (빌드 시점에 값이 들어감) |
| 백엔드 시작 시 `Communications link failure` / SSL 오류 | `DB_URL`의 포트 4000, `sslMode=VERIFY_IDENTITY` 확인 |
| `Access denied for user` | `DB_USER` 접두사(`xxxx.root`) 누락 또는 비밀번호 오타 |
| `Unknown database 'miryang'` | 1-3의 `CREATE DATABASE` 실행 여부 확인 |
| Render 배포 중 메모리 부족 | 환경변수 `JAVA_OPTS=-Xmx300m` 로 낮춤 |
| 첫 요청만 매우 느림 | Render 슬립 해제 + TiDB 연결 시간. 정상 동작이며 프론트에 안내 문구가 있음 |

## 보안 메모
- 비밀번호/DB 접속 정보는 **Render 환경변수에만** 두고 저장소에 커밋하지 않습니다.
- 로컬 `application.yml` 의 기본값(`root`/`root`)은 로컬 Docker MySQL 전용입니다.
