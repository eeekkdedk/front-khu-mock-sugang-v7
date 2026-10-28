package com.ncp03.coursechange.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;

/** 분반별 수업 시간. 화·목 수업이면 2행. weekday: 1=월 ... 7=일. */
@Entity
@Table(name = "class_times")
public class ClassTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_time_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false)
    private CourseOffering offering;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(nullable = false)
    private Integer weekday;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    protected ClassTime() {
    }

    /** 같은 요일이고 시간이 겹치면 true. 끝나는 시각과 시작 시각이 같으면 겹치지 않는다. */
    public boolean overlaps(ClassTime other) {
        return weekday.equals(other.weekday)
                && startTime.isBefore(other.endTime)
                && other.startTime.isBefore(endTime);
    }

    public Long getId() { return id; }
    public CourseOffering getOffering() { return offering; }
    public Integer getWeekday() { return weekday; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
}
