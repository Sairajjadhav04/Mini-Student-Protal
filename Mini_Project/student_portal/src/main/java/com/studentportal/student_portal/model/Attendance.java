package com.studentportal.student_portal.model;

import jakarta.persistence.*;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User student;

    @ManyToOne(optional = false)
    private Subject subject;

    private Double percentage;

    private Integer totalClasses = 0;

    private Integer presentClasses = 0;

    public Attendance() {
    }

    public Long getId() {
        return id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Integer getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(Integer totalClasses) {
        this.totalClasses = totalClasses;
    }

    public Integer getPresentClasses() {
        return presentClasses;
    }

    public void setPresentClasses(Integer presentClasses) {
        this.presentClasses = presentClasses;
    }

    public void calculatePercentage() {

        if (totalClasses == null ||
                totalClasses == 0) {

            percentage = 0.0;

        } else {

            percentage =
                    (presentClasses * 100.0)
                            / totalClasses;
        }
    }
}