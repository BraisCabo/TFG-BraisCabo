package com.tfg.brais.Model.DTOS;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.tfg.brais.Model.Exam;

import lombok.Data;

@Data
public class ImportedExamDTO {

    private String name;

    private String calificationPercentaje = "0";

    private boolean visibleExam = true;

    private boolean calificationVisible = true;

    private Date openingDate = new Date();

    private Date closingDate = new Date();

    private boolean canUploadLate = true;

    private String maxTime;

    private MultipartFile file;

    public ImportedExamDTO(){}

        public Exam createExam(){
        Exam exam = new Exam();
        exam.setName(this.getName());
        exam.setType("QUESTIONS");
        exam.setCalificationPercentaje(this.getCalificationPercentaje());
        exam.setVisibleExam(this.isVisibleExam());
        exam.setCalificationVisible(this.isCalificationVisible());
        exam.setOpeningDate(this.getOpeningDate());
        exam.setClosingDate(this.getClosingDate());
        exam.setCanUploadLate(this.isCanUploadLate());
        if (this.getMaxTime() != null){
            exam.setMaxTime(Integer.parseInt(this.getMaxTime()));
        }
        return exam;
    }
}
