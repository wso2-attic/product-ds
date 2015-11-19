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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests the dashboard related functionality such as adding and removing blocks and gadgets, gadget maximization
 * and toggle fluid layout
 *
 * @author Lasantha Samarakoon <lasanthas@wso2.com>
 */
public class AddGadgetToDashboardTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "sampledashboard1";

    private String designerWindowHandle;

    @Factory(dataProvider = "userMode")
    public AddGadgetToDashboardTest(TestUserMode userMode) {
        super(userMode);
    }

    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN}};
    }

    /**
     * Get JS script to simulate adding gadgets to the page
     *
     * @param mappings array of gadget mappings in { gadget id, target id} format
     * @return JS script
     */
    private static String getAddGadgetScript(String[][] mappings) {

        String script =
                "$('.ues-thumbnail').draggable({" +
                        "    cancel: false," +
                        "    appendTo: 'body'," +
                        "    helper: 'clone'," +
                        "    start: function (event, ui) {" +
                        "        ui.helper.addClass('ues-store-thumbnail');" +
                        "    }," +
                        "    stop: function (event, ui) {" +
                        "        ui.helper.removeClass('ues-store-thumbnail');" +
                        "    }" +
                        "});" +
                        "function performDrag(id, targetId) {" +
                        "    var gadget = $('[data-id=' + id + ']');" +
                        "    var target = $('#' + targetId);" +
                        "    " +
                        "    var gadgetOffset = gadget.offset();" +
                        "    var targetOffset = target.offset();" +
                        "    " +
                        "    var dx = targetOffset.left - gadgetOffset.left;" +
                        "    var dy = targetOffset.top - gadgetOffset.top;" +
                        "    " +
                        "    gadget.simulate('drag', { dx: dx, dy: dy});" +
                        "}";

        for (String[] mapping : mappings) {
            script += "performDrag('" + mapping[0] + "', '" + mapping[1] + "');";
        }

        return script;
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {

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

    @Test(groups = "wso2.ues.dashboard", description = "Adding blocks from an existing dashboard")
    public void testAddBlocks() throws MalformedURLException, XPathExpressionException {

        DSWebDriver driver = getDriver();

        navigateToDesigner(DASHBOARD_TITLE);

        driver.findElement(By.cssSelector("#ues-add-block-menu-item > a")).click();
        driver.findElement(By.id("ues-add-block-btn")).click();

        viewDashboard();

        assertTrue(isBlockExists("a"), "The block 'a' does not exists");
        backToDesigner();
    }

    @Test(groups = "wso2.ues.dashboard", description = "Removing blocks from an existing dashboard",
            dependsOnMethods = "testAddBlocks")
    public void testRemoveBlock() throws MalformedURLException, XPathExpressionException, InterruptedException {

        DSWebDriver driver = getDriver();
        driver.findElement(By.cssSelector("#a.ues-component-box .ues-component-box-remove-handle")).click();

        synchronized (driver) {
            driver.wait(2000);
        }

        viewDashboard();

        assertFalse(isBlockExists("a"), "The block 'a' exists after deletion");
        backToDesigner();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding gadgets to existing dashboard from dashboard server",
            dependsOnMethods = "testRemoveBlock")
    public void testAddGadgetToDashboard() throws Exception {
        DSWebDriver driver = getDriver();

        String[][] gadgetMappings = {{"g1", "b"}, {"usa-map", "c"}};
        String script = getAddGadgetScript(gadgetMappings);

        driver.findElement(By.cssSelector("i.fw.fw-pie-chart")).click();
        driver.executeScript(script);

        synchronized (driver) {
            driver.wait(2000);
        }

        viewDashboard();

        // verify the existence of gadgets in the view dashboard mode
        boolean gadgetsAvailable = true;
        for (String[] mapping : gadgetMappings) {

            List<WebElement> elements = driver.findElements(By.cssSelector("div#" + mapping[1] + ".ues-component-box .ues-component"));
            if (elements.size() == 0) {
                gadgetsAvailable = false;
            }
        }

        assertTrue(gadgetsAvailable, "The gadget(s) not found");

        backToDesigner();
    }

    @Test(groups = "wso2.ues.dashboard", description = "Test fluid layout",
            dependsOnMethods = "testAddGadgetToDashboard")
    public void testFluidLayout() throws MalformedURLException, XPathExpressionException {

        DSWebDriver driver = getDriver();

        driver.findElement(By.cssSelector("a.ues-page-properties-toggle")).click();
        driver.findElement(By.cssSelector("#ues-properties input[name=fluidLayout]")).click();

        viewDashboard();

        boolean isFluidLayout = false;

        List<WebElement> elements = getDriver().findElements(By.cssSelector("#wrapper > .container-fluid"));
        if (elements.size() > 0) {
            isFluidLayout = true;
        }

        assertTrue(isFluidLayout, "The layout is not fluid");
        backToDesigner();
    }

    /**
     * Navigate to designer from the DS home page
     *
     * @param id ID of the dashboard
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void navigateToDesigner(String id) throws MalformedURLException, XPathExpressionException {

        getDriver().findElement(By.cssSelector("#" + id + " a.ues-edit")).click();
    }

    /**
     * Navigate to dashboard view mode
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void viewDashboard() throws MalformedURLException, XPathExpressionException {

        DSWebDriver driver = getDriver();

        driver.findElement(By.cssSelector("a.ues-dashboard-preview")).click();

        // get the designer's window handle
        designerWindowHandle = driver.getWindowHandle();

        // iterate through all the window handles and switch to the child window
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(designerWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    /**
     * Return to the designer from a child window
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void backToDesigner() throws MalformedURLException, XPathExpressionException {

        DSWebDriver driver = getDriver();
        driver.close();
        driver.switchTo().window(designerWindowHandle);
    }

    /**
     * Check whether a block exists
     *
     * @param id ID of the block
     * @return
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private boolean isBlockExists(String id) throws MalformedURLException, XPathExpressionException {

        List<WebElement> elements = getDriver().findElements(By.cssSelector("div#" + id + ".ues-component-box"));
        return (elements.size() > 0);
    }
}
