package com.ncp03.coursechange.common.web;

/** 패키지 내부 메서드를 테스트에서 호출하기 위한 통로 */
public final class CurrentStudentIdTestAccess {
    private CurrentStudentIdTestAccess() {
    }

    public static Long parse(String raw) {
        return CurrentStudentIdArgumentResolver.parse(raw);
    }
}
