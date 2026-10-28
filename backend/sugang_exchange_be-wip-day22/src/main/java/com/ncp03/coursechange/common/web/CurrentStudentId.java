package com.ncp03.coursechange.common.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 파라미터에 붙이면 X-Student-Id 헤더 값을 Long으로 받는다.
 * 헤더가 없으면 401(MISSING_STUDENT_ID), 숫자가 아니면 400(INVALID_STUDENT_ID).
 * 학생이 실제로 존재하는지는 서비스에서 확인한다.
 * 데모용 가상 로그인이다. 로그인 기능을 붙이면 이 리졸버만 바꾸면 된다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentStudentId {
}
