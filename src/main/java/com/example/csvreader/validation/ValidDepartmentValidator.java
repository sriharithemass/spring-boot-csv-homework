package com.example.csvreader.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class ValidDepartmentValidator implements ConstraintValidator<ValidDepartment,String> {
    private final Set<String> departments = Set.of("IT","CSE","EEE","ECE","MECH");

    @Override
    public boolean isValid(String department, ConstraintValidatorContext context) {
        if (department == null || !departments.contains(department)) {
            throw new ValidationException("\""+department+"\"" + " is not a valid department. " +" The only departments valid are 'EEE,ECE,IT,MECH,CSE'");

//            return false;
        }

        return true;
    }
}
