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
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.ues.ui.integration.util.BaseUITestCase;
import org.wso2.ues.ui.integration.util.UESUtil;
import org.wso2.ues.ui.integration.util.UESWebDriver;

import static org.testng.Assert.assertEquals;

public class UESAddUserTestCase extends BaseUITestCase{
    private static final int MAX_WAIT_TIME = 30;
    private static final String USER_NAME = "sampleuser1";
    private static final String PASSWORD = "sampleuser1";
    private static final String RETYPE_PASSWORD = "sampleuser1";

    @BeforeClass
    public void setUp() throws Exception {
        super.init();
        driver = new UESWebDriver(BrowserManager.getWebDriver());
        baseUrl = getWebAppURL();
        wait = new WebDriverWait(driver, MAX_WAIT_TIME);
        AutomationContext automationContext = new AutomationContext(PRODUCT_GROUP_NAME, TestUserMode.SUPER_TENANT_ADMIN);

        adminUserName = automationContext.getSuperTenant().getTenantAdmin().getUserName();
        adminUserPwd = automationContext.getSuperTenant().getTenantAdmin().getPassword();
        UESUtil.loginToAdminConsole(driver,baseUrl,adminUserName,adminUserPwd);
    }

    @Test(groups = "wso2.ues.common", description = "Adding user to User Engagement Server")
    public void testAddUserToUES() throws Exception {
        driver.findElement(By.linkText("Users and Roles")).click();
        driver.findElement(By.linkText("Users")).click();
        driver.findElement(By.linkText("Add New User")).click();
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys(USER_NAME);

        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys(PASSWORD);


        driver.findElement(By.name("retype")).clear();
        driver.findElement(By.name("retype")).sendKeys(RETYPE_PASSWORD);

        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.cssSelector("td.buttonRow > input.button")).click();
        driver.findElement(By.cssSelector("button[type=\"button\"]")).click();
        driver.findElement(By.linkText("Sign-out")).click();
        UESUtil.login(driver,baseUrl,USER_NAME,RETYPE_PASSWORD);

        assertEquals(USER_NAME, driver.findElement(By.xpath("//nav[1]/div/div[2]/ul/li/a")).getText());

    }

    @AfterClass
    public void tearDown() throws Exception {
        UESUtil.logout(driver,baseUrl,USER_NAME);
        driver.quit();
    }

}