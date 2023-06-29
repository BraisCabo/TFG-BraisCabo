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
import com.tfg.brais.Model.DTOS.CalificationDTO;
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
    public class changeCalificationTest {

        @Test
        public void changeCalificationTestCantSeeUploads(){
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any())).thenReturn(ResponseEntity.status(403).build());
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalification(1L, 1L, 1L, null, null, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationTestIncorrectFunction() {
            ExerciseUpload upload = new ExerciseUpload();
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalification(1L, 1L, 1L, null,
                    null, (calification) -> true);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationNullCalification() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification(null);
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalification(1L, 1L, 1L, null,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationEmptyCalification() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification("");
            CalificationDTO calificationDTO = new CalificationDTO();
            calificationDTO.setCalification("");
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalification(1L, 1L, 1L,
                    calificationDTO,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationLessThan0Calification() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification("-0.1");
            CalificationDTO calificationDTO = new CalificationDTO();
            calificationDTO.setCalification("-0.1");
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalification(1L, 1L, 1L,
                    calificationDTO,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationMoreThan10Calification() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification("10.1");
            CalificationDTO calificationDTO = new CalificationDTO();
            calificationDTO.setCalification("10.1");
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalification(1L, 1L, 1L,
                    calificationDTO,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void changeCalificationCorrect() {
            ExerciseUpload upload = new ExerciseUpload();
            upload.setCalification("10");
            CalificationDTO calificationDTO = new CalificationDTO();
            calificationDTO.setCalification("10");
            when(exerciseUploadCheckService.checkIfCanSeeUploads(anyLong(), any()))
                    .thenReturn(ResponseEntity.ok().build());
            when(uploadService.findUploadById(anyLong(), anyLong(), anyLong(), any()))
                    .thenReturn(ResponseEntity.ok(upload));
            when(exerciseUploadRepository.save(any())).thenReturn(upload);
            ResponseEntity<ExerciseUpload> response = calificationCheckService.changeCalification(1L, 1L, 1L,
                    calificationDTO,
                    null, (calification) -> false);
            assertTrue(response.getStatusCode().is2xxSuccessful());
        }

    }
}
