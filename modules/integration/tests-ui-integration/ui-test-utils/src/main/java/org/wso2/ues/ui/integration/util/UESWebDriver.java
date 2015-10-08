package org.wso2.ues.ui.integration.util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;

public class UESWebDriver implements WebDriver {
    private static int maxWaitTime;
    private WebDriver driver = null;

    public UESWebDriver(WebDriver webDriver, int maxWaitTime) {
        this.driver = webDriver;
        UESWebDriver.maxWaitTime = maxWaitTime;
    }

    /**
     * This method checks whether a given element is present in the page
     *
     * @param by Element to be present in the page
     * @return true if element is present false otherwise
     */
    public boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
            //coding by exception since driver doesn't support a check
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * This method will wait until a given element is present in the page for a given amount of time
     *
     * @param by          Element to be present in the current page
     * @param waitTimeSec Time to wait in seconds
     */
    private void waitTillElementPresent(By by, int waitTimeSec) {
        WebDriverWait wait = new WebDriverWait(driver, waitTimeSec);
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    /**
     * This method has override the findElement method in a way it will wait for maximum of 30 seconds
     *
     * @param by By element for findElement method
     * @return return the result of default WebDriver.findElement(By by) subjected to 30sec of max wait time
     */
    @Override
    public WebElement findElement(By by) {
        waitTillElementPresent(by, maxWaitTime);
        return driver.findElement(by);
    }

    // proxying to the WebDriver
    @Override
    public void get(String s) {
        driver.get(s);
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }

}

