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
import org.openqa.selenium.support.ui.Select;
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
 * Tests the multiple view support for various roles
 */
public class MultipleViewRoleTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "multipleviewroledashboard";
    private static final String ROLE1 = "multipleviewrole1";
    private static final String ROLE2 = "multipleviewrole2";
    private static final String USERNAME_ROLE1 = "role1";
    private static final String PASSWORD_ROLE1 = "role1";
    private static final String USERNAME_ROLE2 = "role2";
    private static final String PASSWORD_ROLE2 = "role2";
    private static final String USERNAME_EDITOR1 = "editormultiple";
    private static final String PASSWORD_EDITOR1 = "editormultiple";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public MultipleViewRoleTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return user modes
     */
    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][] { { TestUserMode.SUPER_TENANT_ADMIN } };
    }

    /**
     * Setup the testing environment.
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws AutomationUtilException
     */
    @BeforeClass(alwaysRun = true) public void setUp()
            throws AutomationUtilException, XPathExpressionException, IOException {
        String[] userListForRole1 = { getCurrentUsername(), USERNAME_ROLE1 };
        String[] userListForRole2 = { getCurrentUsername(), USERNAME_ROLE2 };
        String[] userList = { USERNAME_ROLE1, USERNAME_ROLE2 };
        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, "This is a test dashboard");
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
        addUser(USERNAME_ROLE1, PASSWORD_ROLE1, PASSWORD_ROLE1);
        addUser(USERNAME_ROLE2, PASSWORD_ROLE2, PASSWORD_ROLE2);
        addUser(USERNAME_EDITOR1, PASSWORD_EDITOR1, PASSWORD_EDITOR1);
        addRole(ROLE1);
        assignRoleToUser(userListForRole1);
        addRole(ROLE2);
        assignRoleToUser(userListForRole2);
        assignInternalRoleToUser(DASHBOARD_TITLE + "-viewer", userList);
        assignInternalRoleToUser(DASHBOARD_TITLE + "-editor", new String[] { USERNAME_EDITOR1 });
        addLoginRole(USERNAME_ROLE1);
        addLoginRole(USERNAME_ROLE2);
        addLoginRole(USERNAME_EDITOR1);
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
     * Checks for anon view
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the creation of anon view")
    public void testAnonView() throws MalformedURLException, XPathExpressionException {
        String[][] gadgetMappings = { { "gadget-resize", "b" } };
        String script = generateAddGadgetScript(gadgetMappings);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-edit")).click();
        addARoleToView("default", "anonymous");
        getDriver().findElement(By.id("ues-modal-confirm-yes")).click();
        selectPane("gadgets");
        getDriver().executeScript(script);
        assertTrue(getDriver().findElement(By.id("gadget-resize-0")).isDisplayed(),
                "Gadget resize gadget is not displayed in the page");
        selectPane("gadgets");
        assertFalse(getDriver().isElementPresent(By.id("publisher")),
                "Gadgets that do not have anonymous roles visible in the gadgets pane");
        assertFalse(getDriver().isElementPresent(By.id("text-box")),
                "Gadgets that do not have anonymous roles visible in the gadgets pane");
        assertTrue(getDriver().isElementPresent(By.id("gadget-state")),
                "Gadgets that have anonymous roles is not visible in the gadgets pane");
        assertTrue(getDriver().isElementPresent(By.id("test1")),
                "Gadgets that have anonymous roles is not visible in the gadgets pane");
        clickViewButton();
        pushWindow();
        assertTrue(getDriver().findElement(By.id("gadget-resize-0")).isDisplayed(),
                "Gadget resize gadget is not displayed in view mode");
        logout();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT + "/" + DASHBOARD_TITLE + "/landing");
        assertTrue(getDriver().findElement(By.id("gadget-resize-0")).isDisplayed(),
                "Gadget resize gadget is not displayed in view mode");
        getDriver().close();
        popWindow();
        login(getCurrentUsername(), getCurrentPassword());
    }

    /**
     * To test the view based on roles
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the view based on roles",
            dependsOnMethods = "testAnonView")
    public void testViewRole() throws MalformedURLException, XPathExpressionException {
        String[][] gadgetMappings = { { "publisherrole1", "a" }, { "publisher", "b" } };
        String script = generateAddGadgetScript(gadgetMappings);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-edit")).click();

        // Create a view for only role 1 and check whether it is displayed only for relevant users
        assertFalse(getDriver().isElementPresent(By.id("publisherrole1")),
                "Gadgets that do not have internal role are visible in gadget pane");
        createNewView("single-column");
        addARoleToView("view0", ROLE1);
        getDriver().findElement(By.cssSelector("div[data-role=\"Internal/everyone\"] .remove-button")).click();
        clickOnView("view0");
        assertTrue(getDriver().isElementPresent(By.id("publisherrole1")),
                "Gadgets that have role1 is not visible in gadgets pane");
        selectPane("gadgets");
        getDriver().executeScript(script);
        assertTrue(getDriver().isElementPresent(By.id("publisherrole1-0")),
                "Gadgets that have role1 is not added to the dashboard");
        assertTrue(getDriver().isElementPresent(By.id("publisher-0")), "Gadgets addition to the dashboard failed");
        clickViewButton();
        pushWindow();
        assertTrue(getDriver().findElement(By.id("publisherrole1-0")).isDisplayed(),
                "Gadget is displayed in view mode");
        assertTrue(getDriver().findElement(By.id("ds-allowed-view-list")).isDisplayed(),
                "Drop down is displayed when there are more than 1 views are available for user");
        Select dropdown = new Select(getDriver().findElement(By.id("ds-allowed-view-list")));
        dropdown.selectByIndex(0);
        assertTrue(getDriver().findElement(By.id("gadget-resize-0")).isDisplayed(),
                "Gadget resize gadget is not displayed in view mode");
        getDriver().close();
        popWindow();
        logout();
        login(USERNAME_ROLE1, PASSWORD_ROLE1);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-view")).click();
        pushWindow();
        assertTrue(getDriver().findElement(By.id("publisherrole1-0")).isDisplayed(),
                "Gadget not is displayed in view mode");
        assertFalse(getDriver().findElement(By.id("ds-allowed-view-list")).isDisplayed(),
                "When only one view is viewable drop down is displayed");
        getDriver().close();
        popWindow();
        logout();
        login(USERNAME_ROLE2, PASSWORD_ROLE2);
        assertTrue(
                getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " #ues-view")).getAttribute("disabled")
                        .equalsIgnoreCase("true"),
                "When there" + "are no views available for a user, view button is not disabled");
        logout();

        // Create a new view for internal/everyone and check whether that view is visible for all the users
        login(USERNAME_EDITOR1, PASSWORD_EDITOR1);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        createNewView("default-grid");
        logout();
        login(USERNAME_ROLE2, PASSWORD_ROLE2);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-view")).click();
        pushWindow();
        assertFalse(getDriver().findElement(By.id("ds-allowed-view-list")).isDisplayed(),
                "When only one view is viewable drop down is displayed");
        getDriver().close();
        popWindow();
        logout();
        login(USERNAME_EDITOR1, PASSWORD_EDITOR1);
    }

    /**
     * Check whether the restricted gadgets are displayed in designer mode for the user who doen`t have access to it
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the functionality of restricting gadgets based on roles",
            dependsOnMethods = "testViewRole")
    public void testRestrictedGadgets() throws MalformedURLException, XPathExpressionException, InterruptedException {
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        clickOnView("view0");
        Thread.sleep(2000);
        assertFalse(getDriver().isElementPresent(By.id("publisherrole1-0")), "Restricted gadget is "
                + "displayed in designer mode to the user who does not have the permission to view it");
        assertTrue(getDriver().isElementPresent(By.id("publisher-0")),
                "Not restricted gadgets are also not " + "rendering correctly.");
        logout();
    }

    /**
     * To test the functionalit of adding new roles to the view
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the functionality of adding roles",
            dependsOnMethods = "testRestrictedGadgets")
    public void testRoleAddition() throws XPathExpressionException, MalformedURLException {
        login(getCurrentUsername(), getCurrentPassword());
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        addARoleToView("view0", "Internal everyone");
        getDriver().findElement(By.id("ues-modal-confirm-no")).click();
        clickOnView("view0");
        getDriver().findElement(By.cssSelector("li[data-view-mode=\"view0\"] .ues-view-component-properties-handle"))
                .click();
        assertFalse(getDriver().isElementPresent(By.cssSelector("div[data-role=\"Internal/everyone\"]")),
                "New role is added without user confirmation");
        clickOnView("view0");
        assertTrue(getDriver().isElementPresent(By.id("publisherrole1-0")), "Gadgets are removed mistakenly");
        addARoleToView("view0", "Internal everyone");
        getDriver().findElement(By.id("ues-modal-confirm-yes")).click();
        clickOnView("view0");
        assertTrue(getDriver().isElementPresent(By.cssSelector("div[data-role=\"Internal/everyone\"]")),
                "New role " + "addition failed");
        assertFalse(getDriver().isElementPresent(By.id("publisherrole1-0")),
                "Restricted gadgets are not removed " + "correctly after role addition");
    }

    /**
     * To test the functionality of role removal in multiple view support
     * @throws XPathExpressionException
     * @throws MalformedURLException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the functionality of removing roles",
            dependsOnMethods = "testRoleAddition")
    public void testRoleDeletion() throws XPathExpressionException, MalformedURLException, InterruptedException {
        clickOnViewSettings("view0");
        assertTrue(getDriver().isElementPresent(By.cssSelector("div[data-role=\"Internal/everyone\"]")),
                "Added role is " + "missing in the view");
        getDriver().findElement(By.cssSelector("div[data-role=\"Internal/everyone\"] .remove-button")).click();
        Thread.sleep(1000);
        assertFalse(getDriver().isElementPresent(By.cssSelector("div[data-role=\"Internal/everyone\"]")),
                "Removed role " + "is still visible in the view configurations");
        getDriver().findElement(By.cssSelector("div[data-role=\"" + ROLE1 + "\"] .remove-button")).click();
        assertTrue(getDriver().isElementPresent(By.cssSelector(".modal-body")),
                "Removal of last role is allowed in this view");
        getDriver().findElement(By.id("ues-modal-info-ok")).click();
    }
}