package com.ncp03.coursechange.student;

import com.ncp03.coursechange.common.error.ApiException;
import com.ncp03.coursechange.common.error.ErrorCode;
import com.ncp03.coursechange.domain.EnrollmentRepository;
import com.ncp03.coursechange.domain.Student;
import com.ncp03.coursechange.domain.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentService(StudentRepository studentRepository, EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<StudentSummaryResponse> list() {
        return studentRepository.findAllByOrderByIdAsc().stream().map(StudentSummaryResponse::from).toList();
    }

    public MyProfileResponse me(Long studentId) {
        Student student = getStudent(studentId);
        return MyProfileResponse.of(student, enrollmentRepository.sumCreditsByStudentId(studentId));
    }

    /** 다른 서비스에서도 쓰는 학생 조회. 없으면 404 STUDENT_NOT_FOUND. */
    public Student getStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ApiException(ErrorCode.STUDENT_NOT_FOUND));
    }
}
