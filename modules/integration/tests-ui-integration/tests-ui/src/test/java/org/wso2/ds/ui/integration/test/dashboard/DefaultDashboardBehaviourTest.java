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
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests the default behaviour of the dashboard
 */
public class DefaultDashboardBehaviourTest extends DSUIIntegrationTest {
    private static final String USERNAME_EDITOR = "editor1";
    private static final String PASSWORD_EDITOR = "editor123";
    private static final String RETYPE_PASSWORD_EDITOR = "editor123";
    private static final String EDITOR_ROLE = "EditorsRole";

    /**
     * Initialize the class
     */
    @Factory(dataProvider = "userMode")
    public DefaultDashboardBehaviourTest(TestUserMode userMode) throws Exception {
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
        // delete all dashboards if exists
        deleteDashboards();
        logout();
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
        addUser(USERNAME_EDITOR, PASSWORD_EDITOR, RETYPE_PASSWORD_EDITOR);
        addRole(EDITOR_ROLE);
        assignRoleToUser(new String[]{USERNAME_EDITOR});
        logoutFromAdminConsole();
        // set default dashboard behaviour from designer.json file
        setDefaultDashboardBehaviour(true);
        restartServer();
        login(USERNAME_EDITOR, PASSWORD_EDITOR);
    }

    /**
     * Test whether the user is redirected to dashboard creation page
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.default.dashboard", description = "redirecting user to dashboard creation page when there " +
            "are no dashboards")
    public void testWithNoDashboards() throws Exception {
        DSWebDriver driver = getDriver();
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(DS_HOME_CONTEXT + "/create-dashboard"), "User didn't directed to dashboard creation page.");
    }

    /**
     * Test whether the user is redirected to dashboard page
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.default.dashboard", description = "redirecting user to dashboard when there is only one " +
            "dashboards", dependsOnMethods = "testWithNoDashboards")
    public void testWithOneDashboards() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardName = "dashboard-1";
        addDashBoard(dashboardName, "This is first dashboard in default behaviour");
        logout();
        login(USERNAME_EDITOR, PASSWORD_EDITOR);
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(DS_HOME_CONTEXT + "/" + DS_DASHBOARDS_CONTEXT + "/" + dashboardName), "User didn't directed to existing dashboard.");
    }

    /**
     * Test whether the user is redirected to dashboard list page when there are multiple dashboards
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.default.dashboard", description = "redirecting user to dashboard list when there are " +
            "multiple dashboards", dependsOnMethods = "testWithOneDashboards")
    public void testWithMultipleDashboards() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardName = "sales";
        addDashBoard(dashboardName, "This is second dashboard in default behaviour.");
        logout();
        login(USERNAME_EDITOR, PASSWORD_EDITOR);
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith(DS_HOME_CONTEXT + "/" + DS_DASHBOARDS_CONTEXT + "/"), "User didn't directed to dashboard list page.");
    }

    /**
     * Test whether the name of the dashboard is not case sensitive
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.default.dashboard", description = "Testing whether the name of the dashboard is not case " +
            "sensitive.", dependsOnMethods = "testWithMultipleDashboards")
    public void testDashboardNameCaseSensitivity() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardTitle = "SALES";
        String description = "Dashboard with uppercase title";
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        driver.findElement(By.id("ues-dashboard-description")).clear();
        driver.findElement(By.id("ues-dashboard-description")).sendKeys(description);
        driver.findElement(By.id("ues-dashboard-create")).click();
        Thread.sleep(500);
        String message = driver.findElement(By.cssSelector("div.modal-body")).getText().trim();
        assertEquals(message, "A dashboard with same URL already exists. Please enter a different dashboard URL.",
                "Dashboard name is not case insensitive.");
        driver.findElement(By.cssSelector("button#ues-modal-info-ok")).click();
        driver.findElement(By.cssSelector("a[href='./dashboards']")).click();
    }

    /**
     * Test whether the length of the dashboard name is valid
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.default.dashboard", description = "Testing the length of the dashboard name",
            dependsOnMethods = "testDashboardNameCaseSensitivity")
    public void testDashboardNameLength() throws Exception {
        DSWebDriver driver = getDriver();
        // title with 300 characters with spaces (without spaces: 257)
        String dashboardTitle = "Lorem ipsum dolor sit amets consectetur adipiscing elitq Etiam rhoncusw urna vitae " +
                "mattis hendrerite risus augue consequat ext non dignissim tellus metus a semg Aenean dictum tellus " +
                "eu fermentum vulputatec Morbi rhoncus bibendum arcu in hendrerith Quisque commodo lacus sit amet " +
                "est sagittis malesuam";
        String description = "Dashboard with more than 300 characters";
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        driver.findElement(By.id("ues-dashboard-description")).clear();
        driver.findElement(By.id("ues-dashboard-description")).sendKeys(description);
        String title = driver.findElement(By.id("ues-dashboard-title")).getAttribute("value");
        // verifying entered title to text box has valid number of characters
        assertTrue(title.length() <= 250, "Length of the title is not validated");
        driver.findElement(By.id("ues-dashboard-create")).click();
        selectLayout("default-grid");
        title = driver.findElement(By.cssSelector("div.page-title h1")).getText();
        // verifying saved name has valid number of characters
        assertTrue(title.length() <= 250, "Length of the saved title is not correct");
    }

    /**
     * Test whether the dashboard name has invalid characters
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.default.dashboard", description = "Testing invalid characters are allowed in dashboard name",
            dependsOnMethods = "testDashboardNameLength")
    public void testDashboardNameHasInvalidCharacters() throws Exception {
        DSWebDriver driver = getDriver();
        // title with invalid characters
        String dashboardTitle = "`~!@#$%^&*()_+=|}{[]?><";
        String description = "Dashboard name with invalid characters";
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        driver.findElement(By.id("ues-dashboard-description")).clear();
        driver.findElement(By.id("ues-dashboard-description")).sendKeys(description);
        String title = driver.findElement(By.id("ues-dashboard-title")).getAttribute("value");
        // verifying entered title to text box has valid characters
        assertTrue(title.length() == 0, "Title has invalid characters");
        driver.findElement(By.id("ues-dashboard-create")).click();
        String message = driver.findElement(By.cssSelector("#title-error")).getText().trim();
        assertEquals(message, "Required", "Title has invalid characters");
    }

    /**
     * Test whether the dashboard name allow numbers
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.default.dashboard", description = "Testing numbers are allowed in dashboard name",
            dependsOnMethods = "testDashboardNameHasInvalidCharacters")
    public void testDashboardNameAllowNumbers() throws Exception {
        DSWebDriver driver = getDriver();
        String dashboardTitle = "0123456789";
        String description = "Dashboard name with numbers";
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        driver.findElement(By.id("ues-dashboard-description")).clear();
        driver.findElement(By.id("ues-dashboard-description")).sendKeys(description);
        String title = driver.findElement(By.id("ues-dashboard-title")).getAttribute("value");
        // verifying entered title to text box has exact characters
        assertEquals(title, dashboardTitle, "Name of the dashboard does not allow numbers");
        driver.findElement(By.id("ues-dashboard-create")).click();
        selectLayout("default-grid");
        title = driver.findElement(By.cssSelector("div.page-title h1")).getText();
        // verifying saved name has exact name entered
        assertEquals(title, dashboardTitle, "Name of the dashboard does not allow numbers");
    }

    /**
     * Test whether the length of the dashboard name is valid
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.default.dashboard", description = "Testing the length of the dashboard description",
            dependsOnMethods = "testDashboardNameAllowNumbers")
    public void testDashboardDescriptionLength() throws Exception {
        DSWebDriver driver = getDriver();
        // description with 500 characters (with spaces: 426)
        String dashboardTitle = "Descriptive-Dashboard";
        String dashboardDescription = "Lorem ipsum dolor sit amet consectetur adipiscing elite Aenean nec lobortis enimh Nam " +
                "vel erat non nulla porttitor cursusw Nulla elementaum mauris quis est dignissim facilisis a sed nuncr " +
                "Pellentesque feugiat nisl nec quam mollisq eget dictum felis imperdieta In tincidunt finibus " +
                "volutpatw Etiam vitae lobortis liberor Quisque pellentesque mattis anteb Nunc cursus ac ipsum in " +
                "variusw Praesent mattis elementum nisi et fermentumn Nulla libero antes iaculis a varius nons " +
                "viverra et augue nunc suscid";
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        driver.findElement(By.id("ues-dashboard-description")).clear();
        driver.findElement(By.id("ues-dashboard-description")).sendKeys(dashboardDescription);
        String description = driver.findElement(By.id("ues-dashboard-description")).getAttribute("value").trim();
        // verifying entered title to text box has valid number of characters
        assertTrue(description.length() == 500, "Length of the description is not validated.");
        // add more characters to description
        dashboardDescription += "abc";
        driver.findElement(By.id("ues-dashboard-description")).clear();
        driver.findElement(By.id("ues-dashboard-description")).sendKeys(dashboardDescription);
        description = driver.findElement(By.id("ues-dashboard-description")).getAttribute("value").trim();
        assertTrue(description.length() == 500, "Length of the description is not validated.");
        driver.findElement(By.id("ues-dashboard-create")).click();
        selectLayout("default-grid");
        driver.findElement(By.cssSelector("#dashboard-settings")).click();
        description = driver.findElement(By.id("ues-dashboard-description")).getAttribute("value").trim();
        assertTrue(description.length() == 500, "Length of the description is not validated.");
    }

    /**
     * Set default dashboard behaviour by configuring designer json file.
     *
     * @param behaviour
     * @throws Exception
     */
    public void setDefaultDashboardBehaviour(boolean behaviour) throws Exception {
        PrintWriter pw = null;
        try {
            String designerFilePath = FrameworkPathUtil.getCarbonHome() + File.separator + "repository" + File.separator + "deployment" +
                    File.separator + "server" + File.separator + "jaggeryapps" + File.separator + "portal" +
                    File.separator + "configs" + File.separator + "designer.json";

            File f = new File(designerFilePath);
            BufferedReader br = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            JSONObject designerJson;
            while (br.ready()) {
                sb.append(br.readLine());
            }
            br.close();
            // convert json string to json object
            designerJson = new JSONObject(sb.toString());
            // set active method
            designerJson.put("defaultDashboardRedirect", behaviour);
            pw = new PrintWriter(f);
            pw.println(designerJson.toString());
            pw.flush();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * Restarts the server gracefully
     *
     * @throws Exception
     */
    public void restartServer() throws Exception {
        AutomationContext automationContext = new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME, this.userMode);
        ServerConfigurationManager serverConfigurationManager = new ServerConfigurationManager(automationContext);
        serverConfigurationManager.restartGracefully();
    }

    /**
     * Clean up after running tests
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        setDefaultDashboardBehaviour(false);
        restartServer();
        getDriver().quit();
    }
}
