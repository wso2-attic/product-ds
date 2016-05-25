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

import ds.integration.tests.common.domain.DSIntegrationTestConstants;
import org.openqa.selenium.By;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.testng.Assert.*;

/**
 * Tests related with the embeddable gadget feature
 */
public class EmbeddableGadgetTest extends DSUIIntegrationTest {
    private static final String DASHBOARD1_TITLE = "sampledashboard1";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public EmbeddableGadgetTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return user modes
     */
    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][] { { TestUserMode.SUPER_TENANT_ADMIN} };
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
        addDashBoard(DASHBOARD1_TITLE, "This is a test dashboard");
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
     * Creates a page with gadgets and embeds the page and checks whether same gadgets exist and
     * no other additional gadgets are rendered.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Embedding a page outside dashboard")
    public void testEmbeddingPage() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD1_TITLE + " a.ues-edit")).click();
        String[][] gadgetMappings = { { "publisher", "b" }, { "usa-map", "c" } };
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        Thread.sleep(2000);
        redirectToLocation(DS_HOME_CONTEXT, "gadgets/" + DASHBOARD1_TITLE + "/landing");
        Thread.sleep(2000);
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("usa-map-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        assertTrue(getDriver().findElements(By.id("subscriber-0")).size() < 1,
                "Subscriber gadget is displayed in the page");
    }

    /**
     * Checks whether only one gadget exist when embedding single gadget
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Embedding a gadget outside dashboard",
            dependsOnMethods = "testEmbeddingPage")
    public void testEmbeddingGadget() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, "gadgets/" + DASHBOARD1_TITLE + "/landing/usa-map-0");
        Thread.sleep(2000);
        assertTrue(getDriver().findElement(By.id("usa-map-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        assertTrue(getDriver().findElements(By.id("subscriber-0")).size() < 1,
                "Subscriber gadget is displayed in the page");
        assertTrue(getDriver().findElements(By.id("publisher-0")).size() < 1,
                "Publisher gadget is displayed in the page");
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
    }

    /**
     * Checks whether the gadgets are shown after proper authentication
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Verify gadgets are displayed after proper authentication",
            dependsOnMethods = "testEmbeddingPage")
    public void testAuthentication() throws MalformedURLException, XPathExpressionException, InterruptedException {
        logout();
        Thread.sleep(2000);
        redirectToLocation(DS_HOME_CONTEXT, "gadgets/" + DASHBOARD1_TITLE + "/landing/usa-map-0");
        assertTrue(getDriver().findElements(By.id("usa-map-0")).size() < 1,
                "USA map gadget is displayed in the page without proper authentication");

        String errorMessage = "You do not have permission to access this page.Please contact your administrator and "
                + "request permission.";
        String bodyText = getDriver().findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains(errorMessage), "Un authorized error message is not correctly displayed");
        login(getCurrentUsername(), getCurrentPassword());
    }

    /**
     * Checks whether the correct error message is displayed when the request cannot be served.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Verify the error messages for different faulty requests",
            dependsOnMethods = "testEmbeddingPage")
    public void testErrorMessages() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, "gadgets/" + DASHBOARD1_TITLE);
        String errorMessage = "We are unable to understand the request and process it. Please re-check your request.";
        String bodyText = getDriver().findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains(errorMessage), "Bad request error message is not correctly displayed");

        redirectToLocation(DS_HOME_CONTEXT, "gadgets/" + DASHBOARD1_TITLE + "/landing/usa");
        errorMessage = "We can't find what you are looking for.";
        bodyText = getDriver().findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains(errorMessage), "Page not found error message is not correctly displayed");

        redirectToLocation(DS_HOME_CONTEXT, "gadgets/" + DASHBOARD1_TITLE + "/page0");
        errorMessage = "We can't find what you are looking for.";
        bodyText = getDriver().findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains(errorMessage), "Page not found error message is not correctly displayed");
    }

    /**
     * Checks whether embeddable gadget feature works in multi-tenant scenario
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Verify embedding functionality in muti-tenant scenario")
    public void testMultiTenant() throws MalformedURLException, XPathExpressionException, InterruptedException {
        logout();

        // Login as a tenant user and create dashboard with some gadgets
        AutomationContext automationContext = new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME,
                TestUserMode.TENANT_USER);
        User editor = automationContext.getContextTenant().getTenantUser("editor");
        login(editor.getUserName(), editor.getPassword());

        getDriver().findElement(By.cssSelector("a[href*='create-dashboard']")).click();
        getDriver().findElement(By.id("ues-dashboard-title")).clear();
        getDriver().findElement(By.id("ues-dashboard-title")).sendKeys(DASHBOARD1_TITLE);
        getDriver().findElement(By.id("ues-dashboard-description")).clear();
        getDriver().findElement(By.id("ues-dashboard-description")).sendKeys("This is a test dashboard");
        getDriver().findElement(By.id("ues-dashboard-create")).click();
        selectLayout("default-grid");
        String[][] gadgetMappings = { { "publisher", "a" }, { "subscriber", "b" } };
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        Thread.sleep(2000);

        // Go to a page embedding URL and check whether gadgets are displayed in the page
        redirectToLocation(DS_HOME_CONTEXT, "t/" + editor.getUserDomain() + "/gadgets/" + DASHBOARD1_TITLE + "/landing");
        Thread.sleep(2000);
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("subscriber-0")).isDisplayed(),
                "Subscriber gadget is not displayed in the page");

        // Go to a gadget embedding URL of super domain user and verify whether the tenant user is not allowed to view it
        redirectToLocation(DS_HOME_CONTEXT, "gadgets/" + DASHBOARD1_TITLE + "/landing");
        String errorMessage = "You do not have permission to access this page.Please contact your administrator "
                + "and request permission.";
        String bodyText = getDriver().findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains(errorMessage), "Un authorized error message is not correctly displayed");
        redirectToLocation(DS_HOME_CONTEXT, "t/" + editor.getUserDomain() + "/dashboards");
        getDriver().findElement(By.cssSelector(".dropdown")).click();
        getDriver().findElement(By.cssSelector(".dropdown-menu > li > a")).click();
        login(getCurrentUsername(), getCurrentPassword());
    }

}
