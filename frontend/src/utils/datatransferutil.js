// src/utils/datatransferutil.js

import rankingsData from '../mockdata/rankings.js'
import problemsData from '../mockdata/problems.js'
import teamsData from '../mockdata/teams.js'

/**
 * 랭킹 데이터를 시간 주기에 따라 가져오는 유틸리티 함수
 * @param {string} period - 'daily', 'weekly', 'monthly' 중 하나
 * @param {string} type - 'individual' 또는 'team'
 * @returns {Promise} - 해당 기간과 타입의 랭킹 데이터
 */
export const fetchRankings = (period = 'daily', type = 'individual') => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const data = rankingsData[period][type]
      resolve(data)
    }, 300)
  })
}

/**
 * 전체 랭킹 데이터를 가져오는 함수 (더 많은 데이터)
 * @param {string} period - 'daily', 'weekly', 'monthly' 중 하나
 * @param {string} type - 'individual' 또는 'team'
 * @param {number} page - 페이지 번호
 * @param {number} limit - 페이지당 항목 수
 * @returns {Promise} - 랭킹 데이터와 페이지 정보
 */
export const fetchFullRankings = (period = 'daily', type = 'individual', page = 1, limit = 20) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const baseData = rankingsData[period][type]
      // 더 많은 데이터를 시뮬레이트하기 위해 기존 데이터를 확장
      const extendedData = []
      for (let i = 0; i < 100; i++) {
        const item = baseData[i % baseData.length]
        extendedData.push({
          ...item,
          id: i + 1,
          rank: i + 1,
          isCurrent: i === 25, // 26등을 현재 사용자로 설정
        })
      }

      const startIndex = (page - 1) * limit
      const endIndex = startIndex + limit
      const pageData = extendedData.slice(startIndex, endIndex)

      resolve({
        data: pageData,
        totalCount: extendedData.length,
        currentPage: page,
        totalPages: Math.ceil(extendedData.length / limit),
      })
    }, 500)
  })
}

/**
 * 문제 목록을 가져오는 함수
 * @param {Object} filters - 필터 옵션
 * @returns {Promise} - 문제 목록
 */
export const fetchProblems = (filters = {}) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      let filteredProblems = [...problemsData]

      // 티어 필터링
      if (filters.tier) {
        filteredProblems = filteredProblems.filter((p) => p.tier === filters.tier)
      }

      // 해결 상태 필터링
      if (filters.solvedStatus !== undefined) {
        filteredProblems = filteredProblems.filter((p) => p.isSolved === filters.solvedStatus)
      }

      // 알고리즘 필터링
      if (filters.algorithm) {
        filteredProblems = filteredProblems.filter((p) =>
          p.algorithms.some((alg) => alg.includes(filters.algorithm)),
        )
      }

      // 검색어 필터링
      if (filters.searchQuery) {
        const query = filters.searchQuery.toLowerCase()
        filteredProblems = filteredProblems.filter(
          (p) =>
            p.title.toLowerCase().includes(query) || p.problemNumber.toString().includes(query),
        )
      }

      resolve(filteredProblems)
    }, 400)
  })
}

/**
 * 팀 목록을 가져오는 함수
 * @param {Object} filters - 필터 옵션
 * @returns {Promise} - 팀 목록
 */
export const fetchTeams = (filters = {}) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      let filteredTeams = [...teamsData]

      // 검색어 필터링
      if (filters.searchQuery) {
        const query = filters.searchQuery.toLowerCase()
        filteredTeams = filteredTeams.filter(
          (team) =>
            team.name.toLowerCase().includes(query) ||
            team.description.toLowerCase().includes(query) ||
            team.tags.some((tag) => tag.toLowerCase().includes(query)),
        )
      }

      // 태그 필터링
      if (filters.tag) {
        filteredTeams = filteredTeams.filter((team) => team.tags.includes(filters.tag))
      }

      // 가입 가능 여부 필터링
      if (filters.availableOnly) {
        filteredTeams = filteredTeams.filter((team) => team.memberCount < team.maxMembers)
      }

      resolve(filteredTeams)
    }, 400)
  })
}

/**
 * 특정 팀의 상세 정보를 가져오는 함수
 * @param {number} teamId - 팀 ID
 * @returns {Promise} - 팀 상세 정보
 */
export const fetchTeamDetail = (teamId) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const team = teamsData.find((t) => t.id === teamId)
      if (team) {
        resolve(team)
      } else {
        reject(new Error('팀을 찾을 수 없습니다.'))
      }
    }, 300)
  })
}

/**
 * 팀에 가입하는 함수
 * @param {number} teamId - 팀 ID
 * @returns {Promise} - 가입 결과
 */
export const joinTeam = (teamId) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const teamIndex = teamsData.findIndex((t) => t.id === teamId)
      if (teamIndex !== -1) {
        const team = teamsData[teamIndex]
        if (team.memberCount < team.maxMembers) {
          team.memberCount += 1
          team.isJoined = true
          resolve({ success: true, message: '팀에 성공적으로 가입했습니다!' })
        } else {
          reject(new Error('팀이 가득 찼습니다.'))
        }
      } else {
        reject(new Error('팀을 찾을 수 없습니다.'))
      }
    }, 500)
  })
}

/**
 * 사용자 프로필 정보를 가져오는 함수
 * @returns {Promise} - 사용자 프로필
 */
export const fetchUserProfile = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        const user = JSON.parse(userStr)
        // 추가 프로필 정보 시뮬레이트
        const profile = {
          ...user,
          solvedProblems: 89,
          totalSubmissions: 156,
          correctRate: 57.1,
          streak: 15,
          joinDate: '2023-08-15',
          lastActive: '2024-05-18',
          bio: '알고리즘 문제 해결을 좋아하는 개발자입니다.',
          github: 'https://github.com/test',
          blog: 'https://blog.test.com',
          solvedByTier: {
            Bronze: 25,
            Silver: 35,
            Gold: 20,
            Platinum: 8,
            Diamond: 1,
            Ruby: 0,
          },
          recentSolved: [
            {
              problemNumber: 4792,
              title: '레드 블루 스패닝 트리',
              tier: 'Platinum III',
              solvedAt: '2024-05-18',
              experience: 1355,
            },
            {
              problemNumber: 1027,
              title: '고층 건물',
              tier: 'Gold III',
              solvedAt: '2024-05-17',
              experience: 1245,
            },
            {
              problemNumber: 1188,
              title: '톱니 바퀴',
              tier: 'Gold III',
              solvedAt: '2024-05-16',
              experience: 1245,
            },
            {
              problemNumber: 4792,
              title: '레드 블루 스패닝 트리',
              tier: 'Platinum III',
              solvedAt: '2024-05-15',
              experience: 1355,
            },
            {
              problemNumber: 4792,
              title: '레드 블루 스패닝 트리',
              tier: 'Platinum III',
              solvedAt: '2024-05-14',
              experience: 1355,
            },
            {
              problemNumber: 11726,
              title: '2×n 타일링',
              tier: 'Silver III',
              solvedAt: '2024-05-13',
              experience: 895,
            },
            {
              problemNumber: 2579,
              title: '계단 오르기',
              tier: 'Silver III',
              solvedAt: '2024-05-12',
              experience: 895,
            },
            {
              problemNumber: 1463,
              title: '1로 만들기',
              tier: 'Silver III',
              solvedAt: '2024-05-11',
              experience: 895,
            },
            {
              problemNumber: 9461,
              title: '파도반 수열',
              tier: 'Silver III',
              solvedAt: '2024-05-10',
              experience: 895,
            },
            {
              problemNumber: 1932,
              title: '정수 삼각형',
              tier: 'Silver I',
              solvedAt: '2024-05-09',
              experience: 1095,
            },
            {
              problemNumber: 1149,
              title: 'RGB거리',
              tier: 'Silver I',
              solvedAt: '2024-05-08',
              experience: 1095,
            },
            {
              problemNumber: 1012,
              title: '유기농 배추',
              tier: 'Silver II',
              solvedAt: '2024-05-07',
              experience: 995,
            },
            {
              problemNumber: 2667,
              title: '단지번호붙이기',
              tier: 'Silver I',
              solvedAt: '2024-05-06',
              experience: 1095,
            },
            {
              problemNumber: 7576,
              title: '토마토',
              tier: 'Gold V',
              solvedAt: '2024-05-05',
              experience: 1100,
            },
            {
              problemNumber: 1697,
              title: '숨바꼭질',
              tier: 'Silver I',
              solvedAt: '2024-05-04',
              experience: 1095,
            },
            {
              problemNumber: 2178,
              title: '미로 탐색',
              tier: 'Silver I',
              solvedAt: '2024-05-03',
              experience: 1095,
            },
            {
              problemNumber: 7569,
              title: '토마토',
              tier: 'Gold V',
              solvedAt: '2024-05-02',
              experience: 1100,
            },
            {
              problemNumber: 1991,
              title: '트리 순회',
              tier: 'Silver I',
              solvedAt: '2024-05-01',
              experience: 1095,
            },
            {
              problemNumber: 11725,
              title: '트리의 부모 찾기',
              tier: 'Silver II',
              solvedAt: '2024-04-30',
              experience: 995,
            },
            {
              problemNumber: 1167,
              title: '트리의 지름',
              tier: 'Gold II',
              solvedAt: '2024-04-29',
              experience: 1345,
            },
          ],
        }
        resolve(profile)
      } else {
        resolve(null)
      }
    }, 300)
  })
}

/**
 * 사용자 인증 상태를 확인하는 함수
 * @returns {Promise} - 사용자 정보 또는 null
 */
export const checkAuthStatus = () => {
  return new Promise((resolve) => {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      resolve(JSON.parse(userStr))
    } else {
      resolve(null)
    }
  })
}

/**
 * 로그인 함수
 * @param {Object} credentials - 사용자 자격 증명
 * @returns {Promise} - 로그인 결과
 */
export const login = (credentials) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (credentials.username === 'test' && credentials.password === 'password') {
        const user = {
          id: 1,
          username: 'test',
          profileImage: '/src/mockdata/shiftpsh_profile.png',
          tier: 'Silver II',
          rating: 1245,
        }
        localStorage.setItem('user', JSON.stringify(user))
        resolve(user)
      } else {
        reject(new Error('로그인에 실패했습니다. 사용자 이름과 비밀번호를 확인하세요.'))
      }
    }, 500)
  })
}

/**
 * 로그아웃 함수
 * @returns {Promise} - 로그아웃 결과
 */
export const logout = () => {
  return new Promise((resolve) => {
    localStorage.removeItem('user')
    resolve(true)
  })
}
