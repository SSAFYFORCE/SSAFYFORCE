import api from './index'

export const problemApi = {
  getAllProblems: (queryParams) => api.get(`/problems?${queryParams}`),
}
