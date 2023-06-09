package com.tfg.brais.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tfg.brais.Model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);

    @Query(value = "SELECT COUNT(*) FROM subject_students WHERE subject_id = ?1 AND students_id = ?2", nativeQuery = true)
    int countBySubjectIdAndStudentId(Long subjectId, Long studentId);

    @Query(value = "SELECT COUNT(*) FROM subject_teachers WHERE subject_id = ?1 AND teachers_id = ?2", nativeQuery = true)
    int countBySubjectIdAndTeacherId(Long subjectId, Long teacherId);
}
