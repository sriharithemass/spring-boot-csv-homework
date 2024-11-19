package com.example.csvreader;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CsvService {

    //Variables for Validation
    private static final String FILE_NAME = "src/main/resources/students.csv";
    private final Validator validator;

    public CsvService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }


    public List<Student> readCsvAndValidate() {
        List<Student> students = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(FILE_NAME))) {
            csvReader.readNext(); // Skip header row
            String[] values;

            while ((values = csvReader.readNext()) != null) {
                Student student = new Student(Integer.parseInt(values[0]), values[1],values[2],Integer.parseInt(values[3]));
                Set<ConstraintViolation<Student>> violations = validator.validate(student);

                if (violations.isEmpty()) {
                    students.add(student);
                } else {
                    violations.forEach(violation -> System.out.println("Validation error: " + violation.getMessage()));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        if (students.isEmpty()) {
            System.out.println("No data found");
        }
        return students;
    }

    public List<Student> filterAndSort(List<Student> students, String department, boolean sortByScore) {
        List<Student> filteredStudents = students.stream()
                .filter(student -> department.isEmpty() || student.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());

        if (sortByScore) {
            filteredStudents.sort(Comparator.comparingInt(Student::getTotalScore).reversed());
        }

        return filteredStudents;
    }

    public boolean addStudentToCsv(Student student) {
        Set<ConstraintViolation<Student>> violations = validator.validate(student);
        if (!violations.isEmpty()) {
            violations.forEach(violation -> System.out.println("Validation error: " + violation.getMessage()));
            return false; // Student data is invalid, so do not write to CSV
        }

        try (CSVWriter csvWriter = (CSVWriter) new CSVWriterBuilder(new FileWriter(FILE_NAME, true)).withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build()) {
            String[] studentData = {String.valueOf(student.getId()),student.getName(), student.getDepartment(), String.valueOf(student.getTotalScore())};
            csvWriter.writeNext(studentData);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Student findStudentById(Integer id) {
        return readCsvAndValidate().stream()
                .filter(student -> student.getId()==id)
                .findFirst().orElse(null);
    }

    public boolean updateStudent(Integer id,String newName, String newDepartment, int newTotalScore) {
        List<Student> students = readCsvAndValidate();
        Student student = findStudentById(id);
        if (student == null) return false;

        student.setName(newName.isEmpty() ? student.getName() : newName);
        student.setDepartment(newDepartment.isEmpty() ? student.getDepartment() : newDepartment);
        student.setTotalScore(newTotalScore);

        students.removeIf(student1 -> student1.getId()==student.getId());
        students.add(student);
        students.sort(Comparator.comparingInt(Student::getId));

        return overwriteCsv(students);
    }

    public boolean deleteStudent(Integer id) {
        List<Student> students = readCsvAndValidate();
        boolean removed = students.removeIf(student -> student.getId()==id);

        return removed && overwriteCsv(students);
    }

    private boolean overwriteCsv(List<Student> students) {
        try (CSVWriter csvWriter = (CSVWriter) new CSVWriterBuilder(new FileWriter(FILE_NAME)).withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER).build()) {
            csvWriter.writeNext(new String[]{"ID","Name", "Department", "TotalScore"});
            for (Student student : students) {
                csvWriter.writeNext(new String[]{String.valueOf(student.getId()),student.getName(), student.getDepartment(), String.valueOf(student.getTotalScore())});
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}