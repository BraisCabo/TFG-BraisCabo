package com.tfg.brais.Service.ComplementaryServices;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.ExamChangesDTO;
import com.tfg.brais.Repository.ExamRepository;

@Service
public class ExamCheckService {

    @Autowired
    private SubjectCheckService subjectCheckService;

    @Autowired
    private UserCheckService userCheckService;

    @Autowired
    private ExamRepository examRepository;

    public ExamCheckService(SubjectCheckService subjectCheckService, UserCheckService userCheckService,
            ExamRepository examRepository) {
        this.subjectCheckService = subjectCheckService;
        this.userCheckService = userCheckService;
        this.examRepository = examRepository;
    }

    private ResponseEntity<Exam> checkIfCanCreateOrEdit(long id, Exam exam, Principal userPrincipal) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserNoCkeck(userPrincipal);
        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Exam>(userCheckResponse.getStatusCode());
        }
        User user = userCheckResponse.getBody();

        ResponseEntity<Subject> subjectResponse = subjectCheckService.findById(id);

        if (subjectResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity.notFound().build();
        }

        if (!subjectCheckService.isTeacherOfSubject(user.getId(), id)) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }

        if (exam.getName() == null || exam.getName().isEmpty()) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }

        if (!exam.getType().equals("UPLOAD") && !exam.getType().equals("QUESTIONS")) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }

        if (exam.getType().equals("QUESTIONS") && exam.getQuestions().isEmpty()) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }

        if (exam.getClosingDate().before(exam.getOpeningDate())) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }

        if (exam.getMaxTime() < 0) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Exam> checkIfCanCreate(long id, Exam exam, Principal userPrincipal) {
        ResponseEntity<Exam> checkIfCanCreateOrEdit = checkIfCanCreateOrEdit(id, exam, userPrincipal);
        if (checkIfCanCreateOrEdit.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Exam>(checkIfCanCreateOrEdit.getStatusCode());
        }
        try {
            examRepository.findByNameAndSubjectId(exam.getName(), id).get();
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        } catch (Exception e) {
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<Exam> checkIfCanEdit(long id, long subjectId, Exam exam, Principal userPrincipal) {
        ResponseEntity<Exam> checkIfCanCreateOrEdit = checkIfCanCreateOrEdit(subjectId, exam, userPrincipal);
        if (checkIfCanCreateOrEdit.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Exam>(checkIfCanCreateOrEdit.getStatusCode());
        }
        Exam examToUpdate;
        try {
            examToUpdate = examRepository.findByIdAndSubjectId(id, subjectId).get();
        } catch (Exception e) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(404));
        }

        if (!exam.getName().equals(examToUpdate.getName())) {
            if (examRepository.findByNameAndSubjectId(exam.getName(), subjectId).isPresent()) {
                return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
            }
        }
        return ResponseEntity.ok(examToUpdate);
    }

    public ResponseEntity<Exam> checkIfCanSee(long id, long subjectId, Principal userPrincipal) {
        ResponseEntity<Exam> basicCheck = basicCheck(id, subjectId, userPrincipal);
        if (basicCheck.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Exam>(basicCheck.getStatusCode());
        }
        Exam examToSee;
        try {
            examToSee = examRepository.findByIdAndSubjectId(id, subjectId).get();
        } catch (Exception e) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(404));
        }

        if (examToSee.getOpeningDate().after(new Date())) {
            return new ResponseEntity<Exam>(HttpStatusCode.valueOf(403));
        }

        return ResponseEntity.ok(examToSee);
    }

    private ResponseEntity<Exam> basicCheck(long id, long subjectId, Principal userPrincipal) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserNoCkeck(userPrincipal);
        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Exam>(userCheckResponse.getStatusCode());
        }
        User user = userCheckResponse.getBody();

        ResponseEntity<Subject> subjectResponse = subjectCheckService.findById(subjectId);

        if (subjectResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity.notFound().build();
        }

        if (subjectCheckService.isTeacherOfSubject(user.getId(), subjectId)
                || subjectCheckService.isStudentOfSubject(user.getId(), subjectId)) {
            return ResponseEntity.ok().build();
        }
        return new ResponseEntity<>(HttpStatusCode.valueOf(403));

    }

    public ResponseEntity<Exam> questionsCheck(Exam exam, ExamChangesDTO examChangesDTO) {
        exam.setQuestionsCalifications(new ArrayList<>());
        if (exam.getType() == "UPLOAD") {
            exam.setQuestions(new ArrayList<>());
        } else {
            if (examChangesDTO.getQuestions().size() != examChangesDTO.getQuestionsCalifications().size()) {
                return new ResponseEntity<>(HttpStatus.valueOf(403));
            }
            for (String calification : examChangesDTO.getQuestionsCalifications()) {
                if (Double.valueOf(calification) < 0) {
                    return new ResponseEntity<>(HttpStatus.valueOf(403));
                }
                exam.getQuestionsCalifications().add(Double.valueOf(calification));
            }
        }
        return ResponseEntity.ok(exam);
    }
}
