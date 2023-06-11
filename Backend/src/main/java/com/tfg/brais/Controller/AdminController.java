package com.tfg.brais.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.SubjectDTO;
import com.tfg.brais.Service.AdminService;

@RequestMapping("/api")
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/subjects/")
    public ResponseEntity<Subject> createSubject(@RequestBody SubjectDTO subjectDTO) {
        UriComponentsBuilder path = fromCurrentRequest().path("/{id}");
        return adminService.createSubject(subjectDTO, path);
    }

    @PutMapping("/subjects/{id}")
    public ResponseEntity<Subject> editSubject(@RequestBody SubjectDTO subjectDTO, @PathVariable long id){
        return this.adminService.editSubject(id, subjectDTO);
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Subject> deleteById(@PathVariable long id){
        return this.adminService.deleteById(id);
    }
}
