package com.example.csvreader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;


//---------------------------------------------MAIN MENU---------------------------------------------
@SpringBootApplication
public class CsvReaderApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CsvReaderApplication.class, args);
    }

    @Autowired
    private CsvService csvService;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\nMain Menu:");
            System.out.println("1. Create (Add a new student)");
            System.out.println("2. Read (Display students)");
            System.out.println("3. Update a student");
            System.out.println("4. Delete a student");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createStudentMenu(scanner);
                    break;
                case 2:
                    readStudentMenu(scanner);
                    break;
                case 3:
                    updateStudentMenu(scanner);
                    break;
                case 4:
                    deleteStudentMenu(scanner);
                    break;
                case 5:
                    System.out.println("Exiting application.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    //---------------------------------------------CREATE MENU---------------------------------------------
    private void createStudentMenu(Scanner scanner) {
        boolean validData;
        do {
            System.out.println("\nCreate Menu:");
            System.out.println("Enter 'back' at any prompt to return to the main menu.");

            System.out.print("Enter student name: ");
            String name = scanner.nextLine();
            if (name.equalsIgnoreCase("back")) return;

            System.out.print("Enter student department: ");
            String department = scanner.nextLine();
            if (department.equalsIgnoreCase("back")) return;

            System.out.print("Enter student total score: ");
            String scoreInput = scanner.nextLine();
            if (scoreInput.equalsIgnoreCase("back")) return;
            int totalScore = Integer.parseInt(scoreInput);

            int size = csvService.readCsvAndValidate().size();
            //Variable for obtaining the last ID of the student in the csv file. Used for creating the next student;
            int idGenerator = size==0 ? 0 : csvService.readCsvAndValidate().stream().skip(size-1).findFirst().get().getId();

            Student student = new Student(++idGenerator,name, department, totalScore);
            validData = csvService.addStudentToCsv(student);

            if (!validData) {
                System.out.println("Invalid data. Please re-enter the student information.");
            } else {
                System.out.println("Student added successfully!");
            }

        } while (!validData);
    }


    //---------------------------------------------READ MENU---------------------------------------------
    private void readStudentMenu(Scanner scanner) {
        System.out.println("\nRead Menu:");
        System.out.print("Enter department to filter (or leave blank for all): ");

        String department = scanner.nextLine();

        if (department.equalsIgnoreCase("back")) return;

        System.out.print("Sort by score? (yes/no): ");
        boolean sortByScore = scanner.nextLine().equalsIgnoreCase("yes");

        List<Student> students = csvService.readCsvAndValidate();
        List<Student> filteredStudents = csvService.filterAndSort(students, department, sortByScore);

        printAsJson(filteredStudents);
    }

    //---------------------------------------------UPDATE MENU---------------------------------------------
    private void updateStudentMenu(Scanner scanner) {
        System.out.println("\nUpdate Menu:");
        System.out.print("Enter the ID of the student to update (or type 'back' to go back): ");
        String id = scanner.nextLine();
        if (id.equalsIgnoreCase("back")) return;

        Student existingStudent = csvService.findStudentById(Integer.parseInt(id));
        if (existingStudent == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Enter Student name (leave blank to keep current): ");
        String name = scanner.nextLine();

        System.out.print("Enter new department (leave blank to keep current): ");
        String newDepartment = scanner.nextLine();

        System.out.print("Enter new total score (leave blank to keep current): ");
        String scoreInput = scanner.nextLine();

        if (scoreInput.equalsIgnoreCase("back")) return;

        int newTotalScore = Integer.parseInt(scoreInput);
        boolean updated = csvService.updateStudent(Integer.parseInt(id),name, newDepartment, newTotalScore);

        if (updated) {
            System.out.println("Student updated successfully!");
        } else {
            System.out.println("Failed to update student.");
        }
    }

    //---------------------------------------------DELETE MENU---------------------------------------------
    private void deleteStudentMenu(Scanner scanner) {
        System.out.println("\nDelete Menu:");
        System.out.print("Enter the ID of the student to delete (or type 'back' to go back): ");
        String id = scanner.nextLine();
        if (id.equalsIgnoreCase("back")) return;

        boolean deleted = csvService.deleteStudent(Integer.parseInt(id));

        if (deleted) {
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Failed to delete student.");
        }
    }

    //---------------------------------------------METHOD FOR PRINTING IN JSON---------------------------------------------
    private void printAsJson(List<Student> students) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(students);
            System.out.println("Filtered and Sorted Students in JSON format:");
            System.out.println(json);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting to JSON: " + e.getMessage());
        }
    }
}
