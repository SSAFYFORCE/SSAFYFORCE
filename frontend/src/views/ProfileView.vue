<!-- src/views/ProfileView.vue -->
<template>
  <div class="profile-view">
    <div class="container">
      <div v-if="loading" class="loading">
        <font-awesome-icon :icon="['fas', 'spinner']" spin />
        <span>프로필을 불러오는 중...</span>
      </div>

      <div v-else-if="profile" class="profile-layout">
        <!-- 왼쪽: 프로필 카드 -->
        <div class="profile-card">
          <div class="profile-avatar">
            <img :src="profile.profileImage" :alt="profile.username" />
          </div>

          <div class="profile-main-info">
            <h2 class="username">{{ profile.username }}</h2>
            <div class="tier-info">
              <span class="tier-badge" :style="getTierColor(profile.tier)">
                {{ profile.tier }}
              </span>
              <span class="rating">{{ profile.rating }}</span>
            </div>
            <div class="rank-info">
              <div class="rank-item">
                <span class="rank-label">Rank</span>
                <span class="rank-value">#{{ profile.rank || 1234 }}</span>
              </div>
              <div class="rank-item">
                <span class="rank-label">상위</span>
                <span class="rank-value">{{ profile.topPercent || '0.7' }}%</span>
              </div>
            </div>
          </div>

          <div class="profile-actions">
            <button class="profile-edit-btn" @click="toggleEditMode">프로필 편집하기</button>
          </div>
        </div>

        <!-- 중앙/오른쪽: 통계 및 차트 -->
        <div class="profile-stats">
          <!-- 문제 해결 현황 -->
          <div class="stats-section">
            <h3>문제 해결 현황</h3>
            <div class="solve-stats">
              <div class="solve-count">
                <span class="count-number">{{ profile?.solvedProblems || 0 }}</span>
                <span class="count-label">맞혔습니다!!</span>
              </div>
              <div class="solve-rate">
                <span class="rate-number">{{ profile?.correctRate || 0 }}%</span>
                <span class="rate-label">정답률</span>
              </div>
            </div>
          </div>

          <!-- 티어별 분포 -->
          <div class="tier-distribution-section">
            <h3>티어별 분포</h3>
            <div class="tier-charts">
              <div class="tier-donut-chart">
                <!-- 도넛 차트 시뮬레이션 -->
                <div class="donut-placeholder">
                  <svg width="120" height="120" viewBox="0 0 120 120">
                    <circle
                      cx="60"
                      cy="60"
                      r="40"
                      fill="none"
                      stroke="#ff0062"
                      stroke-width="20"
                      stroke-dasharray="31.4 188.4"
                      transform="rotate(-90 60 60)"
                    />
                    <circle
                      cx="60"
                      cy="60"
                      r="40"
                      fill="none"
                      stroke="#00b4fc"
                      stroke-width="20"
                      stroke-dasharray="25.1 194.7"
                      transform="rotate(45 60 60)"
                    />
                    <circle
                      cx="60"
                      cy="60"
                      r="40"
                      fill="none"
                      stroke="#27e2a4"
                      stroke-width="20"
                      stroke-dasharray="18.8 200.8"
                      transform="rotate(135 60 60)"
                    />
                    <circle
                      cx="60"
                      cy="60"
                      r="40"
                      fill="none"
                      stroke="#ec9a00"
                      stroke-width="20"
                      stroke-dasharray="31.4 188.4"
                      transform="rotate(225 60 60)"
                    />
                    <circle
                      cx="60"
                      cy="60"
                      r="40"
                      fill="none"
                      stroke="#435f7a"
                      stroke-width="20"
                      stroke-dasharray="62.8 157.0"
                      transform="rotate(315 60 60)"
                    />
                  </svg>
                </div>
                <div class="tier-legend">
                  <div class="legend-item">
                    <span class="legend-color" style="background-color: #ff0062"></span>
                    <span class="legend-text">Ruby</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color" style="background-color: #00b4fc"></span>
                    <span class="legend-text">Diamond</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color" style="background-color: #27e2a4"></span>
                    <span class="legend-text">Platinum</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color" style="background-color: #ec9a00"></span>
                    <span class="legend-text">Gold</span>
                  </div>
                  <div class="legend-item">
                    <span class="legend-color" style="background-color: #435f7a"></span>
                    <span class="legend-text">Silver</span>
                  </div>
                </div>
              </div>

              <!-- 난이도별 막대 그래프 -->
              <div class="difficulty-bar-chart">
                <div class="bar-item">
                  <span class="bar-label">구현</span>
                  <div class="bar-container">
                    <div
                      class="bar-fill"
                      style="height: 60%; background-color: var(--samsung-blue)"
                    ></div>
                  </div>
                </div>
                <div class="bar-item">
                  <span class="bar-label">다이나믹 프로그래밍</span>
                  <div class="bar-container">
                    <div
                      class="bar-fill"
                      style="height: 40%; background-color: var(--samsung-blue)"
                    ></div>
                  </div>
                </div>
                <div class="bar-item">
                  <span class="bar-label">그래프 탐색</span>
                  <div class="bar-container">
                    <div
                      class="bar-fill"
                      style="height: 50%; background-color: var(--samsung-blue)"
                    ></div>
                  </div>
                </div>
                <div class="bar-item">
                  <span class="bar-label">그리디 알고리즘</span>
                  <div class="bar-container">
                    <div
                      class="bar-fill"
                      style="height: 80%; background-color: var(--samsung-blue)"
                    ></div>
                  </div>
                </div>
                <div class="bar-item">
                  <span class="bar-label">백트래킹</span>
                  <div class="bar-container">
                    <div
                      class="bar-fill"
                      style="height: 30%; background-color: var(--samsung-blue)"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 하단: 최근 해결한 문제 20개 -->
        <div class="recent-problems-section">
          <h3>최근 해결한 문제</h3>
          <div class="problems-list">
            <div
              v-for="(problem, index) in profile?.recentSolved || []"
              :key="`${problem.problemNumber}-${index}`"
              class="problem-item"
            >
              <div class="problem-tier-badge">
                <span
                  class="tier-indicator"
                  :style="{ backgroundColor: getTierColor(problem.tier).color }"
                >
                  {{ getTierShortName(problem.tier) }}
                </span>
              </div>

              <div class="problem-info">
                <div class="problem-header">
                  <span class="problem-number">{{ problem.problemNumber }}</span>
                  <span class="problem-title">{{ problem.title }}</span>
                </div>
                <div class="problem-meta">
                  <span class="solve-language">java 8</span>
                  <span class="solve-time">{{ getRelativeTime(problem.solvedAt) }}</span>
                </div>
              </div>

              <div class="problem-experience">
                <div class="exp-gained">
                  <span class="exp-label">경험치 획득</span>
                  <span class="exp-value">{{ problem.experience }}원</span>
                </div>
                <div class="exp-total">
                  <span class="exp-total-label">경험치 합계</span>
                  <span class="exp-total-value">{{ problem.experience }}원</span>
                </div>
              </div>
            </div>
          </div>

          <div class="more-problems">
            <button class="more-btn">더보기</button>
          </div>
        </div>
      </div>

      <!-- 편집 모달 -->
      <div v-if="editMode" class="edit-modal-overlay" @click="toggleEditMode">
        <div class="edit-modal" @click.stop>
          <div class="modal-header">
            <h2>프로필 편집</h2>
            <button class="modal-close" @click="toggleEditMode">
              <font-awesome-icon :icon="['fas', 'times']" />
            </button>
          </div>

          <form @submit.prevent="saveProfile" class="edit-form">
            <div class="form-group">
              <label>자기소개</label>
              <textarea
                v-model="editData.bio"
                placeholder="자신을 소개해주세요..."
                class="form-textarea"
                rows="3"
              ></textarea>
            </div>

            <div class="form-group">
              <label>GitHub</label>
              <input
                v-model="editData.github"
                type="url"
                placeholder="https://github.com/username"
                class="form-input"
              />
            </div>

            <div class="form-group">
              <label>블로그</label>
              <input
                v-model="editData.blog"
                type="url"
                placeholder="https://blog.example.com"
                class="form-input"
              />
            </div>

            <div class="modal-actions">
              <button type="button" @click="toggleEditMode" class="btn btn-secondary">취소</button>
              <button type="submit" :disabled="saving" class="btn btn-primary">
                <template v-if="saving">
                  <font-awesome-icon :icon="['fas', 'spinner']" spin />
                  저장 중...
                </template>
                <template v-else> 저장 </template>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { fetchUserProfile } from '@/utils/datatransferutil'

// 반응성 데이터
const profile = ref(null)
const loading = ref(true)
const editMode = ref(false)
const saving = ref(false)

const editData = reactive({
  bio: '',
  github: '',
  blog: '',
})

// 메서드
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

const getTierShortName = (tier) => {
  const tierMap = {
    'Ruby V': 'R5',
    'Ruby IV': 'R4',
    'Ruby III': 'R3',
    'Ruby II': 'R2',
    'Ruby I': 'R1',
    'Diamond V': 'D5',
    'Diamond IV': 'D4',
    'Diamond III': 'D3',
    'Diamond II': 'D2',
    'Diamond I': 'D1',
    'Platinum V': 'P5',
    'Platinum IV': 'P4',
    'Platinum III': 'P3',
    'Platinum II': 'P2',
    'Platinum I': 'P1',
    'Gold V': 'G5',
    'Gold IV': 'G4',
    'Gold III': 'G3',
    'Gold II': 'G2',
    'Gold I': 'G1',
    'Silver V': 'S5',
    'Silver IV': 'S4',
    'Silver III': 'S3',
    'Silver II': 'S2',
    'Silver I': 'S1',
    'Bronze V': 'B5',
    'Bronze IV': 'B4',
    'Bronze III': 'B3',
    'Bronze II': 'B2',
    'Bronze I': 'B1',
  }
  return tierMap[tier] || tier
}

const getRelativeTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))

  if (diffDays === 0) return '오늘'
  if (diffDays === 1) return '1일 전'
  if (diffDays < 7) return `${diffDays}일 전`
  if (diffDays < 30) return `${Math.floor(diffDays / 7)}주 전`
  if (diffDays < 365) return `${Math.floor(diffDays / 30)}개월 전`
  return `${Math.floor(diffDays / 365)}년 전`
}

const loadProfile = async () => {
  loading.value = true
  try {
    const data = await fetchUserProfile()
    profile.value = data

    // 편집 데이터 초기화
    if (data) {
      editData.bio = data.bio || ''
      editData.github = data.github || ''
      editData.blog = data.blog || ''
    }
  } catch (error) {
    console.error('프로필을 불러오는 중 오류 발생:', error)
  } finally {
    loading.value = false
  }
}

const toggleEditMode = () => {
  editMode.value = !editMode.value

  if (editMode.value && profile.value) {
    // 편집 모드로 진입할 때 현재 데이터로 초기화
    editData.bio = profile.value.bio || ''
    editData.github = profile.value.github || ''
    editData.blog = profile.value.blog || ''
  }
}

const saveProfile = async () => {
  saving.value = true

  try {
    // 실제로는 API 호출하여 프로필 업데이트
    // 여기서는 로컬 데이터만 업데이트
    if (profile.value) {
      profile.value.bio = editData.bio
      profile.value.github = editData.github
      profile.value.blog = editData.blog
    }

    editMode.value = false

    // 성공 메시지 (실제로는 토스트 등으로 표시)
    alert('프로필이 성공적으로 업데이트되었습니다!')
  } catch (error) {
    console.error('프로필 저장 중 오류 발생:', error)
    alert('프로필 저장에 실패했습니다.')
  } finally {
    saving.value = false
  }
}

// 컴포넌트 마운트 시 프로필 로드
onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
:root {
  --samsung-blue: #1428a0;
  --samsung-blue-dark: #0f3a7c;
  --samsung-blue-light: #1e5bc6;
  --samsung-blue-alpha: rgba(20, 40, 160, 0.1);
}

.profile-view {
  min-height: calc(100vh - 64px);
  background-color: #f8f9fa;
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 3rem;
  color: #666;
  gap: 1rem;
}

/* 프로필 레이아웃 */
.profile-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  grid-template-rows: auto 1fr;
  gap: 2rem;
  grid-template-areas:
    'profile-card profile-stats'
    'recent-problems recent-problems';
}

/* 프로필 카드 */
.profile-card {
  grid-area: profile-card;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  text-align: center;
  height: fit-content;
}

.profile-avatar {
  margin-bottom: 1.5rem;
}

.profile-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-main-info {
  margin-bottom: 2rem;
}

.username {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.5rem;
}

.tier-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tier-badge {
  font-size: 1.1rem;
  font-weight: 600;
  padding: 0.25rem 0.75rem;
  border-radius: 6px;
  background: rgba(128, 128, 128, 0.1);
}

.rating {
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--samsung-blue);
}

.rank-info {
  display: flex;
  justify-content: space-around;
  gap: 1rem;
}

.rank-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.rank-label {
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 0.25rem;
}

.rank-value {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.profile-edit-btn {
  width: 100%;
  padding: 0.75rem;
  background: var(--samsung-blue);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.profile-edit-btn:hover {
  background: var(--samsung-blue-dark);
}

/* 통계 섹션 */
.profile-stats {
  grid-area: profile-stats;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.stats-section,
.tier-distribution-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
}

.stats-section h3,
.tier-distribution-section h3 {
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.1rem;
  font-weight: 600;
}

.solve-stats {
  display: flex;
  gap: 3rem;
  justify-content: center;
}

.solve-count,
.solve-rate {
  text-align: center;
}

.count-number,
.rate-number {
  display: block;
  font-size: 2rem;
  font-weight: 700;
  color: var(--samsung-blue);
  line-height: 1;
}

.count-label,
.rate-label {
  display: block;
  font-size: 0.9rem;
  color: #666;
  margin-top: 0.25rem;
}

/* 티어 차트 섹션 */
.tier-charts {
  display: flex;
  gap: 3rem;
  justify-content: space-around;
  align-items: center;
}

.tier-donut-chart {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.donut-placeholder {
  position: relative;
}

.tier-legend {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-text {
  font-size: 0.8rem;
  color: #666;
}

.difficulty-bar-chart {
  display: flex;
  gap: 1rem;
  align-items: end;
  height: 120px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

.bar-container {
  width: 40px;
  height: 80px;
  background: #f0f0f0;
  border-radius: 4px;
  display: flex;
  align-items: end;
  overflow: hidden;
}

.bar-fill {
  width: 100%;
  border-radius: 4px 4px 0 0;
  transition: height 0.3s ease;
}

.bar-label {
  font-size: 0.7rem;
  color: #666;
  text-align: center;
  writing-mode: vertical-rl;
  text-orientation: mixed;
  max-width: 40px;
}

/* 최근 해결한 문제 섹션 */
.recent-problems-section {
  grid-area: recent-problems;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
}

.recent-problems-section h3 {
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.2rem;
  font-weight: 600;
}

.problems-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.problem-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid transparent;
  transition: background-color 0.2s;
}

.problem-item:hover {
  background: #f0f0f0;
}

.problem-tier-badge {
  min-width: 60px;
}

.tier-indicator {
  display: inline-block;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
  text-align: center;
  min-width: 50px;
}

.problem-info {
  flex: 1;
}

.problem-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.25rem;
}

.problem-number {
  font-weight: 600;
  color: var(--samsung-blue);
  font-size: 0.9rem;
}

.problem-title {
  color: #333;
  font-size: 0.9rem;
}

.problem-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
  color: #666;
}

.problem-experience {
  text-align: right;
  min-width: 120px;
}

.exp-gained,
.exp-total {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.exp-label,
.exp-total-label {
  font-size: 0.7rem;
  color: #666;
}

.exp-value,
.exp-total-value {
  font-size: 0.8rem;
  font-weight: 600;
  color: #333;
}

.more-problems {
  text-align: center;
}

.more-btn {
  padding: 0.75rem 2rem;
  background: var(--samsung-blue);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.more-btn:hover {
  background: var(--samsung-blue-dark);
}

/* 편집 모달 */
.edit-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.edit-modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h2 {
  margin: 0;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #666;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  transition: color 0.2s;
}

.modal-close:hover {
  color: #333;
}

.edit-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-weight: 500;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
  font-family: inherit;
  resize: vertical;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--samsung-blue);
  box-shadow: 0 0 0 3px var(--samsung-blue-alpha);
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  padding-top: 1rem;
  border-top: 1px solid #e9ecef;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
}

.btn-primary {
  background-color: var(--samsung-blue);
  color: white;
}

.btn-primary:hover {
  background-color: var(--samsung-blue-dark);
}

.btn-secondary {
  background-color: #f8f9fa;
  color: #333;
  border: 1px solid #dee2e6;
}

.btn-secondary:hover {
  background-color: #e9ecef;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .profile-layout {
    grid-template-columns: 1fr;
    grid-template-areas:
      'profile-card'
      'profile-stats'
      'recent-problems';
  }

  .tier-charts {
    flex-direction: column;
    gap: 2rem;
  }

  .solve-stats {
    gap: 2rem;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 1rem;
  }

  .problem-item {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }

  .problem-experience {
    text-align: left;
    min-width: auto;
  }

  .exp-gained,
  .exp-total {
    flex-direction: row;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .solve-stats {
    flex-direction: column;
    gap: 1rem;
  }

  .problem-header {
    flex-direction: column;
    align-items: stretch;
    gap: 0.5rem;
  }

  .problem-meta {
    flex-direction: column;
    gap: 0.25rem;
  }
}
</style>
