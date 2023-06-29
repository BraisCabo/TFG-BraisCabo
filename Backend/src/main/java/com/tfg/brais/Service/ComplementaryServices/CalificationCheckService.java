package com.tfg.brais.Service.ComplementaryServices;

import java.security.Principal;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.DTOS.CalificationDTO;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Service.ControllerServices.UploadService;

@Service
public class CalificationCheckService {

    @Autowired
    private ExerciseUploadRepository exerciseUploadRepository;

    @Autowired
    private ExerciseUploadCheckService exerciseUploadCheckService;

    @Autowired
    private UploadService uploadService;

    public CalificationCheckService(ExerciseUploadRepository exerciseUploadRepository2,
            ExerciseUploadCheckService exerciseUploadCheckService2, UploadService uploadService2) {
        this.exerciseUploadRepository = exerciseUploadRepository2;
        this.exerciseUploadCheckService = exerciseUploadCheckService2;
        this.uploadService = uploadService2;
    }

    public ResponseEntity<ExerciseUpload> changeCalification(long subjectId, long examId, long uploadId,
            CalificationDTO calification, Principal userPrincipal, Predicate<String> function) {
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = exerciseUploadCheckService.checkIfCanSeeUploads(subjectId,
                userPrincipal);
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }
        ResponseEntity<ExerciseUpload> response = uploadService.findUploadById(subjectId, examId, uploadId,
                userPrincipal);
        ExerciseUpload upload = response.getBody();
        if (function.test(upload.getCalification())) {
            return ResponseEntity.status(403).build();
        }
        if (calification.getCalification() == null || calification.getCalification().isEmpty()
                || Double.parseDouble(calification.getCalification()) < 0
                || Double.parseDouble(calification.getCalification()) > 10) {
            return ResponseEntity.status(403).build();
        }

        if (calification.getComment() == null) {
            calification.setComment("");
        }
        upload.setCalification(calification.getCalification());
        upload.setComment(calification.getComment());
        exerciseUploadRepository.save(upload);
        return response;
    }

}
