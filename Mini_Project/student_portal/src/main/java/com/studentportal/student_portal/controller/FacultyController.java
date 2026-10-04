package com.studentportal.student_portal.controller;

import com.studentportal.student_portal.model.*;
import com.studentportal.student_portal.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    private final UserRepository users;
    private final SubjectRepository subjects;
    private final AttendanceRepository attendance;
    private final MarksRepository marks;
    private final AssignmentRepository assignments;

    public FacultyController(UserRepository users, SubjectRepository subjects,
                             AttendanceRepository attendance, MarksRepository marks,
                             AssignmentRepository assignments) {
        this.users = users;
        this.subjects = subjects;
        this.attendance = attendance;
        this.marks = marks;
        this.assignments = assignments;
    }

    private User faculty(Authentication auth) {
        return users.findByUsername(auth.getName()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User f = faculty(auth);
        model.addAttribute("faculty", f);
        model.addAttribute("subjects", subjects.findByFaculty(f));
        return "faculty/dashboard";
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", users.findByRole(Role.STUDENT));
        return "faculty/students";
    }

    @GetMapping("/attendance")
    public String attendance(Authentication auth, Model model) {
        User f = faculty(auth);
        model.addAttribute("students", users.findByRole(Role.STUDENT));
        model.addAttribute("subjects", subjects.findByFaculty(f));
        return "faculty/attendance";
    }

    @PostMapping("/attendance")
    public String saveAttendance(Authentication auth,
                                 @RequestParam Long studentId,
                                 @RequestParam Long subjectId,
                                 @RequestParam Double percentage) {
        User student = users.findById(studentId).orElseThrow();
        Subject subject = subjects.findById(subjectId).orElseThrow();

        if (!subject.getFaculty().getId().equals(faculty(auth).getId())) {
            throw new IllegalArgumentException("Subject is not assigned to this faculty");
        }

        Attendance a = attendance.findByStudentAndSubject(student, subject)
                .orElseGet(Attendance::new);
        a.setStudent(student);
        a.setSubject(subject);
        a.setPercentage(percentage);
        attendance.save(a);

        return "redirect:/faculty/attendance?saved";
    }

    @GetMapping("/marks")
    public String marks(Authentication auth, Model model) {
        User f = faculty(auth);
        model.addAttribute("students", users.findByRole(Role.STUDENT));
        model.addAttribute("subjects", subjects.findByFaculty(f));
        return "faculty/marks";
    }

    @PostMapping("/marks")
    public String saveMarks(Authentication auth,
                            @RequestParam Long studentId,
                            @RequestParam Long subjectId,
                            @RequestParam Double marksValue) {
        User student = users.findById(studentId).orElseThrow();
        Subject subject = subjects.findById(subjectId).orElseThrow();

        if (!subject.getFaculty().getId().equals(faculty(auth).getId())) {
            throw new IllegalArgumentException("Subject is not assigned to this faculty");
        }

        Marks m = marks.findByStudentAndSubject(student, subject)
                .orElseGet(Marks::new);
        m.setStudent(student);
        m.setSubject(subject);
        m.setMarks(marksValue);
        marks.save(m);

        return "redirect:/faculty/marks?saved";
    }

    @GetMapping("/assignments")
    public String assignments(Authentication auth, Model model) {
        model.addAttribute("subjects", subjects.findByFaculty(faculty(auth)));
        return "faculty/assignments";
    }

    @PostMapping("/assignments")
    public String saveAssignment(Authentication auth,
                                 @RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam LocalDate dueDate,
                                 @RequestParam Long subjectId) {
        User f = faculty(auth);
        Subject subject = subjects.findById(subjectId).orElseThrow();

        if (!subject.getFaculty().getId().equals(f.getId())) {
            throw new IllegalArgumentException("Subject is not assigned to this faculty");
        }

        Assignment a = new Assignment();
        a.setTitle(title);
        a.setDescription(description);
        a.setDueDate(dueDate);
        a.setSubject(subject);
        a.setFaculty(f);
        assignments.save(a);

        return "redirect:/faculty/assignments?saved";
    }
}
