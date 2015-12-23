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

package org.wso2.ds.ui.integration.test.dashboard;

import ds.integration.tests.common.domain.DSIntegrationTestConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class AddDeleteDashboardTest extends DSUIIntegrationTest {

    private static final Log LOG = LogFactory.getLog(AddDeleteDashboardTest.class);
    private static final String DASHBOARD_TITLE1 = "sampledashboard1";
    private static final String DASHBOARD_TITLE2 = "sampledashboard2";
    private static final String DASHBOARD_DESCRIPTION = "This is sample description for dashboard";

    private String dashboardTitle;
    private WebElement webElement = null;

    @Factory(dataProvider = "userMode")
    public AddDeleteDashboardTest(TestUserMode userMode, String dashboardTitle) {
        super(userMode);
        this.dashboardTitle = dashboardTitle;
    }

    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{
                {TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE1},
                {TestUserMode.SUPER_TENANT_USER, DASHBOARD_TITLE2}
        };
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        resourcePath = DSIntegrationTestConstants.DASHBOARD_REGISTRY_BASE_PATH + dashboardTitle.toLowerCase();
        login(getCurrentUsername(), getCurrentPassword());
    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding new dashboard for dashboard server")
    public void testAddDashboardNew() throws Exception {
        DSWebDriver driver = getDriver();

        redirectToLocation("portal", "dashboards");
        driver.findElement(By.cssSelector("[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        driver.findElement(By.id("ues-dashboard-description")).clear();
        driver.findElement(By.id("ues-dashboard-description")).sendKeys(DASHBOARD_DESCRIPTION);
        driver.findElement(By.id("ues-dashboard-create")).click();
        driver.findElement(By.cssSelector("a[data-id='single-column']")).click();
        redirectToLocation("portal", "dashboards");

        getWebDriverWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(dashboardTitle)));
        webElement = driver.findElement(By.id(dashboardTitle));

        assertEquals(dashboardTitle, webElement.findElement(By.id("ues-dashboard-title")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, webElement.findElement(By.id("ues-dashboard-description")).getText());
    }

    @Test(groups = "wso2.ds.dashboard", description = "Deleting the existing dashboard from dashboard server",
            dependsOnMethods = "testAddDashboardNew")
    public void testDeleteDashboardNew() throws Exception {
        DSWebDriver driver = getDriver();

        Boolean isResourceExist;
        webElement = driver.findElement(By.id(dashboardTitle));
        webElement.findElement(By.cssSelector("i.fw-delete")).click();
        driver.findElement(By.cssSelector("span.ladda-label")).click();

        modifyTimeOut(2);
        assertFalse(driver.isElementPresent(By.id(dashboardTitle)), "Error occurred while deleting dashboard" +
                dashboardTitle);
        resetTimeOut();

        isResourceExist = isResourceExist(resourcePath);
        assertFalse(isResourceExist, "Registry resource could not be deleted due to some errors");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        dsUITestTearDown();
    }
}

