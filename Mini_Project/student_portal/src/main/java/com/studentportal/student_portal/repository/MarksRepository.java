package com.studentportal.student_portal.repository;

import com.studentportal.student_portal.model.Marks;
import com.studentportal.student_portal.model.Subject;
import com.studentportal.student_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarksRepository extends JpaRepository<Marks, Long> {
    List<Marks> findByStudent(User student);
    Optional<Marks> findByStudentAndSubject(User student, Subject subject);
}
