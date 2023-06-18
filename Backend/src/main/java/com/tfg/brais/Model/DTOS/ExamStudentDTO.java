package com.tfg.brais.Model.DTOS;

import java.util.Date;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;

import lombok.Data;

@Data
public class ExamStudentDTO extends ExamBasicDTO{

    private Long id;

    private String name;

    private String type = "UPLOAD";

    private String calificationPercentaje = "0";

    private ExerciseUpload exerciseUploads;

    private boolean visibleExam = true;

    private boolean calificationVisible = true;

    private Date openingDate = new Date();

    private Date closingDate = new Date();

    public ExamStudentDTO(Exam exam){
        super(exam);
        this.id = exam.getId();
        this.name = exam.getName();
        this.calificationPercentaje = exam.getCalificationPercentaje();
        this.visibleExam = exam.isVisibleExam();
        this.calificationVisible = exam.isCalificationVisible();
        this.openingDate = exam.getOpeningDate();
        this.closingDate = exam.getClosingDate();
    }
}
