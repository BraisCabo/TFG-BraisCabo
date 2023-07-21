package com.tfg.brais.E2ETests;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverMethodsExecutor {
    private WebDriver driver;

    private String baseURL = "http://localhost:8443";

    public DriverMethodsExecutor() {
        startActions();
        driver.get(baseURL);
    }

    private void startActions() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
    }

    public void startLocal() {
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\brais\\Downloads\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    public void clickButton(String buttonId) {
        WebElement findElement = driver.findElement(By.id(buttonId));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", findElement);
    }

    public String getCurrentUrl() {
        waitTime();
        return driver.getCurrentUrl();
    }

    public boolean isEnableButton(String buttonId) {
        return driver.findElement(By.id(buttonId)).isEnabled();
    }

    public void changeInput(String inputId, String value) {
        driver.findElement(By.id(inputId)).clear();
        driver.findElement(By.id(inputId)).sendKeys(value);
    }

    public void clearInput(String inputId) {
        driver.findElement(By.id(inputId)).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
    }

    public void navigateTo(String url) {
        driver.get(baseURL + url);
    }

    public String getTextInfo(String infoId) {
        return driver.findElement(By.id(infoId)).getText();
    }

    public void waitTime() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        driver.close();
    }

    public String getUrlNoWait() {
        return driver.getCurrentUrl();
    }

    public int getNumberOfElements(String elementId, String tagName) {
        WebElement findElements = driver.findElement(By.id(elementId));
        List<WebElement> elements = findElements.findElements(By.tagName(tagName));
        return elements.size();
    }

    public void selectElementOfList(String elementId, String tagName, String buttonId, int index) {
        WebElement findElements = driver.findElement(By.id(elementId));
        List<WebElement> elements = findElements.findElements(By.tagName(tagName));
        elements.get(index).findElement(By.id(buttonId)).click();
    }

    public int getNumberOfElementsWithId(String elementId) {
        return driver.findElements(By.id(elementId)).size();

    }

    public void selectElementWithId(String elementId, String buttonId, int index) {
        List<WebElement> elements = driver.findElements(By.id(elementId));
        elements.get(index).findElement(By.id(buttonId)).click();
    }

    public String getElementWithIdText(String elementId, String textId, int index) {
        List<WebElement> elements = driver.findElements(By.id(elementId));
        return elements.get(index).findElement(By.id(textId)).getText();
    }

    public void login(String email, String password) {
        navigateTo("/login");
        changeInput("email", email);
        changeInput("password", password);
        clickButton("loginButton");
        waitTime();
    }

    public void clickCard(String elementId, int index) {
        WebElement findElements = driver.findElement(By.id(elementId));
        List<WebElement> elements = findElements.findElements(By.tagName("mat-card"));
        elements.get(index).click();
    }

    public void writeOnElement(String elementId, String text, String textId, int index) {
        List<WebElement> elements = driver.findElements(By.id(elementId));
        elements.get(index).findElement(By.id(textId)).clear();
        elements.get(index).findElement(By.id(textId)).sendKeys(text);
        ;
    }

    public String getBaseURL() {
        return this.baseURL;
    }
}
