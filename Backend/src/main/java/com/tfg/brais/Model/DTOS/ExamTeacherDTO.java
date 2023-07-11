package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tfg.brais.Model.Exam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ExamTeacherDTO extends ExamBasicDTO{

    private String calificationPercentaje = "0";

    private int exerciseUploads = 0;

    private boolean calificationVisible = true;

    private Date openingDate = new Date();

    private Date closingDate = new Date();

    private List<String> questions = new ArrayList<>();

    private String examFile;

    private boolean canRepeat = true;

    private boolean canUploadLate = true;

    private List<Double> questionsCalifications;

    private String maxTime;

    public ExamTeacherDTO(){}

    public ExamTeacherDTO(Exam exam){
        super(exam);
        this.calificationPercentaje = exam.getCalificationPercentaje();
        this.exerciseUploads = exam.getExerciseUploads().size();
        this.calificationVisible = exam.isCalificationVisible();
        this.openingDate = exam.getOpeningDate();
        this.closingDate = exam.getClosingDate();
        this.questions = exam.getQuestions();
        this.examFile = exam.getExamFile();
        this.canRepeat = exam.isCanRepeat();
        this.canUploadLate = exam.isCanUploadLate();
        this.questionsCalifications = exam.getQuestionsCalifications();
        this.maxTime = Integer.toString(exam.getMaxTime());
    }

    public Exam createExam(){
        Exam exam = super.creatExam();
        exam.setCalificationPercentaje(this.getCalificationPercentaje());
        exam.setCalificationVisible(this.isCalificationVisible());
        exam.setOpeningDate(this.getOpeningDate());
        exam.setClosingDate(this.getClosingDate());
        exam.setQuestions(this.getQuestions());
        exam.setExamFile(this.getExamFile());
        exam.setCanRepeat(this.isCanRepeat());
        exam.setCanUploadLate(this.isCanUploadLate());
        exam.setMaxTime(Integer.parseInt(this.getMaxTime()));
        return exam;
    }
}
