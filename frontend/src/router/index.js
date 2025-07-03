// src/router/index.js
import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
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
      path: '/teams/create',
      name: 'team-create',
      component: () => import('../views/TeamCreateView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/ranking',
      name: 'ranking',
      component: () => import('../views/RankingView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/SignUpView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/NotFoundView.vue'),
    },
  ],
})

// 네비게이션 가드
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken')
  const isAuthenticated = !!token

  // 인증이 필요한 페이지에 접근하려는 경우
  if (to.meta.requiresAuth) {
    if (!isAuthenticated) {
      next({
        path: '/login',
        query: { redirect: to.fullPath },
      })
    } else {
      next()
    }
  }
  // 비로그인 사용자만 접근 가능한 페이지(로그인, 회원가입)에
  // 로그인된 사용자가 접근하려는 경우
  else if (to.meta.requiresGuest && isAuthenticated) {
    next({ path: '/' })
  }
  // 그 외의 경우는 정상적으로 진행
  else {
    next()
  }
})

export default router
