package com.studentportal.student_portal.model;

import jakarta.persistence.*;

@Entity
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer semester;

    @ManyToOne(optional = false)
    private Department department;

    @ManyToOne
    private User faculty;
    public Subject() {}
    public Subject(String code, String name, Integer semester,
                   Department department, User faculty) {
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.department = department;
        this.faculty = faculty;
    }
    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public User getFaculty() { return faculty; }
    public void setFaculty(User faculty) { this.faculty = faculty; }
}
