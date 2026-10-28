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

/** 개설 분반 (예: 프로그래밍언어론 01분반). 검색·수강·교환의 실제 단위. */
@Entity
@Table(name = "course_offerings")
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offering_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "section_no", nullable = false, length = 5)
    private String sectionNo;

    @Column(name = "professor_name", nullable = false, length = 50)
    private String professorName;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(nullable = false)
    private Integer credits;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(nullable = false)
    private Integer capacity;

    @Column(length = 50)
    private String classroom;

    protected CourseOffering() {
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public String getSectionNo() { return sectionNo; }
    public String getProfessorName() { return professorName; }
    public Integer getCredits() { return credits; }
    public Integer getCapacity() { return capacity; }
    public String getClassroom() { return classroom; }
}
