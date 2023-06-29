package com.tfg.brais.ControllerServicesTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.SubjectDetailedDTO;
import com.tfg.brais.Model.DTOS.SubjectUsersDTO;
import com.tfg.brais.Repository.SubjectRepository;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;
import com.tfg.brais.Service.ControllerServices.UserSubjectService;

public class UserSubjectServiceTest {

    private UserCheckService userCheckService;

    private SubjectRepository subjectRepository;

    private SubjectCheckService subjectCheckService;

    private UserSubjectService userSubjectService;

    @BeforeEach
    public void setUp() {
        userCheckService = Mockito.mock(UserCheckService.class);
        subjectRepository = Mockito.mock(SubjectRepository.class);
        subjectCheckService = Mockito.mock(SubjectCheckService.class);
        userSubjectService = new UserSubjectService(userCheckService, subjectRepository, subjectCheckService);
    }

    @Nested
    class FindAllUserSubjectsTest {

        @Test
        public void findAllUserSubjectsIncorrectPrincipal() {
            when(userCheckService.loadUserPrincipal(1, null)).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(404)));
            assertTrue(userSubjectService.findAllUserSubjects(1, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void findAllUserSubjectsEmptySubjects() {
            when(userCheckService.loadUserPrincipal(1, null)).thenReturn(ResponseEntity.ok(new User()));
            when(subjectRepository.findAllStudiedSubjects(1L)).thenReturn(new ArrayList<>());
            when(subjectRepository.findAllTeachedSubjects(1L)).thenReturn(new ArrayList<>());
            assertTrue(userSubjectService.findAllUserSubjects(1, null).getStatusCode().is2xxSuccessful());
        }

        @Test
        public void findAllUserSubjectsCorrectPrincipal() {
            SubjectUsersDTO subjectDTO = new SubjectUsersDTO();
            subjectDTO.getStudiedSubject().add(new SubjectDetailedDTO());
            subjectDTO.getTeachedSubject().add(new SubjectDetailedDTO());
            List<Subject> subjects = new ArrayList<>();
            Subject subject = new Subject();
            subjects.add(subject);
            Subject subject2 = new Subject();
            List<Subject> subjects2 = new ArrayList<>();
            subjects2.add(subject2);
            User user = new User();
            user.setId(1L);
            when(userCheckService.loadUserPrincipal(1L, null)).thenReturn(ResponseEntity.ok(user));
            when(subjectRepository.findAllStudiedSubjects(anyLong())).thenReturn(subjects);
            when(subjectRepository.findAllTeachedSubjects(anyLong())).thenReturn(subjects2);
            assertTrue(userSubjectService.findAllUserSubjects(1L, null).getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    public class searchStudentsTest {

        @Test
            public void searchStudentsIncorrectCantSee(){
                when(subjectRepository.findAllStudentBySubjectIdAndName(1L, null,null)).thenReturn(new PageImpl<>(new ArrayList<>()));
                when(subjectCheckService.checkIfCanSeeMembers(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
                assertTrue(userSubjectService.searchStudents(1L, null, null, null).getStatusCode().is4xxClientError());
            }

        @Test
            public void searchStudentsIncorrectNoContent(){
                when(subjectRepository.findAllStudentBySubjectIdAndName(anyLong(), any(),any())).thenReturn(new PageImpl<>(new ArrayList<>()));
                when(subjectCheckService.checkIfCanSeeMembers(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
                assertTrue(userSubjectService.searchStudents(1L, null, null, null).getStatusCode().is2xxSuccessful());
            }

        @Test
        public void searchStudentsCorrect() {
            List<User> users = new ArrayList<>();
            users.add(new User());
            when(subjectRepository.findAllStudentBySubjectIdAndName(anyLong(), any(), any()))
                    .thenReturn(new PageImpl<>(users));
            when(subjectCheckService.checkIfCanSeeMembers(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            assertTrue(userSubjectService.searchStudents(1L, null, null, null).getStatusCode().is2xxSuccessful());
        }

    }

    @Nested
    public class searchTeachersTest {

        @Test
            public void searchTeachersIncorrectCantSee(){
                when(subjectRepository.findAllTeachersBySubjectIdAndName(1L, null,null)).thenReturn(new PageImpl<>(new ArrayList<>()));
                when(subjectCheckService.checkIfCanSeeMembers(anyLong(), any())).thenReturn(ResponseEntity.notFound().build());
                assertTrue(userSubjectService.searchTeachers(1L, null, null, null).getStatusCode().is4xxClientError());
            }

        @Test
            public void searchTeachersIncorrectNoContent(){
                when(subjectRepository.findAllTeachersBySubjectIdAndName(anyLong(), any(),any())).thenReturn(new PageImpl<>(new ArrayList<>()));
                when(subjectCheckService.checkIfCanSeeMembers(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
                assertTrue(userSubjectService.searchTeachers(1L, null, null, null).getStatusCode().is2xxSuccessful());
            }

        @Test
        public void searchTeachersCorrect() {
            List<User> users = new ArrayList<>();
            users.add(new User());
            when(subjectRepository.findAllTeachersBySubjectIdAndName(anyLong(), any(), any()))
                    .thenReturn(new PageImpl<>(users));
            when(subjectCheckService.checkIfCanSeeMembers(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
            assertTrue(userSubjectService.searchTeachers(1L, null, null, null).getStatusCode().is2xxSuccessful());
        }

    }

    @Nested
    public class isTeacherOfSubjectTest {

        @Test
        public void isTeacherOfSubjectIsntTeacher(){
                    when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(false);
                    ResponseEntity<Boolean> teacherOfSubject = userSubjectService.isTeacherOfSubject(1L, 1L);
                    assertFalse(teacherOfSubject.getBody());
                    assertTrue(teacherOfSubject.getStatusCode().is2xxSuccessful());
                }

        @Test
        public void isTeacherOfSubjectIsTeacher(){
                    when(subjectCheckService.isTeacherOfSubject(anyLong(), anyLong())).thenReturn(true);
                    ResponseEntity<Boolean> teacherOfSubject = userSubjectService.isTeacherOfSubject(1L, 1L);
                    assertTrue(teacherOfSubject.getBody());
                    assertTrue(teacherOfSubject.getStatusCode().is2xxSuccessful());
                }
    }
}
