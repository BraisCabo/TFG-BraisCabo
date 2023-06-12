package com.tfg.brais.Repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);

    @Query(value = "SELECT COUNT(*) FROM subject_students WHERE subject_id = ?1 AND students_id = ?2", nativeQuery = true)
    int countBySubjectIdAndStudentId(Long subjectId, Long studentId);

    @Query(value = "SELECT COUNT(*) FROM subject_teachers WHERE subject_id = ?1 AND teachers_id = ?2", nativeQuery = true)
    int countBySubjectIdAndTeacherId(Long subjectId, Long teacherId);

    @Query("SELECT u FROM Subject s JOIN s.teachers u WHERE s.id = ?1 AND LOWER(CONCAT(u.name, ' ', u.lastName)) LIKE %?2%")
    Page<User> findAllTeachersBySubjectIdAndName(Long subjectId, String name, Pageable pageable);

    
    @Query("SELECT u FROM Subject s JOIN s.students u WHERE s.id = ?1 AND LOWER(CONCAT(u.name, ' ', u.lastName)) LIKE %?2%")
    Page<User> findAllStudentBySubjectIdAndName(Long subjectId, String name, Pageable pageable);

    @Query("SELECT s FROM Subject s WHERE LOWER(s.name) LIKE %?1%")
    Page<Subject> findAllByName(String name, Pageable pageable);

    @Query("SELECT s FROM Subject s JOIN s.teachers u WHERE u.id = ?1")
    List<Subject> findAllTeachedSubjects(Long teacherId);

    @Query("SELECT s FROM Subject s JOIN s.students u WHERE u.id = ?1")
    List<Subject> findAllStudiedSubjects(Long studentID);
}
