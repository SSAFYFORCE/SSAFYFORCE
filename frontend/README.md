# 프론트엔드 환경세팅 시작하기

이 문서는 프로젝트의 프론트엔드 개발 환경 설정 및 시작 방법에 대한 가이드입니다.

## 개발 환경

- React 19
- Vite 6
- Tailwind CSS
- Jotai (상태 관리)

## 시작하기

### 1. 의존성 설치

프로젝트 루트 디렉토리에서 다음 명령어를 실행합니다:

```bash
cd frontend
npm install
```

### 2. 개발 서버 실행

```bash
npm run dev
```

이제 [http://localhost:3000](http://localhost:3000)에서 개발 서버가 실행됩니다.

## 프로젝트 구조

```
frontend/
├── public/            # 정적 파일 (이미지, 파비콘 등)
├── src/
│   ├── assets/        # 이미지, 폰트 등 정적 리소스
│   ├── components/    # 재사용 가능한 컴포넌트
│   │   ├── common/    # 공통 컴포넌트 (버튼, 인풋 등)
│   │   └── layout/    # 레이아웃 컴포넌트 (헤더, 푸터 등)
│   ├── constants/     # 상수 정의
│   ├── hooks/         # 커스텀 훅
│   ├── pages/         # 페이지 컴포넌트
│   ├── services/      # API 서비스
│   ├── store/         # 상태 관리 (Jotai)
│   ├── styles/        # 글로벌 스타일
│   ├── utils/         # 유틸리티 함수
│   ├── App.jsx        # 앱 진입점
│   └── main.jsx       # 메인 렌더링 파일
├── .env               # 환경 변수
└── vite.config.js     # Vite 설정
```

## 주요 명명 규칙

- **파일 경로 대소문자 주의**: Windows 환경에서 발생할 수 있는 문제를 방지하기 위해 import 경로의 대소문자를 정확히 일치시켜야 합니다.
- **컴포넌트 파일**: `PascalCase.jsx` (예: `Button.jsx`)
- **폴더 및 비컴포넌트 파일**: `camelCase.js` (예: `utils.js`)

## 환경 변수

Vite는 기본적으로 `import.meta.env`를 통해 환경 변수에 접근합니다. `process.env` 대신 이 방식을 사용해야 합니다.

```javascript
// 올바른 방법
const isDev = import.meta.env.DEV;
const apiUrl = import.meta.env.VITE_API_URL;

// 잘못된 방법 (오류 발생)
const isDev = process.env.NODE_ENV === 'development';
```

커스텀 환경 변수는 반드시 `VITE_` 접두사를 붙여야 클라이언트 코드에서 접근할 수 있습니다:

```
# .env 파일 예시
VITE_API_URL=http://localhost:8000
```

## 유용한 명령어

```bash
# 개발 서버 실행
npm run dev

# 프로덕션 빌드
npm run build

# 빌드된 결과물 로컬에서 미리보기
npm run preview

# 코드 린팅
npm run lint

# Tailwind 초기화 (필요한 경우)
npm run tailwind:init
```

## 주요 라이브러리

- **상태 관리**: [Jotai](https://jotai.org/) - 간단하고 효율적인 상태 관리 라이브러리
- **UI 컴포넌트**: 모든 컴포넌트는 `components` 폴더에서 관리됩니다.
- **HTTP 클라이언트**: [Axios](https://axios-http.com/) - API 요청 처리
- **스타일링**: [Tailwind CSS](https://tailwindcss.com/) 및 [Emotion](https://emotion.sh/)
- **아이콘**: [Lucide React](https://lucide.dev/) - 간단하고 일관된 아이콘 세트
- **차트**: [Chart.js](https://www.chartjs.org/)와 React 래퍼

## 주의사항

- **환경 변수 사용**: `process.env` 대신 `import.meta.env`를 사용해야 합니다.
- **파일 경로 대소문자**: import 경로의 대소문자를 정확히 일치시켜야 합니다.
- **빌드 결과물**: 빌드 결과물은 `dist` 폴더에 생성됩니다.

## 추가 학습 자료

- [React 공식 문서](https://react.dev/)
- [Vite 공식 문서](https://vitejs.dev/guide/)
- [Tailwind CSS 공식 문서](https://tailwindcss.com/docs)
- [Jotai 공식 문서](https://jotai.org/docs/introduction)

## 문제 해결

일반적인 문제 해결 방법:

1. **빌드 오류**: `node_modules`를 삭제하고 `npm install`을 다시 실행해보세요.
2. **캐시 문제**: `node_modules/.vite` 폴더를 삭제하고 다시 시도해보세요.
3. **환경 변수 문제**: `.env` 파일에 `VITE_` 접두사가 붙어있는지 확인하세요.
4. **경로 문제**: import 경로의 대소문자가 정확히 일치하는지 확인하세요.

---

더 자세한 정보나 질문이 있으면 팀 채널에 문의해주세요.
