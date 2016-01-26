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

package org.wso2.ds.ui.integration.util;

import ds.integration.tests.common.domain.DSIntegrationTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.ds.integration.common.clients.ResourceAdminServiceClient;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public abstract class DSUIIntegrationTest extends DSIntegrationTest {

    private static final Log LOG = LogFactory.getLog(DSUIIntegrationTest.class);
    private static final String DS_SUFFIX = "/portal/login-controller?destination=%2Fportal%2F";
    private static final String DS_HOME_SUFFIX = "/portal/dashboards";
    private static final String ADMIN_CONSOLE_SUFFIX = "/carbon/admin/index.jsp";

    protected String resourcePath;
    private DSWebDriver driver = null;
    private WebDriverWait wait = null;
    private Stack<String> windowHandles = new Stack<String>();

    /**
     * Constructor for the DSUIIntegrationTest
     */
    public DSUIIntegrationTest() {
        super();
    }

    /**
     * Constructor for the DSUIIntegrationTest
     *
     * @param userMode user mode to initiate the class
     */
    public DSUIIntegrationTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Get JS script to simulate adding gadgets to the page
     *
     * @param mappings array of gadget mappings in { gadget id, target id} format
     * @return JS script
     */
    public String generateAddGadgetScript(String[][] mappings) {
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

    /**
     * Switch to a child window while remembering the parent window
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    public void pushWindow() throws MalformedURLException, XPathExpressionException {
        driver = getDriver();
        String currentWindowHandle = driver.getWindowHandle();

        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(currentWindowHandle)) {
                driver.switchTo().window(windowHandle);
                driver.manage().window().setSize(new Dimension(1920, 1080));
                break;
            }
        }

        windowHandles.push(currentWindowHandle);
    }

    /**
     * Switch to the parent window (while is remembered previously) from a child window
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
     * To login to Dashboard server
     *
     * @param userName user name
     * @param pwd      password
     * @throws javax.xml.xpath.XPathExpressionException,InterruptedException
     */
    public void login(String userName, String pwd) throws Exception {
        String fullUrl = "";
        fullUrl = getBaseUrl() + DS_SUFFIX;
        driver = getDriver();

        driver.get(fullUrl);
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys(userName);
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys(pwd);
        driver.findElement(By.cssSelector(".ues-signin")).click();
    }

    /**
     * To login to Dashboard server when SSO is enabled
     *
     * @param userName user name
     * @param pwd      password
     * @throws javax.xml.xpath.XPathExpressionException,InterruptedException
     */
    public void loginWithSSO(String userName, String pwd) throws Exception {
        String fullUrl = "";
        fullUrl = getBaseUrl() + DS_SUFFIX;
        driver = getDriver();

        driver.get(fullUrl);
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys(userName);
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys(pwd);
        driver.findElement(By.tagName("button")).click();
    }

    /**
     * To logout from Dashboard server
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public void logout() throws Exception {
        String fullUrl = "";
        fullUrl = getBaseUrl() + DS_HOME_SUFFIX;
        driver = getDriver();

        driver.get(fullUrl);
        driver.findElement(By.cssSelector(".dropdown-toggle")).click();
        driver.findElement(By.cssSelector(".dropdown-menu > li > a")).click();
    }

    /**
     * To login to admin console DashBoard server
     *
     * @param userName user name
     * @param pwd      password
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public void loginToAdminConsole(String userName, String pwd) throws Exception {
        driver = getDriver();

        driver.get(getBaseUrl() + ADMIN_CONSOLE_SUFFIX);
        driver.findElement(By.id("txtUserName")).clear();
        driver.findElement(By.id("txtUserName")).sendKeys(userName);
        driver.findElement(By.id("txtPassword")).clear();
        driver.findElement(By.id("txtPassword")).sendKeys(pwd);
        driver.findElement(By.cssSelector("input.button")).click();
    }

    /**
     * To logout from admin console dashboard server
     */
    public void logoutFromAdminConsole() throws Exception {
        driver = getDriver();

        driver.get(getBaseUrl() + ADMIN_CONSOLE_SUFFIX);
        driver.findElement(By.cssSelector(".right > a")).click();
    }

    /**
     * Add dashboard to DashboardServer
     *
     * @param dashBoardTitle the title of a dashboard
     * @param description    the description about dashboard
     */
    public void addDashBoard(String dashBoardTitle, String description) throws Exception {
        driver = getDriver();

        redirectToLocation("portal", "dashboards");
        driver.findElement(By.cssSelector("[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(dashBoardTitle);
        driver.findElement(By.id("ues-dashboard-description")).clear();
        driver.findElement(By.id("ues-dashboard-description")).sendKeys(description);
        driver.findElement(By.id("ues-dashboard-create")).click();
        selectLayout("default-grid");
        redirectToLocation("portal", "dashboards");
    }

    /**
     * Select the given layout
     *
     * @param layout name of the layout to be selected
     */
    public void selectLayout(String layout) throws Exception {
        driver = getDriver();

        driver.findElement(By.cssSelector("a[data-id='" + layout + "']")).click();
    }

    /**
     * Redirect user to given location
     *
     * @param domain   name of the domain where user wants to direct in to
     * @param location name of the location to be directed to
     */
    public void redirectToLocation(String domain, String location) throws Exception {
        driver = getDriver();
        String url = getBaseUrl() + "/" + domain;

        if (location != null && !location.isEmpty()) {
            url += "/" + location;
        }
        driver.get(url);
    }

    /**
     * Modify the timeout as to the given value
     *
     * @param seconds Time to replace the default timeout of selenium
     */
    public void modifyTimeOut(int seconds) throws Exception {
        getDriver().manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }

    /**
     * Reset the timeout of selenium back to default
     */
    public void resetTimeOut() throws Exception {
        getDriver().manage().timeouts().implicitlyWait(getMaxWaitTime(), TimeUnit.SECONDS);
    }

    /**
     * Add a page to the dashboard
     */
    public void addPageToDashboard() throws Exception {
        driver = getDriver();
        driver.findElement(By.cssSelector("a.ues-pages-toggle")).click();
        driver.findElement(By.cssSelector(".ues-page-add")).click();
        selectLayout("single-column");
        driver.findElement(By.cssSelector(".ues-page-item.active")).findElement(By.cssSelector("a.accordion-toggle")).click();
    }

    /**
     * Switch to the given page
     *
     * @param pageID ID of the page to be switched to
     */
    public void switchPage(String pageID) throws Exception {
        driver = getDriver();
        driver.findElement(By.cssSelector("a[data-id='" + pageID + "']")).click();
    }

    /**
     * Add dashboard to DashboardServer
     *
     * @param username       username of user
     * @param password       password of user
     * @param retypePassword retype password of user
     */
    public void addUser(String username, String password, String retypePassword) throws Exception {
        driver = getDriver();

        driver.findElement(By.cssSelector("a[href=\"../userstore/add-user-role" +
                ".jsp?region=region1&item=user_mgt_menu_add\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"../user/add-step1.jsp\"]")).click();
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("retype")).clear();
        driver.findElement(By.name("retype")).sendKeys(retypePassword);
        //get a way to next button
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.cssSelector("td.buttonRow > input.button")).click();
        driver.findElement(By.cssSelector("button[type=\"button\"]")).click();
    }

    /**
     * Add dashboard to DashboardServer
     *
     * @param roleName name of role
     */
    public void addRole(String roleName) throws Exception {
        driver = getDriver();
        driver.findElement(By.cssSelector("a[href=\"../userstore/add-user-role" +
                ".jsp?region=region1&item=user_mgt_menu_add\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"../role/add-step1.jsp\"]")).click();
        driver.findElement(By.name("roleName")).clear();
        driver.findElement(By.name("roleName")).sendKeys(roleName);
        driver.findElement(By.cssSelector("input.button")).click();
        driver.findElement(By.cssSelector("td.buttonRow > input.button")).click();
    }

    /**
     * Assign roles for users
     *
     * @param userNames array fo userNames
     */
    public void assignRoleToUser(String[] userNames) throws Exception {
        driver = getDriver();

        for (String userName : userNames) {
            driver.findElement(By.cssSelector("input[value='" + userName + "']")).click();
        }
        driver.findElement(By.cssSelector("input.button[value='Finish']")).click();
        driver.findElement(By.cssSelector("div.ui-dialog-buttonpane button")).click();
    }

    /**
     * This method returns the web driver instance
     *
     * @return DSWebDriver - the driver instance of DSWebDriver
     */
    public DSWebDriver getDriver() throws MalformedURLException, XPathExpressionException {
        if (driver == null) {
            driver = new DSWebDriver(BrowserManager.getWebDriver(), getMaxWaitTime());
        }
        return driver;
    }

    /**
     * This method returns the we driver wait instance
     *
     * @return DSWEbDriverWait - the webDriverWait instance of DSWebDriverWait
     */
    public WebDriverWait getWebDriverWait() throws MalformedURLException, XPathExpressionException {
        if (wait == null) {
            wait = new WebDriverWait(getDriver(), getMaxWaitTime());
        }
        return wait;
    }

    /**
     * final method for each test classes
     *
     * @throws Exception
     */
    public void dsUITestTearDown() throws Exception {
        driver = getDriver();
        try {
            logout();
        } finally {
            driver.quit();
        }
    }

    /**
     * This method will check the resource is exist or not in registry
     *
     * @param resourcePath - the path of resource
     * @return isResourceExist - true/false
     */
    public boolean isResourceExist(String resourcePath) {
        boolean isResourceExist;
        try {
            String backendURL = getBackEndUrl();
            ResourceAdminServiceClient resourceAdminServiceClient = new ResourceAdminServiceClient(backendURL,
                    getCurrentUsername(),
                    getCurrentPassword());
            resourceAdminServiceClient.getResourceContent(resourcePath);
            isResourceExist = true;
        } catch (Exception ex) {
            isResourceExist = false;
        }
        return isResourceExist;
    }

}
