package com.tfg.brais.E2ETests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterTest {

    private DriverMethodsExecutor driver;

    public RegisterTest(DriverMethodsExecutor driver){
        this.driver = driver;
    }

    public void Register() {
        driver.navigateTo("/register");
        assertFalse(driver.isEnableButton("registerButton"));
        driver.changeInput("name", "NameTest");
        assertFalse(driver.isEnableButton("registerButton"));
        driver.changeInput("lastName", "LastNameTest");
        assertFalse(driver.isEnableButton("registerButton"));
        driver.changeInput("email", "test@gmail.com");
        assertFalse(driver.isEnableButton("registerButton"));
        driver.changeInput("password", "12345678");
        assertFalse(driver.isEnableButton("registerButton"));
        driver.changeInput("repeatPassword", "1234567");
        assertFalse(driver.isEnableButton("registerButton"));
        driver.changeInput("repeatPassword", "12345678");
        assertTrue(driver.isEnableButton("registerButton"));
        driver.changeInput("email", "testgmail");
        assertFalse(driver.isEnableButton("registerButton"));
        driver.changeInput("email", "test@gmail.com");
        assertTrue(driver.isEnableButton("registerButton"));
        driver.clickButton("registerButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/");
        driver.navigateTo("/register");
        driver.changeInput("name", "NameTest2");
        driver.changeInput("lastName", "LastNameTest2");
        driver.changeInput("email", "test@gmail.com");
        driver.changeInput("password", "12345678");
        driver.changeInput("repeatPassword", "12345678");
        driver.clickButton("registerButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/register");
        driver.changeInput("email", "test2@gmail.com");
        driver.clickButton("registerButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/");
    }

}
