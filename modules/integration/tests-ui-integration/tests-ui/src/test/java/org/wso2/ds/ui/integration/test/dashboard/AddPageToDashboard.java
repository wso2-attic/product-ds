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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AddPageToDashboard extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "sampleDashBoard";
    private static final String DASHBOARD_DESCRIPTION = "This is description about " + DASHBOARD_TITLE;
    private static final String DASHBOARD_PAGENAME = "newPage1SampleDashboard";
    private static final String DASHBOARD_URL = "newPage1SampleDashboardURL";
    private String dashboardTitle;

    @Factory(dataProvider = "userMode")
    public AddPageToDashboard(TestUserMode userMode, String dashboardTitle) {
        super(userMode);
        this.dashboardTitle = dashboardTitle;
    }

    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE}};
    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding a new page to dashboard App and edit the name of page " +
            "name for dashboard server")
    public void testAddEditPageDashboardNew() throws Exception {
        DSWebDriver driver = getDriver();

        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, DASHBOARD_DESCRIPTION);

        WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
        webElement.findElement(By.cssSelector(".ues-edit")).click();

        addPageToDashboard();
        driver.findElement(By.cssSelector("[name=title]")).clear();
        driver.findElement(By.cssSelector("[name=title]")).sendKeys(DASHBOARD_PAGENAME);
        driver.executeScript("$('[name=title]').change();");
        driver.findElement(By.cssSelector("[name=id]")).clear();
        driver.findElement(By.cssSelector("[name=id]")).sendKeys(DASHBOARD_URL);
        driver.executeScript("$('[name=id]').change();");

        assertEquals(driver.findElement(By.cssSelector("h4.ues-page-title")).
                        findElement(By.cssSelector("span.page-title")).getText(), DASHBOARD_PAGENAME,
                "error occurred while edit the new page name");

        //checks the name of added newest page under pages drop list
        int count = 0;
        WebElement pageList = driver.findElement(By.cssSelector("#ues-page-properties"));
        List<WebElement> pages = pageList.findElements(By.cssSelector("a.accordion-toggle"));
        for (WebElement we : pages) {
            if (we.getText().equalsIgnoreCase(DASHBOARD_PAGENAME)) {
                count++;
            }
        }

        assertEquals(count, 1, "some errors occurred when editing the new page Title in dashboard App");
    }

    @Test(groups = "wso2.ds.dashboard", description = "Test the checkbox of landing view", dependsOnMethods =
            "testAddEditPageDashboardNew")
    public void testLandingCheckBox() throws Exception {
        DSWebDriver driver = getDriver();

        switchPage(DASHBOARD_URL.toLowerCase());
        driver.findElement(By.cssSelector("[name=landing]")).click();
        driver.findElement(By.cssSelector("i.fw.fw-view")).click();
        pushWindow();

        String fullUrl = driver.getCurrentUrl();
        Boolean status = fullUrl.toLowerCase().contains(DASHBOARD_URL.toLowerCase());

        assertFalse(status, "landing with a newly added page is not configured properly");

        driver.close();
        popWindow();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Deleting a newly added page from dashboard App " +
            "name for dashboard server", dependsOnMethods = "testLandingCheckBox")
    public void testDeletePageDashboardNew() throws Exception {
        DSWebDriver driver = getDriver();

        WebElement pageList = driver.findElement(By.cssSelector("#ues-page-properties"));
        WebElement newPageElement = driver.findElement(By.
                cssSelector("a[data-id='" + DASHBOARD_URL.toLowerCase() + "']"));
        newPageElement.findElement(By.cssSelector(".ues-trash")).click();
        driver.findElement(By.id("button-0")).click();

        int count = 0;
        List<WebElement> pages = pageList.findElements(By.cssSelector("a.accordion-toggle"));
        for (WebElement we : pages) {
            if (we.getText().equalsIgnoreCase(DASHBOARD_PAGENAME)) {
                count++;
            }
        }

        assertEquals(count, 0, "some errors occurred when deleting the newly added page to dashboard App");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        dsUITestTearDown();
    }
}
