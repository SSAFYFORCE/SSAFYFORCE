import api from './index'

export const problemApi = {
  getAllProblems: (queryParams) => api.post(`/problems?${queryParams}`),
}
