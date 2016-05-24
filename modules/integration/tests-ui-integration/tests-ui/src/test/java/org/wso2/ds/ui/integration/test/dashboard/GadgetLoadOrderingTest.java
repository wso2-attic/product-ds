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

import ds.integration.tests.common.domain.DSIntegrationTestConstants;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import java.util.Date;

/**
 * Test load ordering for gadgets with various priorities
 */
public class GadgetLoadOrderingTest extends DSUIIntegrationTest {

    private static final String DASHBOARD_TITLE = "sampledashboardPriorityTest";

    private static final String UESDASHBOARDS_GADGETSTORE_PATH = "/store" + File.separator + "carbon.super" + File.separator + "fs" + File.separator + "gadget";

    /**
     * Initializes the class.
     *
     * @param userMode       user mode
     * @param dashboardTitle title of the dashboard
     */
    @Factory(dataProvider = "userMode")
    public GadgetLoadOrderingTest(TestUserMode userMode, String dashboardTitle) {
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
        String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();
        String gadgetStorePath = getPortalFilePath(UESDASHBOARDS_GADGETSTORE_PATH);
        String testGadgetPathLowPriority = systemResourceLocation + "gadgets/textbox-testGadgetLowPriority.zip";
        String testGadgetPathMediumPriority = systemResourceLocation + "gadgets/textbox-testGadgetMediumPriority.zip";
        String testGadgetPathHighPriority = systemResourceLocation + "gadgets/textbox-testGadgetHighPriority.zip";

        FileUtils.copyFileToDirectory(new File(testGadgetPathLowPriority), new File(gadgetStorePath));
        FileUtils.copyFileToDirectory(new File(testGadgetPathMediumPriority), new File(gadgetStorePath));
        FileUtils.copyFileToDirectory(new File(testGadgetPathHighPriority), new File(gadgetStorePath));

        // Restart the server after the custom gadgets are loaded
        AutomationContext automationContext =
                new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME, this.userMode);
        ServerConfigurationManager serverConfigurationManager = new ServerConfigurationManager(automationContext);
        serverConfigurationManager.restartGracefully();

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
    @Test(groups = "wso2.ds.dashboard", description = "test the gadget load ordering")
    public void testGadgetLoadOrder() throws MalformedURLException, XPathExpressionException {
        redirectToLocation("portal", "dashboards");
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        String[][] gadgetMappings = {{"textbox-TestGadgetLowPriority", "a"}, {"textbox-TestGadgetMediumPriority", "b"}, {"textbox-TestGadgetHighPriority", "c"}};
        String script = generateAddGadgetScript(gadgetMappings);
        selectPane("gadgets");
        getDriver().executeScript(script);
        clickViewButton();
        for (String winHandle : getDriver().getWindowHandles()) {
            getDriver().switchTo().window(winHandle);
        }
        boolean correctLoadOrder = false;
        Date timeBeforeLoadingHighPriority = new Date();
        (new WebDriverWait(getDriver(), 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("textbox-TestGadgetHighPriority-0")));
        Date timeBeforeLoadingMediumPriority = new Date();
        (new WebDriverWait(getDriver(), 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("textbox-TestGadgetMediumPriority-0")));
        Date timeBeforeLoadingLowPriority = new Date();
        (new WebDriverWait(getDriver(), 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("textbox-TestGadgetLowPriority-0")));
        Date timeAfterLoading = new Date();
        if (timeBeforeLoadingMediumPriority.getTime() - timeBeforeLoadingHighPriority.getTime() < 5000 &&
                timeBeforeLoadingLowPriority.getTime() - timeBeforeLoadingMediumPriority.getTime() >= 5000 &&
                timeAfterLoading.getTime() - timeBeforeLoadingLowPriority.getTime() >= 5000) {
            correctLoadOrder = true;
        }
        Assert.assertEquals(correctLoadOrder, true);
    }

}
