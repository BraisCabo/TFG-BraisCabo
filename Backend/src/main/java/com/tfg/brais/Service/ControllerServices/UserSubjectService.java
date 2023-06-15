package com.tfg.brais.Service.ControllerServices;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.User;
import com.tfg.brais.Model.UserSubjectDTO;
import com.tfg.brais.Repository.SubjectRepository;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;

@Service
public class UserSubjectService {

    @Autowired
    private UserCheckService userCheckService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectCheckService subjectCheckService;

    public UserSubjectService(UserCheckService userCheckService, SubjectRepository subjectRepository,
            SubjectCheckService subjectCheckService) {
        this.userCheckService = userCheckService;
        this.subjectRepository = subjectRepository;
        this.subjectCheckService = subjectCheckService;
    }

    public ResponseEntity<UserSubjectDTO> findAllUserSubjects(long id, Principal userPrincipal) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserPrincipal(id, userPrincipal);

        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<UserSubjectDTO>(userCheckResponse.getStatusCode());
        }
        User user = userCheckResponse.getBody();

        UserSubjectDTO userSubjectDTO = new UserSubjectDTO();
        userSubjectDTO.setStudiedSubject(subjectRepository.findAllStudiedSubjects(user.getId()));
        userSubjectDTO.setTeachedSubject(subjectRepository.findAllTeachedSubjects(user.getId()));

        if (userSubjectDTO.getStudiedSubject().isEmpty() && userSubjectDTO.getTeachedSubject().isEmpty()) {
            return new ResponseEntity<UserSubjectDTO>(HttpStatusCode.valueOf(404));
        }
        return ResponseEntity.ok(userSubjectDTO);
    }

    public ResponseEntity<Page<User>> searchStudents(long id, Principal userPrincipal, String name,
            PageRequest pageRequest) {
        return this.searchUsers(id, userPrincipal, this.fixName(name),
                subjectRepository.findAllStudentBySubjectIdAndName(id, this.fixName(name), pageRequest));

    }

    public ResponseEntity<Page<User>> searchTeachers(long id, Principal userPrincipal, String name,
            PageRequest pageRequest) {
        return this.searchUsers(id, userPrincipal, this.fixName(name),
                subjectRepository.findAllTeachersBySubjectIdAndName(id, this.fixName(name), pageRequest));
    }

    private String fixName(String name) {
        return name == null ? "" : name;
    }

    private ResponseEntity<Page<User>> searchUsers(long id, Principal userPrincipal, String name, Page<User> users) {
        ResponseEntity<Page<User>> response = subjectCheckService.checkIfCanSeeMembers(id, userPrincipal);
        if (response.getStatusCode().is4xxClientError()) {
            return response;
        }

        if (!users.hasContent()) {
            return new ResponseEntity<Page<User>>(HttpStatusCode.valueOf(404));
        }
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<Boolean> isTeacherOfSubject(long id, long userId) {
        if (subjectCheckService.isTeacherOfSubject(id, userId)) {
            return ResponseEntity.ok(true);
        }
        return new ResponseEntity<Boolean>(false, HttpStatusCode.valueOf(404));
    }
}
