// mockdata/profile.js

/**
 * 개인 프로필 정보 Mock 데이터 (API 응답 형태와 동일)
 */
export const mockProfileResponse = {
  profileImage:
    'https://static.solved.ac/uploads/profile/360x360/shiftpsh-picture-1750015381134.png',
  name: 'shiftpsh',
  solvedAcId: 'shiftpsh',
  lastProblemSyncTime: '2024-05-18T10:30:00Z',
}

/**
 * 개인이 소속된 팀 정보 Mock 데이터 (API 응답 형태와 동일)
 */
export const mockTeamsResponse = {
  teams: [
    {
      id: 1,
      name: '알고리즘 스터디',
      profileImage:
        'https://static.solved.ac/uploads/profile/360x360/shiftpsh-picture-1750015381134.png',
    },
    {
      id: 2,
      name: '코딩테스트 준비반',
      profileImage:
        'https://static.solved.ac/uploads/profile/360x360/shiftpsh-picture-1750015381134.png',
    },
    {
      id: 3,
      name: '백준 마스터즈',
      profileImage:
        'https://static.solved.ac/uploads/profile/360x360/shiftpsh-picture-1750015381134.png',
    },
  ],
}

/**
 * 개인 통계 정보 Mock 데이터 (API 응답 형태와 동일)
 */
export const mockStatsResponse = {
  tier: 'Ruby V',
  rating: 2745,
  rank: 1234,
  topPercent: 0.7,
  solvedProblems: 89,
  correctRate: 57.1,
  totalSubmissions: 156,
  streak: 23,
  recentSolved: [
    {
      problemNumber: 4792,
      title: '레드 블루 스패닝 트리',
      tier: 'Platinum III',
      solvedAt: '2024-05-18T15:30:00Z',
      experience: 1355,
      language: 'java 8',
    },
    {
      problemNumber: 1027,
      title: '고층 건물',
      tier: 'Gold III',
      solvedAt: '2024-05-17T14:20:00Z',
      experience: 1245,
      language: 'java 8',
    },
    {
      problemNumber: 1188,
      title: '음식 평론가',
      tier: 'Gold III',
      solvedAt: '2024-05-16T09:15:00Z',
      experience: 1245,
      language: 'java 8',
    },
    {
      problemNumber: 4792,
      title: '레드 블루 스패닝 트리',
      tier: 'Platinum III',
      solvedAt: '2024-05-15T16:45:00Z',
      experience: 1355,
      language: 'java 8',
    },
    {
      problemNumber: 4792,
      title: '레드 블루 스패닝 트리',
      tier: 'Platinum III',
      solvedAt: '2024-05-14T11:30:00Z',
      experience: 1355,
      language: 'java 8',
    },
  ],
}
