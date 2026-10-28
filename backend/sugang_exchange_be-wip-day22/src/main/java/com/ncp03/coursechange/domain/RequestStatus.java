package com.ncp03.coursechange.domain;

/** 교환 요청 상태. WAITING → MATCHED → COMPLETED, 중간에 CANCELLED 가능. */
public enum RequestStatus {
    WAITING, MATCHED, COMPLETED, CANCELLED;

    /** 진행 중인 요청인지 (같은 수강 내역은 진행 중 요청 1개에만 묶인다). */
    public boolean isActive() {
        return this == WAITING || this == MATCHED;
    }
}
