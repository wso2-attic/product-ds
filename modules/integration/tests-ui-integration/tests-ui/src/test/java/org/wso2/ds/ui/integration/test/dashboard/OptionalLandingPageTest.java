/*
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
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * To test the functionality when landing page is made as an optional one
 */
public class OptionalLandingPageTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "optionallandingdashboard";
    private static final String ROLE1 = "optionalrole1";
    private static final String ROLE2 = "optionalrole2";
    private static final String USERNAME_EDITOR = "optional_editor";
    private static final String PASSWORD_EDITOR = "optional_editor";
    private static final String USERNAME_VIEWER = "optional_viewer";
    private static final String PASSWORD_VIEWER = "optional_viewer";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public OptionalLandingPageTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return user modes
     */
    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN}};
    }

    /**
     * Setup the testing environment.
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws AutomationUtilException
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws AutomationUtilException, XPathExpressionException, IOException, InterruptedException {
        String[] userListForRole1 = {getCurrentUsername(), USERNAME_EDITOR};
        String[] userListForRole2 = {getCurrentUsername(), USERNAME_VIEWER};
        login(getCurrentUsername(), getCurrentPassword());
        deleteDashboards();
        addDashBoardWithoutLandingPage(DASHBOARD_TITLE, "This is a test dashboard");
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
        addUser(USERNAME_EDITOR, PASSWORD_EDITOR, PASSWORD_EDITOR);
        addUser(USERNAME_VIEWER, PASSWORD_VIEWER, PASSWORD_VIEWER);
        addRole(ROLE1);
        assignRoleToUser(userListForRole1);
        addRole(ROLE2);
        assignRoleToUser(userListForRole2);
        assignInternalRoleToUser(DASHBOARD_TITLE + "-viewer", new String[]{USERNAME_VIEWER});
        assignInternalRoleToUser(DASHBOARD_TITLE + "-editor", new String[]{USERNAME_EDITOR});
        addLoginRole(USERNAME_EDITOR);
        addLoginRole(USERNAME_VIEWER);
    }

    /**
     * Clean up after running tests.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws XPathExpressionException, MalformedURLException {
        logout();
        getDriver().quit();
    }

    /**
     * To test the creation of dashboard without landing page and checking the basic flow without landing page
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the basic flow of dashboard page creation when landing "
            + "page is optional")
    public void testCreateDashboard() throws XPathExpressionException, MalformedURLException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-edit")).click();
        addARoleToView("default", ROLE1);
        getDriver().findElement(By.cssSelector("div[data-role=\"Internal/everyone\"] .remove-button")).click();
        clickOnView("default");

        // Check the page creation when there is no internal/everyone role in first page
        addPageToDashboard("default-grid");
        assertFalse(getDriver().isElementPresent(By.cssSelector("div.modal-body")), "Adding a new page is not allowed "
                + "when landing page doesn`t contain internal/everyone role even though landing page is optional");
        assertTrue(getDriver().isElementPresent(By.id("default")),
                "When creating new page default view is not created");
    }

    /**
     * Checks whether anon view is allowed in second page, when first page does not contain anonymous view
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking whether the creation of anonymous view allowed in second page",
            dependsOnMethods = "testCreateDashboard")
    public void testAnonViewCreation() throws XPathExpressionException, MalformedURLException, InterruptedException {
        addARoleToView("default", ROLE2);
        if (!getDriver().isElementPresent(By.cssSelector("div[data-role=\"Internal/everyone\"]"))) {
            clickOnViewSettings("default");
        }
        getDriver().findElement(By.cssSelector("div[data-role=\"Internal/everyone\"] .remove-button")).click();
        createNewView("single-column");
        addARoleToView("view0", "anonymous");
        getDriver().findElement(By.id("ues-modal-confirm-yes")).click();
        assertTrue(getDriver().isElementPresent(By.cssSelector("div[data-role=\"anonymous\"]")),
                "Addition of anonymous " + "role not allowed even the landing page is optional");
    }

    @Test(groups = "wso2.ds.dashboard", description = "Checking whether the required conditions checked before "
            + "making a page as a landing page", dependsOnMethods = "testAnonViewCreation")
    public void testMakingLandingPage() throws XPathExpressionException, MalformedURLException, InterruptedException {
        // Try to set the first page as a landing page, when the second page contains view with anonymous role
        getDriver().findElement(By.className("ues-switch-page-prev")).click();
        getDriver().findElement(By.className("fw-pages")).click();
        getDriver().findElement(By.cssSelector("input[name='landing']")).click();
        Thread.sleep(2000);
        String expected = "Cannot Select This Page As Landing";
        String message = getDriver().findElement(By.cssSelector(".modal-title")).getText().trim();
        assertTrue(expected.equalsIgnoreCase(message),
                "Creating of landing page allowed without checking for required conditions");
        getDriver().findElement(By.id("ues-modal-info-ok")).click();
        createNewView("single-column");
        getDriver().findElement(By.className("fw-pages")).click();
        Thread.sleep(2000);
        getDriver().findElement(By.cssSelector("input[name='landing']")).click();
        message = getDriver().findElement(By.cssSelector(".modal-title")).getText().trim();
        assertTrue(expected.equalsIgnoreCase(message),
                "Creating of landing page allowed without checking for required conditions");
        getDriver().findElement(By.id("ues-modal-info-ok")).click();
        createNewView("single-column");
        addARoleToView("view1", "anonymous");
        getDriver().findElement(By.id("ues-modal-confirm-yes")).click();
        getDriver().findElement(By.className("fw-pages")).click();
        Thread.sleep(2000);
        getDriver().findElement(By.cssSelector("input[name='landing']")).click();
        assertFalse(getDriver().isElementPresent(By.cssSelector("modal-title")),
                "Creating a landing page is not " + "allowed even the necessary conditions satisfied");
        getDriver().findElement(By.cssSelector("input[name='landing']")).click();
    }

    /**
     * To test the view mode of the dashboard based on the role of the user
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the view mode and the pages that are shown for each user",
            dependsOnMethods = "testMakingLandingPage")
    public void testViewMode() throws XPathExpressionException, MalformedURLException, InterruptedException {
        deleteView("view0");
        deleteView("view1");
        clickOnView("default");
        Thread.sleep(2000);
        String[][] gadgetMappings = {{"publisher", "b"}, {"usa-map", "c"}};
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().findElement(By.cssSelector("#btn-sidebar-gadgets i.fw.fw-gadget")).click();
        Thread.sleep(2000);
        waitTillElementToBeClickable(By.id("publisher"));
        getDriver().executeScript(script);
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(), "Publisher gadget is not added");
        getDriver().findElement(By.className("ues-switch-page-next")).click();
        deleteView("view0");
        logout();
        login(USERNAME_EDITOR, PASSWORD_EDITOR);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-actions .ues-view")).click();
        pushWindow();
        assertTrue(getDriver().isElementPresent(By.cssSelector("a[href=\"page0\"]")),
                "The page that has the view for " + "the particular user is not visible in view mode");
        assertTrue(getDriver().isElementPresent(By.id("publisher-0")),
                "The correct gadgets are not displayed in view mode");
        assertTrue(getDriver().isElementPresent(By.id("usa-map-0")),
                "The correct gadgets are not displayed in view mode");
        assertFalse(getDriver().isElementPresent(By.cssSelector("a[href=\"page1\"]")),
                "The page that does not has the view for " + "the particular user is visible in view mode");
        getDriver().close();
        popWindow();
        logout();
        login(USERNAME_VIEWER, PASSWORD_VIEWER);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-actions .ues-view")).click();
        pushWindow();
        assertTrue(getDriver().isElementPresent(By.cssSelector("a[href=\"page1\"]")),
                "The page that has the view for " + "the particular user is not visible in view mode");
        assertFalse(getDriver().isElementPresent(By.cssSelector("a[href=\"page0\"]")),
                "The page that does not has the view for " + "the particular user is visible in view mode");
        getDriver().close();
        popWindow();
    }
}
