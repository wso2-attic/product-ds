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
 * This class tests the multiple view support for page based on roles
 */
public class MultipleViewSupportTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "multipleviewdashboard";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public MultipleViewSupportTest(TestUserMode userMode) {
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
     * Checks for single view
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking a dashboard with single view")
    public void testSingleView() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        String[][] gadgetMappings = {{"publisher", "b"}, {"usa-map", "c"}};
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("usa-map-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
    }

    /**
     * Checks for multiple view by copying the views
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking a dashboard with multiple view by copying views",
            dependsOnMethods = "testSingleView")
    public void testCopyView() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();

        // Create views by copying other views
        copyView(1);
        clickOnView("view0");
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("usa-map-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        copyView(2);
        clickOnView("view1");
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("usa-map-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");

        // Delete the gadgets from copied view and check whether it affects the other views
        // that are copied from particular view
        clickOnView("view0");
        getDriver().findElement(By.cssSelector("#publisher-0 .ues-trash-handle")).click();
        getDriver().findElement(By.id("btn-delete")).click();
        Thread.sleep(2000);
        assertFalse(getDriver().isElementPresent(By.id("publisher-0")), "Publisher gadget is not deleted");
        clickOnView("default");
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is deleted when the same gadget is deleted from copied view");
        clickOnView("view1");
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is deleted when the same gadget is deleted from copied view");
    }

    /**
     * Checks for multiple views by creating
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking a dashboard with multiple view by creating new views",
            dependsOnMethods = "testCopyView")
    public void testMultipleNewViews() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        createNewView("default-grid");
        clickOnView("view2");
        String[][] gadgetMappings = {{"usa-business-revenue", "a"}, {"subscriber", "d"}};
        String[][] gadgetMappingForNewView = {{"publisher", "a"}, {"subscriber", "b"}};
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        assertTrue(getDriver().findElement(By.id("usa-business-revenue-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("subscriber-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        createNewView("single-column");
        clickOnView("view3");
        script = generateAddGadgetScript(gadgetMappingForNewView);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("subscriber-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
    }

    /**
     * To test the functionality of getting new layout for a page
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the functionality of replacing layout of a particular view",
            dependsOnMethods = "testMultipleNewViews")
    public void testReplaceLayout() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        clickOnView("default");
        getDriver().findElement(By.id("btn-sidebar-dashboard-layout")).click();
        selectViewLayout("single-column");
        getDriver().findElement(By.id("ues-modal-confirm-yes")).click();
        assertFalse(getDriver().isElementPresent(By.id("publisher-0")),
                "Gadgets that are on existing layout is not replaced");
        assertFalse(getDriver().isElementPresent(By.id("usa-map-0")),
                "Gadgets that are on existing layout is not replaced");

        // Check the other views which are created by copying the other views, whether they are affected by replacement
        // of layout
        clickOnView("view1");
        assertTrue(getDriver().isElementPresent(By.id("publisher-0")),
                "When replacing the layout of a view other views are affected");
        assertTrue(getDriver().isElementPresent(By.id("usa-map-0")),
                "When replacing the layout of a view other views are affected");
        clickOnView("view0");
        assertTrue(getDriver().isElementPresent(By.id("usa-map-0")),
                "When replacing the layout of a view other views are affected");
    }

    /**
     * To test the functionality of deleting views in a page
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the view delete functionality",
            dependsOnMethods = "testReplaceLayout")
    public void testDeleteView() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        deleteView("default");
        assertFalse(getDriver().isElementPresent(By.id("default")), "View is not deleted even when after deleting it");
        deleteView("view0");
        assertFalse(getDriver().isElementPresent(By.id("view0")), "View is not deleted even when after deleting it");
    }

    /**
     * To test the functionality of closing views in a page.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the view close functionality",
            dependsOnMethods = "testDeleteView")
    public void testCloseView() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        closeView("view1");
        assertFalse(getDriver().isElementPresent(By.id("view1")), "View is not closed after pressing close button");
        assertTrue(getDriver().isElementPresent(By.id("more-views")), "Drop down for re-opening the view is not "
                + "visible after closing a view");
        getDriver().findElement(By.id("more-views")).click();
        getDriver().findElement(By.id("view1")).click();
        assertTrue(getDriver().isElementPresent(By.id("view1")), "View is not visible after re-opening the closed view");
        clickOnView("view1");
        assertTrue(getDriver().isElementPresent(By.id("publisher-0")),
                "After closing and re-opening publisher gadget is not visible");
        assertTrue(getDriver().isElementPresent(By.id("usa-map-0")),
                "After closing and re-opening usa map gadget is not visible");
    }

    /**
     * To test the functionality of closing last view
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the view close functionality when closing last view",
            dependsOnMethods = "testCloseView")
    public void testCloseLastView() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        closeView("view1");
        closeView("view2");
        closeView("view3");
        assertTrue(getDriver().isElementPresent(By.className("modal-content")), "Closing of last view is also allowed");
        getDriver().findElement(By.id("ues-modal-info-ok")).click();
        assertTrue(getDriver().isElementPresent(By.id("view3")), "Closing of last view is also allowed");
        assertFalse(getDriver().isElementPresent(By.id("view1")), "Closed views are still visible");
        assertFalse(getDriver().isElementPresent(By.id("view2")), "Closed views are still visible");
    }

    /**
     * To test the functionality of delete last view
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the view close functionality when closing last view",
            dependsOnMethods = "testCloseLastView")
    public void testDeleteLastView() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        deleteView("view1");
        deleteView("view2");
        clickOnView("view3");
        getDriver().findElement(By.cssSelector("li#nav-tab-view3.active .ues-trash-handle")).click();
        assertTrue(getDriver().isElementPresent(By.className("modal-content")),
                "Deleting of last view is also allowed");
        getDriver().findElement(By.id("ues-modal-info-ok")).click();
        assertTrue(getDriver().isElementPresent(By.id("view3")), "Deleting of last view is also allowed");
        assertFalse(getDriver().isElementPresent(By.id("view1")), "Deleted views are still visible");
        assertFalse(getDriver().isElementPresent(By.id("view2")), "Deleted views are still visible");
    }

    /**
     * To test the functionality of adding new page
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking adding new page to dashboard", dependsOnMethods = "testDeleteLastView")
    public void testAddPage() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        addPageToDashboard("default-grid");
        assertTrue(getDriver().isElementPresent(By.id("default")),
                "When creating new page default view is not created");
        // Create a new add some gadgets
        createNewView("single-column");
        assertTrue(getDriver().isElementPresent(By.id("view0")), "Second view is not created in second page");

        //Go to dashboard listing page and move to designer mode again and check whether those views are still there
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        getDriver().findElement(By.className("ues-switch-page-next")).click();
        assertTrue(getDriver().isElementPresent(By.id("default")),
                "When creating new page default view is not created");
        assertTrue(getDriver().isElementPresent(By.id("view0")), "Second view is not created in second page");
    }
}