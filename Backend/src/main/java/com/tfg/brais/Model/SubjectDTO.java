package com.tfg.brais.Model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SubjectDTO {
    private String name;
    private List<Long> students = new ArrayList<>();
    private List<Long> teachers = new ArrayList<>();

    public Subject generateSubject(){
        Subject s = new Subject();
        s.setName(name);
        return s;
    }
}
