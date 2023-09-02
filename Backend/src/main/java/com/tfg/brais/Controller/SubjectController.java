package com.tfg.brais.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tfg.brais.Model.DTOS.SubjectDetailedDTO;
import com.tfg.brais.Model.DTOS.UserBasicDTO;
import com.tfg.brais.Service.ControllerServices.SubjectService;
import com.tfg.brais.Service.ControllerServices.UserSubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserSubjectService userSubjectService;

    @GetMapping("/")
    @Operation(summary = "Get all subjects by name, page and page size")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subjects found"),
    })
    public ResponseEntity<Page<SubjectDetailedDTO>> findAll(String name, int page, int size){
        return subjectService.findAll(name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a subject by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject found"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<SubjectDetailedDTO> findById(@PathVariable long id){
        return this.subjectService.findById(id);
    }

    @GetMapping("/{id}/students/")
    @Operation(summary = "Get all students of a subject by name, page and page size")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students found"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<Page<UserBasicDTO>> findAllStudents(@PathVariable long id, HttpServletRequest request, String name, int page, int size){
        return this.userSubjectService.searchStudents(id, request.getUserPrincipal(), name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/teachers/")
    @Operation(summary = "Get all teachers of a subject by name, page and page size")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teachers found"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<Page<UserBasicDTO>> findAllTeachers(@PathVariable long id, HttpServletRequest request, String name, int page, int size){
        return this.userSubjectService.searchTeachers(id, request.getUserPrincipal(), name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/users/{teacherId}/")
    @Operation(summary = "Check if a user is teacher of a subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User is teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or user not found")
    })
    public ResponseEntity<Boolean> isTeacherOfSubject(@PathVariable long id, @PathVariable long teacherId){
        return this.userSubjectService.isTeacherOfSubject(id, teacherId);
    }
}
