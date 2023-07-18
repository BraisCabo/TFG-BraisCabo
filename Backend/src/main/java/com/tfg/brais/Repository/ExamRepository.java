package com.tfg.brais.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tfg.brais.Model.Exam;


public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    Optional<Exam> findByIdAndSubjectId(long id, long subjectId);

    @Query("SELECT e FROM Exam e WHERE LOWER(e.name) = ?1 AND e.subject.id = ?2")
    Optional<Exam> findByNameAndSubjectId(String name, long subjectId);

    List<Exam> findAllBySubjectId(long subjectId);

    @Query("SELECT DISTINCT e FROM Exam e WHERE e.subject.id = ?1 AND e.visibleExam = true")
    List<Exam> findAllBySubjectIdAndVisible(long subjectId);
}
