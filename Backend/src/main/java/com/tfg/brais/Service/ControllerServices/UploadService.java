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
import com.tfg.brais.Service.ComplementaryServices.CSVService;
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

    @Autowired
    private CSVService csvService;

    public UploadService(ExerciseUploadRepository exerciseUploadRepository2,
            ExerciseUploadCheckService exerciseUploadCheckService2, FileService fileService2,
            ExamRepository examRepository2) {
        this.exerciseUploadRepository = exerciseUploadRepository2;
        this.exerciseUploadCheckService = exerciseUploadCheckService2;
        this.fileService = fileService2;
        this.examRepository = examRepository2;
    }

    public ResponseEntity<Resource> downloadUploadById(long subjectId, long examId, long uploadId,
            Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanDownload = exerciseUploadCheckService.checkIfCanDownload(subjectId,
                examId,
                uploadId, principal);
        if (checkIfCanDownload.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanDownload.getStatusCode()).build();
        }
        ExerciseUpload upload = checkIfCanDownload.getBody();
        Path filePath = Paths.get(upload.getExam().getSubject().getId().toString(), upload.getExam().getId().toString(),
                upload.getStudent().getName() + upload.getStudent().getLastName(), upload.getFileName());
        return fileService.downloadFile(filePath);
    }

    public ResponseEntity<ExerciseUpload> uploadExercise(long subjectId, long examId, MultipartFile file,
            Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanUpload = exerciseUploadCheckService.checkIfCanUpload(subjectId, examId,
                principal);
        if (checkIfCanUpload.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanUpload.getStatusCode()).build();
        }
        ExerciseUpload upload = checkIfCanUpload.getBody();
        User user = upload.getStudent();
        Exam exam = upload.getExam();
        if (!exam.getType().equals("UPLOAD")) {
            return ResponseEntity.status(403).build();
        }
        try {
            Path path = Paths.get(exam.getSubject().getId().toString(), exam.getId().toString(),
                    user.getName() + user.getLastName());
            if (upload.isUploaded()) {
                fileService.deleteFile(Paths.get(path.toString(), upload.getFileName()).toString());
            }
            fileService.saveFile(file, path);
            upload.setFileName(file.getOriginalFilename());
            upload.setUploaded(true);
            exerciseUploadRepository.save(upload);
            return ResponseEntity.ok(upload);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public ResponseEntity<ExerciseUpload> findUploadById(long subjectId, long examId, long uploadId,
            Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = exerciseUploadCheckService.checkIfCanSeeUploads(subjectId,
                principal);
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }
        try {
            return ResponseEntity
                    .ok(exerciseUploadRepository.findByIdAndExamIdAndExamSubjectId(uploadId, examId, subjectId).get());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Resource> findAllUploadsCompressed(long subjectId, long examId, Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = exerciseUploadCheckService.checkIfCanSeeUploads(subjectId,
                principal);
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }
        try {
            Exam exam = examRepository.findByIdAndSubjectId(examId, subjectId).get();
            Path path = Paths.get(exam.getSubject().getId().toString(), exam.getId().toString());
            ResponseEntity<Resource> compressDirectory = fileService.compressDirectory(path.toString(),
                    exam.getName() + ".zip",
                    exam.getSubject().getId().toString());
            return compressDirectory;
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<List<ExerciseUpload>> findAllUploads(long subjectId, long examId, Principal principal,
            String name) {
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = exerciseUploadCheckService.checkIfCanSeeUploads(subjectId,
                principal);
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }
        try {
            return ResponseEntity.ok(exerciseUploadRepository.findAllByExamIdAndExamSubjectId(examId, subjectId, name));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<ExerciseUpload> deleteUploadById(long subjectId, long examId, long uploaId,
            Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = exerciseUploadCheckService.checkIfCanSeeUploads(subjectId,
                principal);
        ResponseEntity<ExerciseUpload> response = findUploadById(subjectId, examId, uploaId, principal);
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }
        try {
            ExerciseUpload upload = response.getBody();
            Path path = Paths.get(Long.toString(subjectId), Long.toString(examId),
                    upload.getStudent().getName() + upload.getStudent().getLastName());
            fileService.deleteDirectory(path.toString());
            if (!upload.isUploaded()) {
                return ResponseEntity.status(403).build();
            }
            upload.deleteUpload();
            exerciseUploadRepository.save(upload);
            return response;
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<ExerciseUpload> uploadExercise(long subjectId, long examId, List<String> answers,
            Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanUpload = exerciseUploadCheckService.checkIfCanUpload(subjectId, examId,
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
            upload.setFileName(user.getName() + user.getLastName() + ".csv");
            Path path = Paths.get(exam.getSubject().getId().toString(), exam.getId().toString(),
                    user.getName() + user.getLastName());
            csvService.examToCSV(user.getName() + " " + user.getLastName(), user.getEmail(), path.toString(),
                    upload.getFileName(), exam.getQuestions(), upload.getAnswers());
            upload.setUploaded(true);
            exerciseUploadRepository.save(upload);
            return ResponseEntity.ok(upload);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public ResponseEntity<AnswersDTO> findUploadQuestionsAndAnswersById(long subjectId, long examId, long uploadId,
            Principal principal) {
        ResponseEntity<ExerciseUpload> checkIfCanDownload = exerciseUploadCheckService.checkIfCanDownload(subjectId,
                examId,
                uploadId, principal);
        if (checkIfCanDownload.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanDownload.getStatusCode()).build();
        }
        ExerciseUpload upload = checkIfCanDownload.getBody();
        AnswersDTO answersDTO = new AnswersDTO();
        answersDTO.setAnswers(upload.getAnswers());
        answersDTO.setQuestions(upload.getExam().getQuestions());
        answersDTO.setQuestionCalifications(upload.getExam().getQuestionsCalifications());
        if (exerciseUploadCheckService.checkIfCanSeeCalifications(subjectId, principal, upload)){
            answersDTO.setCalifications(upload.getQuestionsCalification());
        }
        return ResponseEntity.ok(answersDTO);
    }

}
