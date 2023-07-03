package com.tfg.brais.Model.DTOS;

import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tfg.brais.Model.Exam;

import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ExamChangesDTO extends ExamBasicDTO{

    private String calificationPercentaje = "0";

    private boolean calificationVisible = true;

    private Date openingDate = new Date();

    private Date closingDate = new Date();

    private List<String> questions = new ArrayList<>();

    private boolean deletedFile = false;

    private boolean canRepeat = true;

    private boolean canUploadLate = true;

    private List<String> questionsCalifications = new ArrayList<>();

    public ExamChangesDTO(){}

    public ExamChangesDTO(Exam exam){
        super(exam);
        this.calificationPercentaje = exam.getCalificationPercentaje();
        this.calificationVisible = exam.isCalificationVisible();
        this.openingDate = exam.getOpeningDate();
        this.closingDate = exam.getClosingDate();
        this.questions = exam.getQuestions();
        this.canRepeat = exam.isCanRepeat();
        this.canUploadLate = exam.isCanUploadLate();
    }

    public Exam createExam(){
        Exam exam = super.creatExam();
        exam.setCalificationPercentaje(this.getCalificationPercentaje());
        exam.setCalificationVisible(this.isCalificationVisible());
        exam.setOpeningDate(this.getOpeningDate());
        exam.setClosingDate(this.getClosingDate());
        exam.setQuestions(this.getQuestions());
        exam.setCanRepeat(this.isCanRepeat());
        exam.setCanUploadLate(this.isCanUploadLate());
        return exam;
    }
}
