import java.util.*;
public class StudentGradeTracker {
    //  Inner class — holds one student's data
    static class Student {
        private String name;
        private ArrayList<Double> grades;

        public Student(String name) {
            this.name   = name;
            this.grades = new ArrayList<>();
        }

        public void addGrade(double grade) {
            grades.add(grade);
        }

        public double getAverage() {
            if (grades.isEmpty()) return 0.0;
            double sum = 0;
            for (double g : grades) sum += g;
            return sum / grades.size();
        }

        public double getHighest() {
            return grades.isEmpty() ? 0 : Collections.max(grades);
        }

        public double getLowest() {
            return grades.isEmpty() ? 0 : Collections.min(grades);
        }

        public String getLetterGrade() {
            double avg = getAverage();
            if (avg >= 90) return "A";
            if (avg >= 80) return "B";
            if (avg >= 70) return "C";
            if (avg >= 60) return "D";
            return "F";
        }

        public String getName()              { return name; }
        public ArrayList<Double> getGrades() { return grades; }
    }
    //  Helper — draw a horizontal separator
    static void line(int width) {
        System.out.println("─".repeat(width));
    }
    //  Display the full summary report

    static void displayReport(ArrayList<Student> students) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║               STUDENT GRADE SUMMARY REPORT               ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        if (students.isEmpty()) {
            System.out.println("  No students found.");
            return;
        }

        // Table header
        System.out.printf("%-20s %-10s %-10s %-10s %-8s %-6s%n",
            "Name", "Average", "Highest", "Lowest", "Grade", "Scores");
        line(68);

        double classTotal = 0;
        double classHigh  = Double.MIN_VALUE;
        double classLow   = Double.MAX_VALUE;
        String topStudent = "";

        for (Student s : students) {
            double avg  = s.getAverage();
            double high = s.getHighest();
            double low  = s.getLowest();

            classTotal += avg;
            if (high > classHigh) { classHigh = high; }
            if (low  < classLow)  { classLow  = low;  }
            if (avg == classHigh || topStudent.isEmpty()) topStudent = s.getName();

            System.out.printf("%-20s %-10.2f %-10.2f %-10.2f %-8s %s%n",
                s.getName(), avg, high, low, s.getLetterGrade(),
                s.getGrades().toString());
        }

        // Find actual top student
        double bestAvg = -1;
        for (Student s : students) {
            if (s.getAverage() > bestAvg) {
                bestAvg    = s.getAverage();
                topStudent = s.getName();
            }
        }

        line(68);
        double classAvg = classTotal / students.size();
        System.out.printf("%n  ✅ Total Students  : %d%n", students.size());
        System.out.printf("  📈 Class Average   : %.2f%n", classAvg);
        System.out.printf("  🏆 Top Student     : %s (%.2f)%n", topStudent, bestAvg);
        System.out.printf("  ⬆  Highest Score   : %.2f%n", classHigh);
        System.out.printf("  ⬇  Lowest Score    : %.2f%n", classLow);
        System.out.println();
    }
    //  Search student by name
    static Student findStudent(ArrayList<Student> students, String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }
    //  Show per-student detail
    static void viewStudent(ArrayList<Student> students, Scanner sc) {
        System.out.print("  Enter student name to view: ");
        String name = sc.nextLine().trim();
        Student s = findStudent(students, name);
        if (s == null) {
            System.out.println("  ⚠  Student not found.");
            return;
        }
        System.out.println();
        System.out.println("  ┌─────────────────────────────────┐");
        System.out.printf( "  │  Student : %-21s│%n", s.getName());
        System.out.printf( "  │  Grades  : %-21s│%n", s.getGrades().toString());
        System.out.printf( "  │  Average : %-21.2f│%n", s.getAverage());
        System.out.printf( "  │  Highest : %-21.2f│%n", s.getHighest());
        System.out.printf( "  │  Lowest  : %-21.2f│%n", s.getLowest());
        System.out.printf( "  │  Grade   : %-21s│%n", s.getLetterGrade());
        System.out.println("  └─────────────────────────────────┘");
    }
    //  Main menu loop
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();

        System.out.println();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       Student Grade Tracker          ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("  ┌────────────PORTAL──────────────┐");
            System.out.println("  │  1. Add Student                │");
            System.out.println("  │  2. Add Grade to Student       │");
            System.out.println("  │  3. View Student Details       │");
            System.out.println("  │  4. View Full Summary Report   │");
            System.out.println("  │  5. List All Students          │");
            System.out.println("  │  6. Exit                       │");
            System.out.println("  └────────────────────────────────┘");
            System.out.print("  Choose options from portal S.NO given above : ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": {
                    System.out.print("  Enter student name: ");
                    String name = sc.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("  ⚠  Name cannot be empty.");
                        break;
                    }
                    if (findStudent(students, name) != null) {
                        System.out.println("  ⚠  Student already exists.");
                        break;
                    }
                    students.add(new Student(name));
                    System.out.println("  ✅ Student \"" + name + "\" added.");
                    break;
                }
                case "2": {
                    System.out.print("  Enter student name: ");
                    String name = sc.nextLine().trim();
                    Student s = findStudent(students, name);
                    if (s == null) {
                        System.out.println("  ⚠  Student not found.");
                        break;
                    }
                    System.out.print("  Enter grade (0–100): ");
                    try {
                        double grade = Double.parseDouble(sc.nextLine().trim());
                        if (grade < 0 || grade > 100) {
                            System.out.println("  ⚠  Grade must be between 0 and 100.");
                        } else {
                            s.addGrade(grade);
                            System.out.printf("  ✅ Grade %.2f added for %s.%n", grade, name);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("  ⚠  Invalid grade. Please enter a number.");
                    }
                    break;
                }
                case "3": {
                    viewStudent(students, sc);
                    break;
                }
                case "4": {
                    displayReport(students);
                    break;
                }
                case "5": {
                    if (students.isEmpty()) {
                        System.out.println("  No students added yet.");
                    } else {
                        System.out.println("\n  Registered Students:");
                        for (int i = 0; i < students.size(); i++) {
                            System.out.printf("   %d. %s (%d grade(s) entered)%n",
                                i + 1,
                                students.get(i).getName(),
                                students.get(i).getGrades().size());
                        }
                    }
                    break;
                }
                case "6": {
                    System.out.println("\n   ");
                    running = false;
                    break;
                }
                default:
                    System.out.println("  ⚠  Invalid option. Please choose 1–6.");
            }
        }
        sc.close();
    }
}
