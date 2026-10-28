package com.ncp03.coursechange.common.error;

import io.swagger.v3.oas.annotations.media.Schema;

/** 모든 에러 응답의 공통 형식. */
@Schema(description = "에러 응답")
public record ErrorResponse(
        @Schema(description = "HTTP 상태 코드", example = "404") int status,
        @Schema(description = "에러 코드", example = "COURSE_NOT_FOUND") String code,
        @Schema(description = "사용자에게 보여 줄 메시지", example = "존재하지 않는 강의(분반)입니다.") String message
) {
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.getStatus().value(), errorCode.name(), message);
    }
}
