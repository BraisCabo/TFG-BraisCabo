package com.tfg.brais.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.tfg.brais.Model.DTOS.ExamBasicDTO;
import com.tfg.brais.Model.DTOS.ExamTeacherDTO;
import com.tfg.brais.Service.ControllerServices.ExamService;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/subjects/{id}/exams")
@RestController
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping("/")
    public ResponseEntity<ExamTeacherDTO> createExam(@PathVariable long id, @RequestBody ExamTeacherDTO exam, HttpServletRequest request){
        return this.examService.createExam(id, exam, request.getUserPrincipal(), fromCurrentRequest().path("/{id}"));
    }

    @GetMapping("/")
    public ResponseEntity<List<ExamBasicDTO>> getExams(@PathVariable long id, HttpServletRequest request){
        return this.examService.findAllExamsBySubjectId(id, request.getUserPrincipal());
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamBasicDTO> getExam(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return this.examService.findBySubjectIdAndId(id, examId, request.getUserPrincipal());
    }

    @GetMapping("/{examId}/questions")
    public ResponseEntity<List<String>> getQuestions(@PathVariable long id, @PathVariable long examId, HttpServletRequest request){
        return this.examService.getExamQuestions(id, examId, request.getUserPrincipal());
    }

    @PutMapping("/{examId}")
    public ResponseEntity<ExamTeacherDTO> updateExam(@PathVariable long id, @PathVariable long examId, @RequestBody ExamTeacherDTO exam, HttpServletRequest request){
        return this.examService.updateExam(id, examId, exam, request.getUserPrincipal());
    }

    @PatchMapping("/{examId}")
    public ResponseEntity<ExamTeacherDTO> changeVisibility(@PathVariable long id, @PathVariable long examId, @RequestBody Boolean newVisibility, HttpServletRequest request){
        return this.examService.changeVisibility(id, examId, newVisibility, request.getUserPrincipal());
    }
}
