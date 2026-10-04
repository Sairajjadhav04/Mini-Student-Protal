package com.studentportal.student_portal.controller;

import com.studentportal.student_portal.model.*;
import com.studentportal.student_portal.repository.*;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserRepository users;
    private final SubjectRepository subjects;
    private final AttendanceRepository attendance;
    private final MarksRepository marks;
    private final AssignmentRepository assignments;

    public StudentController(UserRepository users,
                             SubjectRepository subjects,
                             AttendanceRepository attendance,
                             MarksRepository marks,
                             AssignmentRepository assignments) {

        this.users = users;
        this.subjects = subjects;
        this.attendance = attendance;
        this.marks = marks;
        this.assignments = assignments;
    }

    private User currentStudent(Authentication auth) {
        return users.findByUsername(auth.getName()).orElseThrow();
    }

    private List<Subject> currentSubjects(User student) {
        return subjects.findBySemesterAndDepartment(
                student.getSemester(),
                student.getDepartment()
        );
    }

    // =========================
    // STUDENT DASHBOARD
    // =========================

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {

        User student = currentStudent(auth);

        List<Subject> subjectList = currentSubjects(student);

        // Student information
        model.addAttribute("student", student);

        // Subjects
        model.addAttribute("subjects", subjectList);

        // Attendance
        model.addAttribute(
                "attendance",
                attendance.findByStudent(student)
                        .stream()
                        .filter(a -> subjectList.contains(a.getSubject()))
                        .toList()
        );

        // Marks / Results
        model.addAttribute(
                "marks",
                marks.findByStudent(student)
                        .stream()
                        .filter(m -> subjectList.contains(m.getSubject()))
                        .toList()
        );

        // Assignments
        model.addAttribute(
                "assignments",
                assignments.findBySubjectIn(subjectList)
        );

        return "student/dashboard";
    }


    // =========================
    // PROFILE
    // =========================

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {

        model.addAttribute(
                "student",
                currentStudent(auth)
        );

        return "student/profile";
    }


    @PostMapping("/profile")
    public String updateProfile(
            Authentication auth,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone) {

        User student = currentStudent(auth);

        student.setFullName(fullName);
        student.setEmail(email);
        student.setPhone(phone);

        users.save(student);

        return "redirect:/student/profile?updated";
    }


    // =========================
    // SUBJECTS
    // =========================

    @GetMapping("/subjects")
    public String subjects(Authentication auth, Model model) {

        User student = currentStudent(auth);

        model.addAttribute("student", student);
        model.addAttribute(
                "subjects",
                currentSubjects(student)
        );

        return "student/subjects";
    }


    // =========================
    // OLD ATTENDANCE PAGE
    // =========================

    @GetMapping("/attendance")
    public String attendance(Authentication auth, Model model) {

        User student = currentStudent(auth);

        List<Subject> subjectList = currentSubjects(student);

        model.addAttribute("student", student);

        model.addAttribute(
                "attendance",
                attendance.findByStudent(student)
                        .stream()
                        .filter(a -> subjectList.contains(a.getSubject()))
                        .toList()
        );

        return "student/attendance";
    }


    // =========================
    // OLD RESULTS PAGE
    // =========================

    @GetMapping("/results")
    public String results(Authentication auth, Model model) {

        User student = currentStudent(auth);

        List<Subject> subjectList = currentSubjects(student);

        model.addAttribute("student", student);

        model.addAttribute(
                "marks",
                marks.findByStudent(student)
                        .stream()
                        .filter(m -> subjectList.contains(m.getSubject()))
                        .toList()
        );

        return "student/results";
    }


    // =========================
    // OLD ASSIGNMENTS PAGE
    // =========================

    @GetMapping("/assignments")
    public String assignments(Authentication auth, Model model) {

        User student = currentStudent(auth);

        List<Subject> subjectList = currentSubjects(student);

        model.addAttribute("student", student);

        model.addAttribute(
                "assignments",
                assignments.findBySubjectIn(subjectList)
        );

        return "student/assignments";
    }
}