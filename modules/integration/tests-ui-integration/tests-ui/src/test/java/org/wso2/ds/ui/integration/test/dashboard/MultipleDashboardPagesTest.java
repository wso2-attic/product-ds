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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MultipleDashboardPagesTest extends DSUIIntegrationTest {
    private static final String USERNAME_EDITOR = "pageeditor";
    private static final String PASSWORD_EDITOR = "editor123";
    private static final String EDITOR_ROLE = "dashboardEditorRole";

    /**
     * Initialize the class
     */
    @Factory(dataProvider = "userMode")
    public MultipleDashboardPagesTest(TestUserMode userMode) throws Exception {
        super(userMode);
    }

    /**
     * Provides user modes
     *
     * @return
     */
    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN}};
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        login(getCurrentUsername(), getCurrentPassword());
        deleteDashboards();
        logout();
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
        addUser(USERNAME_EDITOR, PASSWORD_EDITOR, PASSWORD_EDITOR);
        addRole(EDITOR_ROLE);
        assignRoleToUser(new String[]{USERNAME_EDITOR});
        logoutFromAdminConsole();
        login(USERNAME_EDITOR, PASSWORD_EDITOR);
    }

    /**
     * Test whether the user is redirected to dashboard creation page
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.dashboard.pages", description = "testing whether multiple pages allow multiple layouts")
    public void testMultiplePagesWithDifferentLayouts() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardTitle = "dashboardpages";
        String[][] pageDesc = { // {pageId, grid name}
                {"landing", "default-grid"},
                {"page0", "right-grid"},
                {"page1", "single-column"},
                {"page2", "blank"},
                {"page3", "balanced"},
                {"page4", "banner"}
        };
        String errorMsg = " is not valid in ";
        addDashBoard(dashboardTitle, "This is a test dashboard");
        WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        webElement.findElement(By.cssSelector(".ues-edit")).click();
        addPageToDashboard(pageDesc[1][1]); // page0
        // deselect pages pane, first
        selectPane("pages");
        addPageToDashboard(pageDesc[2][1]); // page1
        selectPane("pages");
        addPageToDashboard(pageDesc[3][1]); // page2
        selectPane("pages");
        addPageToDashboard(pageDesc[4][1]);// page3
        selectPane("pages");
        addPageToDashboard(pageDesc[5][1]);// page4
        for (String[] page : pageDesc) {
            assertTrue(isLayoutValid(page[0], page[1]), (page[1] + errorMsg + page[0]));
        }
    }

    /**
     * Test whether the user is redirected to dashboard page
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.dashboard.pages", description = "Tests the same layout can be added more than once in a " +
            "dashboard", dependsOnMethods = "testMultiplePagesWithDifferentLayouts")
    public void testSameLayoutInMultiplePages() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardTitle = "dashboardpages2";
        String[] pageIds = {"landing", "page0", "page1", "page2"};
        String gridId = "default-grid", errorMsg = " is not valid in ";
        addDashBoard(dashboardTitle, "This is a test dashboard");
        Thread.sleep(500);
        WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        webElement.findElement(By.cssSelector(".ues-edit")).click();
        addPageToDashboard(gridId); // page0
        // deselect pages pane, first
        selectPane("pages");
        addPageToDashboard(gridId); // page1
        selectPane("pages");
        addPageToDashboard(gridId); // page2
        for (String page : pageIds) {
            assertTrue(isLayoutValid(page, gridId), (gridId + errorMsg + page));
        }
    }

    /**
     * Test whether the editor can view all the pages when there are multiple pages in a dashboard
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.dashboard.pages", description = "Tests an editor can view all the pages when there are " +
            "multiple pages with both anon and no anon pages", dependsOnMethods = "testSameLayoutInMultiplePages")
    public void testAnonViewFromAnonPage() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardTitle = "dashboardpages3";
        String[] pageIds = {"landing", "page0", "page1", "page2", "page3"};
        String gadgetTitle = "USA Map";
        String[][] gadgetMapping = {{"usa-map", "a"}};
        String script = generateAddGadgetScript(gadgetMapping);
        addDashBoard(dashboardTitle, "This is a test dashboard");
        WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        webElement.findElement(By.cssSelector(".ues-edit")).click();
        addPageToDashboard(); // page0
        // deselect pages pane, first
        selectPane("pages");
        addPageToDashboard(); // page1
        selectPane("pages");
        addPageToDashboard(); // page2
        selectPane("pages");
        addPageToDashboard(); // page3
        driver.findElement(By.cssSelector("div[data-id='" + pageIds[0] + "']")).click();
        driver.findElement(By.cssSelector("input[name='anon']")).click();
        switchView("anon");
        selectPane("gadgets");
        driver.executeScript(script);
        Thread.sleep(500);
        selectPane("pages");
        driver.findElement(By.cssSelector("div[data-id='" + pageIds[1] + "']")).click();
        driver.findElement(By.cssSelector("input[name='anon']")).click();
        switchView("anon");
        selectPane("gadgets");
        driver.executeScript(script);
        Thread.sleep(500);
        clickViewButton();
        pushWindow();
        List<WebElement> elements = driver.findElements(By.cssSelector("div.page-actions a"));
        for (int i = 0; i < elements.size(); i++) {
            driver.findElement(By.cssSelector("div.page-actions a:nth-child(" + (i + 1) + ")")).click();
            String gTitle = driver.findElement(By.cssSelector("iframe")).getAttribute("title").trim().substring(0, 7);
            assertEquals(gadgetTitle, gTitle);
        }
        driver.close();
        popWindow();
    }

    /**
     * Test whether the fluid layout is working on multiple pages
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.dashboard.pages", description = "Tests fluid layout in pages",
            dependsOnMethods = "testAnonViewFromAnonPage")
    public void testFluidLayoutInPages() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardTitle = "dashboardpages4";
        String[][] pageDesc = { // {pageId, grid name}
                {"landing", "default-grid"},
                {"page0", "right-grid"},
                {"page1", "single-column"}
        };
        String[][] gadgetMapping = {{"usa-map", "a"}};
        String script = generateAddGadgetScript(gadgetMapping);
        addDashBoard(dashboardTitle, "This is a test dashboard");
        WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        webElement.findElement(By.cssSelector(".ues-edit")).click();
        addPageToDashboard(pageDesc[1][1]); // page0
        // deselect pages pane, first
        selectPane("pages");
        addPageToDashboard(pageDesc[2][1]); // page1
        // select "full width" and add gadgets in all pages
        for (String[] page : pageDesc) {
            driver.findElement(By.cssSelector("div[data-id='" + page[0] + "']")).click();
            driver.findElement(By.cssSelector("input[name='fluidLayout']")).click();
            selectPane("gadgets");
            driver.executeScript(script);
            Thread.sleep(500);
            selectPane("pages");
        }
        clickViewButton();
        pushWindow();
        List<WebElement> elements = driver.findElements(By.cssSelector("div.page-actions a"));
        for (int i = 0; i < elements.size(); i++) {
            driver.findElement(By.cssSelector("div.page-actions a:nth-child(" + (i + 1) + ")")).click();
            List<WebElement> fluidElems = driver.findElements(By.cssSelector("div.page-content-wrapper div.container-fluid"));
            assertTrue(fluidElems.size() > 0, "Page is not in full width");
        }
        driver.close();
        popWindow();
    }

    /**
     * Test whether the landing page can be seen in view, when there are multiple pages
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.dashboard.pages", description = "Tests landing page when there are multiple pages",
            dependsOnMethods = "testFluidLayoutInPages")
    public void testLandingPageInPages() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardTitle = "dashboardpages5";
        String gadgetTitle = "USA Map";
        String[][] gadgetMapping = {{"usa-map", "a"}};
        String script = generateAddGadgetScript(gadgetMapping);
        addDashBoard(dashboardTitle, "This is a test dashboard");
        WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        webElement.findElement(By.cssSelector(".ues-edit")).click();
        addPageToDashboard(); // page0
        // deselect pages pane, first
        selectPane("pages");
        addPageToDashboard(); // page1
        selectPane("pages");
        addPageToDashboard(); // page2
        driver.findElement(By.cssSelector("input[name='landing']")).click();
        selectPane("gadgets");
        driver.executeScript(script);
        Thread.sleep(500);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector(".ues-dashboard[data-id='" + dashboardTitle + "'] a.ues-view")).click();
        pushWindow();
        String gTitle = driver.findElement(By.cssSelector("iframe")).getAttribute("title").trim().substring(0, 7);
        assertEquals(gadgetTitle, gTitle);
        driver.close();
        popWindow();
    }

    /**
     * Test whether the title and the URL can be customized in each page when there are multiple pages
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.dashboard.pages", description = "Tests customization of title and url in pages",
            dependsOnMethods = "testLandingPageInPages")
    public void testChangingTitleAndUrlInPages() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardTitle = "dashboardpages6";
        String[][] pageDesc = {
                {"Overview", "overview"},
                {"Sales", "sales"},
                {"Marketing", "marketing"},
                {"Engineering", "engineering"}
        };
        addDashBoard(dashboardTitle, "This is a test dashboard");
        WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        webElement.findElement(By.cssSelector(".ues-edit")).click();
        selectPane("pages");
        setPageTitleAndUrl(pageDesc[0][0], pageDesc[0][1]);
        // deselect pages pane, first
        selectPane("pages");
        addPageToDashboard(); // page0
        setPageTitleAndUrl(pageDesc[1][0], pageDesc[1][1]);
        selectPane("pages");
        addPageToDashboard(); // page1
        setPageTitleAndUrl(pageDesc[2][0], pageDesc[2][1]);
        selectPane("pages");
        addPageToDashboard(); // page2
        setPageTitleAndUrl(pageDesc[3][0], pageDesc[3][1]);
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector(".ues-dashboard[data-id='" + dashboardTitle + "'] a.ues-view")).click();
        pushWindow();
        List<WebElement> elements = driver.findElements(By.cssSelector("div.page-actions a"));
        String pTitle, pUrl;
        for (int i = 0; i < elements.size(); i++) {
            driver.findElement(By.cssSelector("div.page-actions a:nth-child(" + (i + 1) + ")")).click();
            // checking page title
            pTitle = driver.findElement(By.cssSelector("div.page-actions a:nth-child(" + (i + 1) + ")")).getText();
            assertEquals(pTitle, pageDesc[i][0], "Page title is not correct");
            // checking url
            pUrl = driver.getCurrentUrl();
            assertTrue(pUrl.endsWith(pageDesc[i][1]), "Url is correct for page " + pageDesc[i][0]);
        }
        driver.close();
        popWindow();
    }

    /**
     * Set title and url to page
     *
     * @param pageTitle new title of the page
     * @param pageUrl   new url of the page
     * @throws Exception
     */
    private void setPageTitleAndUrl(String pageTitle, String pageUrl) throws Exception {
        DSWebDriver driver = getDriver();
        driver.findElement(By.cssSelector("input#page-title")).clear();
        driver.findElement(By.cssSelector("input#page-title")).sendKeys(pageTitle);
        driver.findElement(By.cssSelector("input#page-url")).clear();
        driver.findElement(By.cssSelector("input#page-url")).sendKeys(pageUrl);
    }

    /**
     * Checks whether the page has specified layout
     *
     * @param pageID Page id of the page
     * @param layout Name of the layout
     * @return true if the page contains specified layout. Otherwise returns false
     * @throws Exception
     */
    private boolean isLayoutValid(String pageID, String layout) throws Exception {
        boolean flag = false;
        String sizeX;
        DSWebDriver driver = getDriver();
        driver.findElement(By.cssSelector("div[data-id='" + pageID + "']")).click();
        List<WebElement> elements = driver.findElements(By.cssSelector("div.grid-stack-item"));
        if (layout.equals("blank")) {
            if (elements.size() == 0) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            for (WebElement elem : elements) {
                sizeX = elem.getAttribute("data-gs-width").trim();
                if (layout.equals("default-grid")) {
                    if (sizeX.equals("4")) {
                        flag = true;
                    } else {
                        flag = false;
                        break;
                    }
                } else if (layout.equals("right-grid")) {
                    if (elem.getAttribute("data-id").equals("d")) {
                        if (sizeX.equals("8")) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    } else {
                        if (sizeX.equals("4")) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    }
                } else if (layout.equals("single-column")) {
                    if (sizeX.equals("12")) {
                        flag = true;
                    } else {
                        flag = false;
                        break;
                    }
                } else if (layout.equals("balanced")) {
                    if (elem.getAttribute("data-id").equals("d") || elem.getAttribute("data-id").equals("e")) {
                        if (sizeX.equals("6")) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    } else {
                        if (sizeX.equals("4")) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    }
                } else if (layout.equals("banner")) {
                    if (elem.getAttribute("data-id").equals("a")) {
                        if (sizeX.equals("12")) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    } else {
                        if (sizeX.equals("4")) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * Clean up after running tests
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        deleteDashboards();
        getDriver().quit();
    }
}
