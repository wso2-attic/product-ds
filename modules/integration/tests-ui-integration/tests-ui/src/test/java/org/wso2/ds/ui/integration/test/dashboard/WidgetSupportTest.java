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
import org.wso2.ds.ui.integration.util.DSWebDriver;

import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.testng.Assert.assertTrue;

/**
 * Tests support for widgets in a dashboard.
 */
public class WidgetSupportTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "widgetsupportdashboard";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public WidgetSupportTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return
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
    public void setUp() throws XPathExpressionException, IOException, AutomationUtilException {
        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, "This is widget support test dashboard");
    }

    /**
     * Tests gadget resize functionality.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Test gadget resize")
    public void testResizeGadget() throws MalformedURLException, XPathExpressionException, InterruptedException {
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        String[][] gadgetMappings = {{"gadget-resize", "b"}};
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        clickViewButton();
        pushWindow();
        Thread.sleep(3000);
        // Get the original dimension
        Dimension originalDimension = getGadgetBlockSize(getDriver());

        // click resize button
        getDriver().executeScript("var iframe = $(\"iframe[title='Gadget Resize']\")[0];" +
                "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                "innerDoc.getElementById('btn-resize').click();");
        Thread.sleep(500);
        Dimension dimension = getGadgetBlockSize(getDriver());
        assertTrue(dimension.getWidth() == 800 && dimension.getHeight() == 400, "Gadget not resized");

        // click the restore button
        getDriver().executeScript("var iframe = $(\"iframe[title='Gadget Resize']\")[0];" +
                "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                "innerDoc.getElementById('btn-restore').click();");
        Thread.sleep(500);
        dimension = getGadgetBlockSize(getDriver());
        assertTrue(dimension.getWidth() == originalDimension.getWidth() &&
                dimension.getHeight() == originalDimension.getHeight(), "Gadget not restored");
    }

    /**
     * Get size of the gadget block.
     *
     * @param driver Web driver
     * @return Size of the gadget
     */
    private Dimension getGadgetBlockSize(DSWebDriver driver) {
        Object width = driver.executeScript("return $('#b.ues-component-box').closest('.grid-stack-item').width();");
        Object height = driver.executeScript("return $('#b.ues-component-box').closest('.grid-stack-item').height();");
        return new Dimension(Integer.parseInt(width.toString()), Integer.parseInt(height.toString()));
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
}
