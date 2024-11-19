package com.example.csvreader.validation;

import jakarta.validation.*;

public class ValidMarkValidator implements ConstraintValidator<ValidMark, Integer> {

    private int passMark;

    @Override
    public void initialize(ValidMark validMark) {
        this.passMark = validMark.value();
    }

    @Override
    public boolean isValid(Integer score, ConstraintValidatorContext context) {
        if(score == null || score < 0 || score > 100)
        {
            throw new ValidationException("The score must be between 0 and 100");

//            return false;
        }

        return true;
    }
}