package com.tfg.brais.ControllerServicesTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Service.ComplementaryServices.ExamCheckService;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;
import com.tfg.brais.Service.ControllerServices.ExamService;
import com.tfg.brais.Service.ControllerServices.SubjectService;

public class ExamServiceTest {
    
    private ExamRepository examRepository;

    private SubjectService subjectService;

    private ExamCheckService examCheckService;

    private SubjectCheckService subjectCheckService;

    private UserCheckService userCheckService;

    private ExamService examService;

    @BeforeEach
    public void setUp() {
        examRepository = mock(ExamRepository.class);
        subjectService = mock(SubjectService.class);
        examCheckService = mock(ExamCheckService.class);
        subjectCheckService = mock(SubjectCheckService.class);
        userCheckService = mock(UserCheckService.class);
        examService = new ExamService(examRepository, subjectService, examCheckService, subjectCheckService, userCheckService);
    }

    @Nested
    public class FindBySubjectIdAndIdTest {
        
        @Test
        public void findBySubjectIdAndIdTestCantSeeContent() {
            when(subjectCheckService.checkIfCanSeeContent(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(examService.findBySubjectIdAndId(1L, 1L, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void findBySubjectIdAndIdTestExamNotFound() {
            when(subjectCheckService.checkIfCanSeeContent(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.empty());
            assertTrue(examService.findBySubjectIdAndId(1L, 1L, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void findBySubjectIdAndIdTestOk() {
            when(subjectCheckService.checkIfCanSeeContent(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(new Exam()));
            assertTrue(examService.findBySubjectIdAndId(1L, 1L, null).getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    public class FindAllExamsBySubjectIdTest {
        
        @Test
        public void findAllExamsBySubjectIdTestCantSeeContent() {
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(examService.findAllExamsBySubjectId(1L, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void findAllExamsBySubjectIdTestEmptyContent() {
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(examRepository.findAllBySubjectIdAndVisible(anyLong())).thenReturn(new ArrayList<>());
            assertTrue(examService.findAllExamsBySubjectId(1L, null).getStatusCode().is2xxSuccessful());
        }

        @Test
        public void findAllExamsBySubjectIdTestCorrect() {
            User user = new User();
            user.setId(1L);
            List<Exam> exams = new ArrayList<>();
            exams.add(new Exam());
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(examRepository.findAllBySubjectIdAndVisible(anyLong())).thenReturn(exams);
            assertTrue(examService.findAllExamsBySubjectId(1L, null).getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    public class UpdateExamTest{

        @Test
        public void updateExamTestCantEdit() {
            when(examCheckService.checkIfCanEdit(anyLong(), anyLong(),any(), any())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(examService.updateExam(1L, 1L, null, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void updateExamTestCorrect() {
            when(examCheckService.checkIfCanEdit(anyLong(), anyLong(),any(), any())).thenReturn(ResponseEntity.ok(new Exam()));
            assertTrue(examService.updateExam(1L, 1L, new Exam(), null).getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    public class CreateExamTest{
            
            @Test
            public void createExamTestCantCreate() {
                when(examCheckService.checkIfCanCreate(anyLong(), any(), any())).thenReturn(ResponseEntity.notFound().build());
                assertTrue(examService.createExam(1L, new Exam(), null, null).getStatusCode().is4xxClientError());
            }
    
            @Test
            public void createExamTestCorrect() {
                when(examCheckService.checkIfCanCreate(anyLong(), any(), any())).thenReturn(ResponseEntity.ok().build());
                when(subjectService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
                assertTrue(examService.createExam(1L, new Exam(), null, UriComponentsBuilder.newInstance()).getStatusCode().is2xxSuccessful());
            }
    }
}
