import axios from 'axios';

const api = axios.create({
  baseURL: '', // vue.config.js 프록시 타도록 상대 경로 설정
});

// [요청 인터셉터] README 규칙에 따른 인증 헤더 주입
api.interceptors.request.use((config) => {
  const studentId = localStorage.getItem('selectedStudentId') || localStorage.getItem('studentId');
  const token = localStorage.getItem('accessToken');

  // 1. 학번 헤더 주입 (DEMO 모드 및 학생 구분용)
  if (studentId) {
    config.headers['X-Student-Id'] = studentId;
  }
  
  // 2. Bearer 토큰 주입 (TOKEN 모드용)
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }

  return config;
});

// [응답 인터셉터]
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // 401(Unauthorized) 또는 403(Forbidden) 인증 에러 시 팝업을 차단하고 콘솔 경고만 출력
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      console.warn(`[API ${error.response.status}] 인증/권한 부족 (데모 데이터 유지):`, error.config?.url);
      return Promise.reject(error);
    }
    
    // 그 외 백엔드가 전달한 에러 메시지 팝업 처리
    if (error.response && error.response.data) {
      const { message } = error.response.data;
      if (message) alert(message);
    }
    return Promise.reject(error);
  }
);

export default api;