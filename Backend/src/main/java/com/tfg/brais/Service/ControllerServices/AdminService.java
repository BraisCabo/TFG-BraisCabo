package com.tfg.brais.Service.ControllerServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.SubjectDTO;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.SubjectRepository;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
@Service
public class AdminService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectCheckService subjectCheckService;

    public ResponseEntity<Subject> createSubject(SubjectDTO subjectDTO, UriComponentsBuilder path){
        if (subjectCheckService.canCreateSubject(subjectDTO)){
            return new ResponseEntity<Subject>(HttpStatusCode.valueOf(403));
        }
        Subject subject = subjectDTO.generateSubject();
        subject.setStudents(loadUsers(subjectDTO.getStudents()));
        subject.setTeachers(loadUsers(subjectDTO.getTeachers()));
        subjectRepository.save(subject);
        return ResponseEntity.created(path.buildAndExpand(subject.getId()).toUri()).body(subject);
    }

    public List<User> loadUsers(List<Long> userList){
        return userRepository.findAllById(userList);
    }

    public ResponseEntity<Subject> deleteById(long id){
        ResponseEntity<Subject> response = subjectCheckService.findById(id);
        if (response.getStatusCode().is2xxSuccessful()){
            subjectRepository.deleteById(id);
            return response;
        }
        return response;
    }

    public ResponseEntity<Subject> editSubject(long id, SubjectDTO subjectDto){
        ResponseEntity<Subject> response = subjectCheckService.canEditSubject(id, subjectDto);
        if (response.getStatusCode().is4xxClientError()){
            return response;
        }
        Subject subject = response.getBody();
        subject.setName(subjectDto.getName());
        subject.setStudents(loadUsers(subjectDto.getStudents()));
        subject.setTeachers(loadUsers(subjectDto.getTeachers()));
        subjectRepository.save(subject);
        return ResponseEntity.ok(subject);
    }
}
