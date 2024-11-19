package com.example.csvreader.validation;

import jakarta.validation.*;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidMarkValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMark {

    String message() default "Total score must be at least {value} to pass.";

    int value() default 40; // Default pass score threshold

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
