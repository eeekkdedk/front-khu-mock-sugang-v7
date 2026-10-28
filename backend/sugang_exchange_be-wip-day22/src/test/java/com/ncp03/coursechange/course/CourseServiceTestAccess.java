package com.ncp03.coursechange.course;

/** 패키지 내부 메서드를 테스트에서 호출하기 위한 통로 */
public final class CourseServiceTestAccess {
    private CourseServiceTestAccess() {
    }

    public static String toLikePattern(String keyword) {
        return CourseService.toLikePattern(keyword);
    }
}
