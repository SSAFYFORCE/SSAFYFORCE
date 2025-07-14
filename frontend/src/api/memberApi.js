import api from './index'

export const memberApi = {
  // 닉네임 중복 확인
  checkNickname: (solvedAcId) =>
    api.get('/members/check-nickname', {
      params: { solvedAcId },
    }),

  // 내 정보 조회
  getMyProfile: async () => {
    try {
      const token = localStorage.getItem('accessToken')
      if (!token) {
        throw new Error('인증 토큰이 없습니다.')
      }

      const response = await api.get('/members/me', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      console.log('프로필 조회 응답:', response)
      return response
    } catch (error) {
      console.error('프로필 조회 에러:', error.response || error)
      throw error
    }
  },

  // 회원 정보 수정
  updateProfile: async (updateData) => {
    try {
      const response = await api.patch('/members/me', updateData);
      console.log('프로필 업데이트 응답:', response);
      return response;
    } catch (error) {
      console.error('프로필 업데이트 에러:', error);
      throw error;
    }
  },

  // 비밀번호 변경 (로그인 후)
  changePassword: (passwordData) => api.patch('/members/password', passwordData),

  // 비밀번호 재설정 (로그인 전)
  resetPassword: (passwordData) => api.post('/members/password/reset', passwordData),

  // 회원 탈퇴
  deleteMember: () => api.delete('/members/me'),

  // 특정 회원 정보 조회
  getMemberProfile: (solvedAcId) => api.get(`/members/${solvedAcId}`),

  // 내 팀 목록 조회 (getMyTeams 함수 주소 수정)
  getMyTeams: () => {
    return api.get('/teams/me');
  },

  // 회원이 소속된 팀 정보 조회
  getMemberTeams: (solvedAcId) => api.get(`/members/${solvedAcId}/teams`),

  // 프로필 동기화
  syncProfile: async (solvedAcId) => {
    try {
      const response = await api.post(`/members/${solvedAcId}/sync`)
      console.log('프로필 동기화 응답:', response)
      return response
    } catch (error) {
      console.error('프로필 동기화 에러:', error.response || error)
      throw error
    }
  },
}

/**
 * 프로필 이미지를 업로드하고 이미지 파일 이름을 반환합니다.
 * 백엔드에서 직접 DB를 업데이트하므로, 이 함수는 파일만 전송합니다.
 * @param {File} file - 업로드할 이미지 파일
 * @returns {Promise<Object>} - API 응답 객체
 */
export const uploadProfileImage = async (file) => {
  const formData = new FormData();
  formData.append('file', file); 

  console.log('FormData to be sent:', file);

  try {
    const response = await api.post('/members/profile-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    console.log('이미지 업로드 API 응답:', response);
    return response;
  } catch (error) {
    console.error('이미지 업로드 API 호출 실패:', error.response || error);
    throw error;
  }
};

/**
 * 프로필 이미지를 삭제합니다.
 * @returns {Promise<Object>} - API 응답 객체
 */
export const deleteProfileImage = async () => {
  try {
    const response = await api.delete('/members/profile-image');
    console.log('이미지 삭제 API 응답:', response);
    return response;
  } catch (error) {
    console.error('이미지 삭제 API 호출 실패:', error.response || error);
    throw error;
  }
};
