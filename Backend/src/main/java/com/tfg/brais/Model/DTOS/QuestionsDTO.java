package com.tfg.brais.Model.DTOS;

import java.util.Date;
import java.util.List;

import com.tfg.brais.Model.Exam;

import lombok.Data;

@Data
public class QuestionsDTO {

    private List<String> questions;
    private List<Double> califications;
    private Date startedDate;
    private int maxTime;
    private Date closingDate;

    public QuestionsDTO(Exam exam){
        this.questions = exam.getQuestions();
        this.califications = exam.getQuestionsCalifications();
        this.maxTime = exam.getMaxTime();
        this.closingDate = exam.getClosingDate();
    }
}
