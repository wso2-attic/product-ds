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

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests support for widgets in a dashboard.
 */
public class GadgetGenerationFrameworkTest extends DSUIIntegrationTest {
    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public GadgetGenerationFrameworkTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return
     */
    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN}};
    }

    /**
     * Setup the testing environment.
     *
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws java.io.IOException
     * @throws org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws XPathExpressionException, IOException, AutomationUtilException, InterruptedException {
        login(getCurrentUsername(), getCurrentPassword());
        deleteDashboards();
    }


    @Test(groups = "wso2.ds.dashboard",
            description = "Test gadget preview using rdbms provider and line chart templates")
    public void testGadgetPreview() throws MalformedURLException, XPathExpressionException, InterruptedException {
        DSWebDriver driver = getDriver();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.className("navbar-menu-toggle")).click();
        driver.findElement(By.cssSelector("i.fw.fw-gadget")).click();
        driver.findElement(By.cssSelector("i.fw.fw-add")).click();
        new Select(driver.findElement(By.id("providers"))).selectByVisibleText("Relational Database Source");
        driver.findElement(By.linkText("Next")).click();
        driver.findElement(By.name("db_url")).clear();
        driver.findElement(By.name("db_url")).sendKeys("jdbc:h2:repository/database/WSO2CARBON_DB");
        driver.findElement(By.name("table_name")).clear();
        driver.findElement(By.name("table_name")).sendKeys("IDP_METADATA");
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys("wso2carbon");
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("wso2carbon");
        driver.findElement(By.name("query")).clear();
        driver.findElement(By.name("query")).sendKeys("select * from IDP_METADATA");
        assertEquals(driver.findElement(By.id("test-verification-label")).getAttribute("style"), "display: none;",
                "valid text is present even before validating");
        driver.findElement(By.id("test-connection")).click();
        assertEquals(driver.findElement(By.id("test-verification-label")).getAttribute("style"), "",
                "database configuration validation failed");
        driver.findElement(By.id("show-data")).click();
        assertEquals(driver.findElement(By.id("sample-data-message")).getAttribute("style"),
                "display: inline;", "Data table preview is not shown");
        driver.findElement(By.linkText("Next")).click();
        driver.findElement(By.id("gadget-name")).clear();
        driver.findElement(By.id("gadget-name")).sendKeys("test gadget");
        new Select(driver.findElement(By.id("chart-type"))).selectByVisibleText("Line Chart");
        new Select(driver.findElement(By.name("x"))).selectByVisibleText("NAME");
        new Select(driver.findElement(By.name("y"))).selectByVisibleText("IDP_ID");
        assertEquals(driver.findElement(By.id("preview-pane")).findElements(By.xpath(".//*")).size(), 0,
                "Preview Pane is not empty");
        driver.findElement(By.id("preview")).click();
        assertEquals(driver.findElement(By.id("preview-pane")).findElements(By.xpath(".//*")).size(), 1,
                "Preview Pane is not rendered");
    }

    @Test(groups = "wso2.ds.dashboard", description = "Test adding generated gadget to store",
            dependsOnMethods = "testGadgetPreview")
    public void testAddGadgetToStore() throws MalformedURLException, XPathExpressionException, InterruptedException {
        DSWebDriver driver = getDriver();
        driver.findElement(By.linkText("Add to store")).click();
        assertEquals(driver.findElement(By.className("alert")).getText(), "Success!! Gadget is added to the store successfully",
                "Adding gadget to gadget store failed");
    }

    @Test(groups = "wso2.ds.dashboard", description = "Test rendering of the generated gadget",
            dependsOnMethods = "testAddGadgetToStore")
    public void testGeneratedGadget() throws MalformedURLException, XPathExpressionException, InterruptedException {
        DSWebDriver driver = getDriver();
        driver.findElement(By.linkText("Go to Portal")).click();
        driver.findElement(By.cssSelector("i.fw.fw-add")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys("Gadget Gen Dashboard");
        driver.findElement(By.id("ues-dashboard-create")).click();
        selectLayout("single-column");
        String[][] gadgetMappings = {{"test_gadget", "a"}};
        String script = generateAddGadgetScript(gadgetMappings);
        selectPane("gadgets");
        waitTillElementToBeClickable(By.id("test_gadget"));
        getDriver().executeScript(script);
        clickViewButton();
        pushWindow();

        List<WebElement> elements = new ArrayList<WebElement>();
        for (String[] mapping : gadgetMappings) {
            WebElement element = getDriver().findElement(
                    By.cssSelector("div#" + mapping[1] + ".ues-component-box .ues-component"));
            if (element != null) {
                elements.add(element);
            }
        }

        boolean gadgetsAvailable = true;
        if (elements.size() != gadgetMappings.length) {
            gadgetsAvailable = false;
        }

        assertTrue(gadgetsAvailable, "The gadget(s) not found");
        getDriver().close();
        popWindow();


    }

    /**
     * Clean up after running tests.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws XPathExpressionException, MalformedURLException {
        try {
            logout();
        } finally {
            getDriver().quit();
        }
    }

}
