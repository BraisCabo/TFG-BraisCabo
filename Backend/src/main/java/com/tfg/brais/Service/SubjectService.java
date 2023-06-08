package com.tfg.brais.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Repository.SubjectRepository;

@Service
public class SubjectService {
    
    @Autowired
    private SubjectRepository subjectRepository;

    public ResponseEntity<List<Subject>> findAll(){
        List<Subject> list = subjectRepository.findAll();
        if (list.isEmpty()){
            return new ResponseEntity<List<Subject>>(HttpStatusCode.valueOf(404));
        }else{
            return ResponseEntity.ok(list);
        }
    }

    public ResponseEntity<Subject> findById(long id){
        try{
            return ResponseEntity.ok(subjectRepository.findById(id).get());
         } catch (Exception e){
            return new ResponseEntity<Subject>(HttpStatusCode.valueOf(404));
         }
    }
}
