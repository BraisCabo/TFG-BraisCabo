package com.tfg.brais.Service.ControllerServices;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.ExamBasicDTO;
import com.tfg.brais.Model.DTOS.ExamStudentDTO;
import com.tfg.brais.Model.DTOS.ExamTeacherDTO;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Repository.ExerciseUploadRepository;
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

    @Autowired
    private ExerciseUploadRepository exerciseUploadRepository;

    public ExamService(ExamRepository examRepository, SubjectService subjectService, ExamCheckService examCheckService,
            SubjectCheckService subjectCheckService, UserCheckService userCheckService) {
        this.examRepository = examRepository;
        this.subjectService = subjectService;
        this.examCheckService = examCheckService;
        this.subjectCheckService = subjectCheckService;
        this.userCheckService = userCheckService;
    }

    public ResponseEntity<ExamBasicDTO> findBySubjectIdAndId(long subjectId, long id, Principal principal) {
        ResponseEntity<Page<User>> checkIfCanSeeContent = subjectCheckService.checkIfCanSeeContent(subjectId,
                principal);
        if (checkIfCanSeeContent.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(checkIfCanSeeContent.getStatusCode());
        }
        try {
            User user = userCheckService.loadUserNoCkeck(principal).getBody();
            ResponseEntity<Exam> response = ResponseEntity.ok(examRepository.findByIdAndSubjectId(id, subjectId).get());
            if (!response.getBody().isVisibleExam()
                    && !subjectCheckService.isTeacherOfSubject(subjectId, user.getId())) {
                return new ResponseEntity<>(HttpStatusCode.valueOf(403));
            }
            if (subjectCheckService.isTeacherOfSubject(subjectId, user.getId())) {
                return ResponseEntity.ok(new ExamTeacherDTO(response.getBody()));
            } else {
                ExamStudentDTO examStudentDTO = new ExamStudentDTO(response.getBody());
                Optional<ExerciseUpload> responseExercise = exerciseUploadRepository
                        .findByStudentIdAndExamIdAndExamSubjectId(user.getId(), subjectId, id);
                if (responseExercise.isPresent()) {
                    examStudentDTO.setExerciseUpload(responseExercise.get());
                }
                return ResponseEntity.ok(examStudentDTO);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<List<ExamBasicDTO>> findAllExamsBySubjectId(long subjectId, Principal userPrincipal) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserNoCkeck(userPrincipal);
        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(userCheckResponse.getStatusCode());
        }
        User user = userCheckResponse.getBody();

        List<Exam> exams;

        if (!subjectCheckService.isTeacherOfSubject(subjectId, user.getId())) {
            exams = examRepository.findAllBySubjectIdAndVisible(subjectId);
        } else {
            exams = examRepository.findAllBySubjectId(subjectId);
        }
        ExamBasicDTO examBasicDTO = new ExamBasicDTO();
        return ResponseEntity.ok(examBasicDTO.convertToDTO(exams));
    }

    public ResponseEntity<ExamTeacherDTO> updateExam(long subjectId, long id, ExamTeacherDTO examTeacher,
            Principal userPrincipal) {
        Exam exam = examTeacher.createExam();
        ResponseEntity<Exam> checkIfCanCreateOrEdit = examCheckService.checkIfCanEdit(id, subjectId, exam,
                userPrincipal);
        if (checkIfCanCreateOrEdit.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(checkIfCanCreateOrEdit.getStatusCode());
        }
        if (exam.getType() == "UPLOAD") {
            exam.setQuestions(new ArrayList<>());
        }
        Exam examToUpdate = checkIfCanCreateOrEdit.getBody();
        examToUpdate.update(exam);
        examRepository.save(examToUpdate);
        return ResponseEntity.ok(new ExamTeacherDTO(examToUpdate));
    }

    public ResponseEntity<ExamTeacherDTO> createExam(long id, ExamTeacherDTO examTeacher, Principal userPrincipal,
            UriComponentsBuilder uBuilder) {
        Exam exam = examTeacher.createExam();
        ResponseEntity<Exam> checkIfCanCreate = examCheckService.checkIfCanCreate(id, exam, userPrincipal);
        if (checkIfCanCreate.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(checkIfCanCreate.getStatusCode());
        }
        if (exam.getType() == "UPLOAD") {
            exam.setQuestions(new ArrayList<>());
        }
        exam.setSubject(subjectService.findSubjectById(id).getBody());
        examRepository.save(exam);
        return ResponseEntity.created(uBuilder.buildAndExpand(exam.getId()).toUri()).body(new ExamTeacherDTO(exam));
    }

    public ResponseEntity<ExamTeacherDTO> changeVisibility(long id, long examId, Boolean newVisibility,
            Principal userPrincipal) {
        ResponseEntity<ExamBasicDTO> findBySubjectIdAndId = findBySubjectIdAndId(id, examId, userPrincipal);
        if (findBySubjectIdAndId.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(findBySubjectIdAndId.getStatusCode());
        }
        ResponseEntity<Exam> checkIfCanCreateOrEdit = examCheckService.checkIfCanEdit(examId, id, findBySubjectIdAndId.getBody().creatExam(),
                userPrincipal);
        if (checkIfCanCreateOrEdit.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(checkIfCanCreateOrEdit.getStatusCode());
        }
        Exam exam = checkIfCanCreateOrEdit.getBody();
        exam.setVisibleExam(newVisibility);
        examRepository.save(exam);
        return ResponseEntity.ok(new ExamTeacherDTO(exam));
    }

    public ResponseEntity<List<String>> getExamQuestions(long id, long examId, Principal userPrincipal) {
        ResponseEntity<Exam> checkIfCanSee = examCheckService.checkIfCanSee(examId, id, userPrincipal);
        if (checkIfCanSee.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<List<String>>(checkIfCanSee.getStatusCode());
        }
        return ResponseEntity.ok(checkIfCanSee.getBody().getQuestions());
    }
}
