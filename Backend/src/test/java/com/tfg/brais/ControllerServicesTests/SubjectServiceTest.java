package com.tfg.brais.ControllerServicesTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Repository.SubjectRepository;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ControllerServices.SubjectService;

public class SubjectServiceTest {

    private SubjectRepository subjectRepository;

    private SubjectCheckService subjectCheckService;

    private SubjectService subjectService;

    @BeforeEach
    public void setUp() {
        subjectRepository = Mockito.mock(SubjectRepository.class);
        subjectCheckService = Mockito.mock(SubjectCheckService.class);
        subjectService = new SubjectService(subjectRepository, subjectCheckService);
    }

    @Nested
    @DisplayName("Test findByAll")
    public class FindAllTests {
        @Test
        public void testFindAllEmpty() {
            List<Subject> subjects = new ArrayList<Subject>();
            when(subjectRepository.findAllByName(anyString(), any())).thenReturn(new PageImpl<>(subjects));
            assertTrue(subjectService.findAll("name", null).getStatusCode().is4xxClientError());
        }

        @Test
        public void testFindAllCorrect() {
            List<Subject> subjects = new ArrayList<Subject>();
            subjects.add(new Subject());
            when(subjectRepository.findAllByName(anyString(), any())).thenReturn(new PageImpl<>(subjects));
            assertTrue(subjectService.findAll("name", null).getStatusCode().is2xxSuccessful());
        }
    }

    @Nested
    @DisplayName("Test findById")
    public class FindByIdTests {
        @Test
        public void testFindByIdEmpty() {
            when(subjectCheckService.findById(anyLong())).thenReturn(new ResponseEntity<Subject>(HttpStatusCode.valueOf(404)));
            assertTrue(subjectService.findById(1).getStatusCode().is4xxClientError());
        }

        @Test
        public void testFindByIdCorrect() {
            Subject subject = new Subject();
            subject.setName("test");

            when(subjectCheckService.findById(anyLong())).thenReturn(ResponseEntity.ok(subject));
            assertTrue(subjectService.findById(1).getStatusCode().is2xxSuccessful());
            assertEquals(subjectService.findById(1).getBody().getName(), "test");
        }
    }

}
