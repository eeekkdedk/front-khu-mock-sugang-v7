package com.ncp03.coursechange.domain;

public enum PenaltyReason {
    /** 거절 버튼 */
    REJECTED,
    /** 세션 만료까지 무응답 (잠수) */
    NO_RESPONSE,
    /** 매크로·과도한 요청 등 악용 */
    ABUSE
}
