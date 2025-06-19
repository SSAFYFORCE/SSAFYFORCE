<template>
  <div class="ranking-filter">
    <div v-if="showTypeFilter" class="button-group type-filter">
      <button :class="{ active: selectedType === 'individual' }" @click="selectType('individual')">
        개인
      </button>
      <button :class="{ active: selectedType === 'team' }" @click="selectType('team')">팀</button>
    </div>

    <div class="button-group period-filter">
      <button
        v-for="periodOption in periodOptions"
        :key="periodOption.value"
        :class="{ active: selectedPeriod === periodOption.value }"
        @click="selectPeriod(periodOption.value)"
      >
        {{ periodOption.label }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits, defineProps, onMounted } from 'vue' // defineProps 임포트 추가

const props = defineProps({
  // 이 prop이 true일 때만 개인/팀 필터를 보여줍니다.
  showTypeFilter: {
    type: Boolean,
    default: false, // 기본값은 false로 설정하여 메인 페이지에서는 보이지 않도록
  },
})

const emit = defineEmits(['update:period', 'update:type']) // update:type 이벤트 추가

// 기간 선택 옵션 정의
const periodOptions = [
  { label: '일간', value: 'daily' },
  { label: '주간', value: 'weekly' },
  { label: '월간', value: 'monthly' },
]

// 선택된 기간 상태 (기본값 'daily')
const selectedPeriod = ref('daily')
// 선택된 타입 상태 (showTypeFilter가 true일 때만 의미 있음)
const selectedType = ref('individual') // 랭킹 페이지 기본값은 '개인'

// 기간 선택 함수
const selectPeriod = (period) => {
  selectedPeriod.value = period
  emit('update:period', selectedPeriod.value)
}

// 타입 선택 함수 (새로 추가)
const selectType = (type) => {
  selectedType.value = type
  emit('update:type', selectedType.value)
}

// 컴포넌트가 마운트될 때 (처음 로드될 때) 초기 값을 부모에게 전달합니다.
onMounted(() => {
  selectPeriod(selectedPeriod.value) // 초기 'daily' 값 전달

  // showTypeFilter가 true일 때만 초기 타입 값도 전달
  if (props.showTypeFilter) {
    selectType(selectedType.value) // 초기 'individual' 값 전달
  }
})
</script>

<style scoped>
.ranking-filter {
  display: flex;
  /* 타입 필터와 기간 필터 사이의 간격 */
  gap: 1rem;
  justify-content: flex-end; /* 우상단에 배치 */
  margin-bottom: 2rem;
  padding: 1rem 0; /* 상하 패딩만 추가 */
}

.button-group {
  display: flex;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden; /* 버튼 모서리 둥글게 */
}

.button-group button {
  padding: 0.75rem 1.25rem;
  border: none;
  background-color: white;
  cursor: pointer;
  font-size: 1rem;
  color: #555;
  transition:
    background-color 0.2s,
    color 0.2s;
}

.button-group button:not(:last-child) {
  border-right: 1px solid #e0e0e0; /* 버튼 사이에 구분선 */
}

.button-group button.active {
  background-color: var(--samsung-blue); /* 활성 버튼 배경색 */
  color: white; /* 활성 버튼 텍스트 색상 */
  font-weight: 600;
}

.button-group button:hover:not(.active) {
  background-color: #f0f0f0; /* 비활성 버튼 호버 시 배경색 */
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .ranking-filter {
    flex-direction: column; /* 모바일에서 세로로 정렬 */
    justify-content: center; /* 모바일에서 중앙 정렬 */
    padding: 1rem;
    gap: 0.75rem; /* 버튼 그룹 사이 간격 조정 */
  }
  .button-group {
    width: 100%;
    justify-content: center;
  }
  .button-group button {
    flex: 1; /* 버튼들이 가로로 균등하게 공간 차지 */
    padding: 0.75rem 0.5rem; /* 패딩 조정 */
  }
}
</style>
