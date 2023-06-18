package com.tfg.brais.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tfg.brais.Model.ExerciseUpload;

public interface ExerciseUploadRepository extends JpaRepository<ExerciseUpload, Long>  {

    Optional<ExerciseUpload> findByIdAndExamIdAndExamSubjectId(long id, long examId, long subjectId);

    Optional<ExerciseUpload> findByStudentIdAndExamIdAndExamSubjectId(long studentId, long examId, long subjectId);
    
    @Query("SELECT e FROM ExerciseUpload e WHERE e.exam.id = ?1 AND e.exam.subject.id = ?2")
    List<ExerciseUpload> findAllByExamIdAndExamSubjectId(long examId, long subjectId);
}
