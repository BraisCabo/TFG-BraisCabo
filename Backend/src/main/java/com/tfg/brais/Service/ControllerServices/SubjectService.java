package com.tfg.brais.Service.ControllerServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.SubjectDetailedDTO;
import com.tfg.brais.Repository.SubjectRepository;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectCheckService subjectCheckService;

    public SubjectService(SubjectRepository subjectRepository, SubjectCheckService subjectCheckService) {
        this.subjectRepository = subjectRepository;
        this.subjectCheckService = subjectCheckService;
    }

    public ResponseEntity<Page<SubjectDetailedDTO>> findAll(String name, PageRequest pageRequest) {
        Page<SubjectDetailedDTO> page = subjectRepository.findAllByName(name, pageRequest);
        return ResponseEntity.ok(page);
    }

    public ResponseEntity<SubjectDetailedDTO> findById(long subjectId) {
        ResponseEntity<Subject> response = subjectCheckService.findById(subjectId);
        if (response.getStatusCode().is2xxSuccessful()){
            return ResponseEntity.ok(new SubjectDetailedDTO(response.getBody()));
        }
        return new ResponseEntity<>(response.getStatusCode());
    }

    public ResponseEntity<Subject> findSubjectById(long subjectId){
        return subjectCheckService.findById(subjectId);
    }

    public ResponseEntity<Subject> addStudent(User user, long subjectId) {
        ResponseEntity<Subject> subjectCheckResponse = subjectCheckService.findById(subjectId);
        if (subjectCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Subject>(subjectCheckResponse.getStatusCode());
        }
        Subject subject = subjectCheckResponse.getBody();
        subject.getStudents().add(user);
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

        public ResponseEntity<Subject> addTeacher(User user, long subjectId) {
        ResponseEntity<Subject> subjectCheckResponse = subjectCheckService.findById(subjectId);
        if (subjectCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Subject>(subjectCheckResponse.getStatusCode());
        }
        Subject subject = subjectCheckResponse.getBody();
        subject.getTeachers().add(user);
        return ResponseEntity.ok(subjectRepository.save(subject));
    }
}
