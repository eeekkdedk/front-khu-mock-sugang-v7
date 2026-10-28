package com.ncp03.coursechange.enrollment;

import com.ncp03.coursechange.course.CourseAssembler;
import com.ncp03.coursechange.course.CourseResponse;
import com.ncp03.coursechange.domain.Enrollment;
import com.ncp03.coursechange.domain.EnrollmentRepository;
import com.ncp03.coursechange.domain.Student;
import com.ncp03.coursechange.student.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final CourseAssembler courseAssembler;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, StudentService studentService,
                             CourseAssembler courseAssembler) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentService = studentService;
        this.courseAssembler = courseAssembler;
    }

    public MyEnrollmentsResponse myEnrollments(Long studentId) {
        Student student = studentService.getStudent(studentId);
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentIdWithOffering(studentId);
        List<CourseResponse> courses = courseAssembler.toResponses(
                enrollments.stream().map(Enrollment::getOffering).toList());

        List<EnrollmentItemResponse> items = new ArrayList<>();
        long totalCredits = 0;
        for (int i = 0; i < enrollments.size(); i++) {
            Enrollment e = enrollments.get(i);
            CourseResponse course = courses.get(i);
            items.add(new EnrollmentItemResponse(e.getId(), e.getEnrolledAt(), course));
            totalCredits += course.credits();
        }
        return new MyEnrollmentsResponse(student.getId(), student.getNickname(), totalCredits,
                student.getMaxCredits(), items.size(), items);
    }
}
