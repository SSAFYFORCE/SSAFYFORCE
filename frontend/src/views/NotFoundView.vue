<!-- src/views/NotFoundView.vue -->
<template>
  <div class="not-found-view">
    <div class="not-found-container">
      <div class="error-animation">
        <div class="error-code">404</div>
        <div class="error-icon">
          <font-awesome-icon :icon="['fas', 'search']" />
        </div>
      </div>

      <div class="error-content">
        <h1>페이지를 찾을 수 없습니다</h1>
        <p>요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.</p>

        <div class="error-actions">
          <router-link to="/" class="btn btn-primary">
            <font-awesome-icon :icon="['fas', 'home']" />
            홈으로 돌아가기
          </router-link>
          <button @click="$router.go(-1)" class="btn btn-secondary">
            <font-awesome-icon :icon="['fas', 'arrow-left']" />
            이전 페이지
          </button>
        </div>
      </div>
    </div>

    <!-- 배경 애니메이션 -->
    <div class="background-elements">
      <div class="floating-element element-1"></div>
      <div class="floating-element element-2"></div>
      <div class="floating-element element-3"></div>
    </div>
  </div>
</template>

<script setup>
// 404 페이지 스크립트
</script>

<style scoped>
.not-found-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
}

.not-found-container {
  text-align: center;
  max-width: 600px;
  padding: 2rem;
  position: relative;
  z-index: 2;
}

.error-animation {
  position: relative;
  margin-bottom: 2rem;
}

.error-code {
  font-size: 8rem;
  font-weight: 900;
  background: linear-gradient(135deg, var(--samsung-blue), var(--samsung-blue-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  margin-bottom: 1rem;
  animation: pulse 2s ease-in-out infinite;
}

.error-icon {
  font-size: 3rem;
  color: var(--samsung-blue);
  animation: float 3s ease-in-out infinite;
}

.error-content h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 1rem;
  font-weight: 700;
}

.error-content p {
  font-size: 1.1rem;
  color: #666;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.875rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.btn-primary {
  background-color: var(--samsung-blue);
  color: white;
}

.btn-primary:hover {
  background-color: var(--samsung-blue-dark);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(20, 40, 160, 0.3);
}

.btn-secondary {
  background-color: white;
  color: #333;
  border: 2px solid #e1e5e9;
}

.btn-secondary:hover {
  background-color: #f8f9fa;
  border-color: var(--samsung-blue);
  transform: translateY(-2px);
}

/* 배경 애니메이션 */
.background-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.floating-element {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(45deg, var(--samsung-blue-alpha), var(--samsung-blue-light));
  animation: float 6s ease-in-out infinite;
}

.element-1 {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.element-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.element-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 60%;
  animation-delay: 4s;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 1;
  }
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .error-code {
    font-size: 6rem;
  }

  .error-content h1 {
    font-size: 1.5rem;
  }

  .error-actions {
    flex-direction: column;
    align-items: center;
  }

  .btn {
    width: 100%;
    max-width: 250px;
  }
}
</style>
