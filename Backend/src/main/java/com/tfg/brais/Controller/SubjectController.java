package com.tfg.brais.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.brais.Model.DTOS.StudentCalificationDTO;
import com.tfg.brais.Model.DTOS.SubjectDetailedDTO;
import com.tfg.brais.Model.DTOS.UserBasicDTO;
import com.tfg.brais.Service.ControllerServices.SubjectService;
import com.tfg.brais.Service.ControllerServices.UserSubjectService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserSubjectService userSubjectService;

    @GetMapping("/")
    public ResponseEntity<Page<SubjectDetailedDTO>> findAll(String name, int page, int size){
        return subjectService.findAll(name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDetailedDTO> findById(@PathVariable long id){
        return this.subjectService.findById(id);
    }

    @GetMapping("/{id}/students/")
    public ResponseEntity<Page<UserBasicDTO>> findAllStudents(@PathVariable long id, HttpServletRequest request, String name, int page, int size){
        return this.userSubjectService.searchStudents(id, request.getUserPrincipal(), name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/teachers/")
    public ResponseEntity<Page<UserBasicDTO>> findAllTeachers(@PathVariable long id, HttpServletRequest request, String name, int page, int size){
        return this.userSubjectService.searchTeachers(id, request.getUserPrincipal(), name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/users/{teacherId}/")
    public ResponseEntity<Boolean> isTeacherOfSubject(@PathVariable long id, @PathVariable long teacherId){
        return this.userSubjectService.isTeacherOfSubject(id, teacherId);
    }

    @GetMapping("/{id}/users/{studentId}/califications")
    public ResponseEntity<StudentCalificationDTO> findAllCalifications(@PathVariable long id, @PathVariable long studentId, HttpServletRequest request){
        return this.userSubjectService.searchCalifications(id, studentId, request.getUserPrincipal());
    }



}
