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
public class MultipleViewRoleTest extends DSUIIntegrationTest{
    private static final String DASHBOARD_TITLE = "multipleviewroledashboard";
    private static final String ROLE1 = "multipleviewrole1";
    private static final String ROLE2 = "multipleviewrole2";
    private static final String USERNAME_ROLE1 = "role1";
    private static final String PASSWORD_ROLE1 = "role1";
    private static final String USERNAME_ROLE2 = "role2";
    private static final String PASSWORD_ROLE2 = "role2";


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
    @BeforeClass(alwaysRun = true)
    public void setUp() throws AutomationUtilException, XPathExpressionException, IOException {
        String[] userListForRole1 = {getCurrentUsername(), USERNAME_ROLE1};
        String[] userListForRole2 = {getCurrentUsername(), USERNAME_ROLE2};
        String[] userList = {USERNAME_ROLE1, USERNAME_ROLE2};
        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, "This is a test dashboard");
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
        addUser(USERNAME_ROLE1, PASSWORD_ROLE1, PASSWORD_ROLE1);
        addUser(USERNAME_ROLE2, PASSWORD_ROLE2, PASSWORD_ROLE2);
        addRole(ROLE1);
        assignRoleToUser(userListForRole1);
        addRole(ROLE2);
        assignRoleToUser(userListForRole2);
        assignInternalRoleToUser(DASHBOARD_TITLE + "-viewer", userList);
        addLoginRole(USERNAME_ROLE1);
        addLoginRole(USERNAME_ROLE2);
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
    public void testAnonView() throws MalformedURLException, XPathExpressionException{
        String[][] gadgetMappings = { { "gadget-resize", "b" } };
        String script = generateAddGadgetScript(gadgetMappings);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-edit")).click();
        getDriver().findElement(By.xpath("(//button[@type='button'])[10]")).click();
        getDriver().findElement(By.id("ds-view-roles")).click();
        getDriver().findElement(By.id("ds-view-roles")).sendKeys("anonymous");
        getDriver().findElement(By.className("tt-highlight")).click();
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
    @Test(groups = "wso2.ds.dashboard", description = "Checking the view based on roles", dependsOnMethods = "testAnonView")
    public void testViewRole() throws MalformedURLException, XPathExpressionException {
        String[][] gadgetMappings = { { "publisherrole1", "a" } };
        String script = generateAddGadgetScript(gadgetMappings);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-edit")).click();
        assertFalse(getDriver().isElementPresent(By.id("publisherrole1")),
                "Gadgets that do not have internal role are visible in gadget pane");
        createNewView("single-column");
        getDriver().findElement(By.xpath("(//button[@type='button'])[13]")).click();
        getDriver().findElement(By.id("ds-view-roles")).click();
        getDriver().findElement(By.id("ds-view-roles")).sendKeys(ROLE1);
        getDriver().findElement(By.className("tt-highlight")).click();
        getDriver().findElement(By.cssSelector("div[data-role=\"Internal/everyone\"] .remove-button")).click();
        clickOnView("view0");
        assertTrue(getDriver().isElementPresent(By.id("publisherrole1")),
                "Gadgets that have role1 is not visible in gadgets pane");
        selectPane("gadgets");
        getDriver().executeScript(script);
        assertTrue(getDriver().isElementPresent(By.id("publisherrole1-0")),
                "Gadgets that have role1 is not visible in gadgets pane");
        clickViewButton();
        pushWindow();
        assertTrue(getDriver().findElement(By.id("publisherrole1-0")).isDisplayed(),
                "Gadget is displayed in view mode");
        assertTrue(getDriver().findElement(By.id("ds-allowed-view-list")).isDisplayed(),
                "Gadget is displayed in view mode");
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
                "Gadget is displayed in view mode");
        assertFalse(getDriver().findElement(By.id("ds-allowed-view-list")).isDisplayed(),
                "When only one view is viewable drop down is displayed");
        getDriver().close();
        popWindow();
    }
}