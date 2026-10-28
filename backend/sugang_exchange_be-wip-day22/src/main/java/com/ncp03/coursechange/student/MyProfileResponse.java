package com.ncp03.coursechange.student;

import com.ncp03.coursechange.domain.Student;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "내 정보")
public record MyProfileResponse(
        @Schema(description = "학생 ID", example = "1") Long studentId,
        @Schema(description = "익명 닉네임", example = "졸린고양이") String nickname,
        @Schema(description = "학과", example = "컴퓨터공학과") String department,
        @Schema(description = "학년", example = "3") int yearLevel,
        @Schema(description = "신청 가능 최대 학점", example = "19") int maxCredits,
        @Schema(description = "현재 수강 학점 합계", example = "15") long currentCredits,
        @Schema(description = "매칭 이용 제한 종료 시각 (없으면 null)", example = "null", nullable = true)
        LocalDateTime blockedUntil
) {
    public static MyProfileResponse of(Student s, long currentCredits) {
        return new MyProfileResponse(s.getId(), s.getNickname(), s.getDepartment(), s.getYearLevel(),
                s.getMaxCredits(), currentCredits, s.getBlockedUntil());
    }
}
