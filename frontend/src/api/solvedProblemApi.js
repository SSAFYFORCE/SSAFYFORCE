// src/api/solvedProblemApi.js
import api from './index'

export const solvedProblemApi = {
  // 해결한 문제 무한스크롤 조회
  getRecentSolvedProblems(solvedAcId, cursor = null) {
    try {
      const params = {}
      if (cursor) {
        params.cursor = cursor
      }

      return api.get(`/solved-problems/recent/${solvedAcId}`, { params })
    } catch (error) {
      console.error('최근 해결한 문제 조회 실패:', error)
      throw error
    }
  },

  // 프로필 동기화 (백준 문제 해결 내역 동기화) - 긴 타임아웃 적용
  syncSolvedProblems(solvedAcId) {
    try {
      return api.post(
        `/solved-problems/sync/${solvedAcId}`,
        {},
        {
          timeout: 120000, // 2분 (120초) 타임아웃
        },
      )
    } catch (error) {
      console.error('프로필 동기화 실패:', error)
      throw error
    }
  },
}
