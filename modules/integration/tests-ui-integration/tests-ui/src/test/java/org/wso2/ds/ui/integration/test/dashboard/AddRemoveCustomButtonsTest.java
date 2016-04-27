/**
 * Copyright (c) 2016​, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class written for testing the new feature flexibility to adding new custom buttons and hiding default
 * buttons in view mode of dashboard in gadget title bar
 */
public class AddRemoveCustomButtonsTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "sampledashboard1";
    private static final String DASHBOARD_DESCRIPTION = "This is sample description for dashboard";
    private static final String GADGET_NAME = "gadget-sample";

    private WebElement webElement = null;

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public AddRemoveCustomButtonsTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return userMode
     */
    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{
                {TestUserMode.SUPER_TENANT_ADMIN}
        };
    }

    /**
     * Setup the testing environment.
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        login(getCurrentUsername(), getCurrentPassword());
    }

    /**
     * This test method will test adding custom buttons to gadget title bar and shown in dashboard designer mode and
     * view mode
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.dashboard", description = "Adding custom functions to gadget title bar", enabled = false)
    public void testCustomButtonsGadgeTitlebar() throws Exception {
        DSWebDriver driver = getDriver();
        addDashBoard(DASHBOARD_TITLE, DASHBOARD_DESCRIPTION);
        driver.findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        String[][] gadgetMappings = {{GADGET_NAME, "a"}};
        String script = generateAddGadgetScript(gadgetMappings);
        driver.findElement(By.cssSelector("i.fw.fw-gadget")).click();
        driver.executeScript(script);
        JSONObject gadgetJsonObj = getGadgetJSONObject();
        JSONObject toolbarButtons = (JSONObject) gadgetJsonObj.get("toolbarButtons");
        JSONArray customBtnsArr = (JSONArray) toolbarButtons.get("custom");
        for (Object customBtn : customBtnsArr) {
            JSONObject btnObj = (JSONObject) customBtn;
            String action_id = (String) btnObj.get("action");
            webElement = driver.findElement(By.id("a"));
            webElement.findElement(By.id("actionButtons")).click();
            driver.findElement(By.cssSelector("button[data-action='" + action_id + "']"));
            assertTrue(driver.isElementPresent(By.cssSelector("button[data-action='" + action_id + "']")), action_id
                    + "is not present in the designer Mode");
            getDriver().findElement(By.cssSelector("a.ues-dashboard-preview")).click();
            pushWindow();
            assertTrue(driver.isElementPresent(By.cssSelector("button[data-action='" + action_id + "']")), action_id
                    + "is not present in the view mode");
        }

    }

    /**
     * This method will test hiding default buttons in gadget title bar in view mode of dashboard.
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ds.dashboard", description = "Hide dafault buttons on gadget title-bar on view mode of " +
            "dashboard",
            dependsOnMethods = "testCustomButtonsGadgeTitlebar", enabled = false)
    public void testDefaultButtonsGadgetTitlebar() throws Exception {
        DSWebDriver driver = getDriver();
        JSONObject gadgetJsonObj = getGadgetJSONObject();
        JSONObject toolbarButtons = (JSONObject) gadgetJsonObj.get("toolbarButtons");
        JSONObject defaultbtns = (JSONObject) toolbarButtons.get("default");
        Boolean maximizeBtn = (Boolean) defaultbtns.get("maximize");
        if (!maximizeBtn) {
            popWindow();
            webElement = driver.findElement(By.id("a"));
            webElement.findElement(By.id("actionButtons")).click();
            webElement.findElement(By.cssSelector("button.ues-component-full-handle"));
            assertTrue(driver.isElementPresent(By.cssSelector("button.ues-component-full-handle")), " maximize " +
                    "button cannot be find in current UI designer mode");
            pushWindow();
            assertFalse(driver.isElementPresent(By.cssSelector("button.ues-component-full-handle")), "Maximize button" +
                    " should not be present in the view mode of dashboard");
        }
    }

    /**
     * This method will return JSON object of gadget.json file
     * @return JSONObject
     * @throws IOException
     * @throws ParseException
     */
    private JSONObject getGadgetJSONObject() throws IOException, ParseException {
        String carbonHome = FrameworkPathUtil.getCarbonHome();
        String fileName = carbonHome + File.separator + "repository" + File.separator + "deployment" +
                File.separator + "server" + File.separator + "jaggeryapps" + File.separator + "portal" +
                File.separator + "store" + File.separator + "carbon.super" +  File.separator + "fs" + File.separator  +
                "gadget" + File.separator + GADGET_NAME + File.separator + "gadget.json";
        JSONParser parser = new JSONParser();
        Object fileObj = parser.parse(new FileReader(fileName));
        return (JSONObject) fileObj;
    }

    /**
     * Clean up after running tests.
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        dsUITestTearDown();
    }

}
