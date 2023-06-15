package com.tfg.brais.Service.ComplementaryServices;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.SubjectDTO;
import com.tfg.brais.Model.User;
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
    

    public Boolean isTeacherOfSubject(long subjectId, long userId) {
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

    public ResponseEntity<Subject> findById(long id) {
        try {
            return ResponseEntity.ok(subjectRepository.findById(id).get());
        } catch (Exception e) {
            return new ResponseEntity<Subject>(HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<Page<User>> checkIfCanSeeMembers(long id, Principal userPrincipal) {
        return checkIfCanSee(id, userPrincipal, (b1, b2, b3) -> {
            return !(b1 || b2 || b3);
        });
    }

    public ResponseEntity<Page<User>> checkIfCanSeeContent(long id, Principal userPrincipal) {
        return checkIfCanSee(id, userPrincipal, (b1, b2, b3) -> {
            return !(b1 || b2);
        });
    }

    private ResponseEntity<Page<User>> checkIfCanSee(long id, Principal userPrincipal, BooleanFunction predicate) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserPrincipal(id, userPrincipal);
        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(userCheckResponse.getStatusCode());
        }
        User user = userCheckResponse.getBody();

        ResponseEntity<Subject> subjectResponse = findById(id);
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

    public boolean canCreateSubject(SubjectDTO subjectDTO) {
        if (subjectDTO.getName() == null || subjectDTO.getName().isEmpty()){
            return false;
        }
        if (existSubject(subjectDTO.getName())
                || checkMembersList(subjectDTO.getStudents(), subjectDTO.getTeachers())) {
            return false;
        }
        return true;
    }

    public ResponseEntity<Subject> canEditSubject(long id, SubjectDTO subjectDto){
        ResponseEntity<Subject> response = this.findById(id);
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
}
