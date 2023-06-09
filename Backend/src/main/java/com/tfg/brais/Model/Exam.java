package com.tfg.brais.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String calification;

    private String lastCalificationPercentaje;

    @ManyToOne
    private Subject subject;

    @ManyToOne
    private User user;

    private boolean isVisible;

    private boolean calificationVisible;

    private LocalDateTime openingDate;

    private LocalDateTime closingDate;
}
