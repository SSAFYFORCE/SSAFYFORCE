<!-- src/components/common/LoadingSpinner.vue -->
<template>
  <div class="loading-spinner" :class="{ overlay: overlay }">
    <div class="spinner-container">
      <div class="spinner" :class="size"></div>
      <div v-if="message" class="loading-message">{{ message }}</div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value),
  },
  message: {
    type: String,
    default: '',
  },
  overlay: {
    type: Boolean,
    default: false,
  },
})
</script>

<style scoped>
.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.loading-spinner.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  z-index: 9999;
  backdrop-filter: blur(2px);
}

.spinner-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.spinner {
  border: 3px solid #f3f3f3;
  border-top: 3px solid var(--samsung-blue);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.spinner.small {
  width: 24px;
  height: 24px;
  border-width: 2px;
}

.spinner.medium {
  width: 40px;
  height: 40px;
  border-width: 3px;
}

.spinner.large {
  width: 60px;
  height: 60px;
  border-width: 4px;
}

.loading-message {
  color: #666;
  font-size: 0.9rem;
  text-align: center;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
