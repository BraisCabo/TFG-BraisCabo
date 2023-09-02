package com.tfg.brais.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.tfg.brais.Model.DTOS.ExamBasicDTO;
import com.tfg.brais.Model.DTOS.ExamChangesDTO;
import com.tfg.brais.Model.DTOS.ExamTeacherDTO;
import com.tfg.brais.Model.DTOS.ImportedExamDTO;
import com.tfg.brais.Model.DTOS.QuestionsDTO;
import com.tfg.brais.Service.ControllerServices.ExamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/subjects/{id}/exams")
@RestController
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping("/")
    @Operation(summary = "Create a new exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Exam created", content = @Content(schema = @Schema(implementation = ExamTeacherDTO.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<ExamTeacherDTO> createExam(@PathVariable long id, ExamChangesDTO exam, HttpServletRequest request, MultipartFile examFile){
        return this.examService.createExam(id, exam, request.getUserPrincipal(), examFile, fromCurrentRequest().path("/{id}"));
    }

    @GetMapping("/")
    @Operation(summary = "Get all exams of a subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exams found", content = @Content(schema = @Schema(implementation = ExamBasicDTO.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<List<ExamBasicDTO>> getExams(@PathVariable long id, HttpServletRequest request){
        return this.examService.findAllExamsBySubjectId(id, request.getUserPrincipal());
    }

    @GetMapping("/{examId}")
    @Operation(summary = "Get an exam by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam found", content = @Content(schema = @Schema(implementation = ExamBasicDTO.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<ExamBasicDTO> getExam(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return this.examService.findBySubjectIdAndId(id, examId, request.getUserPrincipal());
    }

    @GetMapping("/{examId}/questions")
    @Operation(summary = "Get all information of questions exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Questions found", content = @Content(schema = @Schema(implementation = QuestionsDTO.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<QuestionsDTO> getQuestions(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return this.examService.getExamQuestions(id, examId, request.getUserPrincipal());
    }

    @PutMapping("/{examId}")
    @Operation(summary = "Edit an exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exam edited", content = @Content(schema = @Schema(implementation = ExamTeacherDTO.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<ExamTeacherDTO> updateExam(@PathVariable long id, @PathVariable long examId, ExamChangesDTO exam, HttpServletRequest request, MultipartFile examFile){
        return this.examService.updateExam(id, examId, exam, examFile, request.getUserPrincipal());
    }

    @PatchMapping("/{examId}")
    @Operation(summary = "Change the visibility of an exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visibility changed", content = @Content(schema = @Schema(implementation = ExamTeacherDTO.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<ExamTeacherDTO> changeVisibility(@PathVariable long id, @PathVariable long examId, @RequestBody Boolean newVisibility, HttpServletRequest request){
        return this.examService.changeVisibility(id, examId, newVisibility, request.getUserPrincipal());
    }

    @GetMapping("/{examId}/files")
    @Operation(summary = "Get all files of an exam")
    @ApiResponses (value = {
        @ApiResponse(responseCode = "200", description = "Files found"),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<Resource> getFiles(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return this.examService.getExamFiles(id, examId, request.getUserPrincipal());
    }

    @PostMapping("/files")
    @Operation(summary = "Import an exam file")
    @ApiResponses (value = {
        @ApiResponse(responseCode = "201", description = "File imported", content = @Content(schema = @Schema(implementation = ExamTeacherDTO.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public ResponseEntity<ExamTeacherDTO> importExam(@PathVariable long id, HttpServletRequest request, ImportedExamDTO importedExam){
        return this.examService.importExamFile(id, importedExam, request.getUserPrincipal(), fromCurrentRequest().path("/{id}"));
    }

    @GetMapping("/{examId}/files/exports")
    @Operation(summary = "Get all exported files of an exam")
    @ApiResponses (value = {
        @ApiResponse(responseCode = "200", description = "Files found"),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<Resource> getExportedFiles(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return this.examService.exportExam(id, examId, request.getUserPrincipal());
    }

    @PostMapping("/{examId}")
    @Operation(summary = "Send califications to LTI")
    @ApiResponses (value = {
        @ApiResponse(responseCode = "200", description = "Califications sent"),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<String> sendCalificationsToLti(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return this.examService.sendCalificationsToLti(id, examId, request.getUserPrincipal());
    }
}
