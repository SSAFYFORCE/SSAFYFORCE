import api from './index'

export const teamApi = {
  getTeamById: (teamId) => api.post(`/teams/${teamId}`),
}
