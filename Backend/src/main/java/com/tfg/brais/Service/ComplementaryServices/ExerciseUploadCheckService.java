package com.tfg.brais.Service.ComplementaryServices;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Repository.ExerciseUploadRepository;

@Service
public class ExerciseUploadCheckService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SubjectCheckService subjectCheckService;

    @Autowired
    private ExerciseUploadRepository exerciseUploadRepository;

    @Autowired
    private UserCheckService userCheckService;

    private ResponseEntity<ExerciseUpload> checkIfCanUploadUser(long id, long examId, long userId) {
        Optional<Exam> examOp = examRepository.findByIdAndSubjectId(examId, id);
        if (!examOp.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Exam exam = examOp.get();
        if (exam.getOpeningDate().after(new Date())){
            return ResponseEntity.status(403).build();
        }
        if (exam.getClosingDate().before(new Date()) && !exam.isCanUploadLate()) {
            return ResponseEntity.status(403).build();
        }
        if (!subjectCheckService.isStudentOfSubject(userId, id)) {
            return ResponseEntity.status(403).build();
        }

        try {
            ExerciseUpload exerciseUpload = exerciseUploadRepository.findByStudentIdAndExamIdAndExamSubjectId(userId, examId, id).get();
            if(exerciseUpload.isUploaded() && exerciseUpload.getExam().getType().equals("QUESTIONS")){
                return ResponseEntity.status(403).build();
            }else if (exerciseUpload.isUploaded() && !exerciseUpload.getExam().isCanRepeat()){
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public ResponseEntity<ExerciseUpload> checkIfCanDownload(long id, long examId, long uploadId, Principal principal){
        ResponseEntity<User> responseUser = userCheckService.loadUserNoCkeck(principal);
        if (responseUser.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(responseUser.getStatusCode()).build();
        }

        User user = responseUser.getBody();

        Optional<ExerciseUpload> findById = exerciseUploadRepository.findById(uploadId);
        if (findById.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ExerciseUpload upload = findById.get();
        if (upload.getStudent().getId() != user.getId() && !subjectCheckService.isTeacherOfSubject(user.getId(), id)) {
            return ResponseEntity.status(403).build();
        }

        try{
            ExerciseUpload exerciseUpload = exerciseUploadRepository.findByIdAndExamIdAndExamSubjectId(uploadId, examId, id).get();
            return ResponseEntity.ok(exerciseUpload);
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    public ResponseEntity<ExerciseUpload> checkIfCanSeeUploads(long id, Principal principal) {
        ResponseEntity<User> responseUser = userCheckService.loadUserNoCkeck(principal);
        if (responseUser.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(responseUser.getStatusCode()).build();
        }

        User user = responseUser.getBody();

        if (!subjectCheckService.isTeacherOfSubject(user.getId(), id)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<ExerciseUpload> userUpload(long id, long examId, long userId) {
        Optional<Exam> exam = examRepository.findByIdAndSubjectId(examId, id);
        if (!exam.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        if (!subjectCheckService.isStudentOfSubject(userId, id)) {
            return ResponseEntity.status(403).build();
        }

        try {
            exerciseUploadRepository.findByStudentIdAndExamIdAndExamSubjectId(userId, examId, id).get();
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<ExerciseUpload> checkIfCanUpload(long id, long examId, Principal principal){;
        ResponseEntity<User> responseUser = userCheckService.loadUserNoCkeck(principal);
        if (responseUser.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(responseUser.getStatusCode()).build();
        }
        User user = responseUser.getBody();
        ExerciseUpload upload;
        try {
            upload = exerciseUploadRepository.findByStudentIdAndExamIdAndExamSubjectId(user.getId(), examId, id).get();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
        ResponseEntity<ExerciseUpload> checkIfCanUpload = this.checkIfCanUploadUser(id, examId,
                user.getId());
        if (checkIfCanUpload.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanUpload.getStatusCode()).build();
        }
        Exam exam = examRepository.findById(examId).get();
        upload.setExam(exam);
        upload.setStudent(user);
        return ResponseEntity.ok(upload);
    }

    public boolean checkIfCanSeeCalifications(long subjectId, Principal principal, ExerciseUpload upload){
        ResponseEntity<User> responseUser = userCheckService.loadUserNoCkeck(principal);
        if (responseUser.getStatusCode().is4xxClientError()) {
            return false;
        }
        User user = responseUser.getBody();
        if (upload.getExam().isCalificationVisible()){
            return true;
        }else if (subjectCheckService.isTeacherOfSubject(user.getId(), subjectId)){
            return true;
        }
        return false;
    }
}
