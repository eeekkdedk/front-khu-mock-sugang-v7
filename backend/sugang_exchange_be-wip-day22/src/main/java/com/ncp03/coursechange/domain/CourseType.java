package com.ncp03.coursechange.domain;

/** 이수 구분. DB에는 영문 코드, 화면에는 label을 쓴다. */
public enum CourseType {
    MAJOR_BASIC("전공기초"),
    MAJOR_REQUIRED("전공필수"),
    MAJOR_ELECTIVE("전공선택"),
    GENERAL("교양");

    private final String label;

    CourseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
