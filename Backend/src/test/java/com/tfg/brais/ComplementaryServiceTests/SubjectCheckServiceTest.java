package com.tfg.brais.ComplementaryServiceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.SubjectDTO;
import com.tfg.brais.Model.User;
import com.tfg.brais.Repository.SubjectRepository;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;

public class SubjectCheckServiceTest {

    private SubjectRepository subjectRepository;
    private UserCheckService userCheckService;
    private SubjectCheckService subjectCheckService;

    @BeforeEach
    public void setUp() {
        subjectRepository = mock(SubjectRepository.class);
        userCheckService = mock(UserCheckService.class);
        subjectCheckService = new SubjectCheckService(subjectRepository, userCheckService);
    }

    @Nested
    @DisplayName("Tests for isTeacherOfSubject")
    class IsTeacherOfSubjectTests {

    @Test
    public void testIsTeacherOfSubject() throws Exception {
        when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                .thenReturn(1);
        assertTrue(subjectCheckService.isTeacherOfSubject(1L, 1L));
    }

    @Test
    public void testIsNotTeacherOfSubject() throws Exception {
        when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                .thenReturn(0);
        assertFalse(subjectCheckService.isTeacherOfSubject(1L, 1L));
    }
    }

    @Nested
    @DisplayName("Tests for isStudentOfSubject")
    class IsStudentOfSubjectTests {

    @Test
    public void testIsStudentOfSubject() throws Exception {
        when(subjectRepository.countBySubjectIdAndStudentId(anyLong(), anyLong()))
                .thenReturn(1);
        assertTrue(subjectCheckService.isStudentOfSubject(1L, 1L));
    }

    @Test
    public void testIsNotStudentOfSubject() throws Exception {
        when(subjectRepository.countBySubjectIdAndStudentId(anyLong(), anyLong()))
                .thenReturn(0);
        assertFalse(subjectCheckService.isStudentOfSubject(1L, 1L));
    }
    }

    @Nested
    @DisplayName("Tests for findById")
    class FindByIdTests {

    @Test
    public void testFindByIdDontExist() throws Exception {
        when(subjectRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertTrue(subjectCheckService.findById(1L).getStatusCode().is4xxClientError());
    }

    @Test
    public void testFindByIdExist() throws Exception {
        when(subjectRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Subject()));
        assertTrue(subjectCheckService.findById(1L).getStatusCode().is2xxSuccessful());
    }
    }

    @Nested
    @DisplayName("Tests for checkIfCanSeeMembers")
    class CheckIfCanSeeMembersTests {

    @Test
    public void testCheckIfCanSeeMembersIncorrectUser() throws Exception {
        when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(new ResponseEntity<User>(HttpStatusCode.valueOf(404)));
                when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                .thenReturn(1);
        
        assertTrue(subjectCheckService.checkIfCanSeeMembers(1L, null).getStatusCode().is4xxClientError());
    }

    @Test
    public void testCheckIfCanSeeMembersInexistentSubject() throws Exception {
        when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(new User()));
                when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                .thenReturn(0);
            when(subjectRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertTrue(subjectCheckService.checkIfCanSeeMembers(1L, null).getStatusCode().is4xxClientError());
    }

        @Test
        public void testCheckIfCanSeeMembersNotStudentOrTeacherOrAdmin() throws Exception {
            User user = new User();
            user.setId(1L);
            user.setRoles("USER");
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                    .thenReturn(0);
            when(subjectRepository.countBySubjectIdAndStudentId(anyLong(), anyLong()))
                    .thenReturn(0);
            when(subjectRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Subject()));
            assertTrue(subjectCheckService.checkIfCanSeeMembers(1L, null).getStatusCode().is4xxClientError());
        }

        @Test
        public void testCheckIfCanSeeMembersStudent() throws Exception {
            User user = new User();
            user.setId(1L);
            user.setRoles("USER");
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                    .thenReturn(0);
            when(subjectRepository.countBySubjectIdAndStudentId(any(), any()))
                    .thenReturn(1);
            when(subjectRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Subject()));
            assertTrue(subjectCheckService.checkIfCanSeeMembers(1L, null).getStatusCode().is2xxSuccessful());
        }

        @Test
        public void testCheckIfCanSeeMembersTeacher() throws Exception {
            User user = new User();
            user.setId(1L);
            user.setRoles("USER");
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectRepository.countBySubjectIdAndTeacherId(any(), any()))
                    .thenReturn(1);
            when(subjectRepository.countBySubjectIdAndStudentId(any(), any()))
                    .thenReturn(0);
            when(subjectRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Subject()));
            assertTrue(subjectCheckService.checkIfCanSeeMembers(1L, null).getStatusCode().is2xxSuccessful());
        }

        @Test
        public void testCheckIfCanSeeMembersAdmin() throws Exception {
            User user = new User();
            user.setId(1L);
            user.setRoles("USER", "ADMIN");
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
            when(subjectRepository.countBySubjectIdAndTeacherId(any(), any()))
                    .thenReturn(0);
            when(subjectRepository.countBySubjectIdAndStudentId(any(), any()))
                    .thenReturn(0);
            when(subjectRepository.findById(anyLong()))
                    .thenReturn(Optional.of(new Subject()));
            assertTrue(subjectCheckService.checkIfCanSeeMembers(1L, null).getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("Tests for checkIfCanSeeContent")
    class CheckIfCanSeeContentTests {
        @Test
        public void testCheckIfCanSeeMembersIncorrectUser() throws Exception {
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(new ResponseEntity<User>(HttpStatusCode.valueOf(404)));
                    when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                    .thenReturn(1);
            
            assertTrue(subjectCheckService.checkIfCanSeeContent(1L, null).getStatusCode().is4xxClientError());
        }
    
        @Test
        public void testCheckIfCanSeeMembersInexistentSubject() throws Exception {
            when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(new User()));
                    when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                    .thenReturn(0);
                when(subjectRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());
            assertTrue(subjectCheckService.checkIfCanSeeContent(1L, null).getStatusCode().is4xxClientError());
        }
    
            @Test
            public void testCheckIfCanSeeMembersNotStudentOrTeacherOrAdmin() throws Exception {
                User user = new User();
                user.setId(1L);
                user.setRoles("USER");
                when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
                when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                        .thenReturn(0);
                when(subjectRepository.countBySubjectIdAndStudentId(anyLong(), anyLong()))
                        .thenReturn(0);
                when(subjectRepository.findById(anyLong()))
                        .thenReturn(Optional.of(new Subject()));
                assertTrue(subjectCheckService.checkIfCanSeeContent(1L, null).getStatusCode().is4xxClientError());
            }
    
            @Test
            public void testCheckIfCanSeeMembersStudent() throws Exception {
                User user = new User();
                user.setId(1L);
                user.setRoles("USER");
                when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
                when(subjectRepository.countBySubjectIdAndTeacherId(anyLong(), anyLong()))
                        .thenReturn(0);
                when(subjectRepository.countBySubjectIdAndStudentId(any(), any()))
                        .thenReturn(1);
                when(subjectRepository.findById(anyLong()))
                        .thenReturn(Optional.of(new Subject()));
                assertTrue(subjectCheckService.checkIfCanSeeContent(1L, null).getStatusCode().is2xxSuccessful());
            }
    
            @Test
            public void testCheckIfCanSeeMembersTeacher() throws Exception {
                User user = new User();
                user.setId(1L);
                user.setRoles("USER");
                when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
                when(subjectRepository.countBySubjectIdAndTeacherId(any(), any()))
                        .thenReturn(1);
                when(subjectRepository.countBySubjectIdAndStudentId(any(), any()))
                        .thenReturn(0);
                when(subjectRepository.findById(anyLong()))
                        .thenReturn(Optional.of(new Subject()));
                assertTrue(subjectCheckService.checkIfCanSeeContent(1L, null).getStatusCode().is2xxSuccessful());
            }
    
            @Test
            public void testCheckIfCanSeeMembersAdmin() throws Exception {
                User user = new User();
                user.setId(1L);
                user.setRoles("USER", "ADMIN");
                when(userCheckService.loadUserPrincipal(anyLong(), any())).thenReturn(ResponseEntity.ok(user));
                when(subjectRepository.countBySubjectIdAndTeacherId(any(), any()))
                        .thenReturn(0);
                when(subjectRepository.countBySubjectIdAndStudentId(any(), any()))
                        .thenReturn(0);
                when(subjectRepository.findById(anyLong()))
                        .thenReturn(Optional.of(new Subject()));
                assertFalse(subjectCheckService.checkIfCanSeeContent(1L, null).getStatusCode().is2xxSuccessful());
            }
        }

        @Nested
        @DisplayName("Tests for checkCanCreateSubject")
        public class CheckCanCreateSubjectTests {
            @Test
            public void testCheckCanCreateSubjectExistName() throws Exception {
                SubjectDTO subjectDTO = createSubject();
                when(subjectRepository.findByName(any())).thenReturn(Optional.of(new Subject()));
                assertFalse(subjectCheckService.canCreateSubject(subjectDTO));
                assertFalse(subjectCheckService.canCreateSubject(subjectDTO));
            }
        
            @Test
            public void testCheckCanCreateSubjectSameTeachersAndStudents() throws Exception {
                SubjectDTO subjectDTO = createSubject();
                subjectDTO.getStudents().add(1L);
                subjectDTO.getTeachers().add(1L);
                when(subjectRepository.findByName(any())).thenReturn(Optional.empty());
                assertFalse(subjectCheckService.canCreateSubject(subjectDTO));
            }

            @Test
            public void testCheckCanCreateSubjectEmptyUsers() throws Exception {
                SubjectDTO subjectDTO = createSubject();
                when(subjectRepository.findByName(any())).thenReturn(Optional.empty());
                assertTrue(subjectCheckService.canCreateSubject(subjectDTO));
            }

            @Test
            public void testCheckCanCreateSubjectDifferentUsers() throws Exception{
                SubjectDTO subjectDTO = createSubject();
                subjectDTO.getStudents().add(1L);
                subjectDTO.getTeachers().add(2L);
                when(subjectRepository.findByName(any())).thenReturn(Optional.empty());
                assertTrue(subjectCheckService.canCreateSubject(subjectDTO));
            }

            private SubjectDTO createSubject(){
                SubjectDTO subjectDTO = new SubjectDTO();
                subjectDTO.setName("test");
                subjectDTO.setStudents(new ArrayList<>());
                subjectDTO.setTeachers(new ArrayList<>());
                return subjectDTO;
            }
        }

        @Nested
        @DisplayName("Tests for checkCanEditSubject")
        public class CheckCanEditSubjectTests {
            @Test
            public void testCheckCanEditSubjectincorrectId() throws Exception {
                
                when(subjectRepository.findById(any())).thenReturn(Optional.empty());
                assertTrue(subjectCheckService.canEditSubject(1L, null).getStatusCode().is4xxClientError());
            }


            @Test
            public void testCheckCanEditSubjectSameTeachersAndStudents() throws Exception {
                SubjectDTO subjectDTO = createSubject();
                subjectDTO.getStudents().add(1L);
                subjectDTO.getTeachers().add(1L);
                when(subjectRepository.findByName(any())).thenReturn(Optional.empty());
                when(subjectRepository.findById(any())).thenReturn(Optional.of(new Subject()));
                assertTrue(subjectCheckService.canEditSubject(1L, subjectDTO).getStatusCode().is4xxClientError());
            }

            @Test
            public void testCheckCanEditSubjectIncorrectNewName() throws Exception{
                SubjectDTO subjectDTO = createSubject();
                subjectDTO.setName("");
                when(subjectRepository.findByName(any())).thenReturn(Optional.of(new Subject()));
                when(subjectRepository.findById(any())).thenReturn(Optional.of(new Subject()));
                assertTrue(subjectCheckService.canEditSubject(1L, subjectDTO).getStatusCode().is4xxClientError());
            }

            @Test
            public void testCheckCanEditSubjectNewUsedName() throws Exception{
                SubjectDTO subjectDTO = createSubject();
                Subject subject1 = new Subject();
                subject1.setName("test1");
                Subject subject2 = new Subject();
                subject2.setName("test2");
                when(subjectRepository.findByName(any())).thenReturn(Optional.of(subject1));
                when(subjectRepository.findById(any())).thenReturn(Optional.of(subject2));
                assertTrue(subjectCheckService.canEditSubject(1L, subjectDTO).getStatusCode().is4xxClientError());
            }

            @Test
            public void testCheckCanCreateSubjectEmptyUsers() throws Exception {
                SubjectDTO subjectDTO = createSubject();
                subjectDTO.setName("test");
                Subject subject = new Subject();
                subject.setName("test");
                when(subjectRepository.findByName(any())).thenReturn(Optional.of(subject));
                when(subjectRepository.findById(any())).thenReturn(Optional.of(subject));
                assertTrue(subjectCheckService.canEditSubject(1L, subjectDTO).getStatusCode().is2xxSuccessful());
            }

            @Test
            public void testCheckCanCreateSubjectDifferentUsers() throws Exception{
                SubjectDTO subjectDTO = createSubject();
                subjectDTO.setName("test");
                subjectDTO.getStudents().add(1L);
                subjectDTO.getTeachers().add(2L);
                Subject subject = new Subject();
                subject.setName("test");
                when(subjectRepository.findByName(any())).thenReturn(Optional.of(subject));
                when(subjectRepository.findById(any())).thenReturn(Optional.of(subject));
                assertTrue(subjectCheckService.canEditSubject(1L, subjectDTO).getStatusCode().is2xxSuccessful());
            }


            private SubjectDTO createSubject(){
                SubjectDTO subjectDTO = new SubjectDTO();
                subjectDTO.setName("test");
                subjectDTO.setStudents(new ArrayList<>());
                subjectDTO.setTeachers(new ArrayList<>());
                return subjectDTO;
            }
        }


}
