package com.tfg.brais.Service.ControllerServices;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.ZipUtil;
import org.springframework.core.io.Resource;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;

@Service
public class UploadService {

    @Autowired
    private ExerciseUploadRepository exerciseUploadRepository;

    @Autowired
    private SubjectCheckService subjectCheckService;

    @Autowired
    private UserCheckService userCheckService;

    @Autowired
    private ExamRepository examRepository;

    @Value("${file-dir}")
    private String fileDir;

    public ResponseEntity<Resource> downloadUploadById(long id, long examId, long uploadId, Principal principal) {
        ResponseEntity<ExerciseUpload> findUploadById = findUploadById(id, examId, uploadId, principal);
        if (findUploadById.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(findUploadById.getStatusCode()).build();
        }
        ExerciseUpload upload = findUploadById.getBody();
        Path filePath = Paths.get(upload.getExam().getSubject().getId().toString(), upload.getExam().getId().toString(),
                upload.getStudent().getId().toString(), upload.getFileName());
        return downloadFile(filePath);
    }

    public ResponseEntity<ExerciseUpload> uploadExercise(long id, long examId, ExerciseUpload upload,
            MultipartFile file, Principal principal) {
        ResponseEntity<User> responseUser = userCheckService.loadUserNoCkeck(principal);
        if (responseUser.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(responseUser.getStatusCode()).build();
        }
        User user = responseUser.getBody();
        ResponseEntity<ExerciseUpload> checkIfCanUpload = checkIfCanUpload(id, examId, user.getId());
        if (checkIfCanUpload.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanUpload.getStatusCode()).build();
        }
        Exam exam = examRepository.findById(examId).get();
        upload.setExam(exam);
        upload.setStudent(user);
        try {
            Path path = Paths.get(exam.getSubject().getId().toString(), exam.getId().toString(),
                    user.getName() + user.getLastName());
            updateExamFile(upload, file, path);
            upload.setFileName(file.getOriginalFilename());
            exerciseUploadRepository.save(upload);
            return ResponseEntity.ok(upload);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public ResponseEntity<ExerciseUpload> findUploadById(long id, long examId, long uploadId, Principal principal) {
        ResponseEntity<User> responseUser = userCheckService.loadUserNoCkeck(principal);
        if (responseUser.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(responseUser.getStatusCode()).build();
        }

        User user = responseUser.getBody();

        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = checkIfCanSeeUploads(uploadId, user.getId());
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

    private ResponseEntity<ExerciseUpload> checkIfCanUpload(long id, long examId, long userId) {
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

    private ResponseEntity<ExerciseUpload> checkIfCanSeeUploads(long id, long userId) {
        if (!subjectCheckService.isTeacherOfSubject(id, userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok().build();
    }

    private void updateExamFile(ExerciseUpload exercise, MultipartFile exerciseFile, Path finalPath)
            throws java.io.IOException {
        if (exerciseFile != null && !exerciseFile.isEmpty()) {

            Files.createDirectories(Paths.get(fileDir, finalPath.toString()));
            Files.copy(exerciseFile.getInputStream(),
                    Paths.get(fileDir, finalPath.toString(), exerciseFile.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
        } else {
            throw new java.io.IOException();
        }
    }

    public ResponseEntity<Resource> downloadFile(Path filePath) {
        Resource resource = new FileSystemResource(Paths.get(fileDir, filePath.toString()));
        if (resource.exists()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", resource.getFilename());
            return ResponseEntity.ok().headers(headers).body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Resource> findAllUploadsCompressed(long id, long examId, Principal principal) {
        ResponseEntity<User> responseUser = userCheckService.loadUserNoCkeck(principal);
        if (responseUser.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(responseUser.getStatusCode()).build();
        }
        User user = responseUser.getBody();
        ResponseEntity<ExerciseUpload> checkIfCanSeeUploads = checkIfCanSeeUploads(id, user.getId());
        if (checkIfCanSeeUploads.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(checkIfCanSeeUploads.getStatusCode()).build();
        }

        try {
            Exam exam = examRepository.findByIdAndSubjectId(examId, id).get();
            Path path = Paths.get(exam.getSubject().getId().toString(), exam.getId().toString());
            return findAllCompressed(path.toString(), exam.getName() + ".zip", exam.getSubject().getId().toString());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<Resource> findAllCompressed(String filePath, String zipFileName, String subjectPath) {
        try {
            Path zipPath = Paths.get(fileDir, filePath);
            Path zippedPath = Paths.get(fileDir, subjectPath, zipFileName);
            ZipUtil.pack(new File(zipPath.toString()), new File(zippedPath.toString()));
            ResponseEntity<Resource> downloadFile = downloadFile(Paths.get(subjectPath, zipFileName));
            return downloadFile;
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

}
