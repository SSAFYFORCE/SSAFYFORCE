import api from './index'

export const rankingApi = {
  // 개인 랭킹 조회
  getMemberRanking: (period = 'DAILY', date = null) => {
    const params = { period }
    if (date) {
      params.date = date
    }
    return api.get('/rankings/member', { params })
  },

  // 팀 랭킹 조회  
  getTeamRanking: (period = 'DAILY', date = null) => {
    const params = { period }
    if (date) {
      params.date = date
    }
    return api.get('/rankings/team', { params })
  },

  // 통합 랭킹 조회 (타입에 따라 개인/팀 자동 선택)
  getRanking: (type, period = 'DAILY', date = null) => {
    if (type === 'member') {
      return rankingApi.getMemberRanking(period, date)
    } else if (type === 'team') {
      return rankingApi.getTeamRanking(period, date)
    } else {
      throw new Error(`지원하지 않는 랭킹 타입입니다: ${type}`)
    }
  }
}