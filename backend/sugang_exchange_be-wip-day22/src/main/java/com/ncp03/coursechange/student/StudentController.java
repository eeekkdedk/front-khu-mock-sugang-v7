package com.ncp03.coursechange.student;

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

import java.util.List;

@Tag(name = "학생", description = "데모용 가상 로그인 (X-Student-Id 헤더)")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "학생 목록 (데모 로그인 선택용)",
            description = "로그인 화면에서 학생을 고를 때 씁니다. 학번은 내려주지 않습니다.")
    @GetMapping
    public List<StudentSummaryResponse> list() {
        return studentService.list();
    }

    @Operation(summary = "내 정보", description = "X-Student-Id 헤더의 학생 정보와 현재 수강 학점",
            security = @SecurityRequirement(name = OpenApiConfig.STUDENT_ID_SCHEME))
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "401", description = "헤더 없음 (MISSING_STUDENT_ID)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "없는 학생 (STUDENT_NOT_FOUND)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/me")
    public MyProfileResponse me(@Parameter(hidden = true) @CurrentStudentId Long studentId) {
        return studentService.me(studentId);
    }
}
