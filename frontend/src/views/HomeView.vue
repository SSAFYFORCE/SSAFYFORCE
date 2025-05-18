<!-- src/views/HomeView.vue -->
<template>
  <div class="home">
    <!-- 배너 섹션 -->
    <section class="banner-section">
      <div class="banner-content">
        <h1>알고리즘 문제 해결 능력을 향상시키세요</h1>
        <p>팀과 함께 성장하고 랭킹을 올려보세요</p>
        <div class="banner-buttons">
          <router-link to="/problems" class="btn btn-primary">문제 풀기</router-link>
          <router-link to="/teams" class="btn btn-secondary">팀 찾기</router-link>
        </div>
      </div>
    </section>
    
    <!-- 랭킹 섹션 -->
    <section class="ranking-section">
      <h2 class="section-title">랭킹</h2>
      
      <!-- 랭킹 필터 컴포넌트 -->
      <RankingFilter 
        @update:period="period = $event"
        @update:type="type = $event"
      />
      
      <div class="rankings-container">
        <!-- 개인 랭킹 테이블 -->
        <RankingTable 
          v-if="type === 'individual'"
          :period="period"
          type="individual"
          title="개인 랭킹"
        />
        
        <!-- 팀 랭킹 테이블 -->
        <RankingTable 
          v-else
          :period="period"
          type="team"
          title="팀 랭킹"
        />
      </div>
      
      <div class="view-more">
        <router-link to="/ranking" class="btn btn-outline">전체 랭킹 보기</router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import RankingFilter from '@/components/ranking/RankingFilter.vue';
import RankingTable from '@/components/ranking/RankingTable.vue';

// 상태 정의
const period = ref('daily');
const type = ref('individual');
</script>

<style scoped>
.home {
  padding-bottom: 4rem;
}

/* 배너 섹션 스타일 */
.banner-section {
  height: 500px;
  background: linear-gradient(45deg, 
    var(--samsung-blue), 
    var(--samsung-blue-light), 
    var(--samsung-blue-dark), 
    var(--samsung-blue));
  background-size: 400% 400%;
  animation: gradientMove 8s ease infinite;
  display: flex;
  align-items: center;
  position: relative;
  margin-bottom: 4rem;
  overflow: hidden;
}

/* 그라데이션 애니메이션 */
@keyframes gradientMove {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* 배너 위에 떠다니는 도형들 */
.banner-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="2" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1.5" fill="rgba(255,255,255,0.08)"/><circle cx="50" cy="10" r="1" fill="rgba(255,255,255,0.06)"/><circle cx="10" cy="60" r="1.2" fill="rgba(255,255,255,0.07)"/><circle cx="90" cy="40" r="0.8" fill="rgba(255,255,255,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  animation: float 20s linear infinite;
}

/* 떠다니는 애니메이션 */
@keyframes float {
  0% {
    transform: translateX(-100px);
  }
  100% {
    transform: translateX(100vw);
  }
}

.banner-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
  padding: 0 2rem;
  margin: 0 auto;
  text-align: center;
  color: white;
}

.banner-content h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.banner-content p {
  font-size: 1.25rem;
  margin-bottom: 2rem;
}

.banner-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
}

.btn-primary {
  background-color: var(--samsung-blue);
  color: white;
  box-shadow: 0 4px 15px rgba(20, 40, 160, 0.3);
}

.btn-primary:hover {
  background-color: var(--samsung-blue-dark);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(20, 40, 160, 0.4);
}

.btn-secondary {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid white;
}

.btn-secondary:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.btn-outline {
  border: 1px solid white;
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.btn-outline:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 255, 255, 0.2);
}

/* 랭킹 섹션 스타일 */
.ranking-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.section-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
}

.rankings-container {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.view-more {
  text-align: center;
  margin-top: 2rem;
}
</style>