import api from './index'

export const teamApi = {
  createTeam: (payload) => api.post('/teams', payload),
  getTeamById: (teamId) => api.get(`/teams/${teamId}`),
  fetchTeams: () => api.get('/teams'),
  joinTeam: (teamId) => api.post(`/teams/${teamId}/join`),
  getTeamDetail: (teamId) => api.get(`/teams/${teamId}`),
}
