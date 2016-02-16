/*
 * Copyright 2005-2015 WSO2, Inc. (http://wso2.com)
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
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Tests the dashboard related functionality such as adding and removing blocks and gadgets, gadget maximization
 * and toggle fluid layout
 */
public class AddGadgetToDashboardTest extends DSUIIntegrationTest {
    private String carbonHome;
    private String systemResourceLocation;
    private static final String DASHBOARD_TITLE = "sampledashboard1";

    @Factory(dataProvider = "userMode")
    public AddGadgetToDashboardTest(TestUserMode userMode, String dashboardTitle) {
        super(userMode);
    }

    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE}};
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        String carbonHome = FrameworkPathUtil.getCarbonHome();
        String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();
        String pathToTestGadget = systemResourceLocation + "gadgets" + File.separator + "user-claims-gadget.zip";
        String pathToTarget = carbonHome + File.separator + "repository" + File.separator + "deployment" + File
                .separator + "server" + File.separator + "jaggeryapps" + File.separator + "portal" + File.separator +
                "store" + File.separator + "carbon.super" + File.separator + "gadget" + File.separator +
                "user-claims-gadget.zip" ;

        AutomationContext automationContext = new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME, this.userMode);
        ServerConfigurationManager serverConfigurationManager = new ServerConfigurationManager(automationContext);

        serverConfigurationManager.applyConfigurationWithoutRestart(new File(pathToTestGadget),
                new File(pathToTarget), false);
        serverConfigurationManager.restartGracefully();

        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, "This is a test dashboard");

    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        try {
            logout();
        } finally {
            getDriver().quit();
        }
    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding blocks to an existing dashboard")
    public void testAddBlocks() throws Exception {
        DSWebDriver driver = getDriver();

        redirectToLocation("portal", "dashboards");
        driver.findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        selectPane("layouts");
        driver.findElement(By.id("ues-add-block-btn")).click();

        clickViewButton();
        pushWindow();

        assertTrue(isBlockPresent("a"), "The block 'a' does not exist");
        driver.close();
        popWindow();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Removing blocks from an existing dashboard",
            dependsOnMethods = "testAddBlocks")
    public void testRemoveBlock() throws Exception {
        DSWebDriver driver = getDriver();
        driver.findElement(By.cssSelector("#a.ues-component-box .ues-trash-handle")).click();
        driver.findElement(By.cssSelector("input[value='block']")).click();
        driver.findElement(By.id("btn-delete")).click();

        // TODO: change the behaviour in the dashboard to reflect the change after saving the change. Then remove sleep
        Thread.sleep(500);

        clickViewButton();
        pushWindow();

        assertFalse(isBlockPresent("a"), "The block 'a' exists after deletion");
        driver.close();
        popWindow();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding gadgets to an existing dashboard from dashboard server",
            dependsOnMethods = "testRemoveBlock")
    public void testAddGadgetToDashboard() throws Exception {
        DSWebDriver driver = getDriver();
        String[][] gadgetMappings = {{"publisher", "b"}, {"usa-map", "c"}};
        String script = generateAddGadgetScript(gadgetMappings);
        boolean gadgetsAvailable = true;
        selectPane("gadgets");
        driver.executeScript(script);

        // TODO: change the behaviour in the dashboard to reflect the change after saving the change. Then remove sleep
        Thread.sleep(500);

        clickViewButton();

        pushWindow();

        List<WebElement> elements = new ArrayList<WebElement>();
        for (String[] mapping : gadgetMappings) {
            WebElement element = driver.findElement(By.cssSelector("div#" + mapping[1] + ".ues-component-box .ues-component"));
            if (element != null) {
                elements.add(element);
            }
        }

        if (elements.size() != gadgetMappings.length) {
            gadgetsAvailable = false;
        }

        assertTrue(gadgetsAvailable, "The gadget(s) not found");
        driver.close();
        popWindow();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Accessing user claims from a gadget deployed in dashboard " +
    "server",dependsOnMethods = "testAddGadgetToDashboard")
    public void testUserClaimsInGadget() throws Exception {
        DSWebDriver driver = getDriver();
        String[][] gadgetMappings = {{"user-claims-gadget", "d"}};
        String script = generateAddGadgetScript(gadgetMappings);
        driver.navigate().refresh();
        Thread.sleep(2000);
        selectPane("gadgets");
        Thread.sleep(2000);
        driver.executeScript(script);
        // TODO: change the behaviour in the dashboard to reflect the change after saving the change. Then remove sleep
        Thread.sleep(2000);
        clickViewButton();
        pushWindow();
        Thread.sleep(3000);
        Object txt = driver.executeScript(
                "var iframe = $(\"iframe[title='User Claims']\")[0];" +
                        "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                        "return innerDoc.getElementById('output').textContent;"
        );
        assertEquals("admin", txt.toString());
        driver.close();
        popWindow();
    }

    @Test(groups = "wso2.ds.dashboard", description = "maximizing gadget which added to dashboard", dependsOnMethods
            = "testUserClaimsInGadget", enabled = false)
    public void testMaximizeGadgetInView() throws Exception {
        DSWebDriver driver = getDriver();

        clickViewButton();
        pushWindow();

        // This sleep is used to wait until the content of the iframe appears
        Thread.sleep(200);

        Object txt = driver.executeScript(
                "var iframe = $(\"iframe[title='USA Map']\")[0];" +
                        "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                        "return innerDoc.getElementById('defaultViewLabel').textContent;"
        );

        assertEquals("USA MAP (This is default view)", txt.toString());

        String showToolbarScript =
                "for(i = 0; i < document.getElementsByClassName('ues-component-toolbar').length; i++) {" +
                        "    document.getElementsByClassName('ues-component-toolbar')[i].style.display = 'inline';" +
                        "}";

        driver.executeScript(showToolbarScript);
        driver.findElement(By.cssSelector("#c button.ues-component-full-handle")).click();

        // This sleep is used to wait until the content of the iframe appears
        Thread.sleep(200);

        //maximized Window view
        Object txtMax = driver.executeScript(
                "var iframe = $(\"iFrame[title='USA Map']\")[0];" +
                        "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                        "return innerDoc.getElementById('fullViewLabel').textContent;"
        );

        assertEquals("USA MAP (this is full screen view)", txtMax.toString());

        driver.close();
        popWindow();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Test fluid layout",
            dependsOnMethods = "testMaximizeGadgetInView", enabled = false)
    public void testFluidLayout() throws MalformedURLException, XPathExpressionException {
        boolean isFluidLayout = false;
        DSWebDriver driver = getDriver();

        selectPane("pages");
        driver.findElement(By.cssSelector("[name=landing]")).click();
        driver.findElement(By.cssSelector("[name=fluidLayout]")).click();

        clickViewButton();
        pushWindow();

        List<WebElement> elements = getDriver().findElements(By.cssSelector(".page-content-wrapper > .container-fluid"));
        if (elements.size() > 0) {
            isFluidLayout = true;
        }

        assertTrue(isFluidLayout, "The layout is not fluid");
        driver.close();
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
    private boolean isBlockPresent(String id) throws Exception {
        DSWebDriver driver = getDriver();

        // reduce the timeout to 2 seconds
        modifyTimeOut(2);

        List<WebElement> elements = driver.findElements(By.cssSelector("div#" + id + ".ues-component-box"));

        // restore the original timeout value
        resetTimeOut();

        return (elements.size() > 0);
    }
}
