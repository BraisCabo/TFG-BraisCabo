package com.tfg.brais.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String type = "UPLOAD";

    private String calificationPercentaje = "0";

    @ManyToOne
    @JsonIgnore
    private Subject subject;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseUpload> exerciseUploads = new ArrayList<>();

    private boolean visibleExam = true;

    private boolean calificationVisible = true;

    private LocalDateTime openingDate = LocalDateTime.now();

    private LocalDateTime closingDate = LocalDateTime.now();

    private List<String> questions = new ArrayList<>();

    public void update(Exam exam) {
        this.name = exam.getName();
        this.calificationPercentaje = exam.getCalificationPercentaje();
        this.visibleExam = exam.isVisibleExam();
        this.calificationVisible = exam.isCalificationVisible();
        this.openingDate = exam.getOpeningDate();
        this.closingDate = exam.getClosingDate();
    }


}
