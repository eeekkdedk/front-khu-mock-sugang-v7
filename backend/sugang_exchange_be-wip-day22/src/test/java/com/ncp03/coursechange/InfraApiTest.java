package com.ncp03.coursechange;

import com.ncp03.coursechange.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Swagger(OpenAPI), 헬스체크, CORS */
class InfraApiTest extends IntegrationTestSupport {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("OpenAPI 문서에 모든 API와 X-Student-Id 인증 방식이 있다")
    void openApiDocs() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("수강신청 교환 마켓 API"))
                .andExpect(jsonPath("$.paths['/api/courses'].get").exists())
                .andExpect(jsonPath("$.paths['/api/courses/filters'].get").exists())
                .andExpect(jsonPath("$.paths['/api/courses/{offeringId}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/enrollments/me'].get").exists())
                .andExpect(jsonPath("$.paths['/api/students'].get").exists())
                .andExpect(jsonPath("$.paths['/api/students/me'].get").exists())
                .andExpect(jsonPath("$.paths['/api/enrollments/me'].get.security[0].studentId").exists())
                .andExpect(jsonPath("$.components.securitySchemes.studentId.in").value("header"))
                .andExpect(jsonPath("$.components.securitySchemes.studentId.name").value("X-Student-Id"));
    }

    @Test
    @DisplayName("Swagger UI 페이지가 열린다")
    void swaggerUi() throws Exception {
        mvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("swagger-ui")));
        mvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("헬스체크 /actuator/health = UP (로드밸런서·배포 확인용)")
    void health() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("CORS: 허용된 프론트 주소는 X-Student-Id 헤더 포함 요청 허용, 다른 주소는 거부")
    void cors() throws Exception {
        mvc.perform(options("/api/enrollments/me")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "X-Student-Id"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        mvc.perform(get("/api/courses").header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
        mvc.perform(options("/api/courses")
                        .header("Origin", "http://evil.example.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
