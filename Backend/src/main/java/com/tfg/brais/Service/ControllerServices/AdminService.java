package com.tfg.brais.Service.ControllerServices;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.SubjectChangesDTO;
import com.tfg.brais.Model.DTOS.SubjectDetailedDTO;
import com.tfg.brais.Repository.SubjectRepository;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.Service.ComplementaryServices.FileService;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
@Service
public class AdminService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectCheckService subjectCheckService;

    @Autowired
    private FileService fileService;

    public AdminService(SubjectRepository subjectRepository, UserRepository userRepository, SubjectCheckService subjectCheckService) {
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.subjectCheckService = subjectCheckService;
    }

    public ResponseEntity<SubjectDetailedDTO> createSubject(SubjectChangesDTO subjectDTO, UriComponentsBuilder path){
        if (!subjectCheckService.canCreateSubject(subjectDTO)){
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }
        Subject subject = subjectDTO.generateSubject();
        subject.setStudents(loadUsers(subjectDTO.getStudents()));
        subject.setTeachers(loadUsers(subjectDTO.getTeachers()));
        subjectRepository.save(subject);
        return ResponseEntity.created(path.buildAndExpand(subject.getId()).toUri()).body(new SubjectDetailedDTO(subject));
    }

    private List<User> loadUsers(List<Long> userList){
        return userRepository.findAllById(userList);
    }

    public ResponseEntity<SubjectDetailedDTO> deleteById(long id){
        ResponseEntity<Subject> response = subjectCheckService.findById(id);
        if (response.getStatusCode().is2xxSuccessful()){
            try {
                fileService.deleteDirectory(Long.toString(id));
            } catch (Exception e) {
                System.out.println(e);
            }
            subjectRepository.deleteById(id);
            return ResponseEntity.ok(new SubjectDetailedDTO(response.getBody()));
        }
        return new ResponseEntity<>(response.getStatusCode());
    }

    public ResponseEntity<SubjectDetailedDTO> editSubject(long id, SubjectChangesDTO subjectDto){
        ResponseEntity<Subject> response = subjectCheckService.canEditSubject(id, subjectDto);
        if (response.getStatusCode().is4xxClientError()){
            return new ResponseEntity<>(response.getStatusCode());
        }
        Subject subject = response.getBody();
        subject.setName(subjectDto.getName());
        subject.setStudents(loadUsers(subjectDto.getStudents()));
        subject.setTeachers(loadUsers(subjectDto.getTeachers()));
        subjectRepository.save(subject);
        return ResponseEntity.ok(new SubjectDetailedDTO(subject));
    }
}
