package com.tfg.brais.ControllerServicesTests;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.StudentCalificationDTO;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Service.ComplementaryServices.CalificationCheckService;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;
import com.tfg.brais.Service.ControllerServices.CalificationService;


public class CalificationServiceTest {
    
    private UserCheckService userCheckService;

    private ExerciseUploadRepository exerciseUploadRepository;

    private SubjectCheckService subjectCheckService;

    private CalificationCheckService calificationCheckService;

    private CalificationService calificationService;

    @BeforeEach
    public void setUp() {
        userCheckService = mock(UserCheckService.class);
        exerciseUploadRepository = mock(ExerciseUploadRepository.class);
        subjectCheckService = mock(SubjectCheckService.class);
        calificationCheckService = mock(CalificationCheckService.class);
        calificationService = new CalificationService(userCheckService, exerciseUploadRepository, subjectCheckService, calificationCheckService);
    }

    @Nested
    public class searchCalificationsTest{

        @Test
        public void searchCalificationsTestNoUserPrincipal(){
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            ResponseEntity<StudentCalificationDTO> response = calificationService.searchCalifications(1, 1, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void searchCalificationsTestNoTeacherOfSubject(){
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(new User()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(false);
            ResponseEntity<StudentCalificationDTO> response = calificationService.searchCalifications(1, 1, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void searchCalificationsTestCorrect(){
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(new User()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<StudentCalificationDTO> response = calificationService.searchCalifications(1, 1, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }


    }
}
