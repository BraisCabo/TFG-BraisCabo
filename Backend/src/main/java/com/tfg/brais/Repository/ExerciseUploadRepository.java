package com.tfg.brais.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.brais.Model.ExerciseUpload;

public interface ExerciseUploadRepository extends JpaRepository<ExerciseUpload, Long>  {
    
}
