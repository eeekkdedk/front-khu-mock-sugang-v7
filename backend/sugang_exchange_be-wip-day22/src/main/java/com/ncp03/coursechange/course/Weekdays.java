package com.ncp03.coursechange.course;

/** 요일 숫자(1=월 ... 7=일) ↔ 한글 */
public final class Weekdays {

    private static final String[] LABELS = {"월", "화", "수", "목", "금", "토", "일"};

    private Weekdays() {
    }

    public static String label(int weekday) {
        if (weekday < 1 || weekday > 7) {
            throw new IllegalArgumentException("weekday must be 1..7: " + weekday);
        }
        return LABELS[weekday - 1];
    }
}
