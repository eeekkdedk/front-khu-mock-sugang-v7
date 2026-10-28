package com.ncp03.coursechange;

import com.ncp03.coursechange.common.error.ApiException;
import com.ncp03.coursechange.common.error.ErrorCode;
import com.ncp03.coursechange.common.web.CurrentStudentIdTestAccess;
import com.ncp03.coursechange.course.CourseServiceTestAccess;
import com.ncp03.coursechange.course.ScheduleFormatter;
import com.ncp03.coursechange.course.ScheduleResponse;
import com.ncp03.coursechange.course.Weekdays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** DB 없이 도는 단위 테스트 (Docker가 없어도 항상 실행된다). */
class UnitTest {

    private static ScheduleResponse s(int day, String start, String end) {
        return new ScheduleResponse(day, Weekdays.label(day), LocalTime.parse(start), LocalTime.parse(end));
    }

    @Test
    @DisplayName("수업 시간 한 줄 표시: 같은 시간대 요일은 묶는다")
    void scheduleText() {
        assertThat(ScheduleFormatter.format(List.of(s(4, "15:00", "16:15"), s(2, "15:00", "16:15"))))
                .isEqualTo("화목 15:00-16:15");
        assertThat(ScheduleFormatter.format(List.of(s(1, "09:00", "10:15"), s(3, "13:00", "14:15"))))
                .isEqualTo("월 09:00-10:15, 수 13:00-14:15");
        assertThat(ScheduleFormatter.format(List.of(s(5, "10:30", "12:15")))).isEqualTo("금 10:30-12:15");
        assertThat(ScheduleFormatter.format(List.of())).isEmpty();
    }

    @Test
    @DisplayName("요일 라벨")
    void weekdayLabels() {
        assertThat(Weekdays.label(1)).isEqualTo("월");
        assertThat(Weekdays.label(7)).isEqualTo("일");
        assertThatThrownBy(() -> Weekdays.label(0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("X-Student-Id 파싱")
    void parseStudentId() {
        assertThat(CurrentStudentIdTestAccess.parse("1")).isEqualTo(1L);
        assertThat(CurrentStudentIdTestAccess.parse(" 7 ")).isEqualTo(7L);
        assertError(null, ErrorCode.MISSING_STUDENT_ID);
        assertError("", ErrorCode.MISSING_STUDENT_ID);
        assertError("abc", ErrorCode.INVALID_STUDENT_ID);
        assertError("0", ErrorCode.INVALID_STUDENT_ID);
        assertError("-3", ErrorCode.INVALID_STUDENT_ID);
    }

    private static void assertError(String raw, ErrorCode expected) {
        assertThatThrownBy(() -> CurrentStudentIdTestAccess.parse(raw))
                .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.getErrorCode()).isEqualTo(expected));
    }

    @Test
    @DisplayName("검색어 LIKE 패턴: 소문자 + 특수문자 이스케이프")
    void likePattern() {
        assertThat(CourseServiceTestAccess.toLikePattern(null)).isNull();
        assertThat(CourseServiceTestAccess.toLikePattern("  ")).isNull();
        assertThat(CourseServiceTestAccess.toLikePattern(" CSE ")).isEqualTo("%cse%");
        assertThat(CourseServiceTestAccess.toLikePattern("50%_off")).isEqualTo("%50\\%\\_off%");
    }
}
