package com.tfg.brais.E2ETests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateSubjectTest {
    
    private static DriverMethodsExecutor driver = new DriverMethodsExecutor();

    @AfterAll
    public static void close(){
        driver.close();
    }

    @Test
    @Order(3)
    public void createEmpty(){
        driver.login("admin", "admin");
        driver.clickButton("newSubjectButton");
        assertEquals(driver.getCurrentUrl(), "http://localhost:4200/newSubject");
        assertFalse(driver.isEnableButton("createSubjectButton"));
        driver.changeInput("subjectName", "SubjectTest1");
        assertTrue(driver.isEnableButton("createSubjectButton"));
        driver.clickButton("createSubjectButton");
        driver.clickButton("cancelButton");
        assertEquals(driver.getUrlNoWait(), "http://localhost:4200/newSubject");
        driver.clickButton("createSubjectButton");
        driver.clickButton("confirmButton");
        assertEquals(driver.getCurrentUrl(), "http://localhost:4200/");
    }

    @Test
    @Order(4)
    public void createEqual(){
        driver.login("admin", "admin");
        driver.clickButton("newSubjectButton");
        driver.waitTime();
        driver.changeInput("subjectName", "SubjectTest1");
        driver.clickButton("createSubjectButton");
        driver.clickButton("confirmButton");
        assertEquals(driver.getCurrentUrl(), "http://localhost:4200/newSubject");
    }

    @Test
    @Order(5)
    public void createSubject(){
        driver.login("admin", "admin");
        driver.clickButton("newSubjectButton");
        assertEquals(driver.getCurrentUrl(), "http://localhost:4200/newSubject");
        assertFalse(driver.isEnableButton("createSubjectButton"));
        driver.changeInput("subjectName", "SubjectTest2");
        assertTrue(driver.isEnableButton("createSubjectButton"));
        driver.changeInput("teachersName", "NameTest");
        driver.clickButton("searchTeachersButton");
        driver.waitTime();
        assertEquals(driver.getNumberOfElements("teachersList", "mat-list-item"), 2);
        driver.selectElementOfList("teachersList", "mat-list-item", "checkbox", 0);
        driver.changeInput("studentsName", "NameTest2");
        driver.clickButton("searchStudentsButton");
        driver.waitTime();
        assertEquals(driver.getNumberOfElements("studentsList", "mat-list-item"), 1);
        driver.selectElementOfList("studentsList", "mat-list-item", "checkbox", 0);
        driver.clickButton("createSubjectButton");
        driver.clickButton("confirmButton");
        assertEquals(driver.getCurrentUrl(), "http://localhost:4200/");
    }
}
