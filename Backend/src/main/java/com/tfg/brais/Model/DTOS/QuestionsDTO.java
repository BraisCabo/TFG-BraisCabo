package com.tfg.brais.Model.DTOS;

import java.util.List;

import com.tfg.brais.Model.Exam;

import lombok.Data;

@Data
public class QuestionsDTO {

    private List<String> questions;
    private List<Double> califications;

    public QuestionsDTO(Exam exam){
        this.questions = exam.getQuestions();
        this.califications = exam.getQuestionsCalifications();
    }
}
