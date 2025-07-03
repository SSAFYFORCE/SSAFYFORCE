// src/composables/useInfiniteScroll.js
import { ref, onMounted, onUnmounted } from 'vue'

export function useInfiniteScroll(loadMore, options = {}) {
  const {
    threshold = 200, // 하단에서 몇 픽셀 전에 로드할지
    disabled = ref(false), // 무한 스크롤 비활성화 여부
  } = options

  const isLoading = ref(false)
  const hasMore = ref(true)

  const handleScroll = () => {
    if (disabled.value || isLoading.value || !hasMore.value) return

    const scrollTop = window.pageYOffset || document.documentElement.scrollTop
    const windowHeight = window.innerHeight
    const documentHeight = document.documentElement.scrollHeight

    if (scrollTop + windowHeight >= documentHeight - threshold) {
      loadMore()
    }
  }

  onMounted(() => {
    window.addEventListener('scroll', handleScroll, { passive: true })
  })

  onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll)
  })

  return {
    isLoading,
    hasMore,
    setLoading: (loading) => {
      isLoading.value = loading
    },
    setHasMore: (more) => {
      hasMore.value = more
    },
  }
}
