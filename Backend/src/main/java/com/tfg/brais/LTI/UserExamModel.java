package com.tfg.brais.LTI;

import com.tfg.brais.Model.Exam;

import lombok.Data;

@Data
public class UserExamModel {
    Exam exam;
    String email;
}
