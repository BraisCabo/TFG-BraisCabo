package com.tfg.brais.E2ETests;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;


public class E2ETests {

    DriverMethodsExecutor driver = new DriverMethodsExecutor();
    
    @AfterSuite
    public void close(){
        driver.close();
    }

    @Test(priority = 1)
    public void RegisterTest(){
        RegisterTest registerTest = new RegisterTest(driver);
        registerTest.Register();
    }

    @Test(priority = 2)
    public void LoginTest(){
        LoginTest loginTest = new LoginTest(driver);
        loginTest.login();
    }

    @Test(priority = 3)
    public void CreateSubjectTest(){
        CreateSubjectTest createSubjectTest = new CreateSubjectTest(driver);
        createSubjectTest.createEmpty();
        createSubjectTest.createEqual();
        createSubjectTest.createSubject();
    }

    @Test(priority = 4)
    public void EditSubjectTest(){
        EditSubjectTest editSubjectTest = new EditSubjectTest(driver);
        editSubjectTest.editSubject();
        editSubjectTest.editSubjectRemoveMembers();
        editSubjectTest.editSubjectIncorrectName();
    }

    @Test(priority = 5)
    public void DeleteSubjectTest(){
        DeleteSubjectTest deleteSubjectTest = new DeleteSubjectTest(driver);
        deleteSubjectTest.deleteSubject();
    }

    @Test(priority = 6)
    public void CreateExamTest(){
        CreateExamTest createExamTest = new CreateExamTest(driver);
        createExamTest.createExamFile();
        createExamTest.createExamFileRepeat();
        createExamTest.createExamQuestions();
    }
    
    
}
