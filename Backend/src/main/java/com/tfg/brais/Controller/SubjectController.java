package com.tfg.brais.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Service.SubjectService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @GetMapping("/")
    public ResponseEntity<List<Subject>> findAll(){
        return subjectService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> findById(@PathVariable long id){
        return this.subjectService.findById(id);
    }

    @GetMapping("/{id}/students/")
    public ResponseEntity<List<User>> findAllStudents(@PathVariable long id, HttpServletRequest request){
        return this.subjectService.findAllStudents(id, request.getUserPrincipal());
    }

    @GetMapping("/{id}/teachers/")
    public ResponseEntity<List<User>> findAllTeachers(@PathVariable long id, HttpServletRequest request){
        return this.subjectService.findAllTeachers(id, request.getUserPrincipal());
    }
}
