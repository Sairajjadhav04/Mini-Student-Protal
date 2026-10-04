package com.studentportal.student_portal.repository;

import com.studentportal.student_portal.model.Attendance;
import com.studentportal.student_portal.model.Subject;
import com.studentportal.student_portal.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudent(User student);

    List<Attendance> findBySubject(Subject subject);

    Optional<Attendance> findByStudentAndSubject(
            User student,
            Subject subject
    );
}