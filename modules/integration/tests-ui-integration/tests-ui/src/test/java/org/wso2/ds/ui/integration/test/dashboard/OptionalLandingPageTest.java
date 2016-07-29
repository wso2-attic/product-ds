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
    public void setUp() throws AutomationUtilException, XPathExpressionException, IOException {
        String[] userListForRole1 = {getCurrentUsername(), USERNAME_EDITOR};
        String[] userListForRole2 = {getCurrentUsername(), USERNAME_VIEWER};
        login(getCurrentUsername(), getCurrentPassword());
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
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the basic flow of dashboard page creation when landing "
            + "page is optional")
    public void testCreateDashboard() throws XPathExpressionException, MalformedURLException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " .ues-edit")).click();
        addARoleToView("default", ROLE1);
        // Check whether the removal of internal everyone is allowed from the landing page
        getDriver().findElement(By.cssSelector("div[data-role=\"Internal/everyone\"] .remove-button")).click();
        clickOnView("default");
        getDriver().findElement(By.cssSelector("li[data-view-mode=\"default\"] .ues-view-component-properties-handle"))
                .click();
        assertTrue(getDriver().isElementPresent(By.cssSelector("div[data-role=\"Internal/everyone\"]")),
                "Removal of internal everyone is not allowed even though landing page is not there");

        // Check the page creation when there is no internal/everyone role in first page
        addPageToDashboard("default-grid");
        assertFalse(getDriver().isElementPresent(By.cssSelector("div.modal-body")), "Adding a new page is not allowed "
                + "when landing page doesn`t contain internal/everyone role even though landing page is optional");
        assertTrue(getDriver().isElementPresent(By.id("default")),
                "When creating new page default view is not created");
        addARoleToView("default", ROLE2);
        getDriver().findElement(By.cssSelector("div[data-role=\"Internal/everyone\"] .remove-button")).click();

    }


}
