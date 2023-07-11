package com.tfg.brais.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

    private List<String> questionsCalification;

    private String comment = "";

    @JsonIgnore
    private String fileName;

    private boolean isUploaded;

    private Date startedDate;

    @ManyToOne
    private Exam exam;

    private Date uploadDate = new Date();

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> answers;

    public void deleteUpload() {
        setCalification("");
        setComment("");
        setUploadDate(null);
        setAnswers(null);
        setFileName(null);
        this.isUploaded = false;
        this.startedDate = null;
    }

    public int calculateTimeDifference(){
        return (int) ((exam.getMaxTime() * 60 * 1000 + 3000 + startedDate.getTime() - new Date().getTime()) / 1000);
    }

    public void importUpload(User student, Exam exam, List<String> answers, String uploadDate, String startedDate, String calification) throws ParseException{
        this.isUploaded = true;
        this.student = student;
        this.exam = exam;
        this.answers = answers;
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy HH:mm");
        this.uploadDate =  sdf.parse(uploadDate);
        this.startedDate = sdf.parse(startedDate);
        if (!calification.equals("-")){
            this.calification = calification;
        }
    }
}
