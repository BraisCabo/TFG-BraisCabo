package com.tfg.brais.Model;


import java.util.Date;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ExerciseUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User student;

    private String calification = "";

    private String comment = "";

    private String fileName;

    private boolean isUploaded;

    @ManyToOne
    private Exam exam;

    private Date uploadDate = new Date();

    private List<String> answers;
}
