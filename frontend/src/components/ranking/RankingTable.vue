<!-- src/components/ranking/RankingTable.vue -->
<template>
  <div class="ranking-table-container">
    <h2 class="table-title">{{ title }}</h2>
    <div class="ranking-table">
      <div class="table-header">
        <div class="col-rank">순위</div>
        <div class="col-user">{{ type === 'individual' ? '사용자' : '팀' }}</div>
        <div class="col-tier">티어</div>
        <div class="col-rating">레이팅</div>
      </div>

      <div v-if="loading" class="loading">
        <font-awesome-icon :icon="['fas', 'spinner']" spin />
        데이터를 불러오는 중...
      </div>

      <div v-else-if="rankings.length === 0" class="no-data">랭킹 데이터가 없습니다.</div>

      <template v-else>
        <div
          v-for="item in rankings"
          :key="item.id"
          :class="['table-row', { 'current-user': item.isCurrent }]"
        >
          <div class="col-rank">
            <span v-if="item.rank === 1" class="first-place-crown">👑</span>
            <span :class="{ 'first-place-text': item.rank === 1 }">{{ item.rank }}</span>
          </div>
          <div class="col-user">
            <div class="user-info">
              <div class="user-image-container">
                <img
                  :src="type === 'individual' ? item.profileImage : item.teamImage"
                  :alt="type === 'individual' ? item.username : item.teamName"
                  :class="{ 'first-place-image': item.rank === 1 }"
                />
                <div v-if="item.rank === 1" class="first-place-glow"></div>
              </div>
              <span :class="{ 'first-place-name': item.rank === 1 }">
                {{ type === 'individual' ? item.username : item.teamName }}
              </span>
            </div>
          </div>
          <div class="col-tier">
            <span :style="getTierColor(item.tier)" class="font-medium">
              {{ item.tier }}
            </span>
          </div>
          <div class="col-rating">{{ item.rating }}</div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, watchEffect, onMounted, defineProps } from 'vue'
import { fetchRankings } from '@/utils/datatransferutil'

const props = defineProps({
  period: {
    type: String,
    default: 'daily',
    validator: (value) => ['daily', 'weekly', 'monthly'].includes(value),
  },
  type: {
    type: String,
    default: 'individual',
    validator: (value) => ['individual', 'team'].includes(value),
  },
  title: {
    type: String,
    default: '랭킹',
  },
})

const rankings = ref([])
const loading = ref(true)

// 티어별 색상을 반환하는 함수
const getTierColor = (tier) => {
  const tierName = tier.split(' ')[0].toLowerCase()
  const colors = {
    ruby: '#ff0062',
    diamond: '#00b4fc',
    platinum: '#27e2a4',
    gold: '#ec9a00',
    silver: '#435f7a',
    bronze: '#ad5600',
    unrated: '#2d2d2d',
  }
  return { color: colors[tierName] || '#2d2d2d' }
}
const loadRankings = async () => {
  loading.value = true
  try {
    const data = await fetchRankings(props.period, props.type)
    rankings.value = data
  } catch (error) {
    console.error('랭킹 데이터 로드 중 오류 발생:', error)
    rankings.value = []
  } finally {
    loading.value = false
  }
}

// period 또는 type이 변경될 때마다 랭킹 데이터 다시 로드
watchEffect(() => {
  loadRankings()
})

onMounted(() => {
  loadRankings()
})
</script>

<style scoped>
.ranking-table-container {
  width: 100%;
  max-width: 600px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 2rem;
}

.table-title {
  padding: 1rem;
  font-size: 1.2rem;
  background-color: #f5f5f5;
  border-bottom: 1px solid #eee;
  margin: 0;
}

.ranking-table {
  width: 100%;
}

.table-header {
  display: flex;
  padding: 1rem;
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
  font-weight: 600;
}

.table-row {
  display: flex;
  padding: 1rem;
  border-bottom: 1px solid #eee;
  transition: background-color 0.2s;
}

.table-row:hover {
  background-color: #f9f9f9;
}

.table-row.current-user {
  background-color: var(--samsung-blue-alpha);
}

/* 1등 강조 스타일 */
.table-row.first-place {
  background: linear-gradient(
    135deg,
    rgba(255, 215, 0, 0.2) 0%,
    rgba(255, 223, 0, 0.15) 50%,
    rgba(255, 215, 0, 0.1) 100%
  );
  border: 2px solid rgba(255, 215, 0, 0.5);
  position: relative;
  transform: scale(1.03);
  box-shadow: 0 8px 25px rgba(255, 215, 0, 0.3);
  animation: firstPlaceGlow 2s ease-in-out infinite;
  z-index: 1;
}

@keyframes firstPlaceGlow {
  0%,
  100% {
    box-shadow: 0 8px 25px rgba(255, 215, 0, 0.3);
    transform: scale(1.03);
  }
  50% {
    box-shadow: 0 12px 35px rgba(255, 215, 0, 0.5);
    transform: scale(1.04);
  }
}

.first-place-crown {
  font-size: 1.4em;
  margin-right: 0.5rem;
  animation: crownFloat 1.5s ease-in-out infinite;
  filter: drop-shadow(0 2px 4px rgba(255, 215, 0, 0.5));
}

@keyframes crownFloat {
  0%,
  100% {
    transform: translateY(0) rotate(-5deg);
  }
  50% {
    transform: translateY(-5px) rotate(5deg);
  }
}

.first-place-text {
  font-weight: 900;
  color: #daa520;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  font-size: 1.2em;
}

.user-image-container {
  position: relative;
  display: inline-block;
}

.first-place-image {
  border: 3px solid #ffd700;
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.7);
  animation: imageGlow 1.8s ease-in-out infinite;
}

@keyframes imageGlow {
  0%,
  100% {
    box-shadow: 0 0 15px rgba(255, 215, 0, 0.7);
  }
  50% {
    box-shadow: 0 0 25px rgba(255, 215, 0, 1);
  }
}

.first-place-glow {
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  background: conic-gradient(from 0deg, #ffd700, #ffa500, #ff8c00, #ffd700);
  border-radius: 50%;
  z-index: -1;
  animation: spin 4s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.first-place-name {
  font-weight: 800;
  color: #b8860b;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.2);
  font-size: 1.05em;
}

.col-rank {
  width: 15%;
  text-align: center;
  font-weight: 600;
}

.col-user {
  width: 45%;
}

.col-tier {
  width: 20%;
}

.col-rating {
  width: 20%;
  text-align: right;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.user-info img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.loading,
.no-data {
  padding: 2rem;
  text-align: center;
  color: #757575;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}
</style>
