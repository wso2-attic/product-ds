package org.wso2.ues.ui.integration.test.common;
/*
*Copyright (c) 2015​, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.ues.ui.integration.util.BaseUITestCase;
import org.wso2.ues.ui.integration.util.UESWebDriver;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AddUserToUES extends BaseUITestCase{
    private static final int MAX_WAIT_TIME = 30;

    @BeforeClass
    public void setUp() throws Exception {
        super.init();
        driver = new UESWebDriver(BrowserManager.getWebDriver());
        baseUrl = getWebAppURL();
        wait = new WebDriverWait(driver, MAX_WAIT_TIME);
    }

    @Test(groups = "wso2.ues", description = "")
    public void testAddUserToUES() throws Exception {
        driver.get(baseUrl + "/carbon/admin/login.jsp");
        driver.findElement(By.id("txtUserName")).clear();
        driver.findElement(By.id("txtUserName")).sendKeys("admin");
        driver.findElement(By.id("txtPassword")).clear();
        driver.findElement(By.id("txtPassword")).sendKeys("admin");
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.linkText("Users and Roles")).click();
        driver.findElement(By.linkText("Users")).click();
        driver.findElement(By.linkText("Add New User")).click();
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys("testuser");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("testuser");
        driver.findElement(By.name("retype")).clear();
        driver.findElement(By.name("retype")).sendKeys("testuser");
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.cssSelector("td.buttonRow > input.button")).click();
        driver.findElement(By.cssSelector("button[type=\"button\"]")).click();
        driver.findElement(By.linkText("Sign-out")).click();
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys("testuser");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("testuser");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        driver.findElement(By.cssSelector("span.caret")).click();
        driver.findElement(By.linkText("Logout")).click();
    }

    @AfterClass
    public void tearDown() throws Exception {
        driver.quit();
    }

}