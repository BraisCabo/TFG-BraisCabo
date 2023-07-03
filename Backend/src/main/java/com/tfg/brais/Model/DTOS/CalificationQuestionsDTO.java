package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;

import lombok.Data;

@Data
public class CalificationQuestionsDTO {
    
    private ArrayList<String> questionsCalification;
    private String comment;
}
