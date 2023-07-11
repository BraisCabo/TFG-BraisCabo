package com.tfg.brais.E2ETests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class E2ETests {

    private static DriverMethodsExecutor driver;
    
    @AfterAll
    public static void close(){
        driver.close();
    }

    @BeforeAll
    public static void setUP(){
        driver = new DriverMethodsExecutor();
    }

    @Test
    @Order(1)
    public void RegisterTest(){
        RegisterTest registerTest = new RegisterTest(driver);
        registerTest.Register();
    }

    @Test
    @Order(2)
    public void LoginTest(){
        LoginTest loginTest = new LoginTest(driver);
        loginTest.login();
    }

    @Test
    @Order(3)
    public void CreateSubjectTest(){
        CreateSubjectTest createSubjectTest = new CreateSubjectTest(driver);
        createSubjectTest.createEmpty();
        createSubjectTest.createEqual();
        createSubjectTest.createSubject();
    }

    @Test
    @Order(4)
    public void EditSubjectTest(){
        EditSubjectTest editSubjectTest = new EditSubjectTest(driver);
        editSubjectTest.editSubject();
        editSubjectTest.editSubjectRemoveMembers();
        editSubjectTest.editSubjectIncorrectName();
    }

    @Test
    @Order(5)
    public void DeleteSubjectTest(){
        DeleteSubjectTest deleteSubjectTest = new DeleteSubjectTest(driver);
        deleteSubjectTest.deleteSubject();
    }

    @Test
    @Order(6)
    public void CreateExamTest(){
        CreateExamTest createExamTest = new CreateExamTest(driver);
        createExamTest.createExamFile();
        createExamTest.createExamFileRepeat();
        createExamTest.createExamQuestions();
    }
}
