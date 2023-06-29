package com.tfg.brais.Service.ControllerServices;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.SubjectUsersDTO;
import com.tfg.brais.Model.DTOS.UserBasicDTO;
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

    public ResponseEntity<SubjectUsersDTO> findAllUserSubjects(long userId, Principal userPrincipal) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserPrincipal(userId, userPrincipal);

        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<SubjectUsersDTO>(userCheckResponse.getStatusCode());
        }
        User user = userCheckResponse.getBody();

        SubjectUsersDTO userSubjectDTO = new SubjectUsersDTO();
        userSubjectDTO.setStudiedSubject(subjectRepository.findAllStudiedSubjects(user.getId()));
        userSubjectDTO.setTeachedSubject(subjectRepository.findAllTeachedSubjects(user.getId()));
        return ResponseEntity.ok(userSubjectDTO);
    }

    public ResponseEntity<Page<UserBasicDTO>> searchStudents(long subjectId, Principal userPrincipal, String name,
            PageRequest pageRequest) {
        UserBasicDTO userConverter = new UserBasicDTO();
        return this.searchUsers(subjectId, userPrincipal, this.fixName(name),
                userConverter.convertPage(
                        subjectRepository.findAllStudentBySubjectIdAndName(subjectId, this.fixName(name), pageRequest)));

    }

    public ResponseEntity<Page<UserBasicDTO>> searchTeachers(long subjectId, Principal userPrincipal, String name,
            PageRequest pageRequest) {
        UserBasicDTO userConverter = new UserBasicDTO();
        return this.searchUsers(subjectId, userPrincipal, this.fixName(name),
                userConverter.convertPage(
                        subjectRepository.findAllTeachersBySubjectIdAndName(subjectId, this.fixName(name), pageRequest)));
    }

    private String fixName(String name) {
        return name == null ? "" : name;
    }

    private ResponseEntity<Page<UserBasicDTO>> searchUsers(long subjectId, Principal userPrincipal, String name,
            Page<UserBasicDTO> users) {
        ResponseEntity<Page<User>> response = subjectCheckService.checkIfCanSeeMembers(subjectId, userPrincipal);
        if (response.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Page<UserBasicDTO>>(response.getStatusCode());
        }
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<Boolean> isTeacherOfSubject(long subjectId, long userId) {
        return ResponseEntity.ok(subjectCheckService.isTeacherOfSubject(userId, subjectId));
    }
}
