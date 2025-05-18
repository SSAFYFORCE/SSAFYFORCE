# 알고리즘 플랫폼 프론트엔드

Vue.js 기반의 알고리즘 문제 해결 플랫폼 프론트엔드 애플리케이션입니다.

## 🚀 개발 환경 설정

### 필수 요구사항
- Node.js 16.x 이상
- npm 또는 yarn

### 프로젝트 설치 및 실행

```bash
# 의존성 설치
npm install

# 개발 서버 실행
npm run dev

# 빌드
npm run build

# 미리보기
npm run preview
```

개발 서버는 `http://localhost:5173`에서 실행됩니다.

## 📁 프로젝트 구조

```
src/
├── assets/                 # 정적 자산 (이미지, 스타일)
│   ├── logo.svg
│   └── main.css
├── components/              # Vue 컴포넌트
│   ├── common/             # 공통 컴포넌트
│   │   ├── Navbar.vue
│   │   ├── Footer.vue
│   │   └── LoadingSpinner.vue
│   └── ranking/            # 랭킹 관련 컴포넌트
│       ├── RankingTable.vue
│       └── RankingFilter.vue
├── views/                  # 페이지 컴포넌트
│   ├── HomeView.vue        # 메인 페이지
│   ├── ProblemView.vue     # 문제 페이지
│   ├── TeamView.vue        # 팀 페이지
│   ├── RankingView.vue     # 랭킹 페이지
│   ├── LoginView.vue       # 로그인 페이지
│   ├── ProfileView.vue     # 프로필 페이지
│   └── NotFoundView.vue    # 404 페이지
├── router/                 # Vue Router 설정
│   └── index.js
├── stores/                 # Pinia 상태 관리
│   ├── auth.js            # 인증 스토어
│   └── ranking.js         # 랭킹 스토어
├── mockdata/              # 목 데이터
│   ├── rankings.js        # 랭킹 데이터
│   ├── problems.js        # 문제 데이터
│   ├── teams.js           # 팀 데이터
│   └── userProfile.js     # 사용자 프로필 데이터
├── utils/                 # 유틸리티 함수
│   └── datatransferutil.js # 데이터 전송 유틸
├── App.vue                # 루트 컴포넌트
└── main.js                # 앱 엔트리 포인트
```

## 🛠️ 주요 기술 스택

- **Vue.js 3** - 프론트엔드 프레임워크
- **Vue Router** - 라우팅
- **Pinia** - 상태 관리
- **Font Awesome** - 아이콘
- **Vite** - 빌드 도구

## 📋 주요 기능

### 🏠 홈페이지
- 움직이는 그라데이션 배너
- 랭킹 미리보기 (개인/팀)
- 일간/주간/월간 필터

### 🔧 문제 페이지
- 문제 검색 및 필터링
- 티어별, 알고리즘별 필터
- 해결 상태 필터

### 👥 팀 페이지
- 팀 검색 및 가입
- 팀 정보 및 멤버 미리보기
- 태그 기반 필터링

### 🏆 랭킹 페이지
- 전체 랭킹 (페이지네이션)
- 1등, 2등, 3등 특별 표시
- 개인 랭킹 정보

### 👤 프로필 페이지
- 사용자 통계 정보
- 최근 해결한 문제 20개
- 티어별 분포 차트
- 프로필 편집 기능

### 🔐 인증
- 로그인/로그아웃
- 실시간 상태 업데이트
- 라우트 가드

## 🎨 디자인 시스템

- **메인 컬러**: 삼성 블루 (#1428A0)
- **티어 색상**: Ruby, Diamond, Platinum, Gold, Silver, Bronze
- **반응형 디자인**: 모바일/태블릿/데스크톱 지원

## 🔧 개발 시 참고사항

### 데모 계정
- 사용자명: `test`
- 비밀번호: `password`

### Mock Data
현재 백엔드 API 대신 Mock 데이터를 사용합니다:
- `src/mockdata/` 폴더에서 테스트 데이터 수정 가능
- `datatransferutil.js`에서 API 호출 시뮬레이션

### 새로운 페이지 추가
1. `src/views/`에 Vue 컴포넌트 생성
2. `src/router/index.js`에 라우트 추가
3. 네비게이션바에 링크 추가 (필요시)

### 컴포넌트 개발
- `<script setup>` 구문 사용
- Composition API 권장
- TypeScript 지원 (`.vue` → `.vue.ts`)

## 📦 빌드 및 배포

```bash
# 프로덕션 빌드
npm run build

# 빌드 결과 미리보기
npm run preview

# 린트 검사
npm run lint

# 코드 포맷팅
npm run format
```

빌드된 파일은 `dist/` 폴더에 생성됩니다.

## 🔄 백엔드 연동

백엔드 API와 연동 시 `src/utils/datatransferutil.js` 파일의 함수들을 수정하여 실제 API 호출로 변경하면 됩니다.

```javascript
// 예시: 실제 API 호출로 변경
export const fetchRankings = async (period, type) => {
  const response = await axios.get(`/api/rankings`, {
    params: { period, type }
  });
  return response.data;
};
```

## 🐛 문제 해결

### 개발 서버가 시작되지 않는 경우
```bash
# node_modules 삭제 후 재설치
rm -rf node_modules
npm install
```

### 스타일이 적용되지 않는 경우
- 브라우저 캐시 삭제
- 하드 리프레시 (Ctrl+Shift+R)

## 👨‍💻 개발팀

프론트엔드 개발팀에서 개발 및 관리하고 있습니다.

---

**Happy Coding! 🚀**
