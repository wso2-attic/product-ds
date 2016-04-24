/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.ds.ui.integration.test.dashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class RoleBasedDashboardAccess extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "sampleRoleBasedDashboard";
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
        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, DASHBOARD_DESCRIPTION);
    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding a dashboard and access it with role base " +
            "name for dashboard server")
    public void testRoleBasedDashboardAccessNew() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardId = dashboardTitle.toLowerCase();
        assertTrue(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-view")),
                "view element is present in the current UI");
        assertTrue(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-edit")),
                "edit element is present in the current UI");
        assertTrue(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-settings")),
                "settings element is present in the current UI");
        driver.findElement(By.cssSelector("#" + dashboardId + " .ues-settings")).click();
        driver.findElement(By.cssSelector(".ues-shared-edit")).findElement(By.cssSelector(".remove-button")).click();
        logout();
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
        addUser(USER_NAME, PASSWORD, RETYPE_PASSWORD);
        // login with new user
        login(USER_NAME, PASSWORD);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        WebElement dashboard = driver.findElement(By.id(dashboardId));
        assertEquals(DASHBOARD_TITLE, dashboard.findElement(By.id("ues-dashboard-title")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, dashboard.findElement(By.id("ues-dashboard-description")).getText());
        modifyTimeOut(2);
        assertTrue(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-view")),
                "view element is present in the current UI");
        assertFalse(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-edit")),
                "edit element is present in the current UI");
        assertFalse(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-settings")),
                "settings element is present in the current UI");
        resetTimeOut();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        dsUITestTearDown();
    }
}
