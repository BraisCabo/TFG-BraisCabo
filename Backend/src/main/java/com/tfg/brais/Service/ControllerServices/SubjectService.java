package com.tfg.brais.Service.ControllerServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Subject;
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

    public ResponseEntity<SubjectDetailedDTO> findById(long id) {
        ResponseEntity<Subject> response = subjectCheckService.findById(id);
        if (response.getStatusCode().is2xxSuccessful()){
            return ResponseEntity.ok(new SubjectDetailedDTO(response.getBody()));
        }
        return new ResponseEntity<>(response.getStatusCode());
    }

    public ResponseEntity<Subject> findSubjectById(long id){
        return subjectCheckService.findById(id);

    }
}
