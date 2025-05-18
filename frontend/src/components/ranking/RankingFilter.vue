<!-- src/components/ranking/RankingFilter.vue -->
<template>
  <div class="ranking-filter">
    <div class="period-tabs">
      <button
        :class="['tab', { active: selectedPeriod === 'daily' }]"
        @click="changePeriod('daily')"
      >
        일간
      </button>
      <button
        :class="['tab', { active: selectedPeriod === 'weekly' }]"
        @click="changePeriod('weekly')"
      >
        주간
      </button>
      <button
        :class="['tab', { active: selectedPeriod === 'monthly' }]"
        @click="changePeriod('monthly')"
      >
        월간
      </button>
    </div>

    <div class="type-tabs">
      <button
        :class="['tab', { active: selectedType === 'individual' }]"
        @click="changeType('individual')"
      >
        개인
      </button>
      <button :class="['tab', { active: selectedType === 'team' }]" @click="changeType('team')">
        팀
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits } from 'vue'

const emit = defineEmits(['update:period', 'update:type'])

const selectedPeriod = ref('daily')
const selectedType = ref('individual')

const changePeriod = (period) => {
  selectedPeriod.value = period
  emit('update:period', period)
}

const changeType = (type) => {
  selectedType.value = type
  emit('update:type', type)
}
</script>

<style scoped>
.ranking-filter {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-bottom: 2rem;
}

.period-tabs,
.type-tabs {
  display: flex;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tab {
  padding: 0.75rem 1.5rem;
  background-color: #f5f5f5;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition:
    background-color 0.2s,
    color 0.2s;
}

.tab:not(:last-child) {
  border-right: 1px solid #ddd;
}

.tab:hover {
  background-color: #eee;
}

.tab.active {
  background-color: var(--samsung-blue);
  color: white;
  box-shadow: 0 2px 8px rgba(20, 40, 160, 0.3);
}
</style>
