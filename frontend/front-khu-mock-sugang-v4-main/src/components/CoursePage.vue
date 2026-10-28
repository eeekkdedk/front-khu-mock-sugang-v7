<template>
    <div class="container">
<!-- props 로 sidebar 에서 선택 바꿈 이벤트가 내려오면 탭 바꿔주기-->
        <notice-menu 
         @home="$emit('home')"
         v-if="isNoticeMenu"></notice-menu>
        <wish-menu v-if="isWishMenu"></wish-menu>
        <list-menu v-bind:courseList="courseList" v-if="isListMenu"></list-menu>
        <exchange-menu v-if="isExchangeMenu" v-bind:courseList="courseList" @exchangeComplete="applyExchange"></exchange-menu>
        <time-limit v-if="isRegisterMenu && timeLimit>0" v-bind:timeLimit="timeLimit"></time-limit>
        <!-- timeLimit 보고 모달 띄우기 -->
        <register-menu @reset="resetCourses" @renewal="renewalCourse" @home="$emit('home')" v-bind:Courses="courseList" v-if="isRegisterMenu && timeLimit<=0"></register-menu>
        <course-footer></course-footer>
    </div>
</template>

<script>
import ListMenu from './menu/ListMenu.vue';
import NoticeMenu from './menu/NoticeMenu.vue';
import RegisterMenu from './menu/RegisterMenu.vue';
import WishMenu from './menu/WishMenu.vue';
import ExchangeMenu from './menu/ExchangeMenu.vue';
import TimeLimit from './common/TimeLimit.vue';
import CourseFooter from './common/CourseFooter.vue';

export default {
    props : ['timeLimit','menu'],
    data(){
        return {
            courseList : [],
        }
    },
    components : {
        'list-menu' : ListMenu,
        'notice-menu' : NoticeMenu,
        'register-menu' : RegisterMenu,
        'wish-menu' : WishMenu,
        'exchange-menu' : ExchangeMenu,
        'time-limit' : TimeLimit,
        CourseFooter 
    },
    computed: {
        isNoticeMenu(){
            if(this.menu=='noticeMenu') return true;
            return false ; 
        },
        isWishMenu(){
            if(this.menu=='wishMenu') return true;
            return false;
        },
        isListMenu(){
            if(this.menu=='listMenu') return true;
            return false; 
        },
        isRegisterMenu(){
            if(this.menu=='registerMenu') return true;
            return false; 
        },
        isExchangeMenu(){
            return this.menu === 'exchangeMenu';
        }
    },
    methods : {
        renewalCourse(courses){
            this.courseList=courses;
        },
        resetCourses(){
            this.courseList=[];
        },
        applyExchange(exchange){
            // 시연용: 준 강의는 수강신청내역에서 제거하고 받은 강의를 추가합니다.
            // 실제 API 연결 시 교환 트랜잭션 완료 후 서버의 수강신청내역을 다시 조회하세요.
            if (!exchange) return;
            const ownedCode = exchange.ownedCode;
            const receivedCourse = exchange.receivedCourse || exchange;
            const ownedIndex = this.courseList.findIndex(item => item && item.code && item.code === ownedCode);
            if (ownedIndex !== -1) {
                this.courseList.splice(ownedIndex, 1);
            } else if (this.courseList.length && this.courseList.every(item => typeof item === 'number')) {
                // 기존 데모 수강신청은 숫자 ID만 저장하므로, 코드 매핑이 없는 경우 한 과목을 제거해 교환을 시연합니다.
                this.courseList.pop();
            }
            if (receivedCourse && receivedCourse.code && !this.courseList.some(item => item && item.code === receivedCourse.code)) {
                this.courseList.push(receivedCourse);
            }
        }
    }
}
</script>

<style>

</style>