<template>
   <div class="content-wrapper">
       <course-list v-bind:courseList="courseList" v-bind:listMenu="true"></course-list>
   </div>
</template>

<script>
import CourseList from '../shared/CourseList.vue';
import api from '@/api'; // 1. 백엔드 통신 모듈 불러오기[cite: 3]

export default {
  name: 'ListMenu',
  props: ['courseList'],
  components: { 
    CourseList 
  },
  data() {
    return {
      myEnrollments: [] // 백엔드에서 받아온 내 수강 신청 내역 데이터를 담을 배열[cite: 3]
    };
  },
  async mounted() {
    // 2. 컴포넌트 마운트 시 백엔드 API로부터 내 수강 신청 내역 로드
    this.fetchMyEnrollments();
  },
  methods: {
    async fetchMyEnrollments() {
      try {
        const res = await api.get('/api/enrollments/me'); // GET /api/enrollments/me 호출[cite: 3, 4]
        this.myEnrollments = res.data;
      } catch (err) {
        console.error('수강 내역 로드 실패:', err);
      }
    }
  }
}
</script>

<style>

</style>