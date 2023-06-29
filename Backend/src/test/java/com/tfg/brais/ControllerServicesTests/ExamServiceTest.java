package com.tfg.brais.ControllerServicesTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.ExamBasicDTO;
import com.tfg.brais.Model.DTOS.ExamTeacherDTO;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Repository.ExerciseUploadRepository;
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

    private ExerciseUploadRepository exerciseUploadRepository;

    @BeforeEach
    public void setUp() {
        examRepository = mock(ExamRepository.class);
        subjectService = mock(SubjectService.class);
        examCheckService = mock(ExamCheckService.class);
        subjectCheckService = mock(SubjectCheckService.class);
        userCheckService = mock(UserCheckService.class);
        exerciseUploadRepository = mock(ExerciseUploadRepository.class);
        examService = new ExamService(examRepository, subjectService, examCheckService, subjectCheckService,
                userCheckService, exerciseUploadRepository);
    }

    @Nested
    public class FindBySubjectIdAndIdTest {

        @Test
        public void findBySubjectIdAndIdTestCantSeeContent() {
            when(subjectCheckService.checkIfCanSeeContent(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(examService.findBySubjectIdAndId(1L, 1L, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void findBySubjectIdAndIdTestNotVisibleStudent() {
            when(subjectCheckService.checkIfCanSeeContent(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserNoCkeck(any())).thenReturn(ResponseEntity.ok(user));
            Exam exam = new Exam();
            exam.setVisibleExam(false);
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(exam));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(false);
            assertTrue(examService.findBySubjectIdAndId(1L, 1L, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void findBySubjectIdAndIdTestTeacherNotVisible() {
            when(subjectCheckService.checkIfCanSeeContent(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserNoCkeck(any())).thenReturn(ResponseEntity.ok(user));
            Exam exam = new Exam();
            exam.setVisibleExam(false);
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(exam));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            when(exerciseUploadRepository.findAllUploadedByExamId(anyLong(), anyLong())).thenReturn(new ArrayList<>());
            assertTrue(examService.findBySubjectIdAndId(1L, 1L, null).getStatusCode().is2xxSuccessful());
        }

        @Test
        public void findBySubjectIdAndIdTestTeacherVisible() {
            when(subjectCheckService.checkIfCanSeeContent(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserNoCkeck(any())).thenReturn(ResponseEntity.ok(user));
            Exam exam = new Exam();
            exam.setVisibleExam(true);
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(exam));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            when(exerciseUploadRepository.findAllUploadedByExamId(anyLong(), anyLong())).thenReturn(new ArrayList<>());
            assertTrue(examService.findBySubjectIdAndId(1L, 1L, null).getStatusCode().is2xxSuccessful());
        }

        @Test
        public void findBySubjectIdAndIdTestIsStudentVisibleEmptyUpload() {
            when(subjectCheckService.checkIfCanSeeContent(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserNoCkeck(any())).thenReturn(ResponseEntity.ok(user));
            Exam exam = new Exam();
            exam.setVisibleExam(true);
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(exam));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(false);
            when(exerciseUploadRepository
                .findByStudentIdAndExamIdAndExamSubjectId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.empty());
            assertTrue(examService.findBySubjectIdAndId(1L, 1L, null).getStatusCode().is2xxSuccessful());
        }

        @Test
        public void findBySubjectIdAndIdTestIsStudentVisibleNotEmptyUpload() {
            when(subjectCheckService.checkIfCanSeeContent(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserNoCkeck(any())).thenReturn(ResponseEntity.ok(user));
            Exam exam = new Exam();
            exam.setVisibleExam(true);
            when(examRepository.findByIdAndSubjectId(anyLong(), anyLong())).thenReturn(Optional.of(exam));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(false);
            when(exerciseUploadRepository
                .findByStudentIdAndExamIdAndExamSubjectId(anyLong(), anyLong(), anyLong())).thenReturn(Optional.of(new ExerciseUpload()));
            assertTrue(examService.findBySubjectIdAndId(1L, 1L, null).getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    public class FindAllExamsBySubjectIdTest {

        @Test
        public void findAllExamsBySubjectIdIncorrectPrincipal() {
            when(userCheckService.loadUserNoCkeck(any())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(examService.findAllExamsBySubjectId(1L, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void findAllExamsBySubjectIdStudent() {
            User user = new User();
            user.setId(1L);
            ArrayList<Exam> exams = new ArrayList<>();
            exams.add(new Exam());
            when(userCheckService.loadUserNoCkeck(any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(false);
            when(examRepository.findAllBySubjectIdAndVisible(anyLong())).thenReturn(exams);
            ResponseEntity<List<ExamBasicDTO>> response = examService.findAllExamsBySubjectId(1L, null);
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(1, response.getBody().size());
        }

        @Test
        public void findAllExamsBySubjectIdTeacher() {
            User user = new User();
            user.setId(1L);
            ArrayList<Exam> exams = new ArrayList<>();
            exams.add(new Exam());
            when(userCheckService.loadUserNoCkeck(any())).thenReturn(ResponseEntity.ok(user));
            when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
            when(examRepository.findAllBySubjectId(anyLong())).thenReturn(exams);
            ResponseEntity<List<ExamBasicDTO>> response = examService.findAllExamsBySubjectId(1L, null);
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(1, response.getBody().size());
        }
    }

    @Nested
    public class UpdateExamTest {

        @Test
        public void updateExamTestCantEdit() {
            when(examCheckService.checkIfCanEdit(anyLong(), anyLong(),any(), any())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(examService.updateExam(1L, 1L, new ExamTeacherDTO(), null).getStatusCode().is4xxClientError());
        }

        @Test
        public void updateExamTestUpload() {
            when(examCheckService.checkIfCanEdit(anyLong(), anyLong(),any(), any())).thenReturn(ResponseEntity.ok(new Exam()));
            ExamTeacherDTO examTeacherDTO = new ExamTeacherDTO();
            examTeacherDTO.setType("UPLOAD");
            ArrayList<String> questions = new ArrayList<>();
            questions.add("test");
            examTeacherDTO.setQuestions(questions);
            when(examRepository.save(any())).thenReturn(new Exam());
            ResponseEntity<ExamTeacherDTO> updateExam = examService.updateExam(1L, 1L, examTeacherDTO, null);
            assertTrue(updateExam.getStatusCode().is2xxSuccessful());
            assertEquals("UPLOAD", updateExam.getBody().getType());
            assertEquals(0, updateExam.getBody().getQuestions().size());
        }

        @Test
        public void updateExamTestQuestions() {
            when(examCheckService.checkIfCanEdit(anyLong(), anyLong(),any(), any())).thenReturn(ResponseEntity.ok(new Exam()));
            ExamTeacherDTO examTeacherDTO = new ExamTeacherDTO();
            examTeacherDTO.setType("QUESTIONS");
            ArrayList<String> questions = new ArrayList<>();
            questions.add("test");
            examTeacherDTO.setQuestions(questions);
            when(examRepository.save(any())).thenReturn(new Exam());
            ResponseEntity<ExamTeacherDTO> updateExam = examService.updateExam(1L, 1L, examTeacherDTO, null);
            assertTrue(updateExam.getStatusCode().is2xxSuccessful());
            assertEquals("QUESTIONS", updateExam.getBody().getType());
            assertEquals(1, updateExam.getBody().getQuestions().size());
        }
    }

    @Nested
    public class CreateExamTest {

        @Test
            public void createExamTestCantCreate() {
                when(examCheckService.checkIfCanCreate(anyLong(), any(), any())).thenReturn(ResponseEntity.notFound().build());
                assertTrue(examService.createExam(1L, new ExamTeacherDTO(), null, null).getStatusCode().is4xxClientError());
            }

        @Test
        public void createExamTestUpload() {
            when(examCheckService.checkIfCanCreate(anyLong(), any() , any())).thenReturn(ResponseEntity.ok(new Exam()));
            ExamTeacherDTO examTeacherDTO = new ExamTeacherDTO();
            examTeacherDTO.setType("UPLOAD");
            ArrayList<String> questions = new ArrayList<>();
            questions.add("test");
            examTeacherDTO.setQuestions(questions);
            when(examRepository.save(any())).thenReturn(new Exam());
            when(subjectService.findSubjectById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(examRepository.save(any())).thenReturn(new Exam());
            when(exerciseUploadRepository.save(any())).thenReturn(new ExerciseUpload());
            ResponseEntity<ExamTeacherDTO> updateExam = examService.createExam(1L, examTeacherDTO, null, UriComponentsBuilder.newInstance());
            assertTrue(updateExam.getStatusCode().is2xxSuccessful());
            assertEquals("UPLOAD", updateExam.getBody().getType());
            assertEquals(0, updateExam.getBody().getQuestions().size());
        }

        @Test
        public void createExamTestQuestions() {
            when(examCheckService.checkIfCanCreate(anyLong(), any() , any())).thenReturn(ResponseEntity.ok(new Exam()));
            ExamTeacherDTO examTeacherDTO = new ExamTeacherDTO();
            examTeacherDTO.setType("QUESTIONS");
            ArrayList<String> questions = new ArrayList<>();
            questions.add("test");
            examTeacherDTO.setQuestions(questions);
            when(examRepository.save(any())).thenReturn(new Exam());
            when(subjectService.findSubjectById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            when(examRepository.save(any())).thenReturn(new Exam());
            when(exerciseUploadRepository.save(any())).thenReturn(new ExerciseUpload());
            ResponseEntity<ExamTeacherDTO> updateExam = examService.createExam(1L, examTeacherDTO, null, UriComponentsBuilder.newInstance());
            assertTrue(updateExam.getStatusCode().is2xxSuccessful());
            assertEquals("QUESTIONS", updateExam.getBody().getType());
            assertEquals(1, updateExam.getBody().getQuestions().size());
        }
    }

    @Nested
    public class getExamQuestionsTest{

        @Test
        public void getExamQuestionsCantSee() {
            when(examCheckService.checkIfCanSee(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
            assertTrue(examService.getExamQuestions(1L, 1L, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void getExamQuestions() {
            when(examCheckService.checkIfCanSee(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok(new Exam()));
            ResponseEntity<List<String>> examQuestions = examService.getExamQuestions(1L, 1L, null);
            assertTrue(examQuestions.getStatusCode().is2xxSuccessful());
            assertEquals(0, examQuestions.getBody().size());
        }
    }
}
