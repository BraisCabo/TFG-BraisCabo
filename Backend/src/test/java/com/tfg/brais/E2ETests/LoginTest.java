package com.tfg.brais.E2ETests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    private static DriverMethodsExecutor driver = new DriverMethodsExecutor();

    @AfterAll
    public static void close(){
        driver.close();
    }

    @Test
    @Order(2)
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
