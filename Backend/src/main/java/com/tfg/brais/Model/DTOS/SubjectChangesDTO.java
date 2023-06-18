package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;
import java.util.List;

import com.tfg.brais.Model.Subject;

import lombok.Data;

@Data
public class SubjectChangesDTO {
    private String name;
    private List<Long> students = new ArrayList<>();
    private List<Long> teachers = new ArrayList<>();

    public Subject generateSubject(){
        Subject s = new Subject();
        s.setName(name);
        return s;
    }

}
