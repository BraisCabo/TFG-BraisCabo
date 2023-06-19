package com.tfg.brais.Model.DTOS;

import java.util.Date;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ExamStudentDTO extends ExamBasicDTO{


    private String calificationPercentaje = "0";

    private ExerciseUpload exerciseUpload;

    private boolean calificationVisible = true;

    private Date openingDate = new Date();

    private Date closingDate = new Date();

    public ExamStudentDTO(Exam exam){
        super(exam);
        this.calificationPercentaje = exam.getCalificationPercentaje();
        this.calificationVisible = exam.isCalificationVisible();
        this.openingDate = exam.getOpeningDate();
        this.closingDate = exam.getClosingDate();
    }
}