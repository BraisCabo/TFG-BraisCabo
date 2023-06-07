package com.tfg.brais.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.brais.Model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long>{
    
}
