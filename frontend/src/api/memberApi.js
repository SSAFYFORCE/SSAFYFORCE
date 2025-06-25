import api from './index'

export const memeberApi = {
  login: (credentials) => api.post('/auth/sign-in', credentials),
}
