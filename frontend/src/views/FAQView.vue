<template>
  <div class="faq-container">
    <div class="faq-header">
      <router-link to="/" class="back-link">
        <font-awesome-icon :icon="['fas', 'arrow-left']" />
        질문 목록
      </router-link>
      <h1>자주 묻는 질문</h1>
    </div>
    
    <div class="faq-layout">
      <!-- 왼쪽 카테고리 목록 -->
      <div class="faq-categories">
        <button 
          v-for="category in categories" 
          :key="category.id"
          :class="['category-button', { active: selectedCategory === category.id }]"
          @click="selectedCategory = category.id"
        >
          {{ category.name }}
        </button>
      </div>

      <!-- 오른쪽 답변 내용 -->
      <div class="faq-content">
        <div v-if="selectedCategory === 'background'" class="faq-answer">
          <p>
            김영한님의 JPA 강의를 통해 스터디를 진행한후 해당 기술을 이론학습으로 끝나는게아니라 JPA를 적용해서 실 사용자가 있는 프로젝트를 만들어보자 ! 라는 다짐과 함께 탄생한게 
            <span class="highlight">SSAFY FORCE</span>입니다.
          </p>
        </div>
        <div v-if="selectedCategory === 'members'" class="faq-answer">
          <p>
            <span class="highlight">SSAFY 13기</span> 서울캠퍼스 16반
          </p>
          <div class="member-profiles">
            <div class="member-profile">
              <img src="@/mockdata/jeongyeon.png" alt="김승호" class="profile-image" />
              <span class="profile-name">김승호</span>
            </div>
            <div class="member-profile">
              <img src="@/mockdata/jeongyeon.png" alt="김정연" class="profile-image" />
              <span class="profile-name">김정연</span>
            </div>
            <div class="member-profile">
              <img src="@/mockdata/jeongyeon.png" alt="이권민" class="profile-image" />
              <span class="profile-name">이권민</span>
            </div>
            <div class="member-profile">
              <img src="@/mockdata/jeongyeon.png" alt="이은성" class="profile-image" />
              <span class="profile-name">이은성</span>
            </div>
          </div>
        </div>
        <div v-if="selectedCategory === 'auth'" class="faq-answer">
          <h2 class="answer-title">회원가입, 비밀번호 찾기시 인증이 안돼요.</h2>
          <ul class="answer-list">
            <li>solved.ac 우측상단에 프로필 이미지 클릭 -> 설정 -> 두번째 '이름' 필드</li>
            <li>nameNative (모국어) name (영어) 필드에 인증코드를 붙여넣기 하세요.</li>
            <li>프로필에 이름 표시 스위치가 켜져있는지 확인하세요.</li>
          </ul>
          <div class="image-container">
            <img src="@/mockdata/name.png" alt="인증 예시" class="guide-image" />
            <span class="image-caption">Solved.ac 프로필 이름 설정 예시</span>
          </div>
        </div>
        <div v-if="selectedCategory === 'origin'" class="faq-answer">
          <p>
            김영한님의 JPA 강의를 통해 스터디를 진행한후 해당 기술을 이론학습으로 끝나는게아니라 JPA를 적용해서 실 사용자가 있는 프로젝트를 만들어보자 ! 라는 다짐과 함께 탄생한게 
            <span class="highlight">SSAFY FORCE</span>입니다.
          </p>
        </div>

        <div class="inquiry-guide">
          <p>원하는 답변을 찾지 못하셨나요?</p>
          <p>그럼 하단의 <a href="https://forms.gle/6ZwK4FQjzZVR9D896" target="_blank" class="inquiry-link">문의하기</a>에서 직접 문의 해주세요.</p>
          <p>최대한 빠르게 답변드리도록 하겠습니다!</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const categories = [
  { id: 'background', name: '탄생배경' },
  { id: 'members', name: '구성원이 누군가요?' },
  { id: 'auth', name: '인증' }
]

const selectedCategory = ref('background')
</script>

<style scoped>
.faq-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.faq-header {
  margin-bottom: 2rem;
}

.back-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  text-decoration: none;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.back-link:hover {
  color: var(--samsung-blue);
}

h1 {
  font-size: 2rem;
  color: #333;
  margin: 0;
}

.faq-layout {
  display: flex;
  gap: 2rem;
}

.faq-categories {
  width: 200px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.category-button {
  text-align: left;
  padding: 0.75rem 1rem;
  border: none;
  background: none;
  color: var(--samsung-blue);
  cursor: pointer;
  font-size: 1rem;
  border-radius: 4px;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.category-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f8f9fa;
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.2s ease;
  z-index: -1;
}

.category-button:hover::before,
.category-button.active::before {
  transform: scaleX(1.1);
}

.category-button.active {
  font-weight: 600;
}

.faq-content {
  flex: 1;
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.faq-answer {
  line-height: 1.6;
  color: #333;
}

.highlight {
  color: var(--samsung-blue);
  font-weight: bold;
}

.member-profiles {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 2rem;
}

.member-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.profile-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.profile-image:hover {
  transform: scale(1.05);
}

.profile-name {
  font-size: 1rem;
  color: #333;
  font-weight: 500;
}

.answer-title {
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 1rem;
  font-weight: 600;
}

.answer-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.answer-list li {
  position: relative;
  padding-left: 1.5rem;
  margin-bottom: 0.75rem;
  line-height: 1.6;
}

.answer-list li::before {
  content: '-';
  position: absolute;
  left: 0;
  color: var(--samsung-blue);
}

.image-container {
  margin-top: 1.5rem;
  text-align: center;
}

.guide-image {
  max-width: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.image-caption {
  display: block;
  margin-top: 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

.inquiry-guide {
  text-align: center;
  margin-top: 2rem;
  padding: 2rem;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.inquiry-guide p {
  margin: 0.5rem 0;
  font-size: 1.1rem;
  line-height: 1.6;
}

.inquiry-link {
  color: var(--samsung-blue);
  text-decoration: none;
  font-weight: 600;
}

.inquiry-link:hover {
  text-decoration: underline;
}
</style> 