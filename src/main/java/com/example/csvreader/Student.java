package com.example.csvreader;

import com.example.csvreader.validation.ValidDepartment;
import com.example.csvreader.validation.ValidMark;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Student {
    private int id;

    private String name;

    @ValidDepartment
    private String department;

    @ValidMark
    private int totalScore;
}
