package com.tfg.brais.Service.ControllerServices;

import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.ExamBasicDTO;
import com.tfg.brais.Model.DTOS.ExamChangesDTO;
import com.tfg.brais.Model.DTOS.ExamStudentDTO;
import com.tfg.brais.Model.DTOS.ExamTeacherDTO;
import com.tfg.brais.Model.DTOS.QuestionsDTO;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Service.ComplementaryServices.ExamCheckService;
import com.tfg.brais.Service.ComplementaryServices.FileService;
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

    @Autowired
    private FileService fileService;

    public ExamService(ExamRepository examRepository, SubjectService subjectService, ExamCheckService examCheckService,
            SubjectCheckService subjectCheckService, UserCheckService userCheckService,
            ExerciseUploadRepository exerciseUploadRepository) {
        this.examRepository = examRepository;
        this.subjectService = subjectService;
        this.examCheckService = examCheckService;
        this.subjectCheckService = subjectCheckService;
        this.userCheckService = userCheckService;
        this.exerciseUploadRepository = exerciseUploadRepository;
    }

    public ResponseEntity<ExamBasicDTO> findBySubjectIdAndId(long subjectId, long examId, Principal principal) {
        ResponseEntity<Page<User>> checkIfCanSeeContent = subjectCheckService.checkIfCanSeeContent(subjectId,
                principal);
        if (checkIfCanSeeContent.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(checkIfCanSeeContent.getStatusCode());
        }
        User user = userCheckService.loadUserNoCkeck(principal).getBody();
        ResponseEntity<Exam> response = ResponseEntity.ok(examRepository.findByIdAndSubjectId(examId, subjectId).get());
        if (!response.getBody().isVisibleExam()
                && !subjectCheckService.isTeacherOfSubject(user.getId(), subjectId)) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }
        if (subjectCheckService.isTeacherOfSubject(user.getId(), subjectId)) {
            ExamTeacherDTO examTeacherDTO = new ExamTeacherDTO(response.getBody());
            examTeacherDTO
                    .setExerciseUploads(exerciseUploadRepository.findAllUploadedByExamId(examId, subjectId).size());
            return ResponseEntity.ok(examTeacherDTO);
        }
        ExamStudentDTO examStudentDTO = new ExamStudentDTO(response.getBody());
        Optional<ExerciseUpload> responseExercise = exerciseUploadRepository
                .findByStudentIdAndExamIdAndExamSubjectId(user.getId(), examId, subjectId);
        if (responseExercise.isPresent() && responseExercise.get().isUploaded()) {
            examStudentDTO.setExerciseUpload(responseExercise.get());
        }
        return ResponseEntity.ok(examStudentDTO);
    }

    public ResponseEntity<List<ExamBasicDTO>> findAllExamsBySubjectId(long subjectId, Principal userPrincipal) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserNoCkeck(userPrincipal);
        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(userCheckResponse.getStatusCode());
        }
        User user = userCheckResponse.getBody();

        List<Exam> exams;

        if (!subjectCheckService.isTeacherOfSubject(user.getId(), subjectId)) {
            exams = examRepository.findAllBySubjectIdAndVisible(subjectId);
        } else {
            exams = examRepository.findAllBySubjectId(subjectId);
        }
        ExamBasicDTO examBasicDTO = new ExamBasicDTO();
        return ResponseEntity.ok(examBasicDTO.convertToDTO(exams));
    }

    public ResponseEntity<ExamTeacherDTO> updateExam(long subjectId, long examId, ExamChangesDTO examChanges,
            MultipartFile examFile, Principal userPrincipal) {
        Exam exam = examChanges.createExam();
        ResponseEntity<Exam> checkIfCanCreateOrEdit = examCheckService.checkIfCanEdit(examId, subjectId, exam,
                userPrincipal);
        if (checkIfCanCreateOrEdit.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(checkIfCanCreateOrEdit.getStatusCode());
        }
        ResponseEntity<Exam> questionsCheck = examCheckService.questionsCheck(exam, examChanges);
        if (questionsCheck.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(questionsCheck.getStatusCode());
        }
        exam = questionsCheck.getBody();
        Exam examToUpdate = checkIfCanCreateOrEdit.getBody();
        examToUpdate.update(exam);
        if (examFile != null) {
            try {
                if (examToUpdate.getExamFile() != null) {
                    fileService.deleteFile(Paths.get(Long.toString(subjectId), examToUpdate.getExamFile()).toString());
                }
                fileService.saveFile(examFile, Paths.get(Long.toString(subjectId)));
                examToUpdate.setExamFile(examFile.getOriginalFilename());
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.valueOf(500));
            }
        } else if (examChanges.isDeletedFile() && examToUpdate.getExamFile() != null) {
            try {
                fileService.deleteFile(Paths.get(Long.toString(subjectId), examToUpdate.getExamFile()).toString());
                examToUpdate.setExamFile(null);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.valueOf(500));
            }
        }
        examRepository.save(examToUpdate);
        return ResponseEntity.ok(new ExamTeacherDTO(examToUpdate));
    }

    public ResponseEntity<ExamTeacherDTO> createExam(long subjectId, ExamChangesDTO examChangesDTO,
            Principal userPrincipal, MultipartFile examFile,
            UriComponentsBuilder uBuilder) {
        Exam exam = examChangesDTO.createExam();
        ResponseEntity<Exam> checkIfCanCreate = examCheckService.checkIfCanCreate(subjectId, exam, userPrincipal);
        if (checkIfCanCreate.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(checkIfCanCreate.getStatusCode());
        }
        ResponseEntity<Exam> questionsCheck = examCheckService.questionsCheck(exam, examChangesDTO);
        if (questionsCheck.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(questionsCheck.getStatusCode());
        }
        exam = questionsCheck.getBody();
        exam.setSubject(subjectService.findSubjectById(subjectId).getBody());
        if (examFile != null) {
            try {
                fileService.saveFile(examFile, Paths.get(Long.toString(subjectId)));
                exam.setExamFile(examFile.getOriginalFilename());
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.valueOf(500));
            }
        }
        Exam save = examRepository.save(exam);
        for (User user : exam.getSubject().getStudents()) {
            ExerciseUpload exerciseUpload = new ExerciseUpload();
            exerciseUpload.setExam(save);
            exerciseUpload.setStudent(user);
            exerciseUploadRepository.save(exerciseUpload);
        }
        return ResponseEntity.created(uBuilder.buildAndExpand(exam.getId()).toUri()).body(new ExamTeacherDTO(exam));
    }

    public ResponseEntity<ExamTeacherDTO> changeVisibility(long id, long examId, Boolean newVisibility,
            Principal userPrincipal) {
        ResponseEntity<ExamBasicDTO> findBySubjectIdAndId = findBySubjectIdAndId(id, examId, userPrincipal);
        if (findBySubjectIdAndId.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(findBySubjectIdAndId.getStatusCode());
        }
        ExamTeacherDTO examTeacher = ((ExamTeacherDTO) findBySubjectIdAndId.getBody());
        ResponseEntity<Exam> checkIfCanCreateOrEdit = examCheckService.checkIfCanEdit(examId, id,
                examTeacher.createExam(),
                userPrincipal);
        if (checkIfCanCreateOrEdit.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<ExamTeacherDTO>(checkIfCanCreateOrEdit.getStatusCode());
        }
        Exam exam = checkIfCanCreateOrEdit.getBody();
        exam.setVisibleExam(newVisibility);
        examRepository.save(exam);
        return ResponseEntity.ok(new ExamTeacherDTO(exam));
    }

    public ResponseEntity<QuestionsDTO> getExamQuestions(long subjectId, long examId, Principal userPrincipal) {
        ResponseEntity<Exam> checkIfCanSee = examCheckService.checkIfCanSee(examId, subjectId, userPrincipal);
        if (checkIfCanSee.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(checkIfCanSee.getStatusCode());
        }
        return ResponseEntity.ok(new QuestionsDTO(checkIfCanSee.getBody()));
    }

    public ResponseEntity<Resource> getExamFiles(long subjectId, long examId, Principal userPrincipal) {
        ResponseEntity<Exam> checkIfCanSee = examCheckService.checkIfCanSee(examId, subjectId, userPrincipal);
        if (checkIfCanSee.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(checkIfCanSee.getStatusCode());
        }
        return fileService.downloadFile(Paths.get(Long.toString(subjectId), checkIfCanSee.getBody().getExamFile()));
    }
}
