package com.studentportal.student_portal.config;

import com.studentportal.student_portal.model.*;
import com.studentportal.student_portal.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            SubjectRepository subjectRepository,
            AttendanceRepository attendanceRepository,
            MarksRepository marksRepository,
            AssignmentRepository assignmentRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Prevent duplicate data
            if (userRepository.findByUsername("admin").isPresent()) {
                System.out.println("============================================");
                System.out.println("Demo data already exists.");
                System.out.println("Skipping initialization.");
                System.out.println("============================================");
                return;
            }

            System.out.println("============================================");
            System.out.println("Creating Student Portal demo data...");
            System.out.println("============================================");

            Random random = new Random();

            // =========================================================
            // 1. DEPARTMENTS
            // =========================================================

            Department aiDs = new Department("AI & Data Science");
            Department computer = new Department("Computer Engineering");

            departmentRepository.save(aiDs);
            departmentRepository.save(computer);

            // =========================================================
            // 2. ADMIN
            // =========================================================

            User admin = createUser(
                    "admin",
                    "Admin@123",
                    "System Administrator",
                    "admin@studentportal.com",
                    "9000000001",
                    null,
                    null,
                    Role.ADMIN,
                    null,
                    passwordEncoder
            );

            userRepository.save(admin);

            // =========================================================
            // 3. FACULTY NAMES
            // =========================================================

            String[] facultyNames = {
                    "Dr. Rahul Sharma",
                    "Prof. Priya Patil",
                    "Dr. Amit Joshi",
                    "Prof. Sneha Deshmukh",
                    "Dr. Raj Mehta",
                    "Prof. Neha Kulkarni",
                    "Dr. Akash Shah",
                    "Prof. Pooja More",
                    "Dr. Vikram Singh",
                    "Prof. Anjali Gupta"
            };

            // =========================================================
            // 4. FACULTY USERNAMES
            // =========================================================

            String[] facultyUsernames = {
                    "rahul.sharma",
                    "priya.patil",
                    "amit.joshi",
                    "sneha.deshmukh",
                    "raj.mehta",
                    "neha.kulkarni",
                    "akash.shah",
                    "pooja.more",
                    "vikram.singh",
                    "anjali.gupta"
            };

            // =========================================================
            // 5. FACULTY PASSWORDS
            // =========================================================

            String[] facultyPasswords = {
                    "Rahul@123",
                    "Priya@123",
                    "Amit@123",
                    "Sneha@123",
                    "Raj@123",
                    "Neha@123",
                    "Akash@123",
                    "Pooja@123",
                    "Vikram@123",
                    "Anjali@123"
            };

            List<User> facultyList = new ArrayList<>();

            // =========================================================
            // 6. CREATE FACULTY
            // =========================================================

            for (int i = 0; i < facultyNames.length; i++) {

                Department department;

                if (i < 5) {
                    department = aiDs;
                } else {
                    department = computer;
                }

                User faculty = createUser(
                        facultyUsernames[i],
                        facultyPasswords[i],
                        facultyNames[i],
                        String.format(
                                "faculty%02d@studentportal.com",
                                i + 1
                        ),
                        "90000000" + String.format("%02d", i + 2),
                        null,
                        3,
                        Role.FACULTY,
                        department,
                        passwordEncoder
                );

                facultyList.add(
                        userRepository.save(faculty)
                );
            }

            // =========================================================
            // 7. STUDENT NAMES
            // =========================================================

            String[] studentNames = {
                    "Sairaj Jadhav",
                    "Dhanshri Tarudkar",
                    "Aaysha Tadvi",
                    "Kanishka Ingale",
                    "Saish Naik",
                    "Aryaa Salvi",
                    "Shubham Teli",
                    "Ayush Shahu",
                    "Aanhik Shigwan",
                    "Krutik Makwana"
            };

            // =========================================================
            // 8. STUDENT USERNAMES
            // =========================================================

            String[] studentUsernames = {
                    "sairaj.jadhav",
                    "dhanshri.tarudkar",
                    "aaysha.tadvi",
                    "kanishka.ingale",
                    "saish.naik",
                    "aryaa.salvi",
                    "shubham.teli",
                    "ayush.shahu",
                    "aanhik.shigwan",
                    "krutik.makwana"
            };

            // =========================================================
            // 9. STUDENT PASSWORDS
            // =========================================================

            String[] studentPasswords = {
                    "Sairaj@123",
                    "Dhanshri@123",
                    "Aaysha@123",
                    "Kanishka@123",
                    "Saish@123",
                    "Aryaa@123",
                    "Shubham@123",
                    "Ayush@123",
                    "Aanhik@123",
                    "Krutik@123"
            };

            List<User> studentList = new ArrayList<>();

            // =========================================================
            // 10. CREATE STUDENTS
            // =========================================================

            for (int i = 0; i < studentNames.length; i++) {

                Department department;
                String rollNumber;

                if (i < 5) {

                    // First 5 students - AI & Data Science
                    department = aiDs;

                    rollNumber = String.format(
                            "AI%03d",
                            i + 1
                    );

                } else {

                    // Last 5 students - Computer Engineering
                    department = computer;

                    rollNumber = String.format(
                            "CE%03d",
                            i - 4
                    );
                }

                User student = createUser(
                        studentUsernames[i],
                        studentPasswords[i],
                        studentNames[i],
                        String.format(
                                "student%02d@studentportal.com",
                                i + 1
                        ),
                        "91000000" + String.format("%02d", i + 1),
                        rollNumber,
                        3,
                        Role.STUDENT,
                        department,
                        passwordEncoder
                );

                studentList.add(
                        userRepository.save(student)
                );
            }

            // =========================================================
            // 11. SUBJECTS
            // =========================================================

            List<Subject> subjects = new ArrayList<>();

            // ---------------------------------------------------------
            // AI & DATA SCIENCE SUBJECTS
            // ---------------------------------------------------------

            subjects.add(
                    new Subject(
                            "DSGT",
                            "Data Structures & Graph Theory",
                            3,
                            aiDs,
                            facultyList.get(0)
                    )
            );

            subjects.add(
                    new Subject(
                            "DBMS",
                            "Database Management Systems",
                            3,
                            aiDs,
                            facultyList.get(1)
                    )
            );

            subjects.add(
                    new Subject(
                            "JAVA",
                            "Java Programming",
                            3,
                            aiDs,
                            facultyList.get(2)
                    )
            );

            subjects.add(
                    new Subject(
                            "DM",
                            "Discrete Mathematics",
                            3,
                            aiDs,
                            facultyList.get(3)
                    )
            );

            subjects.add(
                    new Subject(
                            "CN",
                            "Computer Networks",
                            3,
                            aiDs,
                            facultyList.get(4)
                    )
            );

            // ---------------------------------------------------------
            // COMPUTER ENGINEERING SUBJECTS
            // ---------------------------------------------------------

            subjects.add(
                    new Subject(
                            "WT",
                            "Web Technology",
                            3,
                            computer,
                            facultyList.get(5)
                    )
            );

            subjects.add(
                    new Subject(
                            "OS",
                            "Operating Systems",
                            3,
                            computer,
                            facultyList.get(6)
                    )
            );

            subjects.add(
                    new Subject(
                            "SE",
                            "Software Engineering",
                            3,
                            computer,
                            facultyList.get(7)
                    )
            );

            subjects.add(
                    new Subject(
                            "AI",
                            "Artificial Intelligence",
                            3,
                            computer,
                            facultyList.get(8)
                    )
            );

            subjects.add(
                    new Subject(
                            "ML",
                            "Machine Learning",
                            3,
                            computer,
                            facultyList.get(9)
                    )
            );

            subjects = subjectRepository.saveAll(subjects);

            // =========================================================
            // 12. RANDOM ATTENDANCE + RANDOM MARKS
            // =========================================================

            for (User student : studentList) {

                List<Subject> studentSubjects =
                        subjectRepository.findBySemesterAndDepartment(
                                student.getSemester(),
                                student.getDepartment()
                        );

                for (Subject subject : studentSubjects) {

                    // -------------------------------------------------
                    // ATTENDANCE
                    // Random value between 65% and 98%
                    // -------------------------------------------------

                    Attendance attendance = new Attendance();

                    attendance.setStudent(student);
                    attendance.setSubject(subject);

                    double attendancePercentage =
                            65 + random.nextInt(34);

                    attendance.setPercentage(
                            attendancePercentage
                    );

                    attendanceRepository.save(attendance);

                    // -------------------------------------------------
                    // MARKS
                    // Random value between 55 and 95
                    // -------------------------------------------------

                    Marks marks = new Marks();

                    marks.setStudent(student);
                    marks.setSubject(subject);

                    double studentMarks =
                            55 + random.nextInt(41);

                    marks.setMarks(studentMarks);

                    marksRepository.save(marks);
                }
            }

            // =========================================================
            // 13. RANDOM ASSIGNMENTS
            // =========================================================

            String[] assignmentTitles = {
                    "Unit Test Assignment",
                    "Practical Assignment",
                    "Mini Project",
                    "Problem Solving Assignment",
                    "Case Study",
                    "Research Assignment",
                    "Programming Assignment",
                    "Theory Assignment",
                    "Lab Assignment",
                    "Internal Assessment"
            };

            String[] assignmentDescriptions = {
                    "Complete the given questions and submit the assignment.",
                    "Solve the given problems and upload your submission.",
                    "Prepare a detailed solution based on the topics covered in class.",
                    "Complete the practical work and submit it before the due date.",
                    "Prepare a case study related to the subject.",
                    "Study the given topic and prepare a short report.",
                    "Write and execute the required programs.",
                    "Answer all questions from the prescribed unit.",
                    "Complete the laboratory exercises given by the faculty.",
                    "Prepare the required work for internal assessment."
            };

            for (Subject subject : subjects) {

                Assignment assignment = new Assignment();

                // Random assignment title
                String title =
                        assignmentTitles[
                                random.nextInt(
                                        assignmentTitles.length
                                )
                                ];

                // Random description
                String description =
                        assignmentDescriptions[
                                random.nextInt(
                                        assignmentDescriptions.length
                                )
                                ];

                assignment.setTitle(
                        title + " - " + subject.getCode()
                );

                assignment.setDescription(description);

                // Random due date
                // 7 to 30 days from today
                int days =
                        7 + random.nextInt(24);

                assignment.setDueDate(
                        LocalDate.now().plusDays(days)
                );

                assignment.setSubject(subject);

                assignment.setFaculty(
                        subject.getFaculty()
                );

                assignmentRepository.save(assignment);
            }

            // =========================================================
            // 14. SUCCESS MESSAGE
            // =========================================================

            System.out.println();
            System.out.println("============================================");
            System.out.println(
                    "Student Portal demo data created successfully!"
            );
            System.out.println("============================================");

            System.out.println();
            System.out.println("ADMIN LOGIN");
            System.out.println("--------------------------------------------");
            System.out.println("Username : admin");
            System.out.println("Password : Admin@123");

            System.out.println();
            System.out.println("FACULTY LOGIN");
            System.out.println("--------------------------------------------");

            for (int i = 0; i < facultyUsernames.length; i++) {

                System.out.println(
                        facultyNames[i]
                                + " -> "
                                + facultyUsernames[i]
                                + " / "
                                + facultyPasswords[i]
                );
            }

            System.out.println();
            System.out.println("STUDENT LOGIN");
            System.out.println("--------------------------------------------");

            for (int i = 0; i < studentUsernames.length; i++) {

                System.out.println(
                        studentNames[i]
                                + " -> "
                                + studentUsernames[i]
                                + " / "
                                + studentPasswords[i]
                );
            }

            System.out.println();
            System.out.println("DATA CREATED");
            System.out.println("--------------------------------------------");
            System.out.println("Departments : 2");
            System.out.println("Students    : 10");
            System.out.println("Faculty     : 10");
            System.out.println("Subjects    : 10");
            System.out.println("Attendance  : Random 65% - 98%");
            System.out.println("Marks       : Random 55 - 95");
            System.out.println("Assignments : 10");

            System.out.println("============================================");
        };
    }

    // =============================================================
    // USER CREATION METHOD
    // =============================================================

    private User createUser(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            String rollNumber,
            Integer semester,
            Role role,
            Department department,
            PasswordEncoder passwordEncoder) {

        User user = new User();

        user.setUsername(username);

        // Password is stored as BCrypt hash
        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRollNumber(rollNumber);
        user.setSemester(semester);
        user.setRole(role);
        user.setEnabled(true);
        user.setDepartment(department);

        return user;
    }
}