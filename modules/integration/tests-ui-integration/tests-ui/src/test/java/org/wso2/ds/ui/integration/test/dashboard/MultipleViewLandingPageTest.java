/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import static org.testng.Assert.assertTrue;

/**
 * To check whether the landing page concept is maintained throughout the flow
 */
public class MultipleViewLandingPageTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "multipleviewlandingpagedashboard";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode") public MultipleViewLandingPageTest(TestUserMode userMode) {
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
        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, "This is a test dashboard");
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
     * To test the restrictions of landing page when there is only one page
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the restrictions when there are only one page")
    public void testSinglePage() throws MalformedURLException, XPathExpressionException {
        // Try to add anonymous role to first view, it should be allowed as there is only one page
        addARoleToView("default", "anonymous");
        getDriver().findElement(By.id("ues-modal-confirm-yes")).click();
        clickOnViewSettings("default");
        assertTrue(getDriver().isElementPresent(By.cssSelector("div[data-role=\"anonymous\"]")),
                "Anonymous role addition to landing page failed even though there are no other pages in the current "
                        + "dashboard");
    }

    /**
     * To test the restrictions of landing page when there are two pages
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the restrictions when there are only one page")
    public void testTwoPages() throws MalformedURLException, XPathExpressionException {
        // Creating a new page should not be allowed to the user as landing page doesn`t contain a view with
        // internal/everyone
        selectPane("pages");
        getDriver().findElement(By.cssSelector("button[rel='createPage']")).click();
        assertTrue(getDriver().isElementPresent(By.id("ues-modal-info-ok")),
                "Creation of second page " + "allowed when landing page doesn`t contain a view with internal everyone");
        getDriver().findElement(By.id("ues-modal-info-ok")).click();
        // After creating a view with internal/everyone in landing page, creation of another page should be allowed
        createNewView("single-column");
        addPageToDashboard();
        // Add a anonymous view in second page, it should be allowed as landing page contains a anonymous view
        createNewView("default-grid");
        addARoleToView("view0", "anonymous");
        getDriver().findElement(By.id("ues-modal-confirm-yes")).click();
        clickOnViewSettings("view0");
        assertTrue(getDriver().isElementPresent(By.cssSelector("div[data-role=\"anonymous\"]")),
                "Anonymous role addition to landing page failed even though there are no other pages in the current "
                        + "dashboard");
        // Go to landing page and try to remove the anonymous view
        getDriver().findElement(By.className("ues-switch-page-prev")).click();
        addARoleToView("default", "Internal everyone");
        assertTrue(getDriver().isElementPresent(By.id("ues-modal-info-ok")),
                "Deletion of anonymous view from landing page allowed when there is an anonymous view in other page");
        getDriver().findElement(By.id("ues-modal-info-ok")).click();
    }

}
