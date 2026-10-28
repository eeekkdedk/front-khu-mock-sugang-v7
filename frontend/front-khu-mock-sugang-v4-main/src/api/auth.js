import api from './index';

export const authApi = {
  // 백엔드 스펙: { studentNo, password }
  login(studentNo, password) {
    return api.post('/auth/login', { studentNo, password });
  },
  getMe() {
    return api.get('/users/me');
  },
  logout() {
    return api.post('/auth/logout');
  }
};
