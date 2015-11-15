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
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class CreateCustomDashboardPerUser extends DSUIIntegrationTest {
    private static final String USER_NAME_EDITOR = "editor";
    private static final String PASSWORD_EDITOR = "editor123";
    private static final String RETYPE_PASSWORD_EDITOR = "editor123";

    private static final String USER_NAME_VIEWER = "viewer";
    private static final String PASSWORD_VIEWER = "viewer123";
    private static final String RETYPE_PASSWORD_VIEWER = "viewer123";

    private static final String EDITOR_ROLE = "dashboardEditorRole";
    private static final String VIEWER_ROLE = "dashboardViewerRole";

    private static final String DASHBOARD_TITLE = "sampledashboard1";
    private static final String DASHBOARD_DESCRIPTION = "This is sample description for dashboard";

    private static final String DASHBOARD_PAGENAME = "PersonalizeDashBoardTitle";

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
        String backendURL = getBackEndUrl();
        ResourceAdminServiceClient resourceAdminServiceClient = new ResourceAdminServiceClient(backendURL,
                getCurrentUsername(),
                getCurrentPassword());
        resourcePath = DSIntegrationTestConstants.DASHBOARD_REGISTRY_BASE_PATH + dashboardTitle.toLowerCase();
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());

    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding user to admin console and assign editor or viewer roles" +
            " to newly added users")
    public void testAddUserAssignRoles() throws Exception {
        addUser(USER_NAME_EDITOR, PASSWORD_EDITOR, RETYPE_PASSWORD_EDITOR);
        addRole(EDITOR_ROLE);
        assignRoleToUser(new String[]{USER_NAME_EDITOR});
        addUser(USER_NAME_VIEWER, PASSWORD_VIEWER, RETYPE_PASSWORD_VIEWER);
        addRole(VIEWER_ROLE);
        assignRoleToUser(new String[]{USER_NAME_VIEWER});
        logoutFromAdminConsole();

    }

    @Test(groups = "wso2.ds.dashboard", description = "assigning dashboard view and edit permission", dependsOnMethods =
            "testAddUserAssignRoles")
    public void testAddDashboardAndAssignRolesBysetting() throws Exception {
        DSWebDriver driver = getDriver();
        login(USER_NAME_EDITOR, PASSWORD_EDITOR);
        addDashBoard(dashboardTitle, DASHBOARD_DESCRIPTION);
        WebElement webElement = driver.findElement(By.id(dashboardTitle));
        webElement.findElement(By.cssSelector(".ues-edit")).click();
        driver.findElement(By.id("settings-link")).click();
        WebElement element = driver.findElement(By.id("ues-share-view"));
        element.sendKeys(VIEWER_ROLE);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        element.click();
        WebElement selectedElement = driver.findElement(By.cssSelector("div.tt-menu > div > div:first-child"));
        selectedElement.click();
        WebElement element2 = driver.findElement(By.id("ues-share-edit"));
        element2.sendKeys(EDITOR_ROLE);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        element2.click();
        WebElement selectedElement2 = driver.findElement(By.cssSelector("div.tt-menu > div > div:first-child"));
        selectedElement2.click();
        driver.findElement(By.cssSelector(".ues-shared-view > .ues-shared-role > .remove-button")).click();
        driver.findElement(By.cssSelector(".ues-shared-edit > .ues-shared-role > .remove-button")).click();

    }

    @Test(groups = "wso2.ds.dashboard", description = "test for editor role", dependsOnMethods =
            "testAddDashboardAndAssignRolesBysetting")
    public void testForEditorRole() throws Exception {
        DSWebDriver driver = getDriver();
        driver.findElement(By.id("ues-back")).click();
        WebElement dashboard = getDriver().findElement(By.id(dashboardTitle.toLowerCase()));
        assertEquals(DASHBOARD_TITLE, dashboard.findElement(By.id("ues-dashboard-title")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, dashboard.findElement(By.id("ues-dashboard-description")).getText());
        assertTrue(driver.isElementPresent(By.cssSelector(".ues-view")), "view element is present in the " +
                "current UI");
        assertTrue(driver.isElementPresent(By.cssSelector(".ues-edit")), "design element is present in the " +
                "current UI");
        assertTrue(driver.isElementPresent(By.cssSelector(".ues-settings")), "settings element is present in " +
                "the current UI");
        driver.findElement(By.cssSelector(".ues-view")).click();
        // switch the driver to the new window and click on the edit/personalize link
        String parentWindow = driver.getWindowHandle();
        for (String childWindows : driver.getWindowHandles()) {
            if (!childWindows.contains(parentWindow)) {
                driver.switchTo().window(childWindows);
                //it will go to view page as editor
                assertEquals(USER_NAME_EDITOR, getDriver().findElement(By.cssSelector(".dropdown-toggle")).getText(),
                        "Expected Username is not matched");
                assertEquals("Edit", driver.findElement(By.cssSelector("a[href=\"../dashboards/" + dashboardTitle +
                        "/?editor=true\"]")).getText(), "expected Edit Button but cannot find edit Button");
                break;
            }
        }
        driver.close();
        driver.switchTo().window(parentWindow);
        logout();

    }

    @Test(groups = "wso2.ds.dashboard", description = "test for viewer role", dependsOnMethods =
            "testAddDashboardAndAssignRolesBysetting")
    public void testForViewer() throws Exception {
        DSWebDriver driver = getDriver();
        login(USER_NAME_VIEWER, PASSWORD_VIEWER);
        WebElement dashboardViewer = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        assertEquals(DASHBOARD_TITLE, dashboardViewer.findElement(By.id("ues-dashboard-title")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, dashboardViewer.findElement(By.id("ues-dashboard-description")).getText());
        assertTrue(driver.isElementPresent(By.cssSelector(".ues-view")), "view element is present in the " +
                "current UI");
        assertFalse(driver.isElementPresent(By.cssSelector(".ues-edit")), "design element is present in " +
                "the " +
                "current UI");
        assertFalse(driver.isElementPresent(By.cssSelector(".ues-settings")), "settings element is " +
                "present in " +
                "the current UI");
        driver.findElement(By.cssSelector(".ues-view")).click();
        // switch the driver to the new window and click on the edit/personalize link
        String parentViewer = driver.getWindowHandle();
        for (String childWindow : driver.getWindowHandles()) {
            if (!childWindow.contains(parentViewer)) {
                driver.switchTo().window(childWindow);
                //it will go to view page as viewer
                assertEquals(USER_NAME_VIEWER, driver.findElement(By.cssSelector(".dropdown-toggle")).getText(),
                        "Expected Username is not matched");
                assertEquals("Personalize", getDriver().findElement(By.cssSelector("a[href=\"../dashboards/" +
                        dashboardTitle +
                        "/?custom=true\"]")).getText(), "expected Personalize Button but cannot find Personalize " +
                        "Button");
                break;
            }

        }
    }

    @Test(groups = "wso2.ds.dashboard", description = "Per user dashboard settings", dependsOnMethods =
            "testForViewer")
    public void testCustomizeButtonDashboard() throws Exception {
        DSWebDriver driver = getDriver();
        driver.findElement(By.cssSelector("a[href=\"../dashboards/" +
                dashboardTitle + "/?custom=true\"]")).click();
        String parentWindow = driver.getWindowHandle();
        for (String childWindow : driver.getWindowHandles()) {
            if (childWindow.contains(parentWindow)) {
                driver.switchTo().window(childWindow);
                driver.findElement(By.id("edit-page-properties")).click();
                driver.findElement(By.cssSelector(".title")).clear();
                driver.findElement(By.cssSelector(".title")).sendKeys(DASHBOARD_PAGENAME);
                driver.findElement(By.cssSelector("h4.ues-page-title")).click();
                assertEquals(DASHBOARD_PAGENAME, driver.findElement(By.cssSelector("h4.ues-page-title"))
                        .getText(), "error occurred while edit the new page name");
                break;

            }
        }
        driver.switchTo().window(parentWindow);

    }

    @Test(groups = "wso2.ds.dashboard", description = "Per user dashboard settings", dependsOnMethods =
            "testCustomizeButtonDashboard")
    public void checkRegistrySourceForCustomizeDashboard() throws Exception {
        Boolean isResourceExist;
        isResourceExist = isResourceExist(DSIntegrationTestConstants.DASHBOARD_REGISTRY_PATH_CUSTOM_DASHBOARD_PERUSER
                + "/" + USER_NAME_VIEWER + "/dashboards/" + dashboardTitle.toLowerCase());
        assertTrue(isResourceExist, "Registry resource could not be created for personalize dashboard per user due to" +
                " some errors");

    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        dsUITestTearDown();

    }

}
