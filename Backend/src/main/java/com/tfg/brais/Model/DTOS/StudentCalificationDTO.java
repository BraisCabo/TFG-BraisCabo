package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;
import java.util.List;

import com.tfg.brais.Model.ExerciseUpload;

import lombok.Data;

@Data
public class StudentCalificationDTO {

    private List<String> califications = new ArrayList<>();
    private List<String> comments = new ArrayList<>();
    private List<String> percentajes = new ArrayList<>();
    private List<String> examNames = new ArrayList<>();
    private String finalCalification;

    public StudentCalificationDTO(List<ExerciseUpload> uploads) {
        long finalCalification = 0l;
        for (ExerciseUpload upload : uploads) {
            this.califications.add(upload.getCalification() == null ? "" : upload.getCalification());
            this.comments.add(upload.getComment() == null ? "" : upload.getComment());
            this.percentajes.add(upload.getExam().getCalificationPercentaje());
            this.examNames.add(upload.getExam().getName());
            long thisCalification;
            if (upload.getCalification() == null || upload.getCalification().equals("")){
               thisCalification = 0L;
            }else {
                thisCalification = Long.parseLong(upload.getCalification());
            }
            finalCalification += thisCalification
                    * Long.parseLong(upload.getExam().getCalificationPercentaje()) / 100;
        }
        this.finalCalification = String.valueOf(finalCalification);
    }
}
