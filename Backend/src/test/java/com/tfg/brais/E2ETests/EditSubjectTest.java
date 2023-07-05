package com.tfg.brais.E2ETests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EditSubjectTest {

    private static DriverMethodsExecutor driver = new DriverMethodsExecutor();

    @AfterAll
    public static void close(){
        driver.close();
    }

    @Test
    @Order(6)
    public void editSubject(){
        driver.login("admin", "admin");
        assertEquals(driver.getNumberOfElementsWithId("subjectsList"), 2);
        driver.selectElementWithId("subjectsList", "editButton", 0);
        driver.waitTime();
        driver.changeInput("editSubjectName", "SubjectTest3");
        driver.selectElementOfList("teachersList", "mat-list-item", "checkbox", 1);
        driver.selectElementOfList("studentsList", "mat-list-item", "checkbox", 2);
        driver.clickButton("editSubjectButton");
        driver.clickButton("confirmButton");
        driver.waitTime();
        assertEquals(driver.getNumberOfElementsWithId("subjectsList"), 2);
        assertEquals(driver.getElementWithIdText("subjectsList", "subjectName", 0), "SubjectTest3");
        assertEquals(driver.getElementWithIdText("subjectsList", "numberOfStudents", 0), "Hay 1 estudiante asignado.");
    }

        @Test
    @Order(7)
    public void editSubjectRemoveMembers(){
        driver.login("admin", "admin");
        driver.selectElementWithId("subjectsList", "editButton", 0);
        driver.waitTime();
        driver.selectElementOfList("teachersList", "mat-list-item", "checkbox", 1);
        driver.selectElementOfList("studentsList", "mat-list-item", "checkbox", 2);
        driver.clickButton("editSubjectButton");
        driver.clickButton("confirmButton");
        driver.waitTime();
        assertEquals(driver.getNumberOfElementsWithId("subjectsList"), 2);
        assertEquals(driver.getElementWithIdText("subjectsList", "subjectName", 0), "SubjectTest3");
        assertEquals(driver.getElementWithIdText("subjectsList", "numberOfStudents", 0), "No hay estudiantes asignados");
    }

    @Test
    @Order(8)
    public void editSubjectIncorrectName(){
        driver.login("admin", "admin");
        assertEquals(driver.getNumberOfElementsWithId("subjectsList"), 2);
        driver.selectElementWithId("subjectsList", "editButton", 0);
        driver.waitTime();
        driver.changeInput("editSubjectName", "SubjectTest2");
        driver.clickButton("editSubjectButton");
        driver.clickButton("confirmButton");
        driver.waitTime();
        driver.clickButton("closeEdit");
        assertEquals(driver.getElementWithIdText("subjectsList", "subjectName", 0), "SubjectTest3");
    }
}
