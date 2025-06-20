import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  // GitHub Pages 배포를 위한 설정
  base: process.env.NODE_ENV === 'production' ? '/SSAFYFORCE/' : '/',
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
  }
})
