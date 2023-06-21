package com.tfg.brais.Model.DTOS;

import java.util.ArrayList;
import java.util.List;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;

import lombok.Data;

@Data
public class SubjectDetailedDTO {
    
    Long id;
    String name;
    List<UserBasicDTO> students;
    List<UserBasicDTO> teachers;

    public SubjectDetailedDTO() {
    }

    public SubjectDetailedDTO(Subject subject){
        this.name = subject.getName();
        this.id = subject.getId();
        this.students = convertUsers(subject.getStudents());
        this.teachers = convertUsers(subject.getTeachers());
    }

    private List<UserBasicDTO> convertUsers(List<User> users) {
        List<UserBasicDTO> basicUsers = new ArrayList<>();
        for(User user : users){
            basicUsers.add(new UserBasicDTO(user));
        }
        return basicUsers;
    }
}
