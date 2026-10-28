package com.ncp03.coursechange;

import com.ncp03.coursechange.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentAndEnrollmentApiTest extends IntegrationTestSupport {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("GET /api/enrollments/me: 학생1의 수강 목록 5개, 15학점, 학수번호 순")
    void myEnrollments() throws Exception {
        mvc.perform(get("/api/enrollments/me").header("X-Student-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.nickname").value("졸린고양이"))
                .andExpect(jsonPath("$.totalCredits").value(15))
                .andExpect(jsonPath("$.maxCredits").value(19))
                .andExpect(jsonPath("$.count").value(5))
                .andExpect(jsonPath("$.enrollments", hasSize(5)))
                .andExpect(jsonPath("$.enrollments[*].course.offeringId", contains(3, 5, 7, 10, 18)))
                .andExpect(jsonPath("$.enrollments[0].enrollmentId", notNullValue()))
                .andExpect(jsonPath("$.enrollments[0].enrolledAt", notNullValue()))
                .andExpect(jsonPath("$.enrollments[3].course.courseName").value("프로그래밍언어론"))
                .andExpect(jsonPath("$.enrollments[3].course.scheduleText").value("월수 13:30-14:45"))
                .andExpect(jsonPath("$.enrollments[3].course.schedules", hasSize(2)));
    }

    @Test
    @DisplayName("학점 상한 18인 학생8은 18학점")
    void studentAtCreditLimit() throws Exception {
        mvc.perform(get("/api/enrollments/me").header("X-Student-Id", "8"))
                .andExpect(jsonPath("$.totalCredits").value(18))
                .andExpect(jsonPath("$.maxCredits").value(18))
                .andExpect(jsonPath("$.count").value(6));
    }

    @Test
    @DisplayName("X-Student-Id 헤더 오류: 없음 401, 숫자 아님·0 이하 400, 없는 학생 404")
    void headerErrors() throws Exception {
        mvc.perform(get("/api/enrollments/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("MISSING_STUDENT_ID"));
        mvc.perform(get("/api/enrollments/me").header("X-Student-Id", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_STUDENT_ID"));
        mvc.perform(get("/api/enrollments/me").header("X-Student-Id", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_STUDENT_ID"));
        mvc.perform(get("/api/enrollments/me").header("X-Student-Id", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("STUDENT_NOT_FOUND"));
        mvc.perform(get("/api/students/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/students: 10명, 학번은 내려주지 않는다")
    void studentList() throws Exception {
        mvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].studentId").value(1))
                .andExpect(jsonPath("$[0].nickname").value("졸린고양이"))
                .andExpect(jsonPath("$[0].yearLevel").value(3))
                .andExpect(jsonPath("$[0].studentNo").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/students/me: 내 정보와 현재 학점")
    void myProfile() throws Exception {
        mvc.perform(get("/api/students/me").header("X-Student-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.department").value("컴퓨터공학과"))
                .andExpect(jsonPath("$.maxCredits").value(19))
                .andExpect(jsonPath("$.currentCredits").value(15))
                .andExpect(jsonPath("$.blockedUntil").doesNotExist())
                .andExpect(jsonPath("$.studentNo").doesNotExist());
    }
}
