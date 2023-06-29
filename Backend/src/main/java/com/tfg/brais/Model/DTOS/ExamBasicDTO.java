package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;
import java.util.List;

import com.tfg.brais.Model.Exam;

import lombok.Data;

@Data
public class ExamBasicDTO {
    private Long id = null;

    private String name;

    private String type = "UPLOAD";

    private boolean visibleExam = true;

    public ExamBasicDTO() {
    }

    public ExamBasicDTO(Exam exam) {
        this.id = exam.getId();
        this.name = exam.getName();
        this.type = exam.getType();
        this.visibleExam = exam.isVisibleExam();
    }

    public List<ExamBasicDTO> convertToDTO(List<Exam> exams) {
        List<ExamBasicDTO> examsDTO = new ArrayList<>();
        for (Exam exam : exams) {
            examsDTO.add(new ExamBasicDTO(exam));
        }
        return examsDTO;
    }

    public Exam creatExam() {
        Exam exam = new Exam();
        exam.setId(this.id);
        exam.setName(this.name);
        exam.setType(this.type);
        exam.setVisibleExam(this.visibleExam);
        return exam;
    }
}
