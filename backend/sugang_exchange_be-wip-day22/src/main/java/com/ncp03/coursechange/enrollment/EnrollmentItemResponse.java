package com.ncp03.coursechange.enrollment;

import com.ncp03.coursechange.course.CourseResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "수강 내역 1건")
public record EnrollmentItemResponse(
        @Schema(description = "수강 내역 ID. 교환 요청 때 '내놓을 강의'로 이 값을 보낸다", example = "3") Long enrollmentId,
        @Schema(description = "수강 확정 시각", example = "2026-10-22T21:00:00") LocalDateTime enrolledAt,
        @Schema(description = "강의 정보") CourseResponse course
) {
}
