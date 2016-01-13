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

import ds.integration.tests.common.domain.DSIntegrationTestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import static org.testng.Assert.*;

public class CreateCustomDashboardPerUser extends DSUIIntegrationTest {
    private static final String USERNAME_EDITOR = "editor";
    private static final String PASSWORD_EDITOR = "editor123";
    private static final String RETYPE_PASSWORD_EDITOR = "editor123";

    private static final String USERNAME_VIEWER = "viewer";
    private static final String PASSWORD_VIEWER = "viewer123";
    private static final String RETYPE_PASSWORD_VIEWER = "viewer123";

    private static final String EDITOR_ROLE = "dashboardEditorRole";
    private static final String VIEWER_ROLE = "dashboardViewerRole";

    private static final String DASHBOARD_TITLE = "perUserCustomDashboard";
    private static final String DASHBOARD_DESCRIPTION = "This is sample description for dashboard";

    private static final String DASHBOARD_PAGE_NAME = "PersonalizeDashBoardTitle";

    private String dashboardTitle;

    @Factory(dataProvider = "userMode")
    public CreateCustomDashboardPerUser(TestUserMode userMode, String dashboardTitle) {
        super(userMode);
        this.dashboardTitle = dashboardTitle;
    }

    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE}};
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        resourcePath = DSIntegrationTestConstants.DASHBOARD_REGISTRY_BASE_PATH + dashboardTitle.toLowerCase();
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding user to admin console and assign editor or viewer roles" +
            " to newly added users")
    public void testAddUserAssignRoles() throws Exception {
        addUser(USERNAME_EDITOR, PASSWORD_EDITOR, RETYPE_PASSWORD_EDITOR);
        addRole(EDITOR_ROLE);
        assignRoleToUser(new String[]{USERNAME_EDITOR});

        addUser(USERNAME_VIEWER, PASSWORD_VIEWER, RETYPE_PASSWORD_VIEWER);
        addRole(VIEWER_ROLE);
        assignRoleToUser(new String[]{USERNAME_VIEWER});

        logoutFromAdminConsole();
    }

    @Test(groups = "wso2.ds.dashboard", description = "assigning dashboard view and edit permission", dependsOnMethods =
            "testAddUserAssignRoles")
    public void testAddDashboardAndAssignRolesBySetting() throws Exception {
        DSWebDriver driver = getDriver();

        login(USERNAME_EDITOR, PASSWORD_EDITOR);
        addDashBoard(dashboardTitle, DASHBOARD_DESCRIPTION);

        WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        webElement.findElement(By.cssSelector(".ues-edit")).click();
        driver.findElement(By.id("settings-link")).click();
        driver.executeScript("scroll(0, 200);");

        WebElement element = driver.findElement(By.id("ues-share-view"));
        element.sendKeys("dashboardViewer");
        element.sendKeys(Keys.TAB);

        WebElement element2 = driver.findElement(By.id("ues-share-edit"));
        element2.sendKeys("dashboardEditor");
        element2.sendKeys(Keys.TAB);

        driver.findElement(By.cssSelector(".ues-shared-view > .ues-shared-role > .remove-button")).click();
        driver.findElement(By.cssSelector(".ues-shared-edit > .ues-shared-role > .remove-button")).click();
    }

    @Test(groups = "wso2.ds.dashboard", description = "test for editor role", dependsOnMethods =
            "testAddDashboardAndAssignRolesBySetting")
    public void testForEditorRole() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardId = dashboardTitle.toLowerCase();

        redirectToLocation("portal", "dashboards");
        WebElement dashboard = getDriver().findElement(By.id(dashboardId));
        assertEquals(DASHBOARD_TITLE, dashboard.findElement(By.id("ues-dashboard-title")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, dashboard.findElement(By.id("ues-dashboard-description")).getText());

        assertTrue(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-view")),
                "view element is present in the current UI");
        assertTrue(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-edit")),
                "design element is present in the current UI");
        assertTrue(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-settings")),
                "settings element is present in the current UI");
        dashboard.findElement(By.cssSelector(".ues-view")).click();

        // switch the driver to the new window and click on the edit/personalize link
        pushWindow();

        //it will go to view page as editor
        assertEquals(USERNAME_EDITOR, driver.findElement(By.cssSelector(".dropdown-toggle")).getText(),
                "Expected Username is not matched");

        assertEquals("Edit", driver.findElement(By.cssSelector("a.ues-copy")).getText(), "Unable to find the edit button");

        driver.close();
        popWindow();
        logout();
    }

    @Test(groups = "wso2.ds.dashboard", description = "test for viewer role", dependsOnMethods =
            "testAddDashboardAndAssignRolesBySetting")
    public void testForViewer() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardId = dashboardTitle.toLowerCase();

        login(USERNAME_VIEWER, PASSWORD_VIEWER);
        driver.get(getBaseUrl() + "/portal/dashboards");

        WebElement dashboard = driver.findElement(By.id(dashboardId));

        assertEquals(DASHBOARD_TITLE, dashboard.findElement(By.id("ues-dashboard-title")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, dashboard.findElement(By.id("ues-dashboard-description")).getText());

        assertTrue(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-view")),
                "view element is present in the current UI");
        assertFalse(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-edit")),
                "design element is present in the current UI");
        assertFalse(driver.isElementPresent(By.cssSelector("#" + dashboardId + " .ues-settings")),
                "settings element is present in the current UI");
        dashboard.findElement(By.cssSelector(".ues-view")).click();

        // switch the driver to the new window and click on the edit/personalize link
        pushWindow();

        //it will go to view page as viewer
        assertEquals(USERNAME_VIEWER, driver.findElement(By.cssSelector(".dropdown-toggle")).getText(),
                "Expected Username is not matched");
        assertEquals("Personalize", getDriver().findElement(By.cssSelector("a.ues-copy")).getText(),
                "Unable to find the personalize button");
    }

    @Test(groups = "wso2.ds.dashboard", description = "Per user dashboard settings", dependsOnMethods =
            "testForViewer")
    public void testCustomizeButtonDashboard() throws Exception {
        DSWebDriver driver = getDriver();

        driver.findElement(By.cssSelector("a.ues-copy")).click();
        driver.findElement(By.cssSelector("a.ues-pages-toggle")).click();
        driver.findElement(By.cssSelector("#ues-dashboard-pages .ues-page-item.active .accordion-toggle")).click();

        driver.findElement(By.cssSelector("[name=title]")).clear();
        driver.findElement(By.cssSelector("[name=title]")).sendKeys(DASHBOARD_PAGE_NAME);
        driver.findElement(By.cssSelector("h4.ues-page-title")).click();

        assertEquals(DASHBOARD_PAGE_NAME, driver.findElement(By.cssSelector("h4.ues-page-title")).getText(),
                "error occurred while edit the new page name");
    }

    @Test(groups = "wso2.ds.dashboard", description = "Per user dashboard settings", dependsOnMethods =
            "testCustomizeButtonDashboard")
    public void checkRegistrySourceForCustomizeDashboard() throws Exception {
        Boolean isResourceExist;

        isResourceExist = isResourceExist(DSIntegrationTestConstants.DASHBOARD_REGISTRY_PATH_CUSTOM_DASHBOARD_PERUSER
                + "/" + USERNAME_VIEWER + "/dashboards/" + dashboardTitle.toLowerCase());

        assertTrue(isResourceExist,
                "Registry resource could not be created for personalize dashboard per user due to some errors");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        dsUITestTearDown();
    }
}
