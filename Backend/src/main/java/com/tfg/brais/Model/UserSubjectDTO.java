package com.tfg.brais.Model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UserSubjectDTO {
    private List<Subject> teachedSubject = new ArrayList<>();
    private List<Subject> studiedSubject = new ArrayList<>();
}
