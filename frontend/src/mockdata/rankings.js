// src/mockdata/rankings.js

const dailyRankings = {
  individual: [
    {
      id: 1,
      rank: 1,
      username: 'rnsjals0905',
      profileImage:
        'https://static.solved.ac/uploads/profile/360x360/rnsjals0905-picture-1634897037674.png',
      tier: 'Platinum V',
      rating: 2745,
      isCurrent: false,
    },
    {
      id: 2,
      rank: 1, // 동일 순위 가능
      username: 'chosarah',
      profileImage: '/src/mockdata/default_profile.png',
      tier: 'Gold I',
      rating: 2745,
      isCurrent: true,
    },
    {
      id: 3,
      rank: 3,
      username: 'rkdmfqka',
      profileImage:
        'https://static.solved.ac/uploads/profile/360x360/rkdmfqka-picture-1742040676387.png',
      tier: 'Platinum V',
      rating: 2745,
      isCurrent: false,
    },
    {
      id: 4,
      rank: 4,
      username: 'moneycant',
      profileImage:
        'https://static.solved.ac/uploads/profile/360x360/moneycant-picture-1746584335146.png',
      tier: 'Gold III',
      rating: 2745,
      isCurrent: false,
    },
    {
      id: 5,
      rank: 5,
      username: 'epnjh0807',
      profileImage: '/src/mockdata/default_profile.png',
      tier: 'Gold I',
      rating: 2745,
      isCurrent: false,
    },
    {
      id: 6,
      rank: 6,
      username: 'shiftpsh',
      profileImage: '/src/mockdata/shiftpsh_profile.png',
      tier: 'Ruby V',
      rating: 2745,
      isCurrent: false,
    },
  ],
  team: [
    {
      id: 1,
      rank: 1,
      teamName: '13기 서울 16반',
      teamImage: '/src/mockdata/shiftpsh_profile.png',
      tier: 'Ruby V',
      rating: 2745,
    },
    {
      id: 2,
      rank: 2,
      teamName: '13기 서울 16반',
      teamImage: '/src/mockdata/shiftpsh_profile.png',
      tier: 'Ruby V',
      rating: 2740,
    },
    {
      id: 3,
      rank: 3,
      teamName: '13기 서울 16반',
      teamImage: '/src/mockdata/shiftpsh_profile.png',
      tier: 'Ruby IV',
      rating: 2720,
    },
    {
      id: 4,
      rank: 4,
      teamName: '13기 서울 16반',
      teamImage: '/src/mockdata/shiftpsh_profile.png',
      tier: 'Ruby IV',
      rating: 2710,
    },
    {
      id: 5,
      rank: 5,
      teamName: '13기 서울 16반',
      teamImage: '/src/mockdata/shiftpsh_profile.png',
      tier: 'Ruby III',
      rating: 2690,
    },
  ],
}

const weeklyRankings = {
  individual: [
    // 주간 랭킹 데이터 (형식은 일간과 동일)
  ],
  team: [
    // 주간 팀 랭킹 데이터 (형식은 일간과 동일)
  ],
}

const monthlyRankings = {
  individual: [
    // 월간 랭킹 데이터 (형식은 일간과 동일)
  ],
  team: [
    // 월간 팀 랭킹 데이터 (형식은 일간과 동일)
  ],
}

export default {
  daily: dailyRankings,
  weekly: weeklyRankings,
  monthly: monthlyRankings,
}
