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
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;

import static org.testng.Assert.*;

/**
 * Tests the dashboard personalization / user-pref related functionality
 */
public class GadgetUserPrefTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "userPrefDashboard";
    private static final String SUPER_TENANT_EDITOR_ROLE = "editor_userPref";
    private static final String SUPER_TENANT_VIEWER_ROLE = "viewer_userPref";
    private static final String TENANT_EDITOR_ROLE = "t_editor_userPref";
    private static final String TENANT_VIEWER_ROLE = "t_viewer_userPref";
    private static final String ADMIN_ROLE = "admin";
    private String dashboardTitle, description;
    private User editor, viewer;

    @Factory(dataProvider = "userMode")
    public GadgetUserPrefTest(TestUserMode userMode, String dashboardTitle) throws Exception {
        super(TestUserMode.SUPER_TENANT_ADMIN);
        this.dashboardTitle = dashboardTitle;
        boolean isTenant = userMode.equals(TestUserMode.TENANT_ADMIN);
        // Read the editor and viewer users from the automation.xml file.
        AutomationContext automationContext = new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME,
                this.userMode);
        editor = automationContext.getContextTenant().getTenantUser("editor");
        viewer = automationContext.getContextTenant().getTenantUser("viewer");
        // Manage user roles
        UserManagementClient userManagementClient = new UserManagementClient(getBackEndUrl(), getCurrentUsername(),
                getCurrentPassword());
        // Create editor and viewer user roles (separate user roles are created for super tenant and tenant users)
        userManagementClient.addRole(
                isTenant ? TENANT_EDITOR_ROLE : SUPER_TENANT_EDITOR_ROLE, new String[]{editor.getUserName()}, null);
        userManagementClient.addRole(
                isTenant ? TENANT_VIEWER_ROLE : SUPER_TENANT_VIEWER_ROLE, new String[]{viewer.getUserName()}, null);
        // Remove the admin role from the editors and viewers
        String[] rolesToRemove = new String[]{ADMIN_ROLE};
        userManagementClient.addRemoveRolesOfUser(editor.getUserName(), null, rolesToRemove);
        userManagementClient.addRemoveRolesOfUser(viewer.getUserName(), null, rolesToRemove);
    }

    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN, DASHBOARD_TITLE}};
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        login(getCurrentUsername(), getCurrentPassword());
        initDashboard();
        logout();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        getDriver().quit();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Change the settings of gadgets in the Edit mode by an Editor")
    public void changeGadgetPrefsInEditMode() throws Exception {
        login(editor.getUserName(), editor.getPassword());
        DSWebDriver driver = getDriver();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        String[][] gadgetMappings = {{"textbox", "a"}};
        String script = generateAddGadgetScript(gadgetMappings);
        driver.findElement(By.cssSelector("i.fw.fw-gadget")).click();
        driver.executeScript(script);
        setTextBoxValue("Editor Mode", true);
        driver.findElement(By.cssSelector("a.ues-dashboard-preview")).click();
        // TODO: change the behaviour in the dashboard to reflect the change after saving the change. Then remove sleep
        Thread.sleep(500);
        assertEquals(getTextBoxValue(), "Editor Mode");
    }

    @Test(groups = "wso2.ds.dashboard", description = "Change the settings of gadgets in the View mode by an Editor",
            dependsOnMethods = "changeGadgetPrefsInEditMode")
    public void changeGadgetPrefsByEditor() throws Exception {
        DSWebDriver driver = getDriver();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-view")).click();
        pushWindow();
        //verify that an editor can't personalize a dashboard but edit
        driver.findElement(By.linkText(editor.getUserNameWithoutDomain())).click();
        String toolTip = driver.findElement(By.className("ues-copy")).getAttribute("title");
        assertEquals(toolTip, "Edit");
        showGadgetConfigurationIcons();
        driver.findElement(By.cssSelector("#a i.fw.fw-configarations")).click();
        setTextBoxValue("Editor Value", false);
        driver.findElement(By.cssSelector("#a i.fw.fw-configarations")).click();
        //TODO: element is inside an iframe and driver.findElement cannot be used directly. Hence an alternative is needed
        Thread.sleep(500);
        assertEquals(getTextBoxValue(), "Editor Value");
        popWindow();
        logout();
        //login with admin (another editor) to verify that editor can't personalize a dashboard
        login(getCurrentUsername(), getCurrentPassword());
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-view")).click();
        pushWindow();
        //TODO: element is inside an iframe and driver.findElement cannot be used directly. Hence an alternative is needed
        Thread.sleep(500);
        assertEquals(getTextBoxValue(), "Editor Value");
        driver.close();
        popWindow();
        logout();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Change the settings of gadgets in the View mode by a Viewer",
            dependsOnMethods = "changeGadgetPrefsByEditor")
    public void changeGadgetPrefsByViewer() throws Exception {
        login(viewer.getUserName(), viewer.getPassword());
        DSWebDriver driver = getDriver();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-view")).click();
        pushWindow();
        showGadgetConfigurationIcons();
        driver.findElement(By.cssSelector("#a i.fw.fw-configarations")).click();
        setTextBoxValue("Viewer Value", false);
        driver.findElement(By.cssSelector("#a i.fw.fw-configarations")).click();
        //TODO: element is inside an iframe and driver.findElement cannot be used directly. Hence an alternative is needed
        Thread.sleep(500);
        assertEquals(getTextBoxValue(), "Viewer Value");
    }

    @Test(groups = "wso2.ds.dashboard", description = "Change the settings of gadgets in the Personalize mode by Viewer",
            dependsOnMethods = "changeGadgetPrefsByViewer")
    public void personalizeGadgetPrefsByViewer() throws Exception {
        DSWebDriver driver = getDriver();
        getDriver().findElement(By.cssSelector("a.dropdown")).click();
        driver.findElement(By.cssSelector("i.fw.fw-settings")).click();
        //Verify "Settings" is not available in personalized dashboard edit mode
        assertFalse(driver.isElementPresent(By.id("dashboard-settings")));
        driver.findElement(By.className("ues-component-properties-handle"));
        driver.findElement(By.className("ues-component-properties-handle")).click();
        setTextBoxValue("Personalize Mode", true);
        driver.findElement(By.cssSelector("a.ues-dashboard-preview")).click();
        //TODO: element is inside an iframe and driver.findElement cannot be used directly. Hence an alternative is needed
        Thread.sleep(500);
        assertEquals(getTextBoxValue(), "Personalize Mode");
        driver.close();
        popWindow();
        logout();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Verify the original dashboard is not changed after personalizing " +
            "a dashboard", dependsOnMethods = "personalizeGadgetPrefsByViewer")
    public void testOriginalDashboardAfterPersonalizing() throws Exception {
        login(editor.getUserName(), editor.getPassword());
        DSWebDriver driver = getDriver();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-view")).click();
        pushWindow();
        assertEquals(getTextBoxValue(), "Editor Value");
        driver.close();
        popWindow();
        logout();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Reset the personalized dashboard by a viewer",
            dependsOnMethods = "testOriginalDashboardAfterPersonalizing")
    public void resetDashboardByViewer() throws Exception {
        login(viewer.getUserName(), viewer.getPassword());
        DSWebDriver driver = getDriver();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        driver.findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-view")).click();
        pushWindow();
        getDriver().findElement(By.cssSelector("a.dropdown")).click();
        driver.findElement(By.cssSelector("i.fw.fw-settings")).click();
        driver.findElement(By.className("ues-copy")).click();
        driver.findElement(By.id("ues-modal-confirm-yes")).click();
        //TODO: element is inside an iframe and driver.findElement cannot be used directly. Hence an alternative is needed
        Thread.sleep(500);
        assertEquals(getTextBoxValue(), "Editor Value");
        driver.close();
        popWindow();
        logout();
    }

    /**
     * Create the dashboard, select the layout template and change editor and viewer permissions
     *
     * @throws Exception
     */
    private void initDashboard() throws Exception {
        DSWebDriver driver = getDriver();
        driver.get(getBaseUrl() + "/" + DS_HOME_CONTEXT + "/" + DS_DASHBOARDS_CONTEXT);
        // Create dashboard
        driver.findElement(By.cssSelector("a[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        driver.findElement(By.id("ues-dashboard-create")).click();
        selectLayout("default-grid");
        // Change permissions
        driver.findElement(By.id("dashboard-settings")).click();
        driver.executeScript("scroll(0, 200);");
        driver.findElement(By.id("ues-share-view")).clear();
        driver.findElement(By.id("ues-share-view")).sendKeys("viewer_userPr");
        driver.findElement(By.id("ues-share-view")).sendKeys(Keys.TAB);
        driver.findElement(By.id("ues-share-edit")).clear();
        driver.findElement(By.id("ues-share-edit")).sendKeys("editor_userPr");
        driver.findElement(By.id("ues-share-edit")).sendKeys(Keys.TAB);
        // Remove other permissions
        driver.findElement(By.cssSelector(".ues-shared-view " +
                ".ues-shared-role[data-role=\"Internal/everyone\"] span.remove-button")).click();
        driver.findElement(By.cssSelector(".ues-shared-edit " +
                ".ues-shared-role[data-role=\"Internal/everyone\"] span.remove-button")).click();
        driver.findElement(By.id("ues-dashboard-saveBtn")).click();
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
    }

    /**
     * Set the value for the text area in the textbox gadget
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void setTextBoxValue(String value, Boolean isEditMode) throws MalformedURLException,
            XPathExpressionException {
        DSWebDriver driver = getDriver();
        driver.findElement(By.name("content"));
        driver.findElement(By.name("content")).clear();
        driver.findElement(By.name("content")).sendKeys(value);
        if (isEditMode) {
            String fireEvent = "$('textarea[name=content]').change();";
            driver.executeScript(fireEvent);
        }
    }

    /**
     * Get value of the text area in the textbox gadget
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private String getTextBoxValue() throws MalformedURLException, XPathExpressionException {
        DSWebDriver driver = getDriver();
        Object txt = driver.executeScript(
                "var iframe = $(\"iframe[title='Text Box']\")[0];" +
                        "var innerDoc = iframe.contentDocument || (iframe.contentWindow && iframe.contentWindow.document);" +
                        "return innerDoc.getElementsByClassName('col-md-12')[0].textContent;"
        );
        return txt.toString();
    }

    /**
     * By default gadget configuration icons are hidden. A user needs to hover to make them visible.This script make
     * the configuration icons visible
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void showGadgetConfigurationIcons() throws MalformedURLException, XPathExpressionException {
        DSWebDriver driver = getDriver();
        String showToolbarScript =
                "for(i = 0; i < document.getElementsByClassName('ues-component-toolbar').length; i++) {" +
                        "    document.getElementsByClassName('ues-component-toolbar')[i].style.display = 'inline';" +
                        "}";
        driver.executeScript(showToolbarScript);
    }
}
