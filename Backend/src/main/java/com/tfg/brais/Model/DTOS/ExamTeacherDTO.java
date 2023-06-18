package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;

import lombok.Data;

@Data
public class ExamTeacherDTO extends ExamBasicDTO{

    private String calificationPercentaje = "0";

    private int exerciseUploads = 0;

    private boolean calificationVisible = true;

    private Date openingDate = new Date();

    private Date closingDate = new Date();

    private List<String> questions = new ArrayList<>();

    public ExamTeacherDTO(Exam exam){
        super(exam);
        this.calificationPercentaje = exam.getCalificationPercentaje();
        this.exerciseUploads = exam.getExerciseUploads().size();
        this.calificationVisible = exam.isCalificationVisible();
        this.openingDate = exam.getOpeningDate();
        this.closingDate = exam.getClosingDate();
        this.questions = exam.getQuestions();
    }
}
