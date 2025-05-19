<!-- src/components/common/Footer.vue -->
<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3>알고리즘 플랫폼</h3>
        <p>알고리즘 문제 해결 능력을 향상시키고 팀과 함께 성장하세요.</p>
      </div>

      <div class="footer-section">
        <h3>빠른 링크</h3>
        <ul>
          <li><router-link to="/">홈</router-link></li>
          <li><router-link to="/problems">문제</router-link></li>
          <li><router-link to="/teams">팀</router-link></li>
          <li><router-link to="/ranking">랭킹</router-link></li>
        </ul>
      </div>

      <div class="footer-section">
        <h3>고객 지원</h3>
        <ul>
          <li><a href="#">자주 묻는 질문</a></li>
          <li><a href="#">문의하기</a></li>
          <li><a href="#">이용약관</a></li>
          <li><a href="#">개인정보 처리방침</a></li>
        </ul>
      </div>

      <div class="footer-section">
        <h3>소셜 미디어</h3>
        <div class="social-icons">
          <a href="#" aria-label="Github">
            <font-awesome-icon :icon="['fab', 'github']" />
          </a>
          <a href="#" aria-label="Twitter">
            <font-awesome-icon :icon="['fab', 'twitter']" />
          </a>
          <a href="#" aria-label="Discord">
            <font-awesome-icon :icon="['fab', 'discord']" />
          </a>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <p>&copy; 2025 알고리즘 플랫폼. All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup>
// Footer 컴포넌트 로직
</script>

<style scoped>
.footer {
  background-color: #333;
  color: #fff;
  padding: 3rem 2rem 1rem;
}

.footer-content {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
}

.footer-section {
  flex: 1;
  min-width: 200px;
  margin-bottom: 2rem;
  padding-right: 2rem;
}

.footer-section h3 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: #fff;
}

.footer-section p {
  color: #bbb;
  line-height: 1.6;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 0.5rem;
}

.footer-section a {
  color: #bbb;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-section a:hover {
  color: #e91e63;
}

.social-icons {
  display: flex;
  gap: 1rem;
}

.social-icons a {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background-color: #444;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.social-icons a:hover {
  background-color: #e91e63;
}

.footer-bottom {
  text-align: center;
  padding-top: 2rem;
  margin-top: 2rem;
  border-top: 1px solid #444;
}

.footer-bottom p {
  color: #888;
  font-size: 0.9rem;
}
</style>
