// src/stores/ranking.js
import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { fetchRankings } from '@/utils/datatransferutil'

export const useRankingStore = defineStore('ranking', () => {
  const rankings = reactive({
    daily: {
      individual: [],
      team: [],
    },
    weekly: {
      individual: [],
      team: [],
    },
    monthly: {
      individual: [],
      team: [],
    },
  })

  const isLoading = ref(false)
  const error = ref(null)

  // 랭킹 불러오기
  const loadRankings = async (period = 'daily', type = 'individual') => {
    isLoading.value = true
    error.value = null

    try {
      const data = await fetchRankings(period, type)
      rankings[period][type] = data
      return data
    } catch (err) {
      error.value = err.message
      return []
    } finally {
      isLoading.value = false
    }
  }

  // 모든 랭킹 불러오기
  const loadAllRankings = async () => {
    isLoading.value = true
    error.value = null

    const periods = ['daily', 'weekly', 'monthly']
    const types = ['individual', 'team']

    try {
      const promises = []

      for (const period of periods) {
        for (const type of types) {
          promises.push(
            fetchRankings(period, type).then((data) => {
              rankings[period][type] = data
            }),
          )
        }
      }

      await Promise.all(promises)
      return rankings
    } catch (err) {
      error.value = err.message
      return rankings
    } finally {
      isLoading.value = false
    }
  }

  return {
    rankings,
    isLoading,
    error,
    loadRankings,
    loadAllRankings,
  }
})
