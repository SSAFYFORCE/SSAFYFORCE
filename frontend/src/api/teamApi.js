import api from './index'

export const teamApi = {
  createTeam: (payload) => api.post('/teams', payload),
  getTeamById: (teamId) => api.get(`/teams/${teamId}`),
  fetchTeams: () => api.get('/teams'),
//  joinTeam: (teamId) => api.post(`/teams/${teamId}/join`),
  getTeamDetail: (teamId) => api.get(`/teams/${teamId}`),
  /** 가입 요청 생성 */
  requestJoin: teamId => api.post(`/teams/${teamId}/join-requests`),

  /** 특정 팀의 가입 요청 목록 조회(리더 전용) */
  fetchJoinRequests: teamId => api.get(`/teams/${teamId}/join-requests`),
  
  /** 가입 요청 단건 조회 */
  getJoinRequest: (teamId, reqId) => 
    api.get(`/teams/${teamId}/join-requests/${reqId}`),
  
  /** 가입 요청 승인 */
  approveJoinRequest: (teamId, reqId) =>
    api.patch(`/teams/${teamId}/join-requests/${reqId}/approve`),
  
  /** 가입 요청 거절 */
  rejectJoinRequest: (teamId, reqId) => 
    api.patch(`/teams/${teamId}/join-requests/${reqId}/reject`),

  cancelJoinRequest: (teamId) => 
    api.delete(`/teams/${teamId}/join-requests/cancel`),

  /** 내 팀 목록 조회 */
  getMyTeams: () => api.get('/teams/me'),
  /** 내가 가입 신청한 팀 조회 */
  getmyTeamJoinRequestList: () => api.get('/teams/join-requests/me'),
  /** 팀 탈퇴 */
  withdraw: (teamId, memberId) =>
    api.delete(`/teams/${teamId}/members/${memberId}`),
  /** 팀장 위임 */
  mandateLeader: (teamId, newLeaderId) =>
    api.patch(`/teams/${teamId}/leader/${newLeaderId}`),
}
