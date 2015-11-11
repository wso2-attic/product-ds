/*
 * <!--
 *   ~  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *   ~
 *   ~  WSO2 Inc. licenses this file to you under the Apache License,
 *   ~  Version 2.0 (the "License"); you may not use this file except
 *   ~  in compliance with the License.
 *   ~  You may obtain a copy of the License at
 *   ~
 *   ~  http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~  Unless required by applicable law or agreed to in writing,
 *   ~  software distributed under the License is distributed on an
 *   ~  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   ~  KIND, either express or implied.  See the License for the
 *   ~  specific language governing permissions and limitations
 *   ~  under the License.
 *   -->
 */

package org.wso2.ds.ui.integration.test.dashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import static org.testng.Assert.*;

public class RoleBasedDashboardAccess extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "sampleDashBoard";
    private static final String DASHBOARD_DESCRIPTION = "This is description about " + DASHBOARD_TITLE;
    private static final String USER_NAME = "sampleUser";
    private static final String PASSWORD = "qwerty";
    private static final String RETYPE_PASSWORD = "qwerty";
    private String dashboardTitle;

    @Factory(dataProvider = "userMode")
    public RoleBasedDashboardAccess(TestUserMode userMode, String dashboardTitle) {
        super(userMode);
        this.dashboardTitle = dashboardTitle;
    }

    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE}};
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        DSUIIntegrationTest.login(getDriver(), getBaseUrl(), getCurrentUsername(), getCurrentPassword());
        DSUIIntegrationTest.addDashBoard(getDriver(), DASHBOARD_TITLE, DASHBOARD_DESCRIPTION);
    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding a dashboard and access it with role base " +
            "name for dashboard server")
    public void testRoleBasedDashboardAccessNew() throws Exception {
        //TODO can split into two test cases
        DSWebDriver driver = getDriver();
        WebElement createdDashBoard = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        assertTrue(driver.isElementPresent(By.cssSelector(".ues-view")), "view element is present in the " +
                "current UI");
        assertTrue(driver.isElementPresent(By.cssSelector(".ues-edit")), "edit element is present in the " +
                "current UI");
        assertTrue(driver.isElementPresent(By.cssSelector(".ues-settings")), "settings element is present in the " +
                "current UI");

        createdDashBoard.findElement(By.cssSelector(".ues-settings")).click();
        driver.findElement(By.cssSelector(".ues-shared-edit > .ues-shared-role > .remove-button")).click();
        DSUIIntegrationTest.logout(driver, getBaseUrl(), getCurrentUsername());
        DSUIIntegrationTest.loginToAdminConsole(driver, getBaseUrl(), getCurrentUsername(), getCurrentPassword());
        DSUIIntegrationTest.AddUser(driver,USER_NAME,PASSWORD,RETYPE_PASSWORD);
        // login with new user
        DSUIIntegrationTest.login(driver, getBaseUrl(), USER_NAME, PASSWORD);
        WebElement webElement = getDriver().findElement(By.id(dashboardTitle.toLowerCase()));
        assertEquals(DASHBOARD_TITLE, webElement.findElement(By.id("ues-dashboard-title")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, webElement.findElement(By.id("ues-dashboard-description")).getText());
        assertTrue(driver.isElementPresent(By.cssSelector(".ues-view")), "view element is present in the " +
                "current UI");
        assertFalse(driver.isElementPresent(By.cssSelector(".ues-edit")), "edit element is present in the " +
                "current UI");
        assertFalse(driver.isElementPresent(By.cssSelector(".ues-settings")), "settings element is present in the " +
                "current UI");

    }
    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        try {
            DSUIIntegrationTest.logout(getDriver(), getBaseUrl(), getCurrentUsername());
        } finally {
            getDriver().quit();
        }
    }
}
