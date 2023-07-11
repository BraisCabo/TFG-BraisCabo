package com.tfg.brais.E2ETests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

public class CreateExamTest {

    private DriverMethodsExecutor driver;

    public CreateExamTest(DriverMethodsExecutor driver) {
        this.driver = driver;
    }

    public void createExamFile() {
        driver.login("test@gmail.com", "12345678");
        assertEquals(driver.getNumberOfElements("teachedSubjects", "mat-card"), 1);
        driver.clickCard("teachedSubjects", 0);
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/subject/2");
        driver.clickButton("createExamButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/subject/2/newExam");
        assertFalse(driver.isEnableButton("createExamButton"));
        driver.changeInput("examName", "TestExamFile");
        driver.changeInput("examPercentaje", "50");
        driver.clickButton("visibleExam");
        driver.clickButton("visibleCalification");
        driver.clickButton("unlimitedAttemps");
        driver.clickButton("lateUploads");
        LocalDate localDate = LocalDate.now();
        localDate.plusDays(1);
        driver.clearInput("closingDate");
        driver.changeInput("closingDate",
                localDate.getMonthValue() + "/" + (localDate.getDayOfMonth() + 1) + "/" + localDate.getYear());
        assertTrue(driver.isEnableButton("createExamButton"));
        driver.changeInput("examPercentaje", "-1");
        assertFalse(driver.isEnableButton("createExamButton"));
        driver.changeInput("examPercentaje", "101");
        assertFalse(driver.isEnableButton("createExamButton"));
        driver.changeInput("examPercentaje", "50");
        driver.clickButton("createExamButton");
        driver.clickButton("confirmButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/subject/2");
    }

    public void createExamFileRepeat() {
        driver.login("test@gmail.com", "12345678");
        driver.waitTime();
        driver.clickCard("teachedSubjects", 0);
        driver.waitTime();
        driver.clickButton("createExamButton");
        driver.waitTime();
        driver.changeInput("examName", "TestExamFile");
        driver.changeInput("examPercentaje", "50");
        driver.clickButton("visibleExam");
        driver.clickButton("visibleCalification");
        driver.clickButton("unlimitedAttemps");
        driver.clickButton("lateUploads");
        LocalDate localDate = LocalDate.now();
        localDate.plusDays(1);
        driver.clearInput("closingDate");
        driver.changeInput("closingDate",
                localDate.getMonthValue() + "/" + (localDate.getDayOfMonth() + 1) + "/" + localDate.getYear());
        driver.clickButton("createExamButton");
        driver.clickButton("confirmButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/subject/2/newExam");
    }

    public void createExamQuestions() {
        driver.login("test@gmail.com", "12345678");
        driver.waitTime();
        driver.clickCard("teachedSubjects", 0);
        driver.waitTime();
        driver.clickButton("createExamButton");
        driver.waitTime();
        driver.changeInput("examName", "TestExamQuestions");
        driver.changeInput("examPercentaje", "50");
        driver.clickButton("visibleExam");
        driver.clickButton("lateUploads");
        driver.clickButton("typeSelector");
        driver.clickButton("questions");
        LocalDate localDate = LocalDate.now();
        localDate.plusDays(1);
        driver.clearInput("closingDate");
        driver.changeInput("closingDate",
                localDate.getMonthValue() + "/" + (localDate.getDayOfMonth() + 1) + "/" + localDate.getYear());
        assertFalse(driver.isEnableButton("createExamButton"));
        driver.clickButton("addQuestion");
        driver.clickButton("addQuestion");
        driver.waitTime();
        driver.writeOnElement("questionsList", "Pregunta Test 1", "textQuestion", 0);
        driver.writeOnElement("questionsList", "Pregunta Test 2", "textQuestion", 1);
        driver.writeOnElement("questionsList", "10", "calificationQuestion", 0);
        driver.writeOnElement("questionsList", "5", "calificationQuestion", 1);
        driver.clickButton("createExamButton");
        driver.clickButton("confirmButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/subject/2");
    }
}
