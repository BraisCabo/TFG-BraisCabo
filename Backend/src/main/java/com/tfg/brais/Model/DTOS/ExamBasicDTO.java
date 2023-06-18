package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;
import java.util.List;

import com.tfg.brais.Model.Exam;

import lombok.Data;

@Data
public class ExamBasicDTO {
    private Long id;

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
}
