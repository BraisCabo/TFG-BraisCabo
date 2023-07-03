package com.tfg.brais.Model.DTOS;

import java.util.List;

import lombok.Data;

@Data
public class AnswersDTO {
    
    List<String> answers;
    List<String> questions;
    List<String> califications;
    List<Double> questionCalifications;

    public AnswersDTO() {

    }

    public AnswersDTO(List<String> answers, List<String> questions, List<String> califications) {
        this.answers = answers;
        this.questions = questions;
        if (califications != null) {
            this.califications = califications;
        }
    }
}
