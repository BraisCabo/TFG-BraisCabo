package com.tfg.brais.Service.ControllerServices;

import java.security.Principal;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Service.ComplementaryServices.ExamCheckService;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamCheckService examCheckService;

    @Autowired
    private SubjectCheckService subjectCheckService;

    @Autowired
    private UserCheckService userCheckService;

    public ExamService(ExamRepository examRepository, SubjectService subjectService, ExamCheckService examCheckService,
            SubjectCheckService subjectCheckService, UserCheckService userCheckService) {
        this.examRepository = examRepository;
        this.subjectService = subjectService;
        this.examCheckService = examCheckService;
        this.subjectCheckService = subjectCheckService;
        this.userCheckService = userCheckService;
    }

    public ResponseEntity<Exam> findBySubjectIdAndId(long subjectId, long id, Principal principal) {
        ResponseEntity<Page<User>> checkIfCanSeeContent = subjectCheckService.checkIfCanSeeContent(subjectId,
                principal);
        if (checkIfCanSeeContent.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Exam>(checkIfCanSeeContent.getStatusCode());
        }
        try {
            return ResponseEntity.ok(examRepository.findByIdAndSubjectId(id, subjectId).get());
        } catch (Exception e) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<List<Exam>> findAllExamsBySubjectId(long subjectId, Principal userPrincipal) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserPrincipal(subjectId, userPrincipal);
        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<List<Exam>>(userCheckResponse.getStatusCode());
        }
        User user = userCheckResponse.getBody();

        List<Exam> exams;

        if (!subjectCheckService.isTeacherOfSubject(subjectId, user.getId())) {
            exams = examRepository.findAllBySubjectIdAndVisible(subjectId);
        } else {
            exams = examRepository.findAllBySubjectId(subjectId);
        }

        if (exams.isEmpty()) {
            return new ResponseEntity<List<Exam>>(HttpStatusCode.valueOf(404));
        } else {
            return ResponseEntity.ok(exams);
        }
    }

    public ResponseEntity<Exam> updateExam(long subjectId, long id, Exam exam, Principal userPrincipal) {
        ResponseEntity<Exam> checkIfCanCreateOrEdit = examCheckService.checkIfCanEdit(id, subjectId, exam, userPrincipal);
        if (checkIfCanCreateOrEdit.getStatusCode().is4xxClientError()) {
            return checkIfCanCreateOrEdit;
        }

        Exam examToUpdate = checkIfCanCreateOrEdit.getBody();
        examToUpdate.update(exam);
        examRepository.save(examToUpdate);
        return ResponseEntity.ok(examToUpdate);
    }

    public ResponseEntity<Exam> createExam(long id, Exam exam, Principal userPrincipal) {
        ResponseEntity<Exam> checkIfCanCreate = examCheckService.checkIfCanCreate(id, exam, userPrincipal);
        if (checkIfCanCreate.getStatusCode().is4xxClientError()) {
            return checkIfCanCreate;
        }
        exam.setSubject(subjectService.findById(id).getBody());
        examRepository.save(exam);
        return ResponseEntity.ok(exam);
    }
}
