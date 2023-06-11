package com.tfg.brais.Service;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.SubjectRepository;

@Service
public class SubjectService {
    
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private AccountService accountService;

    public ResponseEntity<Page<Subject>> findAll(String name, PageRequest pageRequest){
        Page<Subject> page = subjectRepository.findAllByName(name, pageRequest);
        if (!page.hasContent()){
            return new ResponseEntity<Page<Subject>>(HttpStatusCode.valueOf(404));
        }else{
            return ResponseEntity.ok(page);
        }
    }

    public ResponseEntity<Subject> findById(long id){
        try{
            return ResponseEntity.ok(subjectRepository.findById(id).get());
         } catch (Exception e){
            return new ResponseEntity<Subject>(HttpStatusCode.valueOf(404));
         }
    }

    private ResponseEntity<Page<User>> checkIfCanSee(long id, Principal userPrincipal){
        if (userPrincipal == null){
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }
        ResponseEntity<Subject> subjectResponse = findById(id);
        if (subjectResponse.getStatusCode().is4xxClientError()){
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }

        Subject subject = subjectResponse.getBody();
        ResponseEntity<User> userResponse = accountService.findByMail(userPrincipal.getName());
        if (userResponse.getStatusCode().is4xxClientError()){
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }

        User user = userResponse.getBody();

        boolean isStudent = subjectRepository.countBySubjectIdAndStudentId(subject.getId(),user.getId()) > 0;
        boolean isTeacher = subjectRepository.countBySubjectIdAndTeacherId(subject.getId(),user.getId()) > 0;
        boolean isAdmin = user.getRoles().contains("ADMIN");

        if (!(isStudent || isTeacher || isAdmin)){
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }
            return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<Page<User>> searchStudents(long id, Principal userPrincipal, String name, PageRequest pageRequest) {
        if (name == null){
            name = "";
        }
        ResponseEntity<Page<User>> response = checkIfCanSee(id, userPrincipal);
        if (response.getStatusCode().is4xxClientError()){
            return response;
        }

        Page<User> page = subjectRepository.findAllStudentBySubjectIdAndName(id, name, pageRequest);

        if (!page.hasContent()){
            return new ResponseEntity<Page<User>>(HttpStatusCode.valueOf(404));
        }
        return ResponseEntity.ok(page);

    }

    public ResponseEntity<Page<User>> searchTeachers(long id, Principal userPrincipal, String name, PageRequest pageRequest) {
        if (name == null){
            name = "";
        }
        ResponseEntity<Page<User>> response = checkIfCanSee(id, userPrincipal);
        if (response.getStatusCode().is4xxClientError()){
            return response;
        }

        Page<User> page = subjectRepository.findAllTeachersBySubjectIdAndName(id, name, pageRequest);

        if (!page.hasContent()){
            return new ResponseEntity<Page<User>>(HttpStatusCode.valueOf(404));
        }
        return ResponseEntity.ok(page);
    }
}
