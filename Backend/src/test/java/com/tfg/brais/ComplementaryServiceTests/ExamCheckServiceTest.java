package com.tfg.brais.ComplementaryServiceTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Service.ComplementaryServices.ExamCheckService;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;

public class ExamCheckServiceTest {


    private SubjectCheckService subjectCheckService;

    private UserCheckService userCheckService;

    private ExamRepository examRepository;

    private ExamCheckService examCheckService;

    @BeforeEach
    public void setUp() {
        subjectCheckService = Mockito.mock(SubjectCheckService.class);
        userCheckService = Mockito.mock(UserCheckService.class);
        examRepository = Mockito.mock(ExamRepository.class);
        examCheckService = new ExamCheckService(subjectCheckService, userCheckService, examRepository);
    }

    @Nested
    @DisplayName("CheckIfcanCreate tests")
    class CheckIfCanCreateTests {


        @Test
        public void checkIfCanCreateIncorrectPrincipalTest() {
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(new ResponseEntity<User>(HttpStatus.valueOf(404)));
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1, new Exam(), null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanCreateIncorrectSubjectId(){
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(new User()));
            when(subjectCheckService.findById(anyLong())).thenReturn(new ResponseEntity<Subject>(HttpStatusCode.valueOf(404)));
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1, new Exam(), null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanCreateNoTeacher(){
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(false);
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1L, new Exam(), null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }
        
        @Test
        public void checkIfCanCreateNullName(){
            Exam exam = new Exam();
            exam.setName(null);
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanCreateEmptyName(){
            Exam exam = new Exam();
            exam.setName("");
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanCreateIncorrectType(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("test");
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanCreateQuestionsEmpty(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("QUESTIONS");
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanCreateWrongDate(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("UPLOAD");
            exam.setClosingDate(Date.from(Instant.now()));
            exam.setOpeningDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanCreateIncorrectExistExam(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("QUESTIONS");
            exam.getQuestions().add("Primera pregunta");
            exam.setOpeningDate(Date.from(Instant.now()));
            exam.setClosingDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            when(examRepository.findByNameAndSubjectId(anyString(), anyLong())).thenReturn(Optional.of(new Exam()));
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanCreateCorrectUploadType(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("UPLOAD");
            exam.setOpeningDate(Date.from(Instant.now()));
            exam.setClosingDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            when(examRepository.findByNameAndSubjectId(anyString(), anyLong())).thenReturn(Optional.empty());
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1L, exam, null);
            assertTrue(response.getStatusCode().is2xxSuccessful());
        }

        @Test
        public void checkIfCanCreateCorrectQuestionsype(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("QUESTIONS");
            exam.getQuestions().add("Primera pregunta");
            exam.setOpeningDate(Date.from(Instant.now()));
            exam.setClosingDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            when(examRepository.findByNameAndSubjectId(anyString(), anyLong())).thenReturn(Optional.empty());
            ResponseEntity<Exam> response = examCheckService.checkIfCanCreate(1L, exam, null);
            assertTrue(response.getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("CheckIfcanEdit tests")
    class CheckIfCanEditTests {


        @Test
        public void checkIfCanEditIncorrectPrincipalTest() {
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(new ResponseEntity<User>(HttpStatus.valueOf(404)));
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1, 1, new Exam(), null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanEditIncorrectSubjectId(){
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(new User()));
            when(subjectCheckService.findById(anyLong())).thenReturn(new ResponseEntity<Subject>(HttpStatusCode.valueOf(404)));
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1, 1, new Exam(), null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanEditNoTeacher(){
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(false);
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1L, 1L, new Exam(), null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }
        
        @Test
        public void checkIfCanEditNullName(){
            Exam exam = new Exam();
            exam.setName(null);
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1L , 1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanEditEmptyName(){
            Exam exam = new Exam();
            exam.setName("");
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1L, 1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanEditIncorrectType(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("test");
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1L, 1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanEditQuestionsEmpty(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("QUESTIONS");
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1L, 1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanEditWrongDate(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("UPLOAD");
            exam.setClosingDate(Date.from(Instant.now()));
            exam.setOpeningDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1L, 1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanEditIncorrectDoesntExistOldExam(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("QUESTIONS");
            exam.getQuestions().add("Primera pregunta");
            exam.setOpeningDate(Date.from(Instant.now()));
            exam.setClosingDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.empty());
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1L, 1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanEditNewNameAsigned(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("QUESTIONS");
            exam.getQuestions().add("Primera pregunta");
            exam.setOpeningDate(Date.from(Instant.now()));
            exam.setClosingDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

            Exam exam2 = new Exam();
            exam.setName("test2");
            exam.setType("QUESTIONS");
            exam.getQuestions().add("Primera pregunta");
            exam.setOpeningDate(Date.from(Instant.now()));
            exam.setClosingDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
            User user = new User();
            user.setId(1L);

            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(exam2));
            when(examRepository.findByNameAndSubjectId(anyString(), anyLong())).thenReturn(Optional.of(exam));
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1L, 1L, exam, null);
            assertTrue(response.getStatusCode().is4xxClientError());
        }

        @Test
        public void checkIfCanEditCorrect(){
            Exam exam = new Exam();
            exam.setName("test");
            exam.setType("QUESTIONS");
            exam.getQuestions().add("Primera pregunta");
            exam.setOpeningDate(Date.from(Instant.now()));
            exam.setClosingDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

            User user = new User();
            user.setId(1L);

            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(exam));
            when(examRepository.findByNameAndSubjectId(anyString(), anyLong())).thenReturn(Optional.of(exam));
            ResponseEntity<Exam> response = examCheckService.checkIfCanEdit(1L, 1L, exam, null);
            assertTrue(response.getStatusCode().is2xxSuccessful());
        }
    }
}
