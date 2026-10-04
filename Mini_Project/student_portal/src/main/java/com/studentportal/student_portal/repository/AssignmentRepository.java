package com.studentportal.student_portal.repository;

import com.studentportal.student_portal.model.Assignment;
import com.studentportal.student_portal.model.Subject;
import com.studentportal.student_portal.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository
        extends JpaRepository<Assignment, Long> {

    List<Assignment> findBySubjectIn(
            List<Subject> subjects
    );

    List<Assignment> findByFaculty(
            User faculty
    );
}