package com.tfg.brais.ControllerServicesTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Service.ComplementaryServices.ExerciseUploadCheckService;
import com.tfg.brais.Service.ComplementaryServices.FileService;
import com.tfg.brais.Service.ControllerServices.UploadService;

public class UploadServiceTest {

    private ExerciseUploadRepository exerciseUploadRepository;

    private ExerciseUploadCheckService exerciseUploadCheckService;

    private FileService fileService;

    private ExamRepository examRepository;

    private UploadService uploadService;

    @BeforeEach
    public void setUp() {
        exerciseUploadRepository = Mockito.mock(ExerciseUploadRepository.class);
        exerciseUploadCheckService = Mockito.mock(ExerciseUploadCheckService.class);
        fileService = Mockito.mock(FileService.class);
        examRepository = Mockito.mock(ExamRepository.class);
        uploadService = new UploadService(exerciseUploadRepository, exerciseUploadCheckService, fileService,
                examRepository);
    }

    @Nested
    public class downloadUploadByIdTest {

        @Test
        public void downloadUploadByIdTestCantDownload(){
            when(exerciseUploadCheckService.checkIfCanDownload(anyLong(), anyLong(), anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            ResponseEntity<?> response = uploadService.downloadUploadById(1, 1, 1, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void downloadUploadByIdTestCorrect() {
            ExerciseUpload exerciseUpload = new ExerciseUpload();
            exerciseUpload.setFileName("test");
            Subject subject = new Subject();
            subject.setId(1L);
            User user = new User();
            user.setName("test");
            user.setLastName("test");
            Exam exam = new Exam();
            exam.setSubject(subject);
            exam.setId(1L);
            exerciseUpload.setStudent(user);
            exerciseUpload.setExam(exam);

            when(exerciseUploadCheckService.checkIfCanDownload(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(exerciseUpload));
            when(fileService.downloadFile(any())).thenReturn(ResponseEntity.ok().build());
            ResponseEntity<Resource> response = uploadService.downloadUploadById(1, 1, 1, null);
            assertTrue(response.getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    public class uploadExerciseTest {

        @Test
        public void uploadExerciseTestCantUpload(){
            when(exerciseUploadCheckService.checkIfCanUpload(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            ResponseEntity<ExerciseUpload> response = uploadService.uploadExercise(1l, 1l, (MultipartFile) null, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void uploadExerciseTestNoUpload() {
            ExerciseUpload exerciseUpload = new ExerciseUpload();
            Exam exam = new Exam();
            exam.setType("QUESTIONS");
            exerciseUpload.setExam(exam);
            when(exerciseUploadCheckService.checkIfCanUpload(anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(exerciseUpload));
            ResponseEntity<ExerciseUpload> response = uploadService.uploadExercise(1l, 1l, (MultipartFile) null, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void uploadExerciseTestCantSaveFile() {
            ExerciseUpload exerciseUpload = new ExerciseUpload();
            exerciseUpload.setFileName("test");
            Subject subject = new Subject();
            subject.setId(1L);
            User user = new User();
            user.setName("test");
            user.setLastName("test");
            Exam exam = new Exam();
            exam.setSubject(subject);
            exam.setId(1L);
            exerciseUpload.setStudent(user);
            exam.setType("UPLOAD");
            exerciseUpload.setExam(exam);
            when(exerciseUploadCheckService.checkIfCanUpload(anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(exerciseUpload));
            try {
                doThrow(IOException.class).when(fileService).saveFile(any(), any());
                ResponseEntity<ExerciseUpload> response = uploadService.uploadExercise(1l, 1l, (MultipartFile) null,
                        null);
                assertTrue(response.getStatusCode().is5xxServerError());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Test
        public void uploadExerciseTestCorrect() {
            ExerciseUpload exerciseUpload = new ExerciseUpload();
            exerciseUpload.setFileName("test");
            Subject subject = new Subject();
            subject.setId(1L);
            User user = new User();
            user.setName("test");
            user.setLastName("test");
            Exam exam = new Exam();
            exam.setSubject(subject);
            exam.setId(1L);
            exerciseUpload.setStudent(user);
            exam.setType("UPLOAD");
            exerciseUpload.setExam(exam);
            when(exerciseUploadCheckService.checkIfCanUpload(anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(exerciseUpload));
            ResponseEntity<ExerciseUpload> response = uploadService.uploadExercise(1l, 1l, (MultipartFile) null, null);
            assertTrue(response.getStatusCode().is5xxServerError());
        }
    }

    @Nested
    public class findUploadByIdTest{

        @Test
        public void findUploadByIdTestCantSeeUploads(){
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            ResponseEntity<ExerciseUpload> response = uploadService.findUploadById(1l, 1l, 1l, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void findUploadByIdTestNoUpload(){
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            when(exerciseUploadRepository.findByIdAndExamIdAndExamSubjectId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.empty());
            ResponseEntity<ExerciseUpload> response = uploadService.findUploadById(1l, 1l, 1l, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void findUploadByIdTestCorrect(){
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            when(exerciseUploadRepository.findByIdAndExamIdAndExamSubjectId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.of(new ExerciseUpload()));
            ResponseEntity<ExerciseUpload> response = uploadService.findUploadById(1l, 1l, 1l, null);
            assertTrue(response.getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    public class findAllUploadsCompressedTest{

        @Test 
        public void findAllUploadsCompressedTestCantSee(){
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            ResponseEntity<Resource> response = uploadService.findAllUploadsCompressed(1l, 1l, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void findAllUploadsCompressedTestNotExam(){
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.empty());
            ResponseEntity<Resource> response = uploadService.findAllUploadsCompressed(1l, 1l, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void findAllUploadsCompressedTestCantCompress(){
            Exam exam = new Exam();
            Subject subject = new Subject();
            subject.setId(1L);
            exam.setId(1L);
            exam.setSubject(subject);
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(exam));
            when(fileService.compressDirectory(anyString(), anyString(), anyString())).thenReturn(ResponseEntity.notFound().build());
            ResponseEntity<Resource> response = uploadService.findAllUploadsCompressed(1l, 1l, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void findAllUploadsCompressedTestCorrect(){
            Exam exam = new Exam();
            Subject subject = new Subject();
            subject.setId(1L);
            exam.setId(1L);
            exam.setSubject(subject);
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(exam));
            when(fileService.compressDirectory(anyString(), anyString(), anyString())).thenReturn(ResponseEntity.ok().build());
            ResponseEntity<Resource> response = uploadService.findAllUploadsCompressed(1l, 1l, null);
            assertTrue(response.getStatusCode().is2xxSuccessful());
        }
    }

}
