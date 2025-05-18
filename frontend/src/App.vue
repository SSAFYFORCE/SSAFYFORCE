<!-- src/App.vue -->
<template>
  <div class="app">
    <Navbar />
    <main class="main-content">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import Navbar from '@/components/common/Navbar.vue'
import Footer from '@/components/common/Footer.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// 앱 시작 시 인증 상태 초기화
onMounted(() => {
  authStore.initialize()
})
</script>

<style>
/* 전역 스타일 */
*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html {
  font-family:
    'Pretendard',
    'Noto Sans KR',
    -apple-system,
    BlinkMacSystemFont,
    'Segoe UI',
    Roboto,
    'Helvetica Neue',
    Arial,
    sans-serif;
  line-height: 1.6;
  color: #333;
}

body {
  background-color: #f9f9f9;
  min-height: 100vh;
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
}
</style>
