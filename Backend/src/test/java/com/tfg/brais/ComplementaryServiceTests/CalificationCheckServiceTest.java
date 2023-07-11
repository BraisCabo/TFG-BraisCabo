package com.tfg.brais.ComplementaryServiceTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.DTOS.CalificationFileDTO;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Service.ComplementaryServices.CalificationCheckService;
import com.tfg.brais.Service.ComplementaryServices.ExerciseUploadCheckService;
import com.tfg.brais.Service.ControllerServices.UploadService;

public class CalificationCheckServiceTest {

    private ExerciseUploadRepository exerciseUploadRepository;

    private ExerciseUploadCheckService exerciseUploadCheckService;

    private UploadService uploadService;

    private CalificationCheckService calificationCheckService;

    @BeforeEach
    public void setUp() {
        exerciseUploadRepository = mock(ExerciseUploadRepository.class);
        exerciseUploadCheckService = mock(ExerciseUploadCheckService.class);
        uploadService = mock(UploadService.class);
        calificationCheckService = new CalificationCheckService(exerciseUploadRepository, exerciseUploadCheckService,
                uploadService);
    }

    @Nested
    public class changeCalificationFilesTest {

        @Test
        public void changeCalificationFilesTestCantSeeUploads(){
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any())).thenReturn(ResponseEntity.status(403).build());
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalificationFiles(1L, 1L, 1L, null, null, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationFilesTestIncorrectFunction() {
            ExerciseUpload upload = new ExerciseUpload();
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalificationFiles(1L, 1L, 1L, null,
                    null, (calification) -> true);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationFilesNullCalification() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification(null);
            CalificationFileDTO calificationFileDTO = new CalificationFileDTO();
            calificationFileDTO.setCalification(null);
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalificationFiles(1L, 1L, 1L, calificationFileDTO,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationFilesEmptyCalification() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification("");
            CalificationFileDTO CalificationFileDTO = new CalificationFileDTO();
            CalificationFileDTO.setCalification("");
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalificationFiles(1L, 1L, 1L,
                    CalificationFileDTO,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationFilesLessThan0Calification() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification("-0.1");
            CalificationFileDTO CalificationFileDTO = new CalificationFileDTO();
            CalificationFileDTO.setCalification("-0.1");
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalificationFiles(1L, 1L, 1L,
                    CalificationFileDTO,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationFilesMoreThan10Calification() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification("10.1");
            CalificationFileDTO CalificationFileDTO = new CalificationFileDTO();
            CalificationFileDTO.setCalification("10.1");
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalificationFiles(1L, 1L, 1L,
                    CalificationFileDTO,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationFilesCorrect() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification("10");
            CalificationFileDTO CalificationFileDTO = new CalificationFileDTO();
            CalificationFileDTO.setCalification("10");
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalificationFiles(1L, 1L, 1L,
                    CalificationFileDTO,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is2xxSuccessful());
        }

    }
}
