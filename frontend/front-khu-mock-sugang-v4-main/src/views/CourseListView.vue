<template>
  <div class="course-page">
    <h2>강의 목록</h2>
    <div class="search-bar">
      <input v-model="searchQuery" @input="fetchCourses" placeholder="강의명 검색..." />
    </div>
    <div class="course-grid">
      <div v-for="course in courses" :key="course.id" class="course-item" @click="goToDetail(course.id)">
        <h3>{{ course.name }}</h3>
        <p>{{ course.department }} | {{ course.credits }}학점</p>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/index';

export default {
  data() {
    return {
      courses: [],
      searchQuery: ''
    };
  },
  async mounted() {
    await this.fetchCourses();
  },
  methods: {
    async fetchCourses() {
      try {
        const res = await api.get(`/courses?name=${this.searchQuery}`);
        this.courses = res.data;
      } catch (err) {
        console.error("목록 로드 실패", err);
      }
    },
    goToDetail(id) {
      this.$router.push(`/courses/${id}`);
    }
  }
};
</script>

<style scoped>
.course-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1rem; }
.course-item { border: 1px solid #ddd; padding: 1rem; cursor: pointer; border-radius: 4px; }
.course-item:hover { background: #f0f0f0; }
</style>
