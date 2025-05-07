import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import svgr from 'vite-plugin-svgr';
import tsconfigPaths from 'vite-tsconfig-paths';
import path from 'path';
import {fileURLToPath} from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), tsconfigPaths(), svgr()],
  publicDir: 'public',
  assetsInclude: ['**/*.png'],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'), // 경로 별칭 설정
    },
  },
  server: {
    port: 3000, // 개발 서버 포트 설정
    open: true, // 서버 시작 시 브라우저 자동 열기
  },
  build: {
    outDir: 'dist', // 빌드 출력 디렉토리
    minify: 'terser', // 압축 방식
    sourcemap: false, // 소스맵 생성 여부
  },
});
