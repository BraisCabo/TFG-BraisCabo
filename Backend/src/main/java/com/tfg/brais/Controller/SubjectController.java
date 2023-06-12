package com.tfg.brais.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<Page<Subject>> findAll(String name, int page, int size){
        return subjectService.findAll(name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> findById(@PathVariable long id){
        return this.subjectService.findById(id);
    }

    @GetMapping("/{id}/students/")
    public ResponseEntity<Page<User>> findAllStudents(@PathVariable long id, HttpServletRequest request, String name, int page, int size){
        return this.subjectService.searchStudents(id, request.getUserPrincipal(), name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/teachers/")
    public ResponseEntity<Page<User>> findAllTeachers(@PathVariable long id, HttpServletRequest request, String name, int page, int size){
        return this.subjectService.searchTeachers(id, request.getUserPrincipal(), name, PageRequest.of(page, size));
    }
}
