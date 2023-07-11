package com.tfg.brais.E2ETests;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {

    private DriverMethodsExecutor driver;

    public LoginTest(DriverMethodsExecutor driver){
        this.driver = driver;
    }

    public void login(){
        driver.navigateTo("/login");
        driver.changeInput("email", "nocorrectemail@gmail.com");
        driver.clickButton("loginButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/login");
        driver.clickButton("okButton");
        driver.changeInput("email", "test@gmail.com");
        driver.changeInput("password", "123456789");
        driver.clickButton("loginButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/login");
        driver.clickButton("okButton");
        driver.changeInput("password", "12345678");
        driver.clickButton("loginButton");
        assertEquals(driver.getCurrentUrl(), driver.getBaseURL() + "/");
        assertEquals(driver.getTextInfo("headerName"), "NameTest");
    }
}
