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
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import org.apache.commons.io.FileUtils;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Tests the dashboard related functionality such as adding and removing blocks and gadgets, gadget
 * maximization and toggling fluid layout.
 */
public class AddGadgetToDashboardTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "sampledashboard1";

    /**
     * Initializes the class.
     *
     * @param userMode       user mode
     * @param dashboardTitle title of the dashboard
     */
    @Factory(dataProvider = "userMode")
    public AddGadgetToDashboardTest(TestUserMode userMode, String dashboardTitle) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return
     */
    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE}};
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
        try {
            logout();
        } finally {
            getDriver().quit();
        }
    }

    /**
     * Adds a new block and check whether the block presents in the view mode.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Adding blocks to an existing dashboard")
    public void testAddBlocks() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        selectPane("layouts");
        getDriver().findElement(By.id("ues-add-block-btn")).click();
        clickViewButton();
        pushWindow();
        assertTrue(isBlockPresent("a"), "The block 'a' does not exist");
        getDriver().close();
        popWindow();
    }

    /**
     * Removes a block and check whether the block presents in the view mode.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Removing blocks from an existing dashboard",
            dependsOnMethods = "testAddBlocks")
    public void testRemoveBlock() throws MalformedURLException, XPathExpressionException, InterruptedException {
        getDriver().findElement(By.cssSelector("#a.ues-component-box .ues-trash-handle")).click();
        getDriver().findElement(By.id("btn-delete")).click();
        clickViewButton();
        pushWindow();
        assertFalse(isBlockPresent("a"), "The block 'a' exists after deletion");
        getDriver().close();
        popWindow();
    }

    /**
     * Adds gadgets to the designer and check whether the gadgets available in the view mode.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Adding gadgets to an existing dashboard from dashboard server",
            dependsOnMethods = "testRemoveBlock")
    public void testAddGadgetToDashboard()
            throws MalformedURLException, XPathExpressionException, InterruptedException {
        String[][] gadgetMappings = {{"publisher", "b"}, {"usa-map", "c"}};
        String script = generateAddGadgetScript(gadgetMappings);
        boolean gadgetsAvailable = true;
        selectPane("gadgets");
        getDriver().executeScript(script);
        clickViewButton();
        pushWindow();
        List<WebElement> elements = new ArrayList<WebElement>();
        for (String[] mapping : gadgetMappings) {
            WebElement element = getDriver().findElement(
                    By.cssSelector("div#" + mapping[1] + ".ues-component-box .ues-component"));
            if (element != null) {
                elements.add(element);
            }
        }

        if (elements.size() != gadgetMappings.length) {
            gadgetsAvailable = false;
        }

        assertTrue(gadgetsAvailable, "The gadget(s) not found");
        getDriver().close();
        popWindow();
    }

    /**
     * Tests accessing user claims from a gadget
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Accessing user claims from a gadget deployed in dashboard " +
            "server", dependsOnMethods = "testAddGadgetToDashboard")
    public void testUserClaimsInGadget() throws MalformedURLException, XPathExpressionException, InterruptedException {
        String[][] gadgetMappings = {{"user-claims-gadget", "d"}};
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        clickViewButton();
        pushWindow();
        Thread.sleep(3000);
        Object txt = getDriver().executeScript(
                "var iframe = $(\"iframe[title='User Claims']\")[0];" +
                        "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                        "return innerDoc.getElementById('output').textContent;"
        );
        assertEquals(txt.toString(), "admin");
        getDriver().close();
        popWindow();
    }

    /**
     * Tests gadget hide and show feature
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Using hide-show gadget feature " +
            "server", dependsOnMethods = "testUserClaimsInGadget")
    public void testShowHideGadget() throws MalformedURLException, XPathExpressionException, InterruptedException {
        DSWebDriver driver = getDriver();
        String[][] gadgetMappings = {{"subscriber", "e"}};
        String script = generateAddGadgetScript(gadgetMappings);
        driver.navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        driver.executeScript(script);
        driver.findElement(By.id("optionsButtonEvents")).click();
        driver.findElement(By.linkText("subscriber")).click();
        driver.findElement(By.className("notifier")).click();
        driver.findElement(By.name("hide_gadget")).click();
        clickViewButton();
        pushWindow();
        assertFalse(driver.findElement(By.id("subscriber-0")).isDisplayed());
        driver.executeScript(
                "var iframe = $(\"iframe[title='Publisher']\")[0];" +
                        "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                        "innerDoc.getElementById('txtMessage').value =\"test\";" +
                        "innerDoc.getElementById('btnSend').click();"
        );
        assertTrue(driver.findElement(By.id("subscriber-0")).isDisplayed());
        driver.close();
        popWindow();
    }

    /**
     * Tests gadget maximization in the view mode.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "maximizing gadget which added to dashboard",
            dependsOnMethods = "testShowHideGadget")
    public void testMaximizeGadgetInView() throws MalformedURLException, XPathExpressionException, InterruptedException {
        clickViewButton();
        pushWindow();
        // This sleep is used to wait until the content of the IFRAME appears.
        Thread.sleep(200);
        Object txt = getDriver().executeScript(
                "var iframe = $(\"iframe[title='USA Map']\")[0];" +
                        "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                        "return innerDoc.getElementById('defaultViewLabel').textContent;"
        );
        assertEquals("USA MAP (This is default view)", txt.toString());
        getDriver().findElement(By.cssSelector("#c button.ues-component-full-handle")).click();
        // This sleep is used to wait until the content of the iframe appears
        Thread.sleep(200);
        //maximized Window view
        Object txtMax = getDriver().executeScript(
                "var iframe = $(\"iFrame[title='USA Map']\")[0];" +
                        "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                        "return innerDoc.getElementById('fullViewLabel').textContent;"
        );
        assertEquals("USA MAP (this is full screen view)", txtMax.toString());
        getDriver().close();
        popWindow();
    }

    /**
     * Toggle fluid layout in the dashboard and check for the fluid layout in the view mode.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Test fluid layout",
            dependsOnMethods = "testMaximizeGadgetInView")
    public void testFluidLayout() throws MalformedURLException, XPathExpressionException {
        boolean isFluidLayout = false;
        selectPane("pages");
        getDriver().findElement(By.cssSelector("[name=landing]")).click();
        getDriver().findElement(By.cssSelector("[name=fluidLayout]")).click();
        clickViewButton();
        pushWindow();
        List<WebElement> elements = getDriver().findElements(By.cssSelector(".page-content-wrapper > .container-fluid"));
        if (elements.size() > 0) {
            isFluidLayout = true;
        }
        assertTrue(isFluidLayout, "The layout is not fluid");
        getDriver().close();
        popWindow();
    }

    /**
     * Check whether a block exists
     *
     * @param id ID of the block
     * @return
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private boolean isBlockPresent(String id) throws MalformedURLException, XPathExpressionException {
        // reduce the timeout to 2 seconds
        modifyTimeOut(2);
        List<WebElement> elements = getDriver().findElements(By.cssSelector("div#" + id + ".ues-component-box"));
        // restore the original timeout value
        resetTimeOut();
        return (elements.size() > 0);
    }

    /**
     * Copy all the test gadgets located at resources directory to the pack used for testing
     *
     * @throws XPathExpressionException
     * @throws AutomationUtilException
     * @throws IOException
     */
    @BeforeSuite
    public void copyTestGadgets() throws XPathExpressionException, AutomationUtilException, IOException {
        String carbonHome = FrameworkPathUtil.getCarbonHome();
        String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();
        String pathToGadgets = systemResourceLocation + "gadgets";
        String DestinationPath = carbonHome + File.separator + "repository" + File.separator + "deployment" +
                File.separator + "server" + File.separator + "jaggeryapps" + File.separator + "portal" +
                File.separator + "store" + File.separator + "carbon.super" + File.separator + "gadget";
        File source = new File(pathToGadgets);
        File destination = new File(DestinationPath);
        FileUtils.copyDirectory(source, destination);
        AutomationContext automationContext =
                new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME, this.userMode);
        ServerConfigurationManager serverConfigurationManager = new ServerConfigurationManager(automationContext);
        serverConfigurationManager.restartGracefully();
    }
}
