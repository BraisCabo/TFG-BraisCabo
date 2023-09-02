package com.tfg.brais.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.DTOS.AnswersDTO;
import com.tfg.brais.Service.ControllerServices.UploadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subjects/{id}/exams/{examId}/uploads")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @GetMapping("/")
    @Operation(summary = "Get all uploads of an exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Uploads found", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExerciseUpload.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<List<ExerciseUpload>> findAllUploads(@PathVariable long id, @PathVariable long examId, HttpServletRequest request, String name){
        return uploadService.findAllUploads(id, examId, request.getUserPrincipal(), name);
    }

    @PostMapping("/files")
    @Operation(summary = "Upload a new file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "File uploaded", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExerciseUpload.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<ExerciseUpload> uploadExercise(@PathVariable long id, @PathVariable long examId, HttpServletRequest request, MultipartFile newFile){
        return uploadService.uploadExercise(id, examId, newFile, request.getUserPrincipal());
    }

    @PostMapping("/questions")
    @Operation(summary = "Upload a new exam of questions type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Exam uploaded", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExerciseUpload.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<ExerciseUpload> uploadExercise(@PathVariable long id, @PathVariable long examId, HttpServletRequest request, @RequestBody List<String> answers){
        return uploadService.uploadExercise(id, examId, answers, request.getUserPrincipal());
    }

    @GetMapping("/{uploadId}")
    @Operation(summary = "Get an upload by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upload found", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExerciseUpload.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<ExerciseUpload> findUploadById(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.findUploadById(id, examId, uploadId, request.getUserPrincipal());
    }

    @GetMapping("/{uploadId}/questions")
    @Operation(summary = "Get all information of questions upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upload found", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExerciseUpload.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<AnswersDTO> findUploadQuestionsAndAnswersById(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.findUploadQuestionsAndAnswersById(id, examId, uploadId, request.getUserPrincipal());
    }

    @DeleteMapping("/{uploadId}")
    @Operation(summary = "Delete an upload by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upload deleted", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExerciseUpload.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<ExerciseUpload> deleteUploadById(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.deleteUploadById(id, examId, uploadId, request.getUserPrincipal());
    }

    @GetMapping("/{uploadId}/files")
    @Operation(summary = "Download an upload by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upload downloaded", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExerciseUpload.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<Resource> downloadUpload(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.downloadUploadById(id, examId, uploadId, request.getUserPrincipal());
    }

    @GetMapping("/files")
    @Operation(summary = "Download all uploads of an exam")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Uploads downloaded", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ExerciseUpload.class))),
        @ApiResponse(responseCode = "403", description = "User is not the teacher of the subject"),
        @ApiResponse(responseCode = "404", description = "Subject or exam not found")
    })
    public ResponseEntity<Resource> downloadAll(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return uploadService.findAllUploadsCompressed(id, examId, request.getUserPrincipal());
    }
}
