# 배포 가이드 (Aiven MySQL + Render + Vercel)

```
Vercel (React)  ──HTTPS──▶  Render (Spring Boot)  ──TLS──▶  Aiven (MySQL)
```
배포 순서: **① DB → ② 백엔드 → ③ 프론트 → ④ CORS 반영**

## 1. Aiven MySQL (DB)
1. https://aiven.io 가입 (무료 플랜은 카드 등록 불필요)
2. **Create service → MySQL** 선택 후 플랜은 **Free** 선택 (리전은 가까운 곳)
   - 무료 플랜: 노드 1개 / 1GB 메모리 / 1GB 디스크, 조직당 MySQL 서비스 1개
   - 오래 사용하지 않으면 전원이 꺼질 수 있습니다. 꺼지기 전에 알림이 오고, 콘솔에서 다시 켤 수 있습니다.
3. 서비스가 **Running** 이 되면 Overview 의 연결 정보를 확인합니다.
   - Host: `<서비스명>-<프로젝트>.a.aivencloud.com`
   - Port: **콘솔에 표시된 값** (3306이 아닐 수 있음)
   - User: `avnadmin`
   - Password: 콘솔의 비밀번호 표시/복사 버튼으로 확인
   - Database: `defaultdb` (기본 제공, 그대로 사용 가능)
4. 아래 값을 조합해 `DB_URL` 준비 (TLS 필수):
   ```
   jdbc:mysql://<HOST>:<PORT>/defaultdb?sslMode=REQUIRED&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
   ```
   `sslMode=REQUIRED` 는 TLS 로 암호화하며, 서버 인증서 검증은 생략합니다. 인증서까지 검증하려면 Aiven 의 CA 인증서를 받아 `VERIFY_CA` 로 설정해야 합니다(과제용으로는 선택 사항).

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
   | `DB_USER` | `avnadmin` |
   | `DB_PASSWORD` | Aiven 비밀번호 |
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
- [ ] 새로고침해도 데이터 유지 (DB 저장 확인)
- [ ] 브라우저 새로고침으로 `/restaurants/1` 직접 접속해도 404가 안 남 (`vercel.json` rewrite)

## 문제 해결
| 증상 | 원인 / 조치 |
|---|---|
| 화면에 "Failed to fetch" | `CORS_ORIGINS` 불일치(오타, 끝의 `/`, http/https) 또는 `VITE_API_URL` 오류. 변경 후 Vercel은 **재배포** 필요 (빌드 시점에 값이 들어감) |
| 백엔드 시작 시 `Communications link failure` / SSL 오류 | `DB_URL`의 Host/Port 가 콘솔 값과 같은지, `sslMode=REQUIRED` 가 있는지, Aiven 서비스가 **Running** 인지(꺼져 있으면 켜기) 확인 |
| `Access denied for user` | `DB_USER`(`avnadmin`) 또는 비밀번호 오타 |
| `Unknown database` | `DB_URL` 의 DB 이름이 실제 DB(`defaultdb`)와 같은지 확인 |
| Render 배포 중 메모리 부족 | 환경변수 `JAVA_OPTS=-Xmx300m` 로 낮춤 |
| 첫 요청만 매우 느림 | Render 슬립 해제 + DB 연결 시간. DB 서비스가 꺼져 있으면 Aiven 콘솔에서 켜세요. 정상 동작이며 프론트에 안내 문구가 있음 |

## 보안 메모
- 비밀번호/DB 접속 정보는 **Render 환경변수에만** 두고 저장소에 커밋하지 않습니다.
- 로컬 `application.yml` 의 기본값(`root`/`root`)은 로컬 Docker MySQL 전용입니다.
