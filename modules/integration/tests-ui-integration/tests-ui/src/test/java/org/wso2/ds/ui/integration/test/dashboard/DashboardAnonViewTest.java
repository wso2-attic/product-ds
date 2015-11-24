/*
*Copyright (c) 2015​, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.ds.ui.integration.test.dashboard;

import ds.integration.tests.common.domain.DSIntegrationTestConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test Anonymous Mode for Dashboard with Various Scenarios
 */
public class DashboardAnonViewTest extends DSUIIntegrationTest {
    private static final Log LOG = LogFactory.getLog(DashboardAnonViewTest.class);
    private static final String DASHBOARD_TITLE = "anondashboard";
    private static final String DASHBOARD_DESCRIPTION = "This is sample descrition for dashboard";
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
        String defaultViewGadgetExpected = "USA Map";
        String anonViewGadgetExpected = "G1";

        getDriver().get(getBaseUrl() + "/portal/dashboards");
        getDriver().findElement(By.cssSelector("[href='create-dashboard']")).click();

        getDriver().findElement(By.id("ues-dashboard-title")).clear();
        getDriver().findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        getDriver().findElement(By.id("ues-dashboard-create")).click();
        getDriver().findElement(By.cssSelector("#default-grid")).click();
        getDriver().findElement(By.cssSelector(".ues-page-properties-toggle")).click();
        getDriver().findElement(By.cssSelector("input[name='landing']")).click();
        getDriver().findElement(By.cssSelector("input[name='anon']")).click();
        getDriver().findElement(By.className("toggle-group")).click();
        getDriver().findElement(By.cssSelector("a[data-type='gadget']")).click();

        ((JavascriptExecutor) getDriver()).executeScript(" var draggableElement = $(\"div[data-id='usa-map']\")" +
                ".draggable({cancel: false," +
                "appendTo: 'body'," +
                "helper: 'clone'," +
                "start: function (event, ui) {" +
                "ui.helper.addClass('ues-store-thumbnail');}," +
                "stop: function (event, ui) {" +
                "ui.helper.removeClass('ues-store-thumbnail');" +
                "}})," +
                "dropableElement=$('#a');" +
                "var droppableOffset = dropableElement.offset()," +
                "draggableOffset = draggableElement.offset()," +
                "dx = droppableOffset.left - draggableOffset.left," +
                "dy = droppableOffset.top - draggableOffset.top;" +
                "draggableElement.simulate('drag', {" +
                "dx: dx," +
                "dy: dy" +
                "});");

        String defaultDesignerModeGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");

        getDriver().findElement(By.className("toggle-group")).click();
        ((JavascriptExecutor) getDriver()).executeScript(" var draggableElement = $(\"div[data-id='g1']\")" +
                ".draggable({cancel: false," +
                "appendTo: 'body'," +
                "helper: 'clone'," +
                "start: function (event, ui) {" +
                "ui.helper.addClass('ues-store-thumbnail');}," +
                "stop: function (event, ui) {" +
                "ui.helper.removeClass('ues-store-thumbnail');" +
                "}})," +
                "dropableElement=$('#a');" +
                "var droppableOffset = dropableElement.offset()," +
                "draggableOffset = draggableElement.offset()," +
                "dx = droppableOffset.left - draggableOffset.left," +
                "dy = droppableOffset.top - draggableOffset.top;" +
                "draggableElement.simulate('drag', {" +
                "dx: dx," +
                "dy: dy" +
                "});");

        String anonDesignerModeGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");
        String previousWinHandler = getDriver().getWindowHandle();
        getDriver().findElement(By.className("ues-dashboard-preview")).click();

        for (String windowHandl : getDriver().getWindowHandles()) {
            if (!windowHandl.contains(previousWinHandler)) {
                getDriver().switchTo().window(windowHandl);
                break;
            }
        }

        String anonViewModeGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");
        getDriver().close();
        getDriver().switchTo().window(previousWinHandler);
        getDriver().findElement(By.className("toggle-group")).click();
        getDriver().findElement(By.className("ues-dashboard-preview")).click();

        for (String windowHandl : getDriver().getWindowHandles()) {
            if (!windowHandl.contains(previousWinHandler)) {
                getDriver().switchTo().window(windowHandl);
                break;
            }
        }

        String defaultViewModeGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");
        getDriver().close();
        getDriver().switchTo().window(previousWinHandler);

        assertEquals(anonViewGadgetExpected, anonDesignerModeGadgetActual);
        assertEquals(defaultViewGadgetExpected, defaultDesignerModeGadgetActual);
        assertEquals(anonViewGadgetExpected, anonViewModeGadgetActual);
        assertEquals(defaultViewGadgetExpected, defaultViewModeGadgetActual);
    }

    /**
     * Test case for adding anonymous page inside a anonymous dashboard.
     */
    @Test(groups = "wso2.ds.dashboard", description = "Adding anonymous dashboard page for dashboard",
            dependsOnMethods = "testAnonDashboard")
    public void testAnonDashboardPages() throws Exception {
        String anonGadgetExpected = "USA Social";
        String defaultGadgetExpected = "G2";

        getDriver().findElement(By.className("ues-page-add")).click();
        getDriver().findElement(By.cssSelector("#default-grid")).click();
        getDriver().findElement(By.cssSelector(".ues-page-properties-toggle")).click();
        getDriver().findElement(By.cssSelector("input[name='anon']")).click();
        getDriver().findElement(By.className("toggle-group")).click();

        ((JavascriptExecutor) getDriver()).executeScript(" var draggableElement = $(\"div[data-id='g2']\")" +
                ".draggable({cancel: false," +
                "appendTo: 'body'," +
                "helper: 'clone'," +
                "start: function (event, ui) {" +
                "ui.helper.addClass('ues-store-thumbnail');}," +
                "stop: function (event, ui) {" +
                "ui.helper.removeClass('ues-store-thumbnail');" +
                "}})," +
                "dropableElement=$('#a');" +
                "var droppableOffset = dropableElement.offset()," +
                "draggableOffset = draggableElement.offset()," +
                "dx = droppableOffset.left - draggableOffset.left," +
                "dy = droppableOffset.top - draggableOffset.top;" +
                "draggableElement.simulate('drag', {" +
                "dx: dx," +
                "dy: dy" +
                "});");

        String defaultPageGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");

        getDriver().findElement(By.className("toggle-group")).click();
        ((JavascriptExecutor) getDriver()).executeScript(" var draggableElement = $(\"div[data-id='usa-social']\")" +
                ".draggable({cancel: false," +
                "appendTo: 'body'," +
                "helper: 'clone'," +
                "start: function (event, ui) {" +
                "ui.helper.addClass('ues-store-thumbnail');}," +
                "stop: function (event, ui) {" +
                "ui.helper.removeClass('ues-store-thumbnail');" +
                "}})," +
                "dropableElement=$('#a');" +
                "var droppableOffset = dropableElement.offset()," +
                "draggableOffset = draggableElement.offset()," +
                "dx = droppableOffset.left - draggableOffset.left," +
                "dy = droppableOffset.top - draggableOffset.top;" +
                "draggableElement.simulate('drag', {" +
                "dx: dx," +
                "dy: dy" +
                "});");

        String anonPageGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");

        getDriver().findElement(By.className("ues-page-switcher")).click();

        getDriver().findElement(By.cssSelector("a[data-id='landing']")).click();

        getDriver().findElement(By.className("ues-page-switcher")).click();

        getDriver().findElement(By.cssSelector("a[data-id='page0']")).click();

        String switchedPagedDefaultGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");

        getDriver().findElement(By.className("toggle-group")).click();

        String switchedPageAnonGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");

        getDriver().findElement(By.className("toggle-group")).click();

        String previousWinHandler = getDriver().getWindowHandle();
        getDriver().findElement(By.className("ues-dashboard-preview")).click();

        for (String windowHandl : getDriver().getWindowHandles()) {
            if (!windowHandl.contains(previousWinHandler)) {
                getDriver().switchTo().window(windowHandl);
                break;
            }
        }

        String defaultViewModeGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");
        getDriver().close();
        getDriver().switchTo().window(previousWinHandler);

        getDriver().findElement(By.className("toggle-group")).click();
        getDriver().findElement(By.className("ues-dashboard-preview")).click();

        for (String windowHandl : getDriver().getWindowHandles()) {
            if (!windowHandl.contains(previousWinHandler)) {
                getDriver().switchTo().window(windowHandl);
                break;
            }
        }

        String anonViewModeGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");
        getDriver().close();
        getDriver().switchTo().window(previousWinHandler);

        assertEquals(anonGadgetExpected, anonPageGadgetActual);
        assertEquals(defaultGadgetExpected, defaultPageGadgetActual);
        assertEquals(anonGadgetExpected, switchedPageAnonGadgetActual);
        assertEquals(defaultGadgetExpected, switchedPagedDefaultGadgetActual);
        assertEquals(anonGadgetExpected, anonViewModeGadgetActual);
        assertEquals(defaultGadgetExpected, defaultViewModeGadgetActual);
    }

    /**
     * Test case for removing anonymous view from newly added page of dashboard
     */
    @Test(groups = "wso2.ds.dashboard", description = "Remove anonymous view mode from added dashboard page in dashboard",
            dependsOnMethods = "testAnonDashboardPages")
    public void testAnonDashboardPageRemove() throws Exception {
        String defaultGadgetExpected = "G2";

        getDriver().findElement(By.cssSelector("input[name='anon']")).click();
        boolean isToggleButtonHidden = getDriver().findElements(By.cssSelector(".toggle-design-view.hide")).size() > 0;
        String defaultGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");
        getDriver().get(getBaseUrl() + "/portal/dashboards/" + dashboardTitle + "/landing?isAnonView=true");
        boolean isLinkForNonAnonPageNotExist = getDriver().findElements(By.cssSelector("a[href='" + getBaseUrl() +
                "/portal/dashboards/" + dashboardTitle + "/page0?isAnonView=true']")).size() <= 0;

        assertEquals(defaultGadgetExpected, defaultGadgetActual);
        assertTrue(isToggleButtonHidden, "Anonymous toggle button is not hidden");
        assertTrue(isLinkForNonAnonPageNotExist, "Link for the non anon page is still available");
    }

    /**
     * Test case for removing anonymous view from dashboard.
     */
    @Test(groups = "wso2.ds.dashboard", description = "Remove anonymous view mode form dashboard",
            dependsOnMethods = "testAnonDashboardPageRemove")
    public void testRemoveAnonModeFromDashboard() throws Exception {
        String defaultViewGadgetExpected = "USA Map";

        getDriver().get(getBaseUrl() + "/portal/dashboards/" + dashboardTitle + "?editor=true");
        getDriver().findElement(By.cssSelector(".ues-page-properties-toggle")).click();
        getDriver().findElement(By.cssSelector("input[name='anon']")).click();

        boolean isToggleButtonHidden = getDriver().findElements(By.cssSelector(".toggle-design-view.hide")).size() > 0;
        String defaultGadgetActual = getDriver().findElement(By.cssSelector("iframe")).getAttribute("title");
        getDriver().get(getBaseUrl() + "/portal/dashboards/" + dashboardTitle + "/landing?isAnonView=true");
        boolean noIFramesAvailable = getDriver().findElements(By.cssSelector("iframe")).size() <= 0;

        getDriver().close();

        assertEquals(defaultViewGadgetExpected, defaultGadgetActual);
        assertTrue(isToggleButtonHidden, "Anonymous toggle button is not hidden");
        assertTrue(noIFramesAvailable, "There are gadgets available for anonymous view when no anonymous view available");
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