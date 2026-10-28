import axios from 'axios';

const api = axios.create({
  baseURL: '', // vue.config.js 프록시 설정 사용
});

// [요청 인터셉터] 
// 로그인 시 백엔드가 원하는 'studentNo' 형식을 보장하고, 인증 헤더를 자동으로 주입합니다.
// api/index.js 수정
api.interceptors.request.use((config) => {
  // 1. 기본 헤더 설정
  config.headers['Content-Type'] = 'application/json';
  
  // 2. CSRF 우회를 위한 시도 (일부 서버는 특정 헤더가 있으면 CSRF 체크를 건너뜁니다)
  config.headers['X-Requested-With'] = 'XMLHttpRequest';
  config.headers['X-CSRF-TOKEN'] = ''; // 빈 값을 보내어 형식을 맞춤
  
  // 3. 기존 인증 정보
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }

  return config;
});


// [응답 인터셉터]
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // 401(Unauthorized) 또는 403(Forbidden) 발생 시 대응
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      console.warn(`[API ${error.response.status}] 인증 실패 - 다시 로그인해주세요.`);
      // 로그아웃 처리 로직이 필요하다면 여기에 추가 (예: localStorage.clear())
      return Promise.reject(error);
    }

    // 백엔드에서 보낸 에러 메시지 팝업 출력
    if (error.response && error.response.data && error.response.data.message) {
      alert(error.response.data.message);
    }
    
    return Promise.reject(error);
  }
);

export default api;