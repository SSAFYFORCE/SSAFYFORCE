// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { checkAuthStatus } from '../utils/datatransferutil'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/problems',
      name: 'problems',
      component: () => import('../views/ProblemView.vue'),
    },
    {
      path: '/teams',
      name: 'teams',
      component: () => import('../views/TeamView.vue'),
    },
    {
      path: '/ranking',
      name: 'ranking',
      component: () => import('../views/RankingView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/NotFoundView.vue'),
    },
  ],
})

// 네비게이션 가드 설정
router.beforeEach(async (to, from, next) => {
  const isAuthenticated = await checkAuthStatus().then((user) => !!user)

  // 인증이 필요한 페이지에 접근하려는 경우
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  }
  // 인증이 되어 있어야 하는 페이지에 이미 인증된 사용자가 접근하려는 경우
  else if (to.meta.requiresGuest && isAuthenticated) {
    next({ name: 'home' })
  }
  // 그 외 경우는 정상 진행
  else {
    next()
  }
})

export default router
