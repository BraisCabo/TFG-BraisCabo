package com.tfg.brais.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.brais.Model.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    
}