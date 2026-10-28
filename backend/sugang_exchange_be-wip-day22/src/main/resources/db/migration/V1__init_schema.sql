-- =====================================================================
-- 수강신청 교환 마켓 · MySQL 스키마 (V1)
-- 대상: MySQL 8.0.16 이상 (CHECK 제약 사용). NCP Cloud DB for MySQL 8.x 호환.
-- 한 학기만 다루는 데모라 semesters / student_semesters 테이블은 두지 않는다.
--
-- [학사] students, courses, course_offerings, class_times, enrollments
-- [교환] wishlists, exchange_requests, exchange_preferences,
--        match_sessions, match_participants, penalty_events
-- =====================================================================

-- ---------------------------------------------------------------------
-- 학사: 가상 학사 시스템 (실제 학교와 무관한 가상 데이터만 저장)
-- ---------------------------------------------------------------------

CREATE TABLE students (
    student_id     BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    student_no     VARCHAR(20)      NOT NULL COMMENT '가상 학번. 화면에 노출하지 않음',
    nickname       VARCHAR(30)      NOT NULL COMMENT '익명 랜덤 닉네임. 화면에는 이것만 노출',
    department     VARCHAR(50)      NOT NULL COMMENT '소속 학과',
    year_level     TINYINT UNSIGNED NOT NULL COMMENT '학년 (대기열 우선순위에 사용)',
    max_credits    TINYINT UNSIGNED NOT NULL DEFAULT 19 COMMENT '신청 가능 최대 학점',
    blocked_until  DATETIME(6)      NULL COMMENT '패널티로 매칭 이용이 제한되는 종료 시각',
    created_at     DATETIME(6)      NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (student_id),
    UNIQUE KEY uk_students_student_no (student_no),
    UNIQUE KEY uk_students_nickname (nickname),
    CONSTRAINT ck_students_year_level  CHECK (year_level BETWEEN 1 AND 6),
    CONSTRAINT ck_students_max_credits CHECK (max_credits BETWEEN 1 AND 30)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '학생';

CREATE TABLE courses (
    course_id    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    course_code  VARCHAR(20)     NOT NULL COMMENT '학수번호 (예: CSE3010)',
    course_name  VARCHAR(100)    NOT NULL COMMENT '과목명',
    department   VARCHAR(50)     NOT NULL COMMENT '개설 학과',
    course_type  VARCHAR(20)     NOT NULL COMMENT '이수 구분',
    PRIMARY KEY (course_id),
    UNIQUE KEY uk_courses_course_code (course_code),
    KEY idx_courses_department (department),
    CONSTRAINT ck_courses_course_type
        CHECK (course_type IN ('MAJOR_BASIC', 'MAJOR_REQUIRED', 'MAJOR_ELECTIVE', 'GENERAL'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '과목';

CREATE TABLE course_offerings (
    offering_id     BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT,
    course_id       BIGINT UNSIGNED   NOT NULL,
    section_no      VARCHAR(5)        NOT NULL COMMENT '분반 (예: 01)',
    professor_name  VARCHAR(50)       NOT NULL,
    credits         TINYINT UNSIGNED  NOT NULL COMMENT '학점',
    capacity        SMALLINT UNSIGNED NOT NULL COMMENT '정원',
    classroom       VARCHAR(50)       NULL COMMENT '강의실',
    PRIMARY KEY (offering_id),
    UNIQUE KEY uk_offerings_course_section (course_id, section_no),
    -- enrollments의 (offering_id, course_id) 복합 FK가 참조하는 키
    UNIQUE KEY uk_offerings_id_course (offering_id, course_id),
    CONSTRAINT fk_offerings_course FOREIGN KEY (course_id) REFERENCES courses (course_id),
    CONSTRAINT ck_offerings_credits  CHECK (credits BETWEEN 1 AND 6),
    CONSTRAINT ck_offerings_capacity CHECK (capacity >= 1)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '개설 분반 (교환·검색의 실제 단위)';

CREATE TABLE class_times (
    class_time_id  BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    offering_id    BIGINT UNSIGNED  NOT NULL,
    weekday        TINYINT UNSIGNED NOT NULL COMMENT '1=월 ... 7=일',
    start_time     TIME             NOT NULL,
    end_time       TIME             NOT NULL,
    PRIMARY KEY (class_time_id),
    UNIQUE KEY uk_class_times_slot (offering_id, weekday, start_time),
    CONSTRAINT fk_class_times_offering FOREIGN KEY (offering_id) REFERENCES course_offerings (offering_id),
    CONSTRAINT ck_class_times_weekday CHECK (weekday BETWEEN 1 AND 7),
    CONSTRAINT ck_class_times_range   CHECK (start_time < end_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '분반별 수업 시간 (화·목 수업이면 2행)';

-- 현재 수강 내역 = 학생이 지금 차지하고 있는 좌석.
-- course_id를 함께 저장하고 (offering_id, course_id) 복합 FK로 분반과 일치를 보장한다.
-- 덕분에 UNIQUE(student_id, course_id)로 "같은 과목 두 분반 동시 수강"을 DB가 막는다.
-- 교환은 두 행의 offering_id/course_id를 맞바꾸는 방식으로 한다 (학생은 그대로).
CREATE TABLE enrollments (
    enrollment_id  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    student_id     BIGINT UNSIGNED NOT NULL,
    offering_id    BIGINT UNSIGNED NOT NULL,
    course_id      BIGINT UNSIGNED NOT NULL,
    enrolled_at    DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    version        INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '낙관적 락용',
    PRIMARY KEY (enrollment_id),
    UNIQUE KEY uk_enrollments_student_offering (student_id, offering_id),
    UNIQUE KEY uk_enrollments_student_course (student_id, course_id),
    KEY idx_enrollments_offering (offering_id),
    CONSTRAINT fk_enrollments_student  FOREIGN KEY (student_id) REFERENCES students (student_id),
    CONSTRAINT fk_enrollments_offering FOREIGN KEY (offering_id, course_id)
        REFERENCES course_offerings (offering_id, course_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '현재 수강 내역 (좌석 배정)';

-- ---------------------------------------------------------------------
-- 교환 서비스
-- ---------------------------------------------------------------------

CREATE TABLE wishlists (
    wishlist_id  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    student_id   BIGINT UNSIGNED NOT NULL,
    offering_id  BIGINT UNSIGNED NOT NULL,
    created_at   DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (wishlist_id),
    UNIQUE KEY uk_wishlists_student_offering (student_id, offering_id),
    KEY idx_wishlists_offering (offering_id),
    CONSTRAINT fk_wishlists_student  FOREIGN KEY (student_id) REFERENCES students (student_id),
    CONSTRAINT fk_wishlists_offering FOREIGN KEY (offering_id) REFERENCES course_offerings (offering_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '찜(하트·장바구니). 학점 상한으로 막지 않음';

-- 교환 요청 = "이 수강 내역을 내놓고, 아래 희망 분반 중 하나를 받고 싶다".
-- 학생은 보유 강의마다 요청을 하나씩 낼 수 있고, 같은 수강 내역은 진행 중 요청 1개에만 묶인다.
CREATE TABLE exchange_requests (
    request_id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    student_id             BIGINT UNSIGNED NOT NULL,
    offered_enrollment_id  BIGINT UNSIGNED NOT NULL COMMENT '내놓는 수강 내역',
    status                 VARCHAR(20)     NOT NULL DEFAULT 'WAITING',
    queued_at              DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '대기열 진입 시각 (기다린 시간 가산점)',
    created_at             DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at             DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    -- 진행 중(WAITING, MATCHED)일 때만 값이 생기는 생성 컬럼. 종료된 요청은 NULL이라 이력은 막지 않는다.
    active_enrollment_id   BIGINT UNSIGNED
        GENERATED ALWAYS AS (CASE WHEN status IN ('WAITING', 'MATCHED') THEN offered_enrollment_id END) STORED,
    PRIMARY KEY (request_id),
    UNIQUE KEY uk_requests_active_enrollment (active_enrollment_id),
    KEY idx_requests_status_queued (status, queued_at),
    KEY idx_requests_student (student_id),
    KEY idx_requests_enrollment (offered_enrollment_id),
    CONSTRAINT fk_requests_student    FOREIGN KEY (student_id) REFERENCES students (student_id),
    CONSTRAINT fk_requests_enrollment FOREIGN KEY (offered_enrollment_id) REFERENCES enrollments (enrollment_id),
    CONSTRAINT ck_requests_status CHECK (status IN ('WAITING', 'MATCHED', 'COMPLETED', 'CANCELLED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '교환 요청 (보유자 측)';

-- 요청별 희망 분반. 여러 행은 "이 중 하나"를 뜻한다 (묶음 교환 아님).
-- 다른 과목도 희망 가능. 희망은 분반까지 정확히 지정한다.
CREATE TABLE exchange_preferences (
    preference_id       BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    request_id          BIGINT UNSIGNED  NOT NULL,
    target_offering_id  BIGINT UNSIGNED  NOT NULL,
    preference_rank     TINYINT UNSIGNED NOT NULL COMMENT '1이 가장 원하는 분반',
    PRIMARY KEY (preference_id),
    UNIQUE KEY uk_preferences_request_target (request_id, target_offering_id),
    UNIQUE KEY uk_preferences_request_rank (request_id, preference_rank),
    KEY idx_preferences_target (target_offering_id, request_id),
    CONSTRAINT fk_preferences_request  FOREIGN KEY (request_id) REFERENCES exchange_requests (request_id),
    CONSTRAINT fk_preferences_offering FOREIGN KEY (target_offering_id) REFERENCES course_offerings (offering_id),
    CONSTRAINT ck_preferences_rank CHECK (preference_rank >= 1)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '요청별 희망 분반과 우선순위';

CREATE TABLE match_sessions (
    session_id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    opened_at           DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    first_accepted_at   DATETIME(6)     NULL,
    expires_at          DATETIME(6)     NOT NULL COMMENT '이 시각까지 둘 다 동의하지 않으면 만료',
    finished_at         DATETIME(6)     NULL,
    rematch_allowed_at  DATETIME(6)     NULL COMMENT '실패 후 같은 두 학생이 다시 매칭될 수 있는 시각',
    PRIMARY KEY (session_id),
    KEY idx_sessions_status_expires (status, expires_at),
    CONSTRAINT ck_sessions_status
        CHECK (status IN ('PENDING', 'COMPLETED', 'REJECTED', 'EXPIRED', 'INVALIDATED')),
    CONSTRAINT ck_sessions_expires CHECK (expires_at > opened_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '매칭 세션 (교환 확정 대기)';

-- 세션 참여자. 주는·받는 분반은 매칭 당시 스냅샷이라 이후 수강 내역이 바뀌어도 이력이 유지된다.
CREATE TABLE match_participants (
    participant_id         BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    session_id             BIGINT UNSIGNED  NOT NULL,
    request_id             BIGINT UNSIGNED  NOT NULL,
    student_id             BIGINT UNSIGNED  NOT NULL,
    offered_enrollment_id  BIGINT UNSIGNED  NOT NULL,
    give_offering_id       BIGINT UNSIGNED  NOT NULL,
    receive_offering_id    BIGINT UNSIGNED  NOT NULL,
    response_status        VARCHAR(20)      NOT NULL DEFAULT 'PENDING',
    responded_at           DATETIME(6)      NULL,
    wake_sent_count        TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '상대 깨우기 보낸 횟수 (최대 3)',
    PRIMARY KEY (participant_id),
    UNIQUE KEY uk_participants_session_student (session_id, student_id),
    UNIQUE KEY uk_participants_session_request (session_id, request_id),
    KEY idx_participants_student_session (student_id, session_id),
    KEY idx_participants_request (request_id),
    CONSTRAINT fk_participants_session    FOREIGN KEY (session_id) REFERENCES match_sessions (session_id),
    CONSTRAINT fk_participants_request    FOREIGN KEY (request_id) REFERENCES exchange_requests (request_id),
    CONSTRAINT fk_participants_student    FOREIGN KEY (student_id) REFERENCES students (student_id),
    CONSTRAINT fk_participants_enrollment FOREIGN KEY (offered_enrollment_id) REFERENCES enrollments (enrollment_id),
    CONSTRAINT fk_participants_give       FOREIGN KEY (give_offering_id) REFERENCES course_offerings (offering_id),
    CONSTRAINT fk_participants_receive    FOREIGN KEY (receive_offering_id) REFERENCES course_offerings (offering_id),
    CONSTRAINT ck_participants_response CHECK (response_status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    CONSTRAINT ck_participants_wake     CHECK (wake_sent_count BETWEEN 0 AND 3),
    CONSTRAINT ck_participants_diff     CHECK (give_offering_id <> receive_offering_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '매칭 세션 참여자 (MVP는 세션당 2명)';

CREATE TABLE penalty_events (
    penalty_event_id  BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    student_id        BIGINT UNSIGNED  NOT NULL,
    session_id        BIGINT UNSIGNED  NULL,
    reason_code       VARCHAR(20)      NOT NULL,
    deduction_points  TINYINT UNSIGNED NOT NULL COMMENT '감점 (양수로 저장)',
    idempotency_key   VARCHAR(100)     NOT NULL COMMENT '같은 사건 중복 감점 방지 (예: EXPIRED:세션ID:학생ID)',
    created_at        DATETIME(6)      NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (penalty_event_id),
    UNIQUE KEY uk_penalty_idempotency (idempotency_key),
    KEY idx_penalty_student_created (student_id, created_at),
    KEY idx_penalty_session (session_id),
    CONSTRAINT fk_penalty_student FOREIGN KEY (student_id) REFERENCES students (student_id),
    CONSTRAINT fk_penalty_session FOREIGN KEY (session_id) REFERENCES match_sessions (session_id),
    CONSTRAINT ck_penalty_reason CHECK (reason_code IN ('REJECTED', 'NO_RESPONSE', 'ABUSE')),
    CONSTRAINT ck_penalty_points CHECK (deduction_points >= 1)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '패널티(감점) 이력';
