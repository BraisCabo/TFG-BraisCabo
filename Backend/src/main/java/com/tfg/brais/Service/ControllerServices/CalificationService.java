package com.tfg.brais.Service.ControllerServices;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.CalificationDTO;
import com.tfg.brais.Model.DTOS.StudentCalificationDTO;
import com.tfg.brais.Model.DTOS.TeachersCalificationsDTO;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Service.ComplementaryServices.CalificationCheckService;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;

@Service
public class CalificationService {

    @Autowired
    private UserCheckService userCheckService;

    @Autowired
    private ExerciseUploadRepository exerciseUploadRepository;

    @Autowired
    private SubjectCheckService subjectCheckService;

    @Autowired
    private CalificationCheckService calificationCheckService;

    public CalificationService(UserCheckService userCheckService2, ExerciseUploadRepository exerciseUploadRepository2,
            SubjectCheckService subjectCheckService2, CalificationCheckService calificationCheckService2) {
        this.userCheckService = userCheckService2;
        this.exerciseUploadRepository = exerciseUploadRepository2;
        this.subjectCheckService = subjectCheckService2;
        this.calificationCheckService = calificationCheckService2;

    }

    public ResponseEntity<ExerciseUpload> uploadCalification(long subjectId, long examId, long uploadId,
            CalificationDTO calification, Principal principal) {
        return calificationCheckService.changeCalification(subjectId, examId, uploadId, calification, principal,
                (s1) -> !s1.equals(""));
    }

    public ResponseEntity<ExerciseUpload> editCalification(long subjectId, long examId, long uploadId,
            CalificationDTO calification, Principal principal) {
        return calificationCheckService.changeCalification(subjectId, examId, uploadId, calification, principal,
                (s1) -> s1 == null);
    }

    public ResponseEntity<StudentCalificationDTO> searchCalifications(long subjectId, long studentId,
            Principal userPrincipal) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserPrincipal(studentId, userPrincipal);

        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(userCheckResponse.getStatusCode());
        }

        if (!subjectCheckService.isStudentOfSubject(studentId, subjectId)) {
            return ResponseEntity.status(403).build();
        }

        StudentCalificationDTO studentCalificationDTO = new StudentCalificationDTO(
                exerciseUploadRepository.findBySubjectIdStudentIdAndVisibleCalification(subjectId, studentId));

        return ResponseEntity.ok(studentCalificationDTO);
    }

    public ResponseEntity<List<TeachersCalificationsDTO>> searchCalificationsTeachers(long subjectId, Principal userPrincipal) {
        ResponseEntity<User> userCheckResponse = userCheckService.loadUserNoCkeck(userPrincipal);
        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(userCheckResponse.getStatusCode());
        }

        User user = userCheckResponse.getBody();

        if (!subjectCheckService.isTeacherOfSubject(user.getId(), subjectId)) {
            return ResponseEntity.status(403).build();
        }

        ResponseEntity<Subject> stubjectResponse = subjectCheckService.findById(subjectId);
        if (stubjectResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(stubjectResponse.getStatusCode());
        }

        Subject subject = stubjectResponse.getBody();
        List<TeachersCalificationsDTO> teachersCalificationsDTO = new ArrayList<>();

        for (User student : subject.getStudents()) {
            List<ExerciseUpload> uploads = exerciseUploadRepository.findSubjectCalifications(subjectId, student.getId());
            TeachersCalificationsDTO teachersCalifications = new TeachersCalificationsDTO(uploads, student);
            teachersCalificationsDTO.add(teachersCalifications);
        }

        return ResponseEntity.ok(teachersCalificationsDTO);
    }


}
