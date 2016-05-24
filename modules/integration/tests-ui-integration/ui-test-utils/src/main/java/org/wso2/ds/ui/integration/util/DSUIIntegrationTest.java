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
package org.wso2.ds.ui.integration.util;

import ds.integration.tests.common.domain.DSIntegrationTest;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.ds.integration.common.clients.ResourceAdminServiceClient;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * Defines base class for UI integration tests.
 */
public abstract class DSUIIntegrationTest extends DSIntegrationTest {
    private static final Log LOG = LogFactory.getLog(DSUIIntegrationTest.class);
    private static final String DS_SUFFIX = "/portal/login-controller?destination=%2Fportal%2F";
    private static final String DS_HOME_SUFFIX = "/portal/dashboards";
    private static final String ADMIN_CONSOLE_SUFFIX = "/carbon/admin/index.jsp";
    public static final String DS_HOME_CONTEXT = "portal";
    public static final String DS_DASHBOARDS_CONTEXT = "dashboards";

    protected String resourcePath;
    private DSWebDriver driver = null;
    private WebDriverWait wait = null;
    private Stack<String> windowHandles = new Stack<String>();

    /**
     * Constructor for the DSUIIntegrationTest.
     */
    public DSUIIntegrationTest() {
        super();
    }

    /**
     * Constructor for the DSUIIntegrationTest.
     *
     * @param userMode user mode to initiate the class
     */
    public DSUIIntegrationTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Get JS script to simulate adding gadgets to the page.
     *
     * @param mappings array of gadget mappings in { gadget id, target id} format
     * @return JS script
     */
    public String generateAddGadgetScript(String[][] mappings) {
        String script = "$('.ues-thumbnail').draggable({" +
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
                "    var gadgetOffset = gadget.offset();" +
                "    var targetOffset = target.offset();" +
                "    var dx = targetOffset.left - gadgetOffset.left;" +
                "    var dy = targetOffset.top - gadgetOffset.top;" +
                "    gadget.simulate('drag', { dx: dx, dy: dy});" +
                "}";

        for (String[] mapping : mappings) {
            script += "performDrag('" + mapping[0] + "', '" + mapping[1] + "');";
        }
        return script;
    }

    /**
     * Switch to a child window while remembering the parent window.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void pushWindow() throws MalformedURLException, XPathExpressionException {
        String currentWindowHandle = getDriver().getWindowHandle();
        // Switch to the other window
        for (String windowHandle : getDriver().getWindowHandles()) {
            if (!windowHandle.equals(currentWindowHandle)) {
                getDriver().switchTo().window(windowHandle);
                getDriver().manage().window().setSize(new Dimension(1920, 1080));
                break;
            }
        }
        // Push the parent window to the window list
        windowHandles.push(currentWindowHandle);
    }

    /**
     * Switch to the parent window (which was remembered previously) from a child window.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void popWindow() throws MalformedURLException, XPathExpressionException {
        if (windowHandles.size() > 0) {
            getDriver().switchTo().window(windowHandles.pop());
        }
    }

    /**
     * To login to Dashboard server.
     *
     * @param userName user name
     * @param pwd      password
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    public void login(String userName, String pwd) throws XPathExpressionException, MalformedURLException {
        getDriver().get(getBaseUrl() + DS_SUFFIX);
        getDriver().findElement(By.name("username")).clear();
        getDriver().findElement(By.name("username")).sendKeys(userName);
        getDriver().findElement(By.name("password")).clear();
        getDriver().findElement(By.name("password")).sendKeys(pwd);
        String currentUrl = getDriver().getCurrentUrl();
        if (currentUrl.contains("authenticationendpoint/login.do")) {
            // SSO login enabled
            getDriver().findElement(By.tagName("button")).click();
        } else {
            // Basic login enabled
            getDriver().findElement(By.cssSelector(".ues-signin")).click();
        }
    }

    /**
     * To logout from Dashboard server.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    public void logout() throws XPathExpressionException, MalformedURLException {
        getDriver().get(getBaseUrl() + DS_HOME_SUFFIX);
        getDriver().findElement(By.cssSelector(".dropdown")).click();
        getDriver().findElement(By.cssSelector(".dropdown-menu > li > a")).click();
    }

    /**
     * To login to admin console DashBoard server.
     *
     * @param userName user name
     * @param pwd      password
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void loginToAdminConsole(String userName, String pwd)
            throws MalformedURLException, XPathExpressionException {
        getDriver().get(getBaseUrl() + ADMIN_CONSOLE_SUFFIX);
        getDriver().findElement(By.id("txtUserName")).clear();
        getDriver().findElement(By.id("txtUserName")).sendKeys(userName);
        getDriver().findElement(By.id("txtPassword")).clear();
        getDriver().findElement(By.id("txtPassword")).sendKeys(pwd);
        getDriver().findElement(By.cssSelector("input.button")).click();
    }

    /**
     * To logout from admin console dashboard server.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void logoutFromAdminConsole() throws MalformedURLException, XPathExpressionException {
        getDriver().get(getBaseUrl() + ADMIN_CONSOLE_SUFFIX);
        getDriver().findElement(By.cssSelector(".right > a")).click();
    }

    /**
     * Add dashboard to DashboardServer
     *
     * @param dashBoardTitle title of the dashboard
     * @param description    description of the dashboard
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void addDashBoard(String dashBoardTitle, String description)
            throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("[href='create-dashboard']")).click();
        getDriver().findElement(By.id("ues-dashboard-title")).clear();
        getDriver().findElement(By.id("ues-dashboard-title")).sendKeys(dashBoardTitle);
        getDriver().findElement(By.id("ues-dashboard-description")).clear();
        getDriver().findElement(By.id("ues-dashboard-description")).sendKeys(description);
        getDriver().findElement(By.id("ues-dashboard-create")).click();
        selectLayout("default-grid");
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
    }

    /**
     * Select the given layout.
     *
     * @param layout name of the layout to be selected
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void selectLayout(String layout) throws MalformedURLException, XPathExpressionException {
        WebElement defaultGridElem = getDriver().findElement(By.cssSelector("div[data-id='" + layout + "']"));
        defaultGridElem.click();
    }

    /**
     * Redirect user to the given location.
     *
     * @param domain   name of the domain where user wants to direct in to
     * @param location name of the location to be directed to
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void redirectToLocation(String domain, String location)
            throws MalformedURLException, XPathExpressionException {
        String url = getBaseUrl() + "/" + domain;
        if (location != null && !location.isEmpty()) {
            url += "/" + location;
        }
        getDriver().get(url);
    }

    /**
     * Modify the timeout as to the given value.
     *
     * @param seconds time to replace the default timeout of selenium
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void modifyTimeOut(int seconds) throws MalformedURLException, XPathExpressionException {
        getDriver().manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }

    /**
     * Reset the timeout of selenium back to default.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void resetTimeOut() throws MalformedURLException, XPathExpressionException {
        getDriver().manage().timeouts().implicitlyWait(getMaxWaitTime(), TimeUnit.SECONDS);
    }

    /**
     * Add a page to the dashboard.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void addPageToDashboard() throws MalformedURLException, XPathExpressionException {
        selectPane("pages");
        getDriver().findElement(By.cssSelector("button[rel='createPage']")).click();
        selectLayout("single-column");
    }

    /**
     * Add a page to the dashboard with specified layout.
     *
     * @param layout name for the newly added page's layout
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    public void addPageToDashboard(String layout)
            throws MalformedURLException, XPathExpressionException, InterruptedException {
        selectPane("pages");
        getDriver().findElement(By.cssSelector("button[rel='createPage']")).click();
        selectLayout(layout);
    }

    /**
     * Switch to the given page.
     *
     * @param pageID ID of the page to be switched to
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void switchPage(String pageID) throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.cssSelector("div[data-id='" + pageID + "']")).click();
    }

    /**
     * Switch to the given view in designer mode.
     *
     * @param view Name of the view. Valid names are {@code default} and {@code anon}
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void switchView(String view) throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.cssSelector("ul#designer-view-mode li[data-view-mode='" + view + "']")).click();
    }

    /**
     * Select specified pane in designer mode.
     *
     * @param pane Name of the pane. Valid names are {@code pages}, {@code layouts} and {@code gadgets}
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void selectPane(String pane) throws MalformedURLException, XPathExpressionException {
        pane = pane.trim().toLowerCase();
        if (pane.equals("pages")) {
            getDriver().findElement(By.cssSelector("a#btn-" + pane + "-sidebar")).click();
        } else {
            getDriver().findElement(By.cssSelector("a#btn-sidebar-" + pane)).click();
        }
    }

    /**
     * Clicks the View link in designer mode.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void clickViewButton() throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.className("ues-dashboard-preview")).click();
    }

    /**
     * Add dashboard to Dashboard Server.
     *
     * @param username       username of the user
     * @param password       password of the user
     * @param retypePassword retype password of the user
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void addUser(String username, String password, String retypePassword)
            throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.cssSelector("a[href=\"../userstore/add-user-role.jsp" +
                "?region=region1&item=user_mgt_menu_add\"]")).click();
        getDriver().findElement(By.cssSelector("a[href=\"../user/add-step1.jsp\"]")).click();
        getDriver().findElement(By.name("username")).clear();
        getDriver().findElement(By.name("username")).sendKeys(username);
        getDriver().findElement(By.name("password")).clear();
        getDriver().findElement(By.name("password")).sendKeys(password);
        getDriver().findElement(By.name("retype")).clear();
        getDriver().findElement(By.name("retype")).sendKeys(retypePassword);
        // Get a way to next button
        getDriver().findElement(By.cssSelector("input.button")).click();
        getDriver().findElement(By.cssSelector("td.buttonRow > input.button")).click();
        getDriver().findElement(By.cssSelector("button[type=\"button\"]")).click();
    }

    /**
     * Add dashboard to Dashboard Server.
     *
     * @param roleName name of role
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void addRole(String roleName) throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.cssSelector("a[href=\"../userstore/add-user-role.jsp" +
                "?region=region1&item=user_mgt_menu_add\"]")).click();
        getDriver().findElement(By.cssSelector("a[href=\"../role/add-step1.jsp\"]")).click();
        getDriver().findElement(By.name("roleName")).clear();
        getDriver().findElement(By.name("roleName")).sendKeys(roleName);
        getDriver().findElement(By.cssSelector("input.button")).click();
        getDriver().findElement(By.cssSelector("td.buttonRow > input.button")).click();
    }

    /**
     * Assign roles for users.
     *
     * @param userNames array fo usernames
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void assignRoleToUser(String[] userNames) throws MalformedURLException, XPathExpressionException {
        for (String userName : userNames) {
            getDriver().findElement(By.cssSelector("input[value='" + userName + "']")).click();
        }
        getDriver().findElement(By.cssSelector("input.button[value='Finish']")).click();
        getDriver().findElement(By.cssSelector("div.ui-dialog-buttonpane button")).click();
    }

    /**
     * This method returns the web driver instance.
     *
     * @return DSWebDriver - the driver instance of DSWebDriver
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public DSWebDriver getDriver() throws MalformedURLException, XPathExpressionException {
        if (driver == null) {
            driver = new DSWebDriver(BrowserManager.getWebDriver(), getMaxWaitTime());
        }
        return driver;
    }

    /**
     * This method returns the we driver wait instance.
     *
     * @return DSWebDriverWait - the webDriverWait instance of DSWebDriverWait
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public WebDriverWait getWebDriverWait() throws MalformedURLException, XPathExpressionException {
        if (wait == null) {
            wait = new WebDriverWait(getDriver(), getMaxWaitTime());
        }
        return wait;
    }

    /**
     * Final method for each test classes.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void dsUITestTearDown() throws MalformedURLException, XPathExpressionException {
        try {
            logout();
        } finally {
            getDriver().quit();
        }
    }

    /**
     * Check whether the registry resource exists or not
     *
     * @param resourcePath path to the resource
     * @return a flag saying the resource exists or not
     */
    public boolean isResourceExist(String resourcePath) {
        boolean isResourceExist = true;
        try {
            String backendURL = getBackEndUrl();
            ResourceAdminServiceClient resourceAdminServiceClient = new ResourceAdminServiceClient(backendURL,
                    getCurrentUsername(), getCurrentPassword());
            resourceAdminServiceClient.getResourceContent(resourcePath);
        } catch (ResourceAdminServiceExceptionException ex) {
            isResourceExist = false;
        } catch (AxisFault ex) {
            isResourceExist = false;
        } catch (Exception ex) {
            LOG.error(ex);
        }
        return isResourceExist;
    }

    /**
     * Delete dashboards according to the permissions of logged in user.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    public void deleteDashboards() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        List<WebElement> dashboardElements = getDriver().findElements(By.cssSelector("div.ues-dashboards div.ues-dashboard"));
        List<String> dashboardIds = new ArrayList<String>();
        // Get all dashboard ids from list
        for (WebElement dashboardElement : dashboardElements) {
            dashboardIds.add(dashboardElement.getAttribute("id"));
        }
        // Delete dashboards
        for (String dashboardId : dashboardIds) {
            WebElement dashboardElement = getDriver().findElement(By.id(dashboardId));
            List<WebElement> trashElements = dashboardElement.findElements(By.cssSelector("a.ues-dashboard-trash-handle"));
            if (trashElements.size() == 1) {
                dashboardElement.findElement(By.cssSelector("a.ues-dashboard-trash-handle")).click();
                dashboardElement.findElement(By.cssSelector("a.ues-dashboard-trash-confirm")).click();
            }
        }
    }

    /**
     * Get the full path to a file within the portal.
     *
     * @param relativePath Relative path to the file within a portal
     * @return Full qualified path
     */
    public String getPortalFilePath(String relativePath) {
        return FrameworkPathUtil.getCarbonHome() + File.separator + "repository" + File.separator + "deployment" +
                File.separator + "server" + File.separator + "jaggeryapps" + File.separator + "portal" +
                File.separator + relativePath;
    }
}
