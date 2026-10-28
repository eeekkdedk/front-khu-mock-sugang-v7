package com.ncp03.coursechange.enrollment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "내 수강 목록 (시간표)")
public record MyEnrollmentsResponse(
        @Schema(description = "학생 ID", example = "1") Long studentId,
        @Schema(description = "익명 닉네임", example = "졸린고양이") String nickname,
        @Schema(description = "현재 수강 학점 합계", example = "15") long totalCredits,
        @Schema(description = "신청 가능 최대 학점", example = "19") int maxCredits,
        @Schema(description = "수강 과목 수", example = "5") int count,
        @Schema(description = "수강 내역 (학수번호 순)") List<EnrollmentItemResponse> enrollments
) {
}
