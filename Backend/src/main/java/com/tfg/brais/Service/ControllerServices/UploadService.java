package com.tfg.brais.Service.ControllerServices;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.AnswersDTO;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Service.ComplementaryServices.ExerciseUploadCheckService;
import com.tfg.brais.Service.ComplementaryServices.FileService;

@Service
public class UploadService {

    @Autowired
    private ExerciseUploadRepository exerciseUploadRepository;

    @Autowired
    private ExerciseUploadCheckService exerciseUploadCheckService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ExamRepository examRepository;

    public ResponseEntity<Resource> downloadUploadById(long id, long examId, long uploadId, Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanDownload = exerciseUploadCheckService.checkIfCanDownload(id, examId,
                uploadId, principal);
        if (checkIfCanDownload.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanDownload.getStatusCode()).build();
        }
        ExerciseUpload upload = checkIfCanDownload.getBody();
        Path filePath = Paths.get(upload.getExam().getSubject().getId().toString(), upload.getExam().getId().toString(),
                upload.getStudent().getName() + upload.getStudent().getLastName(), upload.getFileName());
        return fileService.downloadFile(filePath);
    }

    public ResponseEntity<ExerciseUpload> uploadExercise(long id, long examId, MultipartFile file,
            Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanUpload = exerciseUploadCheckService.checkIfCanUpload(id, examId,
                principal);
        if (checkIfCanUpload.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanUpload.getStatusCode()).build();
        }
        ExerciseUpload upload = checkIfCanUpload.getBody();
        User user = upload.getStudent();
        Exam exam = upload.getExam();
        if (exam.getType() != "UPLOADS") {
            return ResponseEntity.status(403).build();
        }
        try {
            Path path = Paths.get(exam.getSubject().getId().toString(), exam.getId().toString(),
                    user.getName() + user.getLastName());
            fileService.saveFile(file, path);
            upload.setFileName(file.getOriginalFilename());
            exerciseUploadRepository.save(upload);
            return ResponseEntity.ok(upload);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public ResponseEntity<ExerciseUpload> findUploadById(long id, long examId, long uploadId, Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = exerciseUploadCheckService.checkIfCanSeeUploads(id,
                principal);
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }
        try {
            return ResponseEntity
                    .ok(exerciseUploadRepository.findByIdAndExamIdAndExamSubjectId(uploadId, examId, id).get());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Resource> findAllUploadsCompressed(long id, long examId, Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = exerciseUploadCheckService.checkIfCanSeeUploads(id,
                principal);
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }
        try {
            Exam exam = examRepository.findByIdAndSubjectId(examId, id).get();
            Path path = Paths.get(exam.getSubject().getId().toString(), exam.getId().toString());
            return fileService.compressDirectory(path.toString(), exam.getName() + ".zip",
                    exam.getSubject().getId().toString());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<List<ExerciseUpload>> findAllUploads(long id, long examId, Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = exerciseUploadCheckService.checkIfCanSeeUploads(id,
                principal);
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }
        try {
            return ResponseEntity.ok(exerciseUploadRepository.findAllByExamIdAndExamSubjectId(examId, id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<ExerciseUpload> deleteUploadById(long id, long examId, long uploaId, Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = exerciseUploadCheckService.checkIfCanSeeUploads(id,
                principal);
        ResponseEntity<ExerciseUpload> response = findUploadById(id, examId, uploaId, principal);
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }
        try {
            exerciseUploadRepository.deleteById(uploaId);
            return response;
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<ExerciseUpload> uploadExercise(long id, long examId, List<String> answers,
            Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanUpload = exerciseUploadCheckService.checkIfCanUpload(id, examId,
                principal);
        if (checkIfCanUpload.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanUpload.getStatusCode()).build();
        }
        ExerciseUpload upload = checkIfCanUpload.getBody();
        User user = upload.getStudent();
        Exam exam = upload.getExam();
        if (!exam.getType().equals("QUESTIONS") || exam.getQuestions().size() != answers.size()) {
            return ResponseEntity.status(403).build();
        }
        upload.setAnswers(answers);
        try {
            upload.setFileName(user.getName() + user.getLastName() + ".txt");
            Path path = Paths.get(exam.getSubject().getId().toString(), exam.getId().toString(),
                    user.getName() + user.getLastName());
            fileService.createTextFile(path.toString(), upload.getFileName(), exam.getQuestions(), upload.getAnswers());
            exerciseUploadRepository.save(upload);
            return ResponseEntity.ok(upload);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public ResponseEntity<AnswersDTO> findUploadQuestionsAndAnswersById(long id, long examId, long uploadId,
            Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanDownload = exerciseUploadCheckService.checkIfCanDownload(id, examId,
                uploadId, principal);
        if (checkIfCanDownload.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanDownload.getStatusCode()).build();
        }
        ExerciseUpload upload = checkIfCanDownload.getBody();
        AnswersDTO answersDTO = new AnswersDTO();
        answersDTO.setAnswers(upload.getAnswers());
        answersDTO.setQuestions(upload.getExam().getQuestions());
        return ResponseEntity.ok(answersDTO);
    }
}
