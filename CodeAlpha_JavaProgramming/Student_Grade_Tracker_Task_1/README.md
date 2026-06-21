# Student Grade Tracker

## 📌 Overview

Student Grade Tracker is a simple Java console-based application that helps manage student records and grades. It allows users to add students, record grades, view individual performance, and generate a complete class summary report.

This project demonstrates the use of **Object-Oriented Programming (OOP)** concepts such as classes, objects, encapsulation, and collections in Java.

---

## ✨ Features

* Add new students
* Record grades for students
* View detailed student performance
* Calculate:

  * Average score
  * Highest score
  * Lowest score
  * Letter grade (A–F)
* Generate class summary reports
* Display top-performing student
* List all registered students
* Input validation and error handling

---

## 🛠 Technologies Used

* Java
* Collections Framework (`ArrayList`)
* OOP Concepts
* Scanner Class for User Input

---

## 📂 Project Structure

```
StudentGradeTracker.java
│
├── Student (Inner Class)
│   ├── addGrade()
│   ├── getAverage()
│   ├── getHighest()
│   ├── getLowest()
│   └── getLetterGrade()
│
├── displayReport()
├── findStudent()
├── viewStudent()
└── main()
```

---

## 📊 Grading System

| Average Score | Grade |
| ------------- | ----- |
| 90 - 100      | A     |
| 80 - 89       | B     |
| 70 - 79       | C     |
| 60 - 69       | D     |
| Below 60      | F     |

---

## 🚀 How to Run

### Compile the Program

```bash
javac StudentGradeTracker.java
```

### Run the Program

```bash
java StudentGradeTracker
```

---

## 📋 Menu Options

```
1. Add Student
2. Add Grade to Student
3. View Student Details
4. View Full Summary Report
5. List All Students
6. Exit
```

---

## 🖥 Sample Output

```
Student Grade Tracker

1. Add Student
2. Add Grade to Student
3. View Student Details
4. View Full Summary Report
5. List All Students
6. Exit

Choose an option: 1
Enter student name: John

Student "John" added successfully.
```

### Summary Report Example

```
STUDENT GRADE SUMMARY REPORT

Name                 Average    Highest    Lowest     Grade
------------------------------------------------------------
John                 88.50      95.00      82.00      B
Alice                92.00      98.00      86.00      A

Total Students : 2
Class Average  : 90.25
Top Student    : Alice
Highest Score  : 98.00
Lowest Score   : 82.00
```

---

## 🎯 Learning Outcomes

This project helps in understanding:

* Java Classes and Objects
* Encapsulation
* Collections (`ArrayList`)
* Loops and Conditional Statements
* User Input Handling
* Data Processing and Reporting
* Console-Based Application Development



This project is developed for educational and learning purposes.
