package com.ncp03.coursechange.enrollment;

import com.ncp03.coursechange.common.config.OpenApiConfig;
import com.ncp03.coursechange.common.error.ErrorResponse;
import com.ncp03.coursechange.common.web.CurrentStudentId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "수강", description = "내 수강 목록·시간표")
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "내 수강 목록",
            description = "X-Student-Id 학생이 지금 수강 중인 분반 목록. 각 항목에 수업 시간이 있어 시간표를 그릴 수 있습니다.",
            security = @SecurityRequirement(name = OpenApiConfig.STUDENT_ID_SCHEME))
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "헤더가 숫자가 아님 (INVALID_STUDENT_ID)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "헤더 없음 (MISSING_STUDENT_ID)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "없는 학생 (STUDENT_NOT_FOUND)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/me")
    public MyEnrollmentsResponse me(@Parameter(hidden = true) @CurrentStudentId Long studentId) {
        return enrollmentService.myEnrollments(studentId);
    }
}
