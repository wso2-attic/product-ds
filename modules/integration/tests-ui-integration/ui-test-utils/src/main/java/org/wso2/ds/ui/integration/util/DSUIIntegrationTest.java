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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;

public abstract class DSUIIntegrationTest extends DSIntegrationTest {

    protected static final String DASHBOARD_REGISTRY_BASE_PATH = "/_system/config/ues/dashboards/";
    private static final Log LOG = LogFactory.getLog(DSUIIntegrationTest.class);
    private static final String DS_SUFFIX = "/portal/login-controller?destination=%2Fportal%2F";
    private static final String DS_HOME_SUFFIX = "/portal/";
    private static final String ADMIN_CONSOLE_SUFFIX = "/carbon/admin/index.jsp";
    protected String resourcePath;
    private DSWebDriver driver = null;
    private WebDriverWait wait = null;

    public DSUIIntegrationTest() {
        super();
    }

    public DSUIIntegrationTest(TestUserMode userMode) {
        super(userMode);

    }

    /**
     * To login to Dashboard server
     *
     * @param driver   WebDriver instance
     * @param url      base url of the server
     * @param userName user name
     * @param pwd      password
     * @throws javax.xml.xpath.XPathExpressionException,InterruptedException
     */
    public static void login(DSWebDriver driver, String url, String userName, String pwd)
            throws InterruptedException, XPathExpressionException {
        String fullUrl = "";
        fullUrl = url + DS_SUFFIX;
        driver.get(fullUrl);
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys(userName);
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys(pwd);
        driver.findElement(By.cssSelector(".ues-signin")).click();
    }

    /**
     * To logout from Dashboard server
     *
     * @param driver   WebDriver instance
     * @param url      base url of the server
     * @param userName user name
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public static void logout(DSWebDriver driver, String url, String userName) throws
            XPathExpressionException {
        String fullUrl = "";
        fullUrl = url + DS_HOME_SUFFIX;
        driver.get(fullUrl);
        driver.findElement(By.cssSelector(".dropdown-toggle")).click();
        driver.findElement(By.cssSelector(".dropdown-menu > li > a")).click();
    }

    /**
     * To login to admin console DashBoard server
     *
     * @param driver   WebDriver instance
     * @param url      base url of the server
     * @param userName user name
     * @param pwd      password
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public static void loginToAdminConsole(DSWebDriver driver, String url, String userName, String pwd) throws
            XPathExpressionException {
        driver.get(url + ADMIN_CONSOLE_SUFFIX);
        driver.findElement(By.id("txtUserName")).clear();
        driver.findElement(By.id("txtUserName")).sendKeys(userName);
        driver.findElement(By.id("txtPassword")).clear();
        driver.findElement(By.id("txtPassword")).sendKeys(pwd);
        driver.findElement(By.cssSelector("input.button")).click();
    }

    /**
     * To logout from admin console dashboard server
     *
     * @param driver WebDriver instance
     * @param url    base url of the server
     */
    public static void logoutFromAdminConsole(DSWebDriver driver, String url) {
        driver.get(url + ADMIN_CONSOLE_SUFFIX);
        driver.findElement(By.cssSelector(".right > a")).click();
    }

    /**
     * Add dashboard to DashboardServer
     *
     * @param driver         WebDriver instance
     * @param dashBoardTitle the title of a dashboard
     * @param description    the description about dashboard
     */
    public static void addDashBoard(DSWebDriver driver, String dashBoardTitle, String description) {
        driver.findElement(By.cssSelector("[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(dashBoardTitle);
        driver.findElement(By.id("ues-dashboard-description")).clear();
        driver.findElement(By.id("ues-dashboard-description")).sendKeys(description);
        driver.findElement(By.id("ues-dashboard-create")).click();
        driver.findElement(By.id("single-column")).click();
        driver.findElement(By.cssSelector("a.navbar-brand.ues-tiles-menu-toggle")).click();
        driver.findElement(By.cssSelector("i.fw.fw-dashboard")).click();

    }

    /**
     * Add dashboard to DashboardServer
     *
     * @param driver         DSwebdriver instance
     * @param username       username of user
     * @param password       password of user
     * @param retypePassword retype password of user
     */
    public static void AddUser(DSWebDriver driver, String username, String password, String retypePassword) {
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
     * @param driver   DSwebdriver instance
     * @param roleName name of role
     */
    public static void addRole(DSWebDriver driver, String roleName) {
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
     * @param driver    DSwebdriver instance
     * @param roleName name of the role
     * @param userNames array fo userNames
     */
    public static void assignRoleToUser(DSWebDriver driver,String roleName, String[] userNames) {
        driver.findElement(By.cssSelector("a[href=\"edit-users.jsp?roleName="+roleName+"&org.wso2.carbon.role" +
                ".read.only=false\"]")).click();
        for (String userName : userNames) {
            driver.findElement(By.cssSelector("input[value='" + userName + "']")).click();
        }
        driver.findElement(By.cssSelector("input.button[value='Finish']")).click();
        driver.findElement(By.cssSelector("div.ui-dialog-buttonpane button")).click();
    }

    /**
     * This method returns the web driver instance
     *
     * @return DSWEbDriver - the driver instance of DSWebDriver
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
}
