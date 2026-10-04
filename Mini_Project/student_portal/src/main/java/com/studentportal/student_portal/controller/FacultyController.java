package com.studentportal.student_portal.controller;

import com.studentportal.student_portal.model.*;
import com.studentportal.student_portal.repository.*;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    private final UserRepository users;
    private final SubjectRepository subjects;
    private final AttendanceRepository attendance;
    private final MarksRepository marks;
    private final AssignmentRepository assignments;

    public FacultyController(
            UserRepository users,
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

    // =========================================================
    // CURRENT FACULTY
    // =========================================================

    private User faculty(Authentication auth) {

        return users.findByUsername(auth.getName())
                .orElseThrow();
    }

    // =========================================================
    // CHECK WHETHER SUBJECT BELONGS TO FACULTY
    // =========================================================

    private Subject facultySubject(
            Authentication auth,
            Long subjectId) {

        User faculty = faculty(auth);

        Subject subject = subjects.findById(subjectId)
                .orElseThrow();

        if (subject.getFaculty() == null ||
                !subject.getFaculty()
                        .getId()
                        .equals(faculty.getId())) {

            throw new IllegalArgumentException(
                    "This subject is not assigned to you."
            );
        }

        return subject;
    }

    // =========================================================
    // STUDENTS OF SUBJECT
    // =========================================================

    private List<User> studentsForSubject(
            Subject subject) {

        return users.findByRole(Role.STUDENT)
                .stream()

                .filter(student ->
                        student.getSemester() != null &&
                                student.getSemester()
                                        .equals(subject.getSemester()))

                .filter(student ->
                        student.getDepartment() != null &&
                                subject.getDepartment() != null &&
                                student.getDepartment()
                                        .getId()
                                        .equals(
                                                subject.getDepartment()
                                                        .getId()
                                        ))

                .toList();
    }

    // =========================================================
    // FACULTY DASHBOARD
    // =========================================================

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication auth,
            Model model) {

        User faculty = faculty(auth);

        model.addAttribute(
                "faculty",
                faculty
        );

        model.addAttribute(
                "subjects",
                subjects.findByFaculty(faculty)
        );

        return "faculty/dashboard";
    }

    // =========================================================
    // FACULTY PROFILE
    // =========================================================

    @GetMapping("/profile")
    public String profile(
            Authentication auth,
            Model model) {

        User faculty = faculty(auth);

        model.addAttribute(
                "faculty",
                faculty
        );

        return "faculty/profile";
    }

    // =========================================================
    // FACULTY STUDENTS
    // =========================================================

    @GetMapping("/students")
    public String students(
            Authentication auth,
            Model model) {

        User faculty = faculty(auth);

        List<User> students = new ArrayList<>();

        for (Subject subject :
                subjects.findByFaculty(faculty)) {

            for (User student :
                    studentsForSubject(subject)) {

                if (!students.contains(student)) {
                    students.add(student);
                }
            }
        }

        model.addAttribute(
                "faculty",
                faculty
        );

        model.addAttribute(
                "students",
                students
        );

        return "faculty/students";
    }

    // =========================================================
    // ATTENDANCE PAGE
    // =========================================================

    @GetMapping("/attendance")
    public String attendancePage(
            Authentication auth,
            @RequestParam(required = false) Long subjectId,
            Model model) {

        User faculty = faculty(auth);

        List<Subject> facultySubjects =
                subjects.findByFaculty(faculty);

        Subject selectedSubject = null;

        if (subjectId != null) {

            selectedSubject =
                    facultySubject(
                            auth,
                            subjectId
                    );

        } else if (!facultySubjects.isEmpty()) {

            selectedSubject =
                    facultySubjects.get(0);
        }

        List<User> students =
                selectedSubject == null
                        ? List.of()
                        : studentsForSubject(
                        selectedSubject
                );

        Map<Long, Attendance> attendanceByStudent =
                new HashMap<>();

        if (selectedSubject != null) {

            List<Attendance> records =
                    attendance.findBySubject(
                            selectedSubject
                    );

            for (Attendance record : records) {

                attendanceByStudent.put(
                        record.getStudent().getId(),
                        record
                );
            }
        }

        model.addAttribute(
                "faculty",
                faculty
        );

        model.addAttribute(
                "subjects",
                facultySubjects
        );

        model.addAttribute(
                "selectedSubject",
                selectedSubject
        );

        model.addAttribute(
                "students",
                students
        );

        model.addAttribute(
                "attendanceByStudent",
                attendanceByStudent
        );

        return "faculty/attendance";
    }

    // =========================================================
    // SAVE ATTENDANCE
    // =========================================================

    @PostMapping("/attendance")
    public String saveAttendance(
            Authentication auth,
            @RequestParam Long subjectId,
            @RequestParam Map<String, String> attendanceStatus) {

        Subject subject =
                facultySubject(
                        auth,
                        subjectId
                );

        List<User> students =
                studentsForSubject(subject);

        for (Map.Entry<String, String> entry :
                attendanceStatus.entrySet()) {

            if (!entry.getKey()
                    .startsWith("status_")) {

                continue;
            }

            Long studentId =
                    Long.parseLong(
                            entry.getKey()
                                    .substring(
                                            "status_".length()
                                    )
                    );

            User student =
                    users.findById(studentId)
                            .orElseThrow();

            // Security check
            if (!students.contains(student)) {
                continue;
            }

            Attendance record =
                    attendance
                            .findByStudentAndSubject(
                                    student,
                                    subject
                            )
                            .orElseGet(
                                    Attendance::new
                            );

            record.setStudent(student);
            record.setSubject(subject);

            // Convert old percentage-based
            // records into a 20-class base.
            if (record.getTotalClasses() == null ||
                    record.getPresentClasses() == null) {

                double oldPercentage =
                        record.getPercentage() == null
                                ? 0
                                : record.getPercentage();

                int totalClasses = 20;

                int presentClasses =
                        (int) Math.round(
                                oldPercentage *
                                        totalClasses /
                                        100.0
                        );

                record.setTotalClasses(
                        totalClasses
                );

                record.setPresentClasses(
                        presentClasses
                );
            }

            // Record one new class
            record.setTotalClasses(
                    record.getTotalClasses() + 1
            );

            if ("PRESENT".equalsIgnoreCase(
                    entry.getValue())) {

                record.setPresentClasses(
                        record.getPresentClasses() + 1
                );
            }

            record.calculatePercentage();

            attendance.save(record);
        }

        return "redirect:/faculty/attendance?subjectId="
                + subjectId
                + "&saved";
    }

    // =========================================================
    // MARKS PAGE
    // =========================================================

    @GetMapping("/marks")
    public String marksPage(
            Authentication auth,
            @RequestParam(required = false) Long subjectId,
            Model model) {

        User faculty = faculty(auth);

        List<Subject> facultySubjects =
                subjects.findByFaculty(faculty);

        Subject selectedSubject = null;

        if (subjectId != null) {

            selectedSubject =
                    facultySubject(
                            auth,
                            subjectId
                    );

        } else if (!facultySubjects.isEmpty()) {

            selectedSubject =
                    facultySubjects.get(0);
        }

        List<User> students =
                selectedSubject == null
                        ? List.of()
                        : studentsForSubject(
                        selectedSubject
                );

        Map<Long, Marks> marksByStudent =
                new HashMap<>();

        if (selectedSubject != null) {

            List<Marks> records =
                    marks.findBySubject(
                            selectedSubject
                    );

            for (Marks record : records) {

                marksByStudent.put(
                        record.getStudent().getId(),
                        record
                );
            }
        }

        model.addAttribute(
                "faculty",
                faculty
        );

        model.addAttribute(
                "subjects",
                facultySubjects
        );

        model.addAttribute(
                "selectedSubject",
                selectedSubject
        );

        model.addAttribute(
                "students",
                students
        );

        model.addAttribute(
                "marksByStudent",
                marksByStudent
        );

        return "faculty/marks";
    }

    // =========================================================
    // SAVE MARKS
    // =========================================================

    @PostMapping("/marks")
    public String saveMarks(
            Authentication auth,
            @RequestParam Long subjectId,
            @RequestParam Map<String, String> markValues) {

        Subject subject =
                facultySubject(
                        auth,
                        subjectId
                );

        List<User> students =
                studentsForSubject(subject);

        for (Map.Entry<String, String> entry :
                markValues.entrySet()) {

            if (!entry.getKey()
                    .startsWith("marks_")) {

                continue;
            }

            String value = entry.getValue();

            if (value == null ||
                    value.isBlank()) {

                continue;
            }

            double marksValue;

            try {

                marksValue =
                        Double.parseDouble(value);

            } catch (NumberFormatException e) {

                continue;
            }

            if (marksValue < 0 ||
                    marksValue > 100) {

                throw new IllegalArgumentException(
                        "Marks must be between 0 and 100."
                );
            }

            Long studentId =
                    Long.parseLong(
                            entry.getKey()
                                    .substring(
                                            "marks_".length()
                                    )
                    );

            User student =
                    users.findById(studentId)
                            .orElseThrow();

            // Security check
            if (!students.contains(student)) {
                continue;
            }

            Marks record =
                    marks
                            .findByStudentAndSubject(
                                    student,
                                    subject
                            )
                            .orElseGet(
                                    Marks::new
                            );

            record.setStudent(student);
            record.setSubject(subject);
            record.setMarks(marksValue);

            marks.save(record);
        }

        return "redirect:/faculty/marks?subjectId="
                + subjectId
                + "&saved";
    }
// =========================================================
// ASSIGNMENTS PAGE
// =========================================================

    @GetMapping("/assignments")
    public String assignmentsPage(
            Authentication auth,
            Model model) {

        User faculty = faculty(auth);

        model.addAttribute(
                "faculty",
                faculty
        );

        model.addAttribute(
                "subjects",
                subjects.findByFaculty(faculty)
        );

        model.addAttribute(
                "assignments",
                assignments.findByFaculty(faculty)
        );

        return "faculty/assignment";
    }


// =========================================================
// ADD ASSIGNMENT
// =========================================================

    @PostMapping("/assignments")
    public String saveAssignment(
            Authentication auth,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam LocalDate dueDate,
            @RequestParam Long subjectId) {

        User faculty = faculty(auth);

        Subject subject =
                facultySubject(
                        auth,
                        subjectId
                );

        Assignment assignment =
                new Assignment();

        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);
        assignment.setSubject(subject);
        assignment.setFaculty(faculty);

        assignments.save(assignment);

        return "redirect:/faculty/assignments?saved";
    }
}