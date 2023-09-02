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
import com.tfg.brais.Model.DTOS.CalificationFileDTO;
import com.tfg.brais.Model.DTOS.CalificationQuestionsDTO;
import com.tfg.brais.Model.DTOS.StudentCalificationDTO;
import com.tfg.brais.Model.DTOS.TeachersCalificationsDTO;
import com.tfg.brais.Service.ControllerServices.CalificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subjects/{id}")
public class CalificationController {

    @Autowired
    private CalificationService calificationService;

    @GetMapping("/users/{studentId}/califications")
    @Operation(summary = "Get all califications of a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Califications found"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to see this califications"),
            @ApiResponse(responseCode = "404", description = "Subject or student not found")
    })
    public ResponseEntity<StudentCalificationDTO> findAllCalifications(@PathVariable long id,
            @PathVariable long studentId, HttpServletRequest request) {
        return this.calificationService.searchCalifications(id, studentId, request.getUserPrincipal());
    }

    @GetMapping("/califications")
    @Operation(summary = "Get all califications of all students")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Califications found"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to see this califications"),
            @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<List<TeachersCalificationsDTO>> findAllTeachersCalifications(@PathVariable long id,
            HttpServletRequest request) {
        return this.calificationService.searchCalificationsTeachers(id, request.getUserPrincipal());
    }

    @PostMapping("/exams/{examId}/uploads/{uploadId}/califications/files")
    @Operation(summary = "Upload a new calification of a file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calification uploaded"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to upload this calification"),
            @ApiResponse(responseCode = "404", description = "Subject, exam or upload not found")
    })
    public ResponseEntity<ExerciseUpload> uploadCalificationFiles(@PathVariable long id, @PathVariable long examId,
            @PathVariable long uploadId, HttpServletRequest request, @RequestBody CalificationFileDTO calification) {
        return calificationService.uploadCalificationFiles(id, examId, uploadId, calification,
                request.getUserPrincipal());
    }

    @PutMapping("/exams/{examId}/uploads/{uploadId}/califications/files")
    @Operation(summary = "Edit a calification of a file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calification edited"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to edit this calification"),
            @ApiResponse(responseCode = "404", description = "Subject, exam or upload not found")
    })
    public ResponseEntity<ExerciseUpload> editCalificationFiles(@PathVariable long id, @PathVariable long examId,
            @PathVariable long uploadId, HttpServletRequest request, @RequestBody CalificationFileDTO calification) {
        return calificationService.editCalificationFiles(id, examId, uploadId, calification,
                request.getUserPrincipal());
    }

    @PostMapping("/exams/{examId}/uploads/{uploadId}/califications/questions")
    @Operation(summary = "Edit a calification of a questions exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calification edited"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to edit this calification"),
            @ApiResponse(responseCode = "404", description = "Subject, exam or upload not found")
    })
    public ResponseEntity<ExerciseUpload> uploadCalificationQuestions(@PathVariable long id, @PathVariable long examId,
            @PathVariable long uploadId, HttpServletRequest request,
            @RequestBody CalificationQuestionsDTO calification) {
        return calificationService.uploadCalificationQuestions(id, examId, uploadId, calification,
                request.getUserPrincipal());
    }

    @PutMapping("/exams/{examId}/uploads/{uploadId}/califications/questions")
    @Operation(summary = "Edit a calification of a questions exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calification edited"),
            @ApiResponse(responseCode = "403", description = "User is not allowed to edit this calification"),
            @ApiResponse(responseCode = "404", description = "Subject, exam or upload not found")
    })
    public ResponseEntity<ExerciseUpload> editCalificationQuestions(@PathVariable long id, @PathVariable long examId,
            @PathVariable long uploadId, HttpServletRequest request,
            @RequestBody CalificationQuestionsDTO calification) {
        return calificationService.editCalificationQuestions(id, examId, uploadId, calification,
                request.getUserPrincipal());
    }
}
