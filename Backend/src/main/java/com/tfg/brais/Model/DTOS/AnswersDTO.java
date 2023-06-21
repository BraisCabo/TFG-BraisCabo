package com.tfg.brais.Model.DTOS;

import java.util.List;

import lombok.Data;

@Data
public class AnswersDTO {
    
    List<String> answers;
    List<String> questions;

    public AnswersDTO() {

    }

    public AnswersDTO(List<String> answers, List<String> questions) {
        this.answers = answers;
        this.questions = questions;
    }
}
