package com.tfg.brais.E2ETests;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteSubjectTest {
    
    private DriverMethodsExecutor driver;

    public DeleteSubjectTest(DriverMethodsExecutor driver){
        this.driver = driver;
    }

    public void deleteSubject() {
        driver.login("admin", "admin");
        assertEquals(driver.getNumberOfElementsWithId("subjectsList"), 2);
        driver.selectElementWithId("subjectsList", "deleteButton", 0);
        driver.clickButton("cancelButton");
        assertEquals(driver.getNumberOfElementsWithId("subjectsList"), 2);
        driver.selectElementWithId("subjectsList", "deleteButton", 0);
        driver.clickButton("confirmButton");
        driver.waitTime();
        assertEquals(driver.getNumberOfElementsWithId("subjectsList"), 1);
        assertEquals(driver.getElementWithIdText("subjectsList", "subjectName", 0), "SubjectTest2");
    }
}
