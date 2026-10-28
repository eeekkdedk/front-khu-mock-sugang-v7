<template>
  <div class="login-wrapper">
    <div class="login-card">
      <h1>KHU 수강 교환</h1>
      <div class="input-group">
        <input v-model="studentNo" type="text" placeholder="학번 (예: 2024000001)" />
        <input v-model="password" type="password" placeholder="비밀번호" />
      </div>
      <button @click="handleLogin" class="login-btn">로그인</button>
    </div>
  </div>
</template>

<script>
import { authApi } from '@/api/auth';

export default {
  data() {
    return {
      studentNo: '2024000001', // 테스트 편의를 위한 기본값
      password: 'password123'
    };
  },
  methods: {
    async handleLogin() {
      try {
        const res = await authApi.login(this.studentNo, this.password);
        // 백엔드에서 준 토큰 저장
        localStorage.setItem('accessToken', res.data.accessToken);
        localStorage.setItem('selectedStudentId', this.studentNo);
        
        alert('로그인 성공!');
        this.$router.push('/courses'); // 로그인 성공 시 강의 목록으로
      } catch (err) {
        const msg = err.response?.data?.message || '로그인 실패 (ID/PW 확인)';
        alert(msg);
      }
    }
  }
};
</script>

<style scoped>
.login-wrapper { display: flex; justify-content: center; align-items: center; height: 100vh; background: #f5f5f5; }
.login-card { padding: 2rem; background: white; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); width: 300px; }
.input-group input { width: 100%; margin-bottom: 1rem; padding: 0.5rem; box-sizing: border-box; }
.login-btn { width: 100%; padding: 0.7rem; background: #007bff; color: white; border: none; cursor: pointer; }
</style>
