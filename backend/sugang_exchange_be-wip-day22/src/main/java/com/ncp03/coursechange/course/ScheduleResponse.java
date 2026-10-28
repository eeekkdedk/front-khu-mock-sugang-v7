package com.ncp03.coursechange.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ncp03.coursechange.domain.ClassTime;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

@Schema(description = "수업 시간 1칸")
public record ScheduleResponse(
        @Schema(description = "요일 (1=월 ... 7=일)", example = "2") int weekday,
        @Schema(description = "요일 한글", example = "화") String weekdayLabel,
        @Schema(description = "시작 시각", example = "15:00", type = "string")
        @JsonFormat(pattern = "HH:mm") LocalTime startTime,
        @Schema(description = "종료 시각", example = "16:15", type = "string")
        @JsonFormat(pattern = "HH:mm") LocalTime endTime
) {
    public static ScheduleResponse from(ClassTime t) {
        return new ScheduleResponse(t.getWeekday(), Weekdays.label(t.getWeekday()), t.getStartTime(), t.getEndTime());
    }
}
