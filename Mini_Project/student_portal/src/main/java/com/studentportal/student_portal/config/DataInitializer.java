package com.studentportal.student_portal.config;

import com.studentportal.student_portal.model.*;
import com.studentportal.student_portal.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            DepartmentRepository departments,
            UserRepository users,
            SubjectRepository subjects,
            AttendanceRepository attendance,
            MarksRepository marks,
            AssignmentRepository assignments,
            PasswordEncoder encoder) {

        return args -> {
            Department ai = getDepartment(departments, "AI & DS");
            Department comp = getDepartment(departments, "Computer Engineering");
            Department it = getDepartment(departments, "Information Technology");

            User faculty = users.findByUsername("faculty").orElseGet(() -> {
                User u = new User();
                u.setUsername("faculty");
                u.setPassword(encoder.encode("faculty123"));
                u.setFullName("Demo Faculty");
                u.setEmail("faculty@studentportal.com");
                u.setRole(Role.FACULTY);
                u.setDepartment(ai);
                return users.save(u);
            });

            User student = users.findByUsername("student").orElseGet(() -> {
                User u = new User();
                u.setUsername("student");
                u.setPassword(encoder.encode("student123"));
                u.setFullName("Demo Student");
                u.setEmail("student@studentportal.com");
                u.setPhone("9876543210");
                u.setRollNumber("AI03");
                u.setSemester(3);
                u.setRole(Role.STUDENT);
                u.setDepartment(ai);
                return users.save(u);
            });

            users.findByUsername("admin").orElseGet(() -> {
                User u = new User();
                u.setUsername("admin");
                u.setPassword(encoder.encode("admin123"));
                u.setFullName("Portal Admin");
                u.setEmail("admin@studentportal.com");
                u.setRole(Role.ADMIN);
                return users.save(u);
            });

            if (subjects.count() == 0) {
                // Demo subjects for multiple departments and semesters.
                save(subjects, "AIDS101", "Programming Fundamentals", 1, ai, faculty);
                save(subjects, "AIDS102", "Engineering Mathematics I", 1, ai, faculty);
                save(subjects, "AIDS201", "Data Structures", 2, ai, faculty);
                save(subjects, "AIDS202", "Engineering Mathematics II", 2, ai, faculty);
                Subject s31 = save(subjects, "AIDS301", "Data Structure and Graph Theory", 3, ai, faculty);
                save(subjects, "AIDS302", "Full Stack Java", 3, ai, faculty);
                save(subjects, "AIDS303", "Engineering Mathematics III", 3, ai, faculty);
                save(subjects, "AIDS401", "Database Management Systems", 4, ai, faculty);
                save(subjects, "AIDS402", "Operating Systems", 4, ai, faculty);
                save(subjects, "AIDS501", "Machine Learning", 5, ai, faculty);
                save(subjects, "AIDS502", "Artificial Intelligence", 5, ai, faculty);
                save(subjects, "AIDS601", "Deep Learning", 6, ai, faculty);
                save(subjects, "AIDS602", "Big Data Analytics", 6, ai, faculty);
                save(subjects, "AIDS701", "Natural Language Processing", 7, ai, faculty);
                save(subjects, "AIDS702", "Computer Vision", 7, ai, faculty);
                save(subjects, "AIDS801", "Major Project", 8, ai, faculty);

                save(subjects, "COMP301", "Data Structures", 3, comp, faculty);
                save(subjects, "COMP302", "Database Systems", 3, comp, faculty);
                save(subjects, "COMP401", "Operating Systems", 4, comp, faculty);
                save(subjects, "COMP402", "Computer Networks", 4, comp, faculty);

                save(subjects, "IT301", "Web Technology", 3, it, faculty);
                save(subjects, "IT302", "Database Management", 3, it, faculty);
                save(subjects, "IT401", "Java Programming", 4, it, faculty);
                save(subjects, "IT402", "Computer Networks", 4, it, faculty);

                Attendance a = new Attendance();
                a.setStudent(student);
                a.setSubject(s31);
                a.setPercentage(82.0);
                attendance.save(a);

                Marks m = new Marks();
                m.setStudent(student);
                m.setSubject(s31);
                m.setMarks(78.0);
                marks.save(m);

                Assignment as = new Assignment();
                as.setTitle("Graph Algorithm Assignment");
                as.setDescription("Write and explain a program for a graph traversal algorithm.");
                as.setDueDate(LocalDate.now().plusDays(10));
                as.setSubject(s31);
                as.setFaculty(faculty);
                assignments.save(as);
            }
        };
    }
    private Department getDepartment(DepartmentRepository repo, String name) {
        return repo.findByNameIgnoreCase(name).orElseGet(() -> repo.save(new Department(name)));
    }
    private Subject save(SubjectRepository repo, String code, String name, int sem,
                         Department department, User faculty) {
        return repo.save(new Subject(code, name, sem, department, faculty));
    }
}
