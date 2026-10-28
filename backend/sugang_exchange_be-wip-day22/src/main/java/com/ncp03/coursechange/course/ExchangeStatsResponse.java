package com.ncp03.coursechange.course;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강의별 교환 현황 (인원수만 공개)")
public record ExchangeStatsResponse(
        @Schema(description = "이 분반을 원하는 교환 희망자 수 (진행 중 요청 기준)", example = "3") long wantCount,
        @Schema(description = "이 분반을 교환에 내놓은 보유자 수 (진행 중 요청 기준)", example = "1") long offerCount
) {
    public static final ExchangeStatsResponse EMPTY = new ExchangeStatsResponse(0, 0);
}
