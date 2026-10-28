package com.ncp03.coursechange.student;

import com.ncp03.coursechange.domain.Student;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "학생 요약 (데모 로그인 선택용). 학번은 노출하지 않는다")
public record StudentSummaryResponse(
        @Schema(description = "학생 ID (X-Student-Id 헤더에 넣는 값)", example = "1") Long studentId,
        @Schema(description = "익명 닉네임", example = "졸린고양이") String nickname,
        @Schema(description = "학과", example = "컴퓨터공학과") String department,
        @Schema(description = "학년", example = "3") int yearLevel
) {
    public static StudentSummaryResponse from(Student s) {
        return new StudentSummaryResponse(s.getId(), s.getNickname(), s.getDepartment(), s.getYearLevel());
    }
}
