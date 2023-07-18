package com.tfg.brais.Service.ComplementaryServices;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.SubjectChangesDTO;
import com.tfg.brais.Repository.SubjectRepository;

@FunctionalInterface
interface BooleanFunction {
    boolean apply(boolean b1, boolean b2, boolean b3);
}

@Service
public class SubjectCheckService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserCheckService userCheckService;

    public SubjectCheckService(SubjectRepository repository, UserCheckService userCheckService) {
        this.subjectRepository = repository;
        this.userCheckService = userCheckService;
    }
    

    public Boolean isTeacherOfSubject(long userId, long subjectId){
        if (subjectRepository.countBySubjectIdAndTeacherId(subjectId, userId) > 0) {
            return true;
        }
        return false;
    }

    public Boolean isStudentOfSubject(long userId, long subjectId) {
        if (subjectRepository.countBySubjectIdAndStudentId(subjectId, userId) > 0) {
            return true;
        }
        return false;
    }

    public ResponseEntity<Subject> findById(long subjectId) {
        try {
            return ResponseEntity.ok(subjectRepository.findById(subjectId).get());
        } catch (Exception e) {
            return new ResponseEntity<Subject>(HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<Page<User>> checkIfCanSeeMembers(long subjectId, Principal userPrincipal) {
        return checkIfCanSee(subjectId, userPrincipal, (b1, b2, b3) -> {
            return !(b1 || b2 || b3);
        });
    }

    public ResponseEntity<Page<User>> checkIfCanSeeContent(long subjectId, Principal userPrincipal) {
        return checkIfCanSee(subjectId, userPrincipal, (b1, b2, b3) -> {
            return !(b1 || b2);
        });
    }

    private ResponseEntity<Page<User>> checkIfCanSee(long subjectId, Principal userPrincipal, BooleanFunction predicate) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserNoCkeck(userPrincipal);
        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(userCheckResponse.getStatusCode());
        }
        User user = userCheckResponse.getBody();

        ResponseEntity<Subject> subjectResponse = findById(subjectId);
        if (subjectResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }

        Subject subject = subjectResponse.getBody();

        boolean isStudent = subjectRepository.countBySubjectIdAndStudentId(subject.getId(), user.getId()) > 0;
        boolean isTeacher = subjectRepository.countBySubjectIdAndTeacherId(subject.getId(), user.getId()) > 0;
        boolean isAdmin = user.getRoles().contains("ADMIN");

        if (predicate.apply(isStudent, isTeacher, isAdmin)) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    private boolean checkMembersList(List<Long> studentList, List<Long> teacherList) {
        for (Long id : teacherList) {
            if (studentList.contains(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean existSubject(String name) {
        return this.subjectRepository.findByName(name).isPresent();
    }

    public boolean canCreateSubject(SubjectChangesDTO subjectDTO) {
        if (subjectDTO.getName() == null || subjectDTO.getName().isEmpty()){
            return false;
        }
        if (existSubject(subjectDTO.getName())
                || checkMembersList(subjectDTO.getStudents(), subjectDTO.getTeachers())) {
            return false;
        }
        return true;
    }

    public ResponseEntity<Subject> canEditSubject(long subjectId, SubjectChangesDTO subjectDto){
        ResponseEntity<Subject> response = this.findById(subjectId);
        if (response.getStatusCode().is4xxClientError()){
            return response;
        }
        if (checkMembersList(subjectDto.getStudents(), subjectDto.getTeachers())){
            return new ResponseEntity<Subject>(HttpStatusCode.valueOf(403));
        }
        Subject subject = response.getBody();
        if (subjectDto.getName() == null || subjectDto.getName().isEmpty()){
            return new ResponseEntity<Subject>(HttpStatusCode.valueOf(403));
        }
        if (existSubject(subjectDto.getName()) && !subject.getName().equals(subjectDto.getName())){
            return new ResponseEntity<Subject>(HttpStatusCode.valueOf(403));
        }
        return ResponseEntity.ok(subject);
    }

    public ResponseEntity<Subject> findByName(String name){
        try {
            Subject subject = this.subjectRepository.findByName(name).get();
            return ResponseEntity.ok(subject);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
