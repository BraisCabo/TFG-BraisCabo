package com.tfg.brais.ControllerServicesTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.SubjectChangesDTO;
import com.tfg.brais.Model.DTOS.SubjectDetailedDTO;
import com.tfg.brais.Repository.SubjectRepository;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ControllerServices.AdminService;

public class AdminServiceTest {
    
    private SubjectRepository subjectRepository;

    private UserRepository userRepository;

    private SubjectCheckService subjectCheckService;

    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        this.subjectRepository = Mockito.mock(SubjectRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.subjectCheckService = Mockito.mock(SubjectCheckService.class);
        this.adminService = new AdminService(subjectRepository, userRepository, subjectCheckService);
    }

    @Nested
    @DisplayName("Test createSubject")
    public class CreateSubjectTests {
        @Test
        public void testCreateSubjectCantCreate() {
            when(subjectCheckService.canCreateSubject(any())).thenReturn(false);
            assertTrue(adminService.createSubject(new SubjectChangesDTO(), null).getStatusCode().is4xxClientError());
        }

        @Test
        public void testCreateSubjectCorrect() {
            when(subjectCheckService.canCreateSubject(any())).thenReturn(true);
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
            assertTrue(adminService.createSubject(new SubjectChangesDTO(), uriComponentsBuilder).getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("Test deleteById")
    public class DeleteByIdTests {
        @Test
        public void testDeleteByIdCantDelete() {
            when(subjectCheckService.findById(anyLong())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(404)));
            assertTrue(adminService.deleteById(1L).getStatusCode().is4xxClientError());
        }

        @Test
        public void testDeleteByIdCorrect() {
            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(new Subject()));
            assertTrue(adminService.deleteById(1L).getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("Test updateSubject")
    public class UpdateSubjectTests {
        @Test
        public void testUpdateSubjectCantUpdate() {
            when(subjectCheckService.canEditSubject(anyLong(), any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(404)));
            assertTrue(adminService.editSubject(1L, new SubjectChangesDTO()).getStatusCode().is4xxClientError());
        }

        @Test
        public void testUpdateSubjectCorrect() {
            SubjectChangesDTO newSubject = new SubjectChangesDTO();
            User u1 = new User();
            u1.setId(1L);
            User u2 = new User();
            u2.setId(2L);
            newSubject.setName("test1");
            newSubject.getTeachers().add(1L);
            newSubject.getTeachers().add(2L);
            Subject oldSubject = new Subject();
            oldSubject.setName("test2");
            oldSubject.getTeachers().add(u1);
            oldSubject.getStudents().add(u2);
            List<User> users = new ArrayList<>();
            users.add(u1);
            users.add(u2);
            when(userRepository.findAllById(newSubject.getTeachers())).thenReturn(users);
            when(userRepository.findAllById(newSubject.getStudents())).thenReturn(new ArrayList<>());
            when(subjectCheckService.canEditSubject(anyLong(), any())).thenReturn(ResponseEntity.ok(oldSubject));

            ResponseEntity<SubjectDetailedDTO> editSubject = adminService.editSubject(1L, newSubject);
            assertTrue(editSubject.getStatusCode().is2xxSuccessful());
            assertTrue(editSubject.getBody().getName().equals("test1"));
            assertTrue(editSubject.getBody().getTeachers().size() == 2);
            assertTrue(editSubject.getBody().getStudents().size() == 0);
        }
    }



}
