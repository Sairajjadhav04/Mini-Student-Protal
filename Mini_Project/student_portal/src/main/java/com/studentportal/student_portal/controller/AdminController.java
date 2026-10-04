package com.studentportal.student_portal.controller;

import com.studentportal.student_portal.model.*;
import com.studentportal.student_portal.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository users;
    private final SubjectRepository subjects;
    private final DepartmentRepository departments;
    private final PasswordEncoder encoder;

    public AdminController(UserRepository users, SubjectRepository subjects,
                           DepartmentRepository departments, PasswordEncoder encoder) {
        this.users = users;
        this.subjects = subjects;
        this.departments = departments;
        this.encoder = encoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", users.findByRole(Role.STUDENT).size());
        model.addAttribute("facultyCount", users.findByRole(Role.FACULTY).size());
        model.addAttribute("subjectCount", subjects.count());
        return "admin/dashboard";
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", users.findByRole(Role.STUDENT));
        model.addAttribute("departments", departments.findAll());
        return "admin/students";
    }

    @PostMapping("/students")
    public String addStudent(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String rollNumber,
                             @RequestParam Integer semester,
                             @RequestParam Long departmentId) {
        User s = new User();
        s.setUsername(username);
        s.setPassword(encoder.encode(password));
        s.setFullName(fullName);
        s.setEmail(email);
        s.setRollNumber(rollNumber);
        s.setSemester(semester);
        s.setRole(Role.STUDENT);
        s.setDepartment(departments.findById(departmentId).orElseThrow());
        users.save(s);
        return "redirect:/admin/students?saved";
    }

    @GetMapping("/faculty")
    public String faculty(Model model) {
        model.addAttribute("faculty", users.findByRole(Role.FACULTY));
        model.addAttribute("departments", departments.findAll());
        return "admin/faculty";
    }

    @PostMapping("/faculty")
    public String addFaculty(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam Long departmentId) {
        User f = new User();
        f.setUsername(username);
        f.setPassword(encoder.encode(password));
        f.setFullName(fullName);
        f.setEmail(email);
        f.setRole(Role.FACULTY);
        f.setDepartment(departments.findById(departmentId).orElseThrow());
        users.save(f);
        return "redirect:/admin/faculty?saved";
    }

    @GetMapping("/subjects")
    public String subjects(Model model) {
        model.addAttribute("subjects", subjects.findAll());
        model.addAttribute("departments", departments.findAll());
        model.addAttribute("faculty", users.findByRole(Role.FACULTY));
        return "admin/subjects";
    }

    @PostMapping("/subjects")
    public String addSubject(@RequestParam String code,
                             @RequestParam String name,
                             @RequestParam Integer semester,
                             @RequestParam Long departmentId,
                             @RequestParam Long facultyId) {
        Subject s = new Subject();
        s.setCode(code);
        s.setName(name);
        s.setSemester(semester);
        s.setDepartment(departments.findById(departmentId).orElseThrow());
        s.setFaculty(users.findById(facultyId).orElseThrow());
        subjects.save(s);
        return "redirect:/admin/subjects?saved";
    }
}
