package com.studentportal.student_portal.repository;

import com.studentportal.student_portal.model.Department;
import com.studentportal.student_portal.model.Subject;
import com.studentportal.student_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findBySemesterAndDepartment(Integer semester, Department department);
    List<Subject> findByFaculty(User faculty);
}
