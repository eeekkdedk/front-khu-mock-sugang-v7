package com.ncp03.coursechange.course;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 수업 시간 목록을 "화목 15:00-16:15" 같은 한 줄 문자열로 만든다. 같은 시간대의 요일은 묶는다. */
public final class ScheduleFormatter {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private ScheduleFormatter() {
    }

    public static String format(List<ScheduleResponse> schedules) {
        if (schedules.isEmpty()) {
            return "";
        }
        Map<String, StringBuilder> daysByRange = new LinkedHashMap<>();
        schedules.stream()
                .sorted((a, b) -> a.weekday() != b.weekday()
                        ? Integer.compare(a.weekday(), b.weekday())
                        : a.startTime().compareTo(b.startTime()))
                .forEach(s -> daysByRange
                        .computeIfAbsent(s.startTime().format(HH_MM) + "-" + s.endTime().format(HH_MM),
                                k -> new StringBuilder())
                        .append(s.weekdayLabel()));
        return daysByRange.entrySet().stream()
                .map(e -> e.getValue() + " " + e.getKey())
                .collect(Collectors.joining(", "));
    }
}
