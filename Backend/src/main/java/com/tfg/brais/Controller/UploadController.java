package com.tfg.brais.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Service.ControllerServices.UploadService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/subjects/{id}/exams/{examId}/uploads")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @GetMapping("/")
    public ResponseEntity<List<ExerciseUpload>> findAllUploads(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return null;
    }

    @PostMapping("/")
    public ResponseEntity<ExerciseUpload> uploadExercise(@PathVariable long id, @PathVariable long examId, ExerciseUpload upload, HttpServletRequest request, MultipartFile newFile){
        return uploadService.uploadExercise(id, examId, upload, newFile, request.getUserPrincipal());
    }

    @GetMapping("/{uploadId}")
    public ResponseEntity<ExerciseUpload> findUploadById(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.findUploadById(id, examId, uploadId, request.getUserPrincipal());
    }

    @DeleteMapping("/{uploadId}")
    public ResponseEntity<ExerciseUpload> deleteUploadById(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return null;
    }

    @GetMapping("/{uploadId}/files")
    public ResponseEntity<Resource> downloadUpload(@PathVariable long id, @PathVariable long examId, @PathVariable  long uploadId, HttpServletRequest request){
        return uploadService.downloadUploadById(id, examId, uploadId, request.getUserPrincipal());
    }

    @GetMapping("/files")
    public ResponseEntity<Resource> downloadAll(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return uploadService.findAllUploadsCompressed(id, examId, request.getUserPrincipal());
    }


}
