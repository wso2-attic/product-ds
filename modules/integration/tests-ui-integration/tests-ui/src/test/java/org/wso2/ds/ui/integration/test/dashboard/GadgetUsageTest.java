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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.testng.Assert.*;

/**
 * Tests related with the gadget usage related warnings
 */
public class GadgetUsageTest extends DSUIIntegrationTest {
    private static final String DASHBOARD1_TITLE = "gadgetusagedashboard1";
    private static final String DASHBOARD2_TITLE = "gadgetusagedashboard2";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public GadgetUsageTest(TestUserMode userMode) {
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
    public void setUp() throws AutomationUtilException, XPathExpressionException, IOException, InterruptedException {
        login(getCurrentUsername(), getCurrentPassword());
        deleteDashboards();
        addDashBoardWithoutLandingPage(DASHBOARD1_TITLE, "This is a test dashboard");
        addDashBoardWithoutLandingPage(DASHBOARD2_TITLE, "This is a test dashboard");
        createRelevantGadgets();
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
     * Create two dashboards with different set of gadgets and when trying to delete those gadgets, check the warning message
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Embedding a page outside dashboard")
    public void testWarningsWhenDeletingGadgets() throws MalformedURLException, XPathExpressionException,
            InterruptedException {
        getDriver().get(getBaseUrl() + "/portal/gadget/");
        Thread.sleep(2000);
        JavascriptExecutor js = ((JavascriptExecutor) getDriver());

        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        // Try to delete the gadget that is not used in any of the dashboards, and check the warning message
        getDriver().findElement(By.cssSelector("#usa-business-revenue > a.ds-asset-trash-handle")).click();
        Thread.sleep(2000);
        assertTrue(getDriver().isElementPresent(By.cssSelector("#usa-business-revenue .alert-warning")),
                "When trying to delete the gadget, " + "warning is not displayed");
        String warningMessage = getDriver().findElement(By.cssSelector(("#usa-business-revenue .alert"))).getText();
        String expectedWarningMessage = "Deleting gadget will completely remove this gadget and this action cannot be undone";
        assertTrue(warningMessage.contains(expectedWarningMessage), "Expected warning message is not displayed");

        // Try to delete the gadgets that is used in the dashboards, and check the warning message
        getDriver().findElement(By.cssSelector("#usa-map > a.ds-asset-trash-handle")).click();
        Thread.sleep(2000);
        assertTrue(getDriver().isElementPresent(By.cssSelector("#usa-map .alert-warning")),
                "When trying to delete the gadget, " + "warning is not displayed");
        warningMessage = getDriver().findElement(By.cssSelector(("#usa-map .alert"))).getText();
        expectedWarningMessage = "This gadget is used in " + DASHBOARD1_TITLE
                + " dashboard(s). Deleting this gadget will affect the functionality of those dashboard(s)";
        assertTrue(warningMessage.contains(expectedWarningMessage), "Expected warning message is not displayed");

        getDriver().findElement(By.cssSelector("#publisher > a.ds-asset-trash-handle")).click();
        Thread.sleep(2000);
        assertTrue(getDriver().isElementPresent(By.cssSelector("#publisher .alert-warning")),
                "When trying to delete the gadget, " + "warning is not displayed");
        warningMessage = getDriver().findElement(By.cssSelector(("#publisher .alert"))).getText();
        expectedWarningMessage = "This gadget is used in " + DASHBOARD1_TITLE + "," + DASHBOARD2_TITLE
                + " dashboard(s). Deleting this gadget will affect the functionality of those dashboard(s)";
        assertTrue(warningMessage.contains(expectedWarningMessage), "Expected warning message is not displayed");

        getDriver().findElement(By.cssSelector("#subscriber > a.ds-asset-trash-handle")).click();
        Thread.sleep(2000);
        assertTrue(getDriver().isElementPresent(By.cssSelector("#subscriber .alert-warning")),
                "When trying to delete the gadget, " + "warning is not displayed");
        warningMessage = getDriver().findElement(By.cssSelector(("#subscriber .alert"))).getText();
        expectedWarningMessage = "This gadget is used in " + DASHBOARD2_TITLE + " dashboard(s). "
                + "Deleting this gadget will affect the functionality of those dashboard(s)";
        assertTrue(warningMessage.contains(expectedWarningMessage), "Expected warning message is not displayed");
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
    }

    /**
     * To check whether the warning is displayed in the dashboard level and page level when some gadget is missing in
     * dashboard
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Testing the warnings in the dashboard after deleting the gadgets",
            dependsOnMethods = "testWarningsWhenDeletingGadgets")
    public void testWarningInDashboardsAfterDeletingGadgets() throws MalformedURLException, XPathExpressionException,
            InterruptedException {
        // Delete the gadget that is not used in any of the dashboards and check whether warning symbol is displayed
        getDriver().get(getBaseUrl() + "/portal/gadget/");
        JavascriptExecutor js = ((JavascriptExecutor) getDriver());

        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        getDriver().findElement(By.cssSelector("#usa-business-revenue > a.ds-asset-trash-handle")).click();
        getDriver().findElement(By.cssSelector("span.ladda-label")).click();
        Thread.sleep(2000);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        assertFalse(getDriver().isElementPresent(By.cssSelector(".fw-alert")),
                "Danger symbol is displayed in the " + "dashboard when dashboard has all the gadgets");

        // Delete the gadgets that is used in the dashboard and check whether danger symbol is displayed
        getDriver().get(getBaseUrl() + "/portal/gadget/");
        js = ((JavascriptExecutor) getDriver());
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        getDriver().findElement(By.cssSelector("#usa-map > a.ds-asset-trash-handle")).click();
        getDriver().findElement(By.cssSelector("span.ladda-label")).click();
        Thread.sleep(2000);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        assertTrue(getDriver().isElementPresent(By.cssSelector("#" + DASHBOARD1_TITLE + " .fw-alert")),
                "Danger symbol " + "is not displayed in the dashboard when some gadgets are deleted from dashboard");
        assertFalse(getDriver().isElementPresent(By.cssSelector("#" + DASHBOARD2_TITLE + " .fw-alert")),
                "Danger symbol is displayed in the " + "dashboard when dashboard has all the gadgets");
        getDriver().get(getBaseUrl() + "/portal/gadget/");
        js = ((JavascriptExecutor) getDriver());
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        getDriver().findElement(By.cssSelector("#publisher > a.ds-asset-trash-handle")).click();
        getDriver().findElement(By.cssSelector("span.ladda-label")).click();
        Thread.sleep(2000);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        assertTrue(getDriver().isElementPresent(By.cssSelector("#" + DASHBOARD2_TITLE + " .fw-alert")),
                "Danger symbol " + "is not displayed in the dashboard when some gadgets are deleted from dashoard");
        assertTrue(getDriver().isElementPresent(By.cssSelector("#" + DASHBOARD2_TITLE + " .fw-alert")),
                "Danger symbol " + "is not displayed in the dashboard when some gadgets are deleted from dashoard");

        //Check the page level warnings
        getDriver().findElement(By.cssSelector("#" + DASHBOARD1_TITLE + " a.ues-edit")).click();
        selectPane("pages");
        assertTrue(getDriver().isElementPresent(By.cssSelector("#pagesButtonpage0 .fw-alert")),
                "Pages with the problem is not shown to the user");
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD2_TITLE + " a.ues-edit")).click();
        selectPane("pages");
        assertTrue(getDriver().isElementPresent(By.cssSelector("#pagesButtonpage0 .fw-alert")),
                "Pages with the problem is not shown to the user");
    }

    /**
     * To test the whether the warnings are updated when a missing gadget is uploaded
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Testing the warnings after uploading some of the deleted gadgets",
            dependsOnMethods = "testWarningInDashboardsAfterDeletingGadgets")
    public void testWarningAfterUploadingAGadget() throws MalformedURLException, XPathExpressionException,
            InterruptedException {
        String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();
        String gadgetFilePath = systemResourceLocation + "files" + File.separator + "publisher.zip";
        getDriver().get(getBaseUrl() + "/portal/gadget/");
        getDriver().findElement(By.cssSelector("a[href*='upload-gadget']")).click();

        // Select the correct gadget zip file and upload
        WebElement inputElement = getDriver().findElement(By.id("selected-file"));
        getDriver()
                .executeScript("document.getElementById('selected-file').style.display='block';");
        inputElement.sendKeys(gadgetFilePath);
        getDriver().findElement(By.xpath("(//button[@type='button'])[4]")).click();
        Thread.sleep(1000);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        assertTrue(getDriver().isElementPresent(By.cssSelector("#" + DASHBOARD1_TITLE + " .fw-alert")),
                "Danger symbol " + "is not displayed in the dashboard when some gadgets are deleted from dashboard");
        assertFalse(getDriver().isElementPresent(By.cssSelector("#" + DASHBOARD2_TITLE + " .fw-alert")),
                "Danger symbol is displayed in the " + "dashboard when dashboard has all the gadgets");
    }

    /**
     * To create some gadgets in 2 dashboards
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    private void createRelevantGadgets() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD1_TITLE + " a.ues-edit")).click();
        String[][] gadgetMappings = { { "publisher", "b" }, { "usa-map", "c" } };
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        Thread.sleep(2000);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD2_TITLE + " a.ues-edit")).click();
        String[][] gadgetMapping = { { "publisher", "b" }, { "subscriber", "d" } };
        script = generateAddGadgetScript(gadgetMapping);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        Thread.sleep(2000);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
    }

}