package com.tfg.brais.Service.ControllerServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Subject;
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

    public ResponseEntity<Page<Subject>> findAll(String name, PageRequest pageRequest) {
        Page<Subject> page = subjectRepository.findAllByName(name, pageRequest);
        if (!page.hasContent()) {
            return new ResponseEntity<Page<Subject>>(HttpStatusCode.valueOf(404));
        } else {
            return ResponseEntity.ok(page);
        }
    }

    public ResponseEntity<Subject> findById(long id) {
        return subjectCheckService.findById(id);
    }
}
