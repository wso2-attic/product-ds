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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test Anonymous Mode for Dashboard with Various Scenarios
 */
public class DashboardAnonViewTest extends DSUIIntegrationTest {
    private static final Log LOG = LogFactory.getLog(DashboardAnonViewTest.class);
    private static final String DASHBOARD_TITLE = "anondashboard";
    private static final String DASHBOARD_DESCRIPTION = "This is sample description for dashboard";
    private static final String GADGET_1 = "Gadget Resize";
    private static final String GADGET_1_ID = "gadget-resize";
    private static final String GADGET_2 = "Gadget Sample";
    private static final String GADGET_2_ID = "gadget-sample";
    private static final String GADGET_3 = "Gadget State";
    private static final String GADGET_3_ID = "gadget-state";
    private static final String GADGET_4 = "Text Box - Test Gadget Medium Priority";
    private static final String GADGET_4_ID = "test1";
    private static final String CONTAINER_A = "a";
    private String dashboardTitle;

    @Factory(dataProvider = "userMode")
    public DashboardAnonViewTest(TestUserMode userMode, String dashboardTitle) {
        super(userMode);
        this.dashboardTitle = dashboardTitle;
    }

    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE}};
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        resourcePath = DSIntegrationTestConstants.DASHBOARD_REGISTRY_BASE_PATH + dashboardTitle.toLowerCase();
        login(getCurrentUsername(), getCurrentPassword());
    }

    /**
     * Test case for adding anon view for the dashboard.
     */
    @Test(groups = "wso2.ds.dashboard", description = "Adding anonymous dashboard for dashboard")
    public void testAnonDashboard() throws Exception {
        String[][] anonViewGadgetMappings = {{GADGET_2_ID, CONTAINER_A}};
        String[][] defaultViewGadgetMappings = {{GADGET_1_ID, CONTAINER_A}};
        String anonViewGadgetAddScript = generateAddGadgetScript(anonViewGadgetMappings);
        String defaultViewGadgetAddScript = generateAddGadgetScript(defaultViewGadgetMappings);
        addDashBoard(dashboardTitle, DASHBOARD_DESCRIPTION);
        getDriver().findElement(By.cssSelector("#" + dashboardTitle.toLowerCase() + " .ues-edit")).click();
        getDriver().findElement(By.xpath("(//button[@type='button'])[10]")).click();
        getDriver().findElement(By.id("ds-view-roles")).click();
        getDriver().findElement(By.id("ds-view-roles")).sendKeys("anonymous");
        getDriver().findElement(By.className("tt-highlight")).click();
        getDriver().findElement(By.id("ues-modal-confirm-yes")).click();
        Thread.sleep(3000);
        getDriver().executeScript(anonViewGadgetAddScript);

        // verifying gadget is rendered correctly in anon view
        assertTrue(getDriver().findElement(By.id(GADGET_2_ID+"-0")).isDisplayed(), "Gadget is not displayed in anonymous view");
        getDriver().findElement(By.id("add-view")).click();
        getDriver().findElement(By.id("new-view")).click();
        selectViewLayout("default-grid");
        Thread.sleep(3000);
        getDriver().executeScript(defaultViewGadgetAddScript);
        // verifying gadget is rendered correctly in default view
        assertEquals(GADGET_1, getAttributeValue("iframe", "title"));
        // verifying correct pages are displayed when toggle views
        getDriver().findElement(By.id("default")).click();
        assertEquals(GADGET_2, getAttributeValue("iframe", "title"));
        getDriver().findElement(By.id("view0")).click();
        assertEquals(GADGET_1, getAttributeValue("iframe", "title"));

        clickViewButton();
        pushWindow();
        assertEquals(GADGET_1, getAttributeValue("iframe", "title"));
        getDriver().close();
        popWindow();
        switchView("anon");
        clickViewButton();
        pushWindow();
        assertEquals(GADGET_2, getAttributeValue("iframe", "title"));
        getDriver().close();
        popWindow();
    }

    /**
     * Test case for adding anonymous page inside a anonymous dashboard.
     */
    @Test(groups = "wso2.ds.dashboard", description = "Adding anonymous dashboard page for dashboard",
            dependsOnMethods = "testAnonDashboard")
    public void testAnonDashboardPages() throws Exception {
        String[][] anonViewGadgetMappings = {{GADGET_4_ID, CONTAINER_A}};
        String[][] defaultViewGadgetMappings = {{GADGET_3_ID, CONTAINER_A}};
        String anonViewGadgetAddScript = generateAddGadgetScript(anonViewGadgetMappings);
        String defaultViewGadgetAddScript = generateAddGadgetScript(defaultViewGadgetMappings);
        addPageToDashboard();
        getDriver().findElement(By.cssSelector("input[name='anon']")).click();
        switchView("anon");
        getDriver().executeScript(anonViewGadgetAddScript);
        assertEquals(GADGET_4, getAttributeValue("iframe", "title"));
        switchView("default");
        getDriver().executeScript(defaultViewGadgetAddScript);
        assertEquals(GADGET_3, getAttributeValue("iframe", "title"));
        switchPage("landing");
        switchPage("page0");
        assertEquals(GADGET_3, getAttributeValue("iframe", "title"));
        switchView("anon");
        assertEquals(GADGET_4, getAttributeValue("iframe", "title"));
        switchView("default");
        clickViewButton();
        pushWindow();
        assertEquals(GADGET_3, getAttributeValue("iframe", "title"));
        getDriver().close();
        popWindow();
        switchView("anon");
        clickViewButton();
        pushWindow();
        assertEquals(GADGET_4, getAttributeValue("iframe", "title"));
        getDriver().close();
        popWindow();
        switchPage("landing");
        getDriver().findElement(By.cssSelector("input[name='anon']")).click();
        assertTrue(getDriver().findElement(By.cssSelector("button#ues-modal-info-ok")) != null, "Can remove the anonymous view of landing page when there are pages with anonymous views");
        getDriver().findElement(By.cssSelector("button.close")).click();
    }

    /**
     * Test case for verifying only anon pages are displayed to viewers
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.dashboard", description = "Verify only anon pages are displayed to viewers",
            dependsOnMethods = "testAnonDashboardPages")
    public void testAnonPagesViews() throws Exception {
        logout();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT + "/" + dashboardTitle + "/landing");
        //check anon view gadgets are there
        assertEquals(GADGET_2, getAttributeValue("iframe", "title"));
        //check default view gadgets are not there
        assertNotEquals(GADGET_1, getAttributeValue("iframe", "title"));
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT + "/" + dashboardTitle + "/page0");
        //check anon view gadgets are there
        assertEquals(GADGET_4, getAttributeValue("iframe", "title"));
        //check default view gadgets are not there
        assertNotEquals(GADGET_3, getAttributeValue("iframe", "title"));
    }

    /**
     * Test case for removing anonymous view from newly added page of dashboard
     */
    @Test(groups = "wso2.ds.dashboard", description = "Remove anonymous view mode from added dashboard page in dashboard",
            dependsOnMethods = "testAnonPagesViews")
    public void testAnonDashboardPageRemove() throws Exception {
        login(getCurrentUsername(), getCurrentPassword());
        getDriver().findElement(By.cssSelector("#" + dashboardTitle.toLowerCase() + " .ues-edit")).click();
        selectPane("pages");
        switchPage("page0");
        getDriver().findElement(By.cssSelector("input[name='anon']")).click();
        boolean isAnonViewHidden = false;
        WebElement element = getDriver().findElement(By.cssSelector("ul#designer-view-mode li[data-view-mode='anon']"));
        String attrValue = element.getAttribute("class");
        if (attrValue.equals("hide")) {
            isAnonViewHidden = true;
        }
        assertTrue(isAnonViewHidden, "Anonymous toggle button is not hidden");
        assertEquals(GADGET_3, getAttributeValue("iframe", "title"));
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT + "/" + dashboardTitle + "/landing?isAnonView=true");
        modifyTimeOut(2);
        boolean isLinkForNonAnonPageNotExist = getDriver().findElements(By.cssSelector("a[href='" + getBaseUrl() +
                "/" + DS_HOME_CONTEXT + "/" + DS_DASHBOARDS_CONTEXT + "/" + dashboardTitle + "/page0?isAnonView=true']")).size() <= 0;
        resetTimeOut();
        assertTrue(isLinkForNonAnonPageNotExist, "Link for the non anon page is still available");
    }

    /**
     * Test case for removing anonymous view from dashboard.
     */
    @Test(groups = "wso2.ds.dashboard", description = "Remove anonymous view mode from dashboard",
            dependsOnMethods = "testAnonDashboardPageRemove")
    public void testRemoveAnonModeFromDashboard() throws Exception {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT + "/" + dashboardTitle + "?editor=true");
        selectPane("pages");
        getDriver().findElement(By.cssSelector("input[name='anon']")).click();
        boolean isAnonViewHidden = false;
        WebElement element = getDriver().findElement(By.cssSelector("ul#designer-view-mode li[data-view-mode='anon']"));
        String attrValue = element.getAttribute("class");
        if (attrValue.equals("hide")) {
            isAnonViewHidden = true;
        }
        assertTrue(isAnonViewHidden, "Anonymous toggle button is not hidden");
        assertEquals(GADGET_1, getAttributeValue("iframe", "title"));
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT + "/" + dashboardTitle + "/landing?isAnonView=true");
        modifyTimeOut(2);
        boolean noIFramesAvailable = getDriver().findElements(By.cssSelector("iframe")).size() <= 0;
        resetTimeOut();
        assertTrue(noIFramesAvailable, "There are gadgets available for anonymous view when no anonymous view available");
    }

    /**
     * Get the value of given attribute of given element
     *
     * @param element       name of the element
     * @param attributeName name of the attribute
     * @return {String}
     */
    private String getAttributeValue(String element, String attributeName) throws Exception {
        return getDriver().findElement(By.cssSelector(element)).getAttribute(attributeName);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        try {
            logout();
        } finally {
            getDriver().quit();
        }
    }
}