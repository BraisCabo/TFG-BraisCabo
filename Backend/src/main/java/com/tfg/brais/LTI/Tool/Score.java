package com.tfg.brais.LTI.Tool;

import lombok.Data;

@Data
public class Score {
    private String userId;
    private double scoreGiven;
    private double scoreMaximum;
    private String comment;
    private String timestamp;
    private String activityProgress;
    private String gradingProgress;
}
