package com.tfg.brais.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.DTOS.AnswersDTO;
import com.tfg.brais.Model.DTOS.CalificationDTO;
import com.tfg.brais.Service.ControllerServices.UploadService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subjects/{id}/exams/{examId}/uploads")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @GetMapping("/")
    public ResponseEntity<List<ExerciseUpload>> findAllUploads(@PathVariable long id, @PathVariable long examId, HttpServletRequest request, String name){
        return uploadService.findAllUploads(id, examId, request.getUserPrincipal(), name);
    }

    @PostMapping("/files")
    public ResponseEntity<ExerciseUpload> uploadExercise(@PathVariable long id, @PathVariable long examId, HttpServletRequest request, MultipartFile newFile){
        return uploadService.uploadExercise(id, examId, newFile, request.getUserPrincipal());
    }

    @PostMapping("/questions")
    public ResponseEntity<ExerciseUpload> uploadExercise(@PathVariable long id, @PathVariable long examId, HttpServletRequest request, @RequestBody List<String> answers){
        return uploadService.uploadExercise(id, examId, answers, request.getUserPrincipal());
    }

    @GetMapping("/{uploadId}")
    public ResponseEntity<ExerciseUpload> findUploadById(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.findUploadById(id, examId, uploadId, request.getUserPrincipal());
    }

    @GetMapping("/{uploadId}/questions")
    public ResponseEntity<AnswersDTO> findUploadQuestionsAndAnswersById(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.findUploadQuestionsAndAnswersById(id, examId, uploadId, request.getUserPrincipal());
    }

    @DeleteMapping("/{uploadId}")
    public ResponseEntity<ExerciseUpload> deleteUploadById(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.deleteUploadById(id, examId, uploadId, request.getUserPrincipal());
    }

    @GetMapping("/{uploadId}/files")
    public ResponseEntity<Resource> downloadUpload(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.downloadUploadById(id, examId, uploadId, request.getUserPrincipal());
    }

    @GetMapping("/files")
    public ResponseEntity<Resource> downloadAll(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return uploadService.findAllUploadsCompressed(id, examId, request.getUserPrincipal());
    }

    @PostMapping("{uploadId}/califications")
    public ResponseEntity<ExerciseUpload> uploadCalification(@PathVariable long id, @PathVariable long examId, @PathVariable long uploadId, HttpServletRequest request, @RequestBody CalificationDTO calification){
        return uploadService.uploadCalification(id, examId, uploadId, calification, request.getUserPrincipal());
    }

    @PutMapping("{uploadId}/califications")
    public ResponseEntity<ExerciseUpload> editCalification(@PathVariable long id, @PathVariable long examId, @PathVariable long uploadId, HttpServletRequest request, @RequestBody CalificationDTO calification){
        return uploadService.editCalification(id, examId, uploadId, calification, request.getUserPrincipal());
    }

}
