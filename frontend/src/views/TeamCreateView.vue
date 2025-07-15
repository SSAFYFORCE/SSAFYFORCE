<!-- src/views/TeamCreateView.vue -->
<template>
    <div class="team-create-view">
      <div class="container">
        <div class="page-header">
          <h1>새 팀 만들기</h1>
          <p>함께 성장할 팀을 생성하세요</p>
        </div>
  
        <form @submit.prevent="handleCreate" class="create-form">
          <div class="form-group">
            <label for="name">팀 이름</label>
            <input
              id="name"
              v-model="form.name"
              type="text"
              placeholder="팀 이름을 입력하세요"
              class="form-input"
              required
            />
          </div>
  
          <div class="form-group">
            <label for="description">팀 설명</label>
            <textarea
              id="description"
              v-model="form.description"
              placeholder="팀에 대한 설명을 적어주세요"
              class="form-textarea"
              rows="3"
            ></textarea>
          </div>  
  
          <button type="submit" :disabled="loading" class="btn btn-primary">
            <template v-if="loading">
              <font-awesome-icon :icon="['fas','spinner']" spin /> 생성 중...
            </template>
            <template v-else>팀 생성하기</template>
          </button>
        </form>
  
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { teamApi } from '@/api/teamApi'
  
  const router = useRouter()
  
  const form = ref({ name: '', description: '', maxMembers: 10 })
  const loading = ref(false)
  const errorMessage = ref('')
  
  const handleCreate = async () => {
    if (!form.value.name) return
    loading.value = true
    errorMessage.value = ''
    try {
      await teamApi.createTeam(form.value)
      router.push({ name: 'teams' })
    } catch (e) {
      console.error('팀 생성 실패:', e)
      errorMessage.value = e.response?.data?.message || '팀 생성 중 오류가 발생했습니다.'
    } finally {
      loading.value = false
    }
  }
  </script>
  
  <style scoped>
  .team-create-view {
    min-height: calc(100vh - 64px);
    background-color: #f9f9f9;
    padding: 2rem 0;
  }
  
  .container {
    max-width: 600px;
    margin: 0 auto;
    padding: 0 2rem;
  }
  
  .page-header {
    text-align: center;
    margin-bottom: 2rem;
  }
  
  .page-header h1 {
    font-size: 2rem;
    color: #333;
    margin-bottom: 0.5rem;
  }
  
  .page-header p {
    color: #666;
  }
  
  .create-form {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    display: flex;
    flex-direction: column;
    align-items: stretch;
  }
  
  .form-group {
    margin-bottom: 1.5rem;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
    color: #333;
  }
  
  .form-input,
  .form-textarea {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
    font-family: inherit;
    line-height: 1.5;
  }
  
  .btn-primary {
    width: 100%;
    max-width: 300px;
    margin: 1.5rem auto 0;
    padding: 0.875rem;
    font-size: 1.1rem;
    font-weight: 600;
    background-color: var(--samsung-blue);
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
  }

  .error-message {
    margin-top: 1rem;
    color: #dc3545;
    text-align: center;
  }

  .form-input:focus,
  .form-textarea:focus {
  outline: none;
  border-color: var(--samsung-blue);
  box-shadow: 0 0 0 3px var(--samsung-blue-alpha);
  }

  </style>