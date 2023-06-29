package com.tfg.brais.Model.DTOS;

import java.util.List;

import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.User;

import lombok.Data;

@Data
public class TeachersCalificationsDTO {
    
    private String studentName;
    private String studentEmail;
    private StudentCalificationDTO studentCalifications;

    public TeachersCalificationsDTO(List<ExerciseUpload> uploads, User user){
        this.studentName = user.getName() + " " + user.getLastName();
        this.studentEmail = user.getEmail();
        this.studentCalifications = new StudentCalificationDTO(uploads);
    }
}
