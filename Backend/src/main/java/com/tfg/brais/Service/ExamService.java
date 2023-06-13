package com.tfg.brais.Service;

import java.security.Principal;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.ExamRepository;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private AccountService accountService;

    public ResponseEntity<Exam> findBySubjectIdAndId(long subjectId, long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.notFound().build();
        }
        if (subjectService.checkIfCanSee(subjectId, principal).getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }
        try {
            return ResponseEntity.ok(examRepository.findByIdAndSubjectId(id, subjectId).get());
        } catch (Exception e) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<List<Exam>> findAllExamsBySubjectId(long subjectId, Principal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.notFound().build();
        }

        if (subjectService.checkIfCanSee(subjectId, userPrincipal).getStatusCode().is4xxClientError()) {
            return new ResponseEntity<List<Exam>>(HttpStatusCode.valueOf(403));
        }

        List<Exam> exams;

        User user = accountService.findByMail(userPrincipal.getName()).getBody();

        ResponseEntity<Boolean> responseBool = subjectService.isTeacherOfSubject(subjectId, user.getId());
        if (responseBool.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<List<Exam>>(HttpStatusCode.valueOf(404));
        }

        if (responseBool.getBody()) {
            exams = examRepository.findAllBySubjectId(subjectId);
        } else {
            exams = examRepository.findAllBySubjectIdAndVisible(subjectId);
        }

        if (exams.isEmpty()) {
            return new ResponseEntity<List<Exam>>(HttpStatusCode.valueOf(404));
        } else {
            return ResponseEntity.ok(exams);
        }
    }

    public ResponseEntity<Exam> updateExam(long subjectId, long id, Exam exam, Principal userPrincipal) {
        ResponseEntity<Exam> checkIfCanCreateOrEdit = checkIfCanCreateOrEdit(id, exam, userPrincipal);
        if (checkIfCanCreateOrEdit.getStatusCode().is4xxClientError()) {
            return checkIfCanCreateOrEdit;
        }
        Exam examToUpdate;
        try {
            examToUpdate = examRepository.findByIdAndSubjectId(id, subjectId).get();
        } catch (Exception e) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(404));
        }

        if (!exam.getName().equals(examToUpdate.getName())) {
            try {
                examRepository.findByNameAndSubjectId(exam.getName(), subjectId).get();
                return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
            } catch (Exception e) {
            }
        }

        examToUpdate.update(exam);
        examRepository.save(examToUpdate);
        return ResponseEntity.ok(examToUpdate);
    }

    public ResponseEntity<Exam> createExam(long id, Exam exam, Principal userPrincipal) {
        ResponseEntity<Exam> checkIfCanCreateOrEdit = checkIfCanCreateOrEdit(id, exam, userPrincipal);
        if (checkIfCanCreateOrEdit.getStatusCode().is4xxClientError()) {
            return checkIfCanCreateOrEdit;
        }
        try {
            examRepository.findByNameAndSubjectId(exam.getName(), id).get();
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        } catch (Exception e) {
            exam.setSubject(subjectService.findById(id).getBody());
            examRepository.save(exam);
            return ResponseEntity.ok(exam);
        }
    }

    private ResponseEntity<Exam> checkIfCanCreateOrEdit(long id, Exam exam, Principal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<Subject> subjectResponse = subjectService.findById(id);
        if (subjectResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<User> userResponse = accountService.findByMail(userPrincipal.getName());
        if (userResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }

        User user = userResponse.getBody();

        ResponseEntity<Boolean> responseBool = subjectService.isTeacherOfSubject(id, user.getId());
        if (responseBool.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }

        if (exam.getName() == null || exam.getName().isEmpty()) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }

        if(!exam.getType().equals("UPLOAD") && !exam.getType().equals("QUESTIONS")){
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }

        if (exam.getClosingDate().isBefore(exam.getOpeningDate())) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }

        if (responseBool.getBody() == false) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }
        return ResponseEntity.ok().build();
    }

}
