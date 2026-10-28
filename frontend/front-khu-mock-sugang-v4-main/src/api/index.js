import axios from 'axios';

const api = axios.create({
  baseURL: '/api', // 프록시 설정을 통해 백엔드 주소로 연결됨
});

// 요청 인터셉터: 모든 요청에 자동으로 인증 토큰을 실어 보냄
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

// 응답 인터셉터: 401(인증만료) 발생 시 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      alert('세션이 만료되었습니다. 다시 로그인해주세요.');
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
