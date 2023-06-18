package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;
import java.util.List;

import com.tfg.brais.Model.Subject;

import lombok.Data;

@Data
public class SubjectUsersDTO {
    private List<SubjectDetailedDTO> teachedSubject = new ArrayList<>();
    private List<SubjectDetailedDTO> studiedSubject = new ArrayList<>();

    public void setStudiedSubject(List<Subject> subjects){
        for(Subject subject : subjects){
            studiedSubject.add(new SubjectDetailedDTO(subject));
        }
    }

    public void setTeachedSubject(List<Subject> subjects){
        for(Subject subject : subjects){
            teachedSubject.add(new SubjectDetailedDTO(subject));
        }
    }
}
