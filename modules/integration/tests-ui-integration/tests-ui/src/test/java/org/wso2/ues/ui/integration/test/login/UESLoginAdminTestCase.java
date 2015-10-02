package org.wso2.ues.ui.integration.test.login;
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
import org.wso2.ues.ui.integration.util.UESWebDriver;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.ues.ui.integration.util.BaseUITestCase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UESLoginAdminTestCase extends BaseUITestCase{
    private static final int MAX_WAIT_TIME = 100;

    @BeforeClass
    public void setUp() throws Exception {
        super.init();
        driver = new UESWebDriver(BrowserManager.getWebDriver());
        baseUrl = getWebAppURL();
        wait = new WebDriverWait(driver, MAX_WAIT_TIME);
    }

    @Test(groups = "wso2.ues", description = "verify login logout to UES Server")
    public void testLoginTestcaseUES() throws Exception {
        driver.get(baseUrl + "/portal/login?destination=%2Fportal%2F");
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("admin");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        driver.findElement(By.linkText("admin")).click();
        driver.findElement(By.linkText("Logout")).click();
    }

//    @Test(groups = "wso2.ues", description = "verify logout to UES Server")
//    public void testLoginTestcaseUES() throws Exception {
//
//    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }

}
