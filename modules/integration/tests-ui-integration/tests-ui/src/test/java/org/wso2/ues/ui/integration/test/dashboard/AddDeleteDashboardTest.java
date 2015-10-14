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

package org.wso2.ues.ui.integration.test.dashboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.registry.resource.stub.beans.xsd.CollectionContentBean;
import org.wso2.ues.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.ues.ui.integration.util.UESUIIntegrationTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class AddDeleteDashboardTest extends UESUIIntegrationTest {

    private static final Log LOG = LogFactory.getLog(AddDeleteDashboardTest.class);
    private static final String DASHBOARD_TITLE1 = "sampledashboard1";
    private static final String DASHBOARD_TITLE2 = "sampledashboard2";
    private static final String DASHBOARD_DESCRIPTION = "This is sample descrition for dashboard";
    private ResourceAdminServiceClient resourceAdminServiceClient;
    private String dashboardTitle;
    private WebElement webElement = null;

    @Factory(dataProvider = "userMode")
    public AddDeleteDashboardTest(TestUserMode userMode, String dashboardTitle) {
        super(userMode);
        this.dashboardTitle = dashboardTitle;
    }

    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE1},
                {TestUserMode.SUPER_TENANT_USER, DASHBOARD_TITLE2}};
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        String backendURL = getBackEndUrl();
        resourceAdminServiceClient = new ResourceAdminServiceClient(backendURL, getCurrentUsername(),
                getCurrentPassword());
        resourcePath = DASHBOARD_REGISTRY_BASE_PATH + dashboardTitle.toLowerCase();
        UESUIIntegrationTest.login(getDriver(), getBaseUrl(), getCurrentUsername(), getCurrentPassword());
    }

    @Test(groups = "wso2.ues.dashboard", description = "Adding new dashboard for dashboard server")
    public void testAddDashboardNew() throws Exception {
        getDriver().findElement(By.cssSelector("[href='create-dashboard']")).click();
        getDriver().findElement(By.id("ues-dashboard-title")).clear();
        getDriver().findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        getDriver().findElement(By.id("ues-dashboard-description")).clear();
        getDriver().findElement(By.id("ues-dashboard-description")).sendKeys(DASHBOARD_DESCRIPTION);
        getDriver().findElement(By.id("ues-dashboard-create")).click();
        getDriver().findElement(By.id("single-column")).click();
        getDriver().findElement(By.cssSelector("a.navbar-brand.ues-tiles-menu-toggle")).click();
        getDriver().findElement(By.cssSelector("i.fw.fw-dashboard")).click();
        getWebDriverWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(dashboardTitle)));
        webElement = getDriver().findElement(By.id(dashboardTitle));
        assertEquals(dashboardTitle, webElement.findElement(By.cssSelector("h2")).getText());
        assertEquals(DASHBOARD_DESCRIPTION, webElement.findElement(By.cssSelector("p")).getText());
    }

    @Test(groups = "wso2.ues.dashboard", description = "Deleting the existing dashboard from dashboard server",
            dependsOnMethods = "testAddDashboardNew")
    public void testDeleteDashboardNew() throws Exception {
        Boolean isResourceExist = false;
        webElement = getDriver().findElement(By.id(dashboardTitle));
        webElement.findElement(By.cssSelector("i.fw-delete")).click();
        getDriver().findElement(By.cssSelector("span.ladda-label")).click();
        assertFalse(getDriver().isElementPresent(By.id(dashboardTitle)), "Error occurred while deleting dashboard" +
                dashboardTitle);
        CollectionContentBean collectionContentBean;
        new CollectionContentBean();
        collectionContentBean = resourceAdminServiceClient.getCollectionContent(DASHBOARD_REGISTRY_BASE_PATH);
        if (collectionContentBean.getChildCount() > 0) {
            String[] childPath = collectionContentBean.getChildPaths();
            for (int i = 0; i <= childPath.length - 1; i++) {
                if (childPath[i].equalsIgnoreCase(resourcePath)) {
                    isResourceExist = true;
                }
            }
        }
        assertFalse(isResourceExist, "Registry resource could not be deleted due to some errors");

    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        try {
            UESUIIntegrationTest.logout(getDriver(), getBaseUrl(), getCurrentUsername());
        } finally {
            getDriver().quit();
        }
    }

}