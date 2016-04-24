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

import ds.integration.tests.common.domain.DSIntegrationTestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;

import static org.testng.Assert.*;

/**
 * Tests per user dashboard personalization.
 */
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

    /**
     * Initialize the class.
     *
     * @param userMode       user mode
     * @param dashboardTitle title of the dashboard
     */
    @Factory(dataProvider = "userMode")
    public CreateCustomDashboardPerUser(TestUserMode userMode, String dashboardTitle) {
        super(userMode);
        this.dashboardTitle = dashboardTitle;
    }

    /**
     * Provides user modes to initialize the class.
     *
     * @return
     */
    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE}};
    }

    /**
     * Setting up the testing environment.
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws XPathExpressionException, MalformedURLException {
        resourcePath = DSIntegrationTestConstants.DASHBOARD_REGISTRY_BASE_PATH + dashboardTitle.toLowerCase();
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
    }

    /**
     * Tests adding users and assign roles in the management console.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Adding user to admin console and assign editor or viewer roles" +
            " to newly added users")
    public void testAddUserAssignRoles() throws MalformedURLException, XPathExpressionException {
        addUser(USERNAME_EDITOR, PASSWORD_EDITOR, RETYPE_PASSWORD_EDITOR);
        addRole(EDITOR_ROLE);
        assignRoleToUser(new String[]{USERNAME_EDITOR});
        addUser(USERNAME_VIEWER, PASSWORD_VIEWER, RETYPE_PASSWORD_VIEWER);
        addRole(VIEWER_ROLE);
        assignRoleToUser(new String[]{USERNAME_VIEWER});
        logoutFromAdminConsole();
    }

    /**
     * Assign dashboard viewers and editors.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Assigning dashboard view and edit permission", dependsOnMethods =
            "testAddUserAssignRoles")
    public void testAddDashboardAndAssignRolesBySetting() throws XPathExpressionException, MalformedURLException {
        login(USERNAME_EDITOR, PASSWORD_EDITOR);
        addDashBoard(dashboardTitle, DASHBOARD_DESCRIPTION);
        WebElement dashboardItem = getDriver().findElement(By.id(dashboardTitle.toLowerCase()));
        dashboardItem.findElement(By.cssSelector(".ues-edit")).click();
        getDriver().findElement(By.id("dashboard-settings")).click();
        getDriver().executeScript("scroll(0, 200);");
        // Add viewer role
        WebElement viewerTextbox = getDriver().findElement(By.id("ues-share-view"));
        viewerTextbox.sendKeys("dashboardViewer");
        viewerTextbox.sendKeys(Keys.TAB);
        // Add editor role
        WebElement editorTextbox = getDriver().findElement(By.id("ues-share-edit"));
        editorTextbox.sendKeys("dashboardEditor");
        editorTextbox.sendKeys(Keys.TAB);
        // Remove all other roles
        getDriver().findElement(By.cssSelector(".ues-shared-view > .ues-shared-role > .remove-button")).click();
        getDriver().findElement(By.cssSelector(".ues-shared-edit > .ues-shared-role > .remove-button")).click();
    }

    /**
     * Tests for the editor role in the dashboards page and the dashboard view page.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "test for editor role", dependsOnMethods =
            "testAddDashboardAndAssignRolesBySetting")
    public void testForEditorRole() throws MalformedURLException, XPathExpressionException {
        String dashboardId = dashboardTitle.toLowerCase();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        WebElement dashboard = getDriver().findElement(By.id(dashboardId));
        assertEquals(DASHBOARD_TITLE, dashboard.findElement(By.id("ues-dashboard-title")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, dashboard.findElement(By.id("ues-dashboard-description")).getText());
        assertTrue(getDriver().isElementPresent(By.cssSelector("#" + dashboardId + " .ues-view")),
                "view element is present in the current UI");
        assertTrue(getDriver().isElementPresent(By.cssSelector("#" + dashboardId + " .ues-edit")),
                "design element is present in the current UI");
        assertTrue(getDriver().isElementPresent(By.cssSelector("#" + dashboardId + " .ues-settings")),
                "settings element is present in the current UI");
        dashboard.findElement(By.cssSelector(".ues-view")).click();
        // Switch the driver to the new window and click on the edit/personalize link
        pushWindow();
        assertEquals(USERNAME_EDITOR, getDriver().findElement(By.cssSelector(".auth .username")).getText(),
                "Expected Username is not matched");
        assertEquals("Edit", getDriver().findElement(By.cssSelector("a.ues-copy")).getAttribute("title"),
                "Unable to find the edit button");
        getDriver().close();
        popWindow();
        logout();
    }

    /**
     * Tests for the viewer role in the dashboards page and the dashboard view page.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Test(groups = "wso2.ds.dashboard", description = "test for viewer role", dependsOnMethods =
            "testAddDashboardAndAssignRolesBySetting")
    public void testForViewer() throws XPathExpressionException, MalformedURLException {
        String dashboardId = dashboardTitle.toLowerCase();
        login(USERNAME_VIEWER, PASSWORD_VIEWER);
        // Go to the dashboards page
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        WebElement dashboard = getDriver().findElement(By.id(dashboardId));
        assertEquals(DASHBOARD_TITLE, dashboard.findElement(By.id("ues-dashboard-title")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, dashboard.findElement(By.id("ues-dashboard-description")).getText());
        assertTrue(getDriver().isElementPresent(By.cssSelector("#" + dashboardId + " .ues-view")),
                "view element is present in the current UI");
        assertFalse(getDriver().isElementPresent(By.cssSelector("#" + dashboardId + " .ues-edit")),
                "design element is present in the current UI");
        assertFalse(getDriver().isElementPresent(By.cssSelector("#" + dashboardId + " .ues-settings")),
                "settings element is present in the current UI");
        dashboard.findElement(By.cssSelector(".ues-view")).click();
        // Switch the driver to the new window and click on the edit/personalize link
        pushWindow();
        assertEquals(USERNAME_VIEWER, getDriver().findElement(By.cssSelector(".auth .username")).getText(),
                "Expected Username is not matched");
        String personalizeText = getDriver().findElement(By.cssSelector("a.ues-copy")).getAttribute("title").trim()
                .substring(0, 11);
        assertEquals("Personalize", personalizeText, "Unable to find the personalize button");
    }

    /**
     * Tests personalizing the dashboard by modifying the dashboard page name.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Tests dashboard personalization", dependsOnMethods = "testForViewer")
    public void testCustomizeButtonDashboard() throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.cssSelector("a.ues-copy")).click();
        selectPane("pages");
        getDriver().findElement(By.cssSelector("[name=title]")).clear();
        getDriver().findElement(By.cssSelector("[name=title]")).sendKeys(DASHBOARD_PAGE_NAME);
        getDriver().findElement(By.cssSelector("div.page-title")).click();
        assertEquals(DASHBOARD_PAGE_NAME, getDriver().findElement(By.cssSelector("div.page-title p.lead")).getText(),
                "error occurred while edit the new page name");
    }

    /**
     * Check registry source for customized dashboard.
     */
    @Test(groups = "wso2.ds.dashboard", description = "Check registry resource for customized dashboard",
            dependsOnMethods = "testCustomizeButtonDashboard")
    public void checkRegistrySourceForCustomizeDashboard() {
        Boolean isResourceExist = isResourceExist(
                DSIntegrationTestConstants.DASHBOARD_REGISTRY_PATH_CUSTOM_DASHBOARD_PERUSER + "/" + USERNAME_VIEWER
                        + "/" + DS_DASHBOARDS_CONTEXT + "/" + dashboardTitle.toLowerCase());
        assertTrue(isResourceExist,
                "Registry resource could not be created for personalize dashboard per user due to some errors");
    }

    /**
     * Cleaning up after running tests.
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws MalformedURLException, XPathExpressionException {
        dsUITestTearDown();
    }
}
