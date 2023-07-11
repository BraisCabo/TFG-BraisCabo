package com.tfg.brais.E2ETests;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {

    private DriverMethodsExecutor driver;

    public LoginTest(DriverMethodsExecutor driver){
        this.driver = driver;
    }

    public void login(){
        driver.navigateTo("http://localhost:4200/login");
        driver.changeInput("email", "nocorrectemail@gmail.com");
        driver.clickButton("loginButton");
        assertEquals(driver.getCurrentUrl(), "http://localhost:4200/login");
        driver.clickButton("okButton");
        driver.changeInput("email", "test@gmail.com");
        driver.changeInput("password", "123456789");
        driver.clickButton("loginButton");
        assertEquals(driver.getCurrentUrl(), "http://localhost:4200/login");
        driver.clickButton("okButton");
        driver.changeInput("password", "12345678");
        driver.clickButton("loginButton");
        assertEquals(driver.getCurrentUrl(), "http://localhost:4200/");
        assertEquals(driver.getTextInfo("headerName"), "NameTest");
    }
}
