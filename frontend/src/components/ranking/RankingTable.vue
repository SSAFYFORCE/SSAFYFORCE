<template>
  <div class="ranking-table-container">
    <h2 v-if="title" class="table-title">{{ title }}</h2>

    <div v-if="loading" class="loading">
      <font-awesome-icon :icon="['fas', 'spinner']" spin />
      <span>랭킹을 불러오는 중...</span>
    </div>

    <div v-else-if="rankings.length === 0" class="no-data">랭킹 데이터가 없습니다.</div>

    <div v-else class="ranking-cards-grid">
      <div
        v-for="item in displayRankings"
        :key="item.id"
        :class="['ranking-card', { 'first-place-card': item.rank === 1 }]"
        @click="handleRowClick(item)"
      >
        <div v-if="item.rank === 1" class="top-ranking-badge">
          <span class="rank-number">1</span>
        </div>
        <div v-else class="normal-ranking-badge">
          <span class="rank-number">{{ item.rank }}</span>
        </div>

        <div class="card-content">
          <div class="user-profile-section">
            <div class="user-image-wrapper">
              <img
                :src="item.profileImage || '/default-profile.png'"
                :alt="type === 'member' ? item.memberName : item.teamName"
                class="profile-image"
              />
            </div>
            <div class="user-text-info">
              <span class="user-name">{{
                type === 'member' ? item.memberName : item.teamName
              }}</span>
              <span class="user-score-display">
                점수:
                <span class="score-value">{{
                  type === 'member' ? formatScore(item.score) : formatScore(item.totalScore)
                }}</span>
              </span>
            </div>
          </div>

          <div v-if="item.rank === 1" class="first-place-details">
            <div class="detail-item">
              <span class="detail-label">해결 문제:</span>
              <span class="detail-value">{{
                type === 'member' ? item.solvedCount : item.totalSolvedCount
              }}</span>
            </div>
            <div v-if="type === 'team'" class="team-extra-info">
              <div class="detail-item">
                <span class="detail-label">멤버:</span>
                <span class="stat-value"
                  >{{ item.activeMemberCount }}/{{ item.teamMemberCount }}</span
                >
              </div>
              <div class="detail-item">
                <span class="detail-label">평균:</span>
                <span class="stat-value">{{ formatScore(item.averageScore) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, defineProps, defineEmits } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  period: {
    type: String,
    default: 'DAILY',
  },
  type: {
    type: String, // 'member' 또는 'team'
    default: 'member',
  },
  rankings: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '',
  },
  limit: {
    type: Number,
    default: null,
  },
  showFull: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['user-click'])
const router = useRouter()

const displayRankings = computed(() => {
  if (props.limit) {
    return props.rankings.slice(0, props.limit)
  }
  return props.rankings
})

const handleRowClick = (item) => {
  if (props.type === 'member') {
    router.push(`/profile/${item.memberId || item.id}`)
  } else {
    router.push(`/teams/${item.teamId || item.id}`)
  }
  emit('user-click', item)
}

const formatScore = (score) => {
  if (typeof score === 'number') {
    return score.toLocaleString()
  }
  return score || '0'
}
</script>

<style scoped>
/* 이전 스타일 유지 */
.ranking-table-container {
  background: none;
  box-shadow: none;
  border-radius: 0;
  overflow: visible;
}

.table-title {
  background: none;
  color: #333;
  padding: 1.5rem 0;
  margin: 0;
  font-size: 1.5rem;
  text-align: left;
  font-weight: 700;
}

.loading,
.no-data {
  text-align: center;
  padding: 3rem;
  color: #666;
  font-size: 1.1rem;
}

.loading svg {
  font-size: 2.5rem;
  color: var(--samsung-blue);
}

.ranking-cards-grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: 1fr;
}

.ranking-cards-grid > .ranking-card:first-child {
  grid-column: 1 / -1;
}

.ranking-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 1rem;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  position: relative;
  overflow: hidden;
}

.ranking-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.ranking-card.first-place-card {
  background: linear-gradient(135deg, #f7f7f7, #ececec);
  border: 1px solid #ddd;
  padding: 1.5rem;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.top-ranking-badge,
.normal-ranking-badge {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 45px;
  height: 45px;
  border-radius: 50%;
  font-weight: 700;
  color: white;
  font-size: 1.2rem;
  position: absolute;
  right: 1.5rem;
  top: 1.5rem;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.top-ranking-badge {
  background: linear-gradient(45deg, #ffc107, #ff9800);
  width: 55px;
  height: 55px;
  font-size: 1.5rem;
}

.normal-ranking-badge {
  background-color: #a0a0a0;
}

.rank-number {
  z-index: 1;
}

.card-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
  gap: 1rem;
}

.first-place-card .card-content {
  align-items: center;
}

.user-profile-section {
  display: flex;
  align-items: center;
  gap: 1rem;
  width: 100%;
}

.first-place-card .user-profile-section {
  flex-direction: column;
  text-align: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.user-image-wrapper {
  position: relative;
  width: 60px;
  height: 60px;
  flex-shrink: 0;
}

.first-place-card .user-image-wrapper {
  width: 80px;
  height: 80px;
}

.profile-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #eee;
  z-index: 2;
  position: relative;
}

.user-text-info {
  display: flex;
  flex-direction: column;
  text-align: left;
  min-width: 0;
}

.first-place-card .user-text-info {
  text-align: center;
}

.user-name {
  font-size: 1.2rem;
  font-weight: 700;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.first-place-card .user-name {
  font-size: 1.5rem;
}

.user-score-display {
  font-size: 0.9rem;
  color: #777;
  display: flex;
  gap: 0.5rem; /* "점수:"와 값 사이 간격 */
  align-items: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.score-value {
  font-weight: 600;
  color: var(--samsung-blue);
}

.first-place-details {
  margin-top: 1.5rem;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  align-items: center;
  padding: 1rem;
  background-color: #f0f0f0;
  border-radius: 8px;
}

/* 상세 정보 라벨과 값 간격 조정 */
.detail-item {
  display: flex;
  /* justify-content: space-between; 제거 */
  gap: 0.5rem; /* 라벨과 값 사이의 간격 줄임 */
  width: 100%;
  max-width: 250px;
  font-size: 0.95rem;
}

.detail-label {
  color: #555;
  font-weight: 500;
  flex-shrink: 0; /* 라벨이 줄어들지 않도록 */
}

.detail-value {
  font-weight: 600;
  color: #333;
  flex-grow: 1; /* 값이 남은 공간을 차지하도록 */
  text-align: right; /* 값을 오른쪽에 정렬 */
}

.team-extra-info {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.team-stat {
  /* detail-item과 동일하게 적용 */
  display: flex;
  /* justify-content: space-between; 제거 */
  gap: 0.5rem; /* 라벨과 값 사이의 간격 줄임 */
  width: 100%;
  font-size: 0.95rem;
}

.stat-label {
  color: #555;
  font-weight: 500;
  flex-shrink: 0;
}

.stat-value {
  font-weight: 600;
  color: #333;
  flex-grow: 1;
  text-align: right;
}

/* 반응형 디자인 */
@media (min-width: 769px) {
  .ranking-cards-grid {
    grid-template-columns: 1fr;
  }

  .ranking-card:not(.first-place-card) {
    padding: 1rem 1.5rem;
  }
}

@media (max-width: 768px) {
  .ranking-cards-grid {
    grid-template-columns: 1fr;
  }

  .ranking-card {
    padding: 1rem;
    flex-direction: row;
    text-align: left;
    align-items: center;
  }

  .top-ranking-badge,
  .normal-ranking-badge {
    position: static;
    margin-right: 1rem;
    width: 40px;
    height: 40px;
    font-size: 1.1rem;
  }
  .top-ranking-badge {
    width: 50px;
    height: 50px;
    font-size: 1.3rem;
  }

  .ranking-card.first-place-card {
    flex-direction: column;
  }

  .user-image-wrapper {
    width: 50px;
    height: 50px;
  }
  .first-place-card .user-image-wrapper {
    width: 70px;
    height: 70px;
  }
  .user-name {
    font-size: 1.1rem;
  }
  .first-place-card .user-name {
    font-size: 1.3rem;
  }

  .first-place-details {
    padding: 0.75rem;
  }
}
</style>
