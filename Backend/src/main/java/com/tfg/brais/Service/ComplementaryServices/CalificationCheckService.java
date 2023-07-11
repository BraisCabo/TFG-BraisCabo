package com.tfg.brais.Service.ComplementaryServices;

import java.security.Principal;
import java.util.ArrayList;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.DTOS.CalificationFileDTO;
import com.tfg.brais.Model.DTOS.CalificationQuestionsDTO;
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

    private ResponseEntity<ExerciseUpload> checkIfCanCalificate(long subjectId, long examId, long uploadId,
            Principal userPrincipal, Predicate<String> function) {
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
        return ResponseEntity.ok(upload);
    }

    private ResponseEntity<ExerciseUpload> modifyCalification(long subjectId, long examId, long uploadId,
            CalificationFileDTO calification, Principal userPrincipal, ExerciseUpload upload) {
        if (calification.getComment() == null) {
            calification.setComment("");
        }
        upload.setCalification(calification.getCalification());
        upload.setComment(calification.getComment());
        exerciseUploadRepository.save(upload);
        return ResponseEntity.ok(upload);
    }

    public ResponseEntity<ExerciseUpload> changeCalificationFiles(long subjectId, long examId, long uploadId,
            CalificationFileDTO calification, Principal userPrincipal, Predicate<String> function) {
        ResponseEntity<ExerciseUpload> checkIfCanCalificate = checkIfCanCalificate(subjectId, examId, uploadId,
                userPrincipal, function);
        if (checkIfCanCalificate.getStatusCode().is4xxClientError()) {
            return checkIfCanCalificate;
        }
        ExerciseUpload upload = checkIfCanCalificate.getBody();
        if (calification.getCalification() == null || calification.getCalification().isEmpty()
                || Double.parseDouble(calification.getCalification()) < 0
                || Double.parseDouble(calification.getCalification()) > 10) {
            return ResponseEntity.status(403).build();
        }

        return modifyCalification(subjectId, examId, uploadId, calification, userPrincipal, upload);
    }

    public ResponseEntity<ExerciseUpload> changeCalificationQuestions(long subjectId, long examId, long uploadId,
            CalificationQuestionsDTO calification, Principal userPrincipal, Predicate<String> function) {
        ResponseEntity<ExerciseUpload> checkIfCanCalificate = checkIfCanCalificate(subjectId, examId, uploadId,
                userPrincipal, function);
        if (checkIfCanCalificate.getStatusCode().is4xxClientError()) {
            return checkIfCanCalificate;
        }

        ExerciseUpload upload = checkIfCanCalificate.getBody();

        upload.setQuestionsCalification(new ArrayList<>());

        Double finalCalification = 0.0;
        Double maxCalification = 0.0;

        for (int i = 0; i < calification.getQuestionsCalification().size(); i++) {
            if (calification.getQuestionsCalification().get(i) == null
                    || calification.getQuestionsCalification().get(i).isEmpty()
                    || Double.parseDouble(calification.getQuestionsCalification().get(i)) < 0
                    || Double.parseDouble(calification.getQuestionsCalification().get(i)) > upload.getExam()
                            .getQuestionsCalifications().get(i)) {
                return ResponseEntity.status(403).build();
            }
            upload.getQuestionsCalification().add(calification.getQuestionsCalification().get(i));
            finalCalification += Double.parseDouble(calification.getQuestionsCalification().get(i));
            maxCalification += upload.getExam().getQuestionsCalifications().get(i);
        }
        CalificationFileDTO calificationFileDTO = new CalificationFileDTO();
        calificationFileDTO.setCalification(maxCalification == 0 ? String.valueOf(0) : String.valueOf(finalCalification * 10 / maxCalification));
        calificationFileDTO.setComment(calification.getComment());
        return modifyCalification(subjectId, examId, uploadId, calificationFileDTO, userPrincipal, upload);
    }

}
