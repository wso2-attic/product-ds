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
 * Tests related with the shared dashboard feature
 */
public class SharedDashboardTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "shareddashboard";
    private User editor;

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode") public SharedDashboardTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return user modes
     */
    @DataProvider(name = "userMode") public static Object[][] userModeProvider() {
        return new Object[][] { { TestUserMode.SUPER_TENANT_ADMIN } };
    }

    /**
     * Setup the testing environment.
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws AutomationUtilException
     */
    @BeforeClass(alwaysRun = true) public void setUp()
            throws AutomationUtilException, XPathExpressionException, IOException {
        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, "This is a test dashboard");
    }

    /**
     * Clean up after running tests.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @AfterClass(alwaysRun = true) public void tearDown() throws XPathExpressionException, MalformedURLException {
        logout();
        getDriver().quit();
    }

    /**
     * Create a shared dashboard in super-tenant and view the same dashboard in tenant mode
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Share a dashboard from super-tenant and view the same dashboard in tenant mode")
    public void testSharingDashboard() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        String[][] gadgetMappings = { { "publisher", "b" }, { "usa-map", "c" } };
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        Thread.sleep(2000);
        getDriver().findElement(By.cssSelector("a#dashboard-settings")).click();
        getDriver().findElement(By.id("share-dashboard")).click();
        getDriver().findElement(By.id("ues-dashboard-saveBtn")).click();
        Thread.sleep(2000);
        logout();
        AutomationContext automationContext = new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME,
                TestUserMode.TENANT_USER);
        editor = automationContext.getContextTenant().getTenantUser("editor");
        login(editor.getUserName(), editor.getPassword());
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-view")).click();
        pushWindow();
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("usa-map-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        redirectToLocation(DS_HOME_CONTEXT, "t/" + editor.getUserDomain() + "/dashboards");
    }

    /**
     * Create two dashboards with the same name as a shared dashboard and tenant-specific dashboard and verify correct
     * view page is displayed when the relevant view button is clicked
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Create a dashboard with the same name as super-tenant shared dashboard and verify",
            dependsOnMethods = "testSharingDashboard")
    public void testDashboardsWithSameName() throws MalformedURLException, XPathExpressionException, InterruptedException {
        getDriver().findElement(By.cssSelector("a[href*='create-dashboard']")).click();
        getDriver().findElement(By.id("ues-dashboard-title")).clear();
        getDriver().findElement(By.id("ues-dashboard-title")).sendKeys(DASHBOARD_TITLE);
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
        clickViewButton();
        Thread.sleep(3000);
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("subscriber-0")).isDisplayed(),
                "Subscriber is not displayed in the page");
        redirectToLocation(DS_HOME_CONTEXT, "t/" + editor.getUserDomain() + "/dashboards");
    }

    /**
     * Check whether correct dashboards are displayed depending on the filter indicated by the user
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Check which dashboards are displayed when different filters are selected",
            dependsOnMethods = "testDashboardsWithSameName")
    public void testFilters()
            throws MalformedURLException, XPathExpressionException, InterruptedException {
        getDriver().findElement(By.cssSelector("button.btn.btn-default")).click();
        getDriver().findElement(By.linkText("Shared Dashboards")).click();
        Thread.sleep(3000);
        assertTrue(getDriver().findElement(By.cssSelector(".ues-dashboard-share")).isDisplayed(),
                "Shared Dashboard is not displayed when selecting shared dashboard filter");
        getDriver().findElement(By.cssSelector("button.btn.btn-default")).click();
        getDriver().findElement(By.linkText("Tenant Specific Dashboards")).click();
        Thread.sleep(3000);
        assertFalse(getDriver().findElement(By.cssSelector(".ues-dashboard-share")).isDisplayed(),
                "Shared Dashboard is displayed when selecting tenant-specific dashboard filter");
        getDriver().findElement(By.cssSelector(".dropdown")).click();
        getDriver().findElement(By.cssSelector(".dropdown-menu > li > a")).click();
        login(getCurrentUsername(), getCurrentPassword());
    }
}
