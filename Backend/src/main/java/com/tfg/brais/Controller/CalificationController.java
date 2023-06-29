package com.tfg.brais.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.DTOS.CalificationDTO;
import com.tfg.brais.Model.DTOS.StudentCalificationDTO;
import com.tfg.brais.Model.DTOS.TeachersCalificationsDTO;
import com.tfg.brais.Service.ControllerServices.CalificationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subjects/{id}")
public class CalificationController {

    @Autowired
    private CalificationService calificationService;

    @GetMapping("/users/{studentId}/califications")
    public ResponseEntity<StudentCalificationDTO> findAllCalifications(@PathVariable long id, @PathVariable long studentId, HttpServletRequest request){
        return this.calificationService.searchCalifications(id, studentId, request.getUserPrincipal());
    }

    @GetMapping("/califications")
    public ResponseEntity<List<TeachersCalificationsDTO>> findAllTeachersCalifications(@PathVariable long id, HttpServletRequest request){
        return this.calificationService.searchCalificationsTeachers(id, request.getUserPrincipal());
    }

    @PostMapping("/exams/{examId}/uploads/{uploadId}/califications")
    public ResponseEntity<ExerciseUpload> uploadCalification(@PathVariable long id, @PathVariable long examId, @PathVariable long uploadId, HttpServletRequest request, @RequestBody CalificationDTO calification){
        return calificationService.uploadCalification(id, examId, uploadId, calification, request.getUserPrincipal());
    }

    @PutMapping("/exams/{examId}/uploads/{uploadId}/califications")
    public ResponseEntity<ExerciseUpload> editCalification(@PathVariable long id, @PathVariable long examId, @PathVariable long uploadId, HttpServletRequest request, @RequestBody CalificationDTO calification){
        return calificationService.editCalification(id, examId, uploadId, calification, request.getUserPrincipal());
    }
}