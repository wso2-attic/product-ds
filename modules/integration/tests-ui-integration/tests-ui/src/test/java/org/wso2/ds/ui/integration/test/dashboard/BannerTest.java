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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.user.mgt.stub.UserAdminUserAdminException;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.rmi.RemoteException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests the dashboard banner uploading functionality for super tenant and tenant user scenarios.
 */
public class BannerTest extends DSUIIntegrationTest {
    private static final Log LOG = LogFactory.getLog(BannerTest.class);
    private static final String ROOT_RESOURCE_PATH = "/_system/config/ues/customizations/";
    private static final String[] IMAGES = {"orange.png", "silver.png"};
    private static final String SUPER_TENANT_EDITOR_ROLE = "editor";
    private static final String SUPER_TENANT_VIEWER_ROLE = "viewer";
    private static final String TENANT_EDITOR_ROLE = "t_editor";
    private static final String TENANT_VIEWER_ROLE = "t_viewer";
    private static final String ADMIN_ROLE = "admin";
    private String dashboardId, dashboardTitle;
    private User editor, viewer;

    /**
     * Initializes the class.
     *
     * @param userMode       user mode
     * @param dashboardId    id of the dashboard
     * @param dashboardTitle title of the dashboard
     * @throws XPathExpressionException
     * @throws RemoteException
     * @throws UserAdminUserAdminException
     */
    @Factory(dataProvider = "userMode")
    public BannerTest(TestUserMode userMode, String dashboardId, String dashboardTitle)
            throws XPathExpressionException, RemoteException, UserAdminUserAdminException {
        super(TestUserMode.SUPER_TENANT_ADMIN);
        this.dashboardId = dashboardId;
        this.dashboardTitle = dashboardTitle;
        boolean isTenant = userMode.equals(TestUserMode.TENANT_ADMIN);
        // Read the editor and viewer users from the automation.xml file.
        AutomationContext automationContext =
                new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME, this.userMode);
        editor = automationContext.getContextTenant().getTenantUser("editor");
        viewer = automationContext.getContextTenant().getTenantUser("viewer");
        // Manage user roles
        UserManagementClient userManagementClient = new UserManagementClient(
                getBackEndUrl(), getCurrentUsername(), getCurrentPassword());
        // Create editor and viewer user roles (separate user roles are created for super tenant and tenant users)
        userManagementClient.addRole(isTenant ? TENANT_EDITOR_ROLE : SUPER_TENANT_EDITOR_ROLE,
                new String[]{editor.getUserName()}, null);
        userManagementClient.addRole(isTenant ? TENANT_VIEWER_ROLE : SUPER_TENANT_VIEWER_ROLE,
                new String[]{viewer.getUserName()}, null);
        // Remove the admin role from the editors and viewers
        String[] rolesToRemove = new String[]{ADMIN_ROLE};
        userManagementClient.addRemoveRolesOfUser(editor.getUserName(), null, rolesToRemove);
        userManagementClient.addRemoveRolesOfUser(viewer.getUserName(), null, rolesToRemove);
    }

    /**
     * Provides user modes.
     *
     * @return
     */
    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{
                {TestUserMode.SUPER_TENANT_ADMIN, "banner-dashboard", "Banner Dashboard"},
                {TestUserMode.TENANT_ADMIN, "tenanted-banner-dashboard", "Tenanted Banner Dashboard"},
        };
    }

    /**
     * Setups the testing environment.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws XPathExpressionException, MalformedURLException {
        // Login to the portal and create the dashboard as the admin user
        login(getCurrentUsername(), getCurrentPassword());
        initDashboard();
        logout();
    }

    /**
     * Tests cancel adding banner by editor.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Cancel adding banner by editor")
    public void cancelAddingBannerByEditor() throws XPathExpressionException, MalformedURLException {
        login(editor.getUserName(), editor.getPassword());
        goToDesigner();
        clickEditBannerButton(0);
        clickCancelBannerButton();
        assertFalse(isResourceExist(ROOT_RESOURCE_PATH + dashboardId + "/banner"),
                "Resource should not be uploaded to the registry");
    }

    /**
     * Tests add banner by editor.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Add banner by editor",
            dependsOnMethods = "cancelAddingBannerByEditor")
    public void addBannerByEditor() throws MalformedURLException, XPathExpressionException, InterruptedException {
        clickEditBannerButton(0);
        clickSaveBannerButton();
        Thread.sleep(500);
        assertTrue(isResourceExist(ROOT_RESOURCE_PATH + dashboardId + "/banner"), "Unable to find the resource");
        //Verify an editor can view the uploaded banner
        assertTrue(isBannerPresent(),"Banner is not visible to the editor");
        //Verify an uploaded banner is loaded into the anonymous view
        createAnonView();
        navigateToAnonView();
        assertTrue(isBannerPresentInDesignerMode(),"Banner is not loaded into the anonymous view");
        //Verify the same banner is uploaded to a new page added with banner layout
        addPageWithBannerLayout();
        assertTrue(isBannerPresentInDesignerMode(),"Banner is not loaded into new pages");
        logout();
    }

    /**
     * Tests cancel adding banner by viewer.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Cancel adding banner by viewer",
            dependsOnMethods = "addBannerByEditor")
    public void cancelAddingBannerByViewer() throws XPathExpressionException, MalformedURLException {
        login(viewer.getUserName(), viewer.getPassword());
        customizeDashboard();
        clickEditBannerButton(1);
        clickCancelBannerButton();
        assertFalse(isResourceExist(ROOT_RESOURCE_PATH + dashboardId + "/" + viewer.getUserName() + "/banner"),
                "Resource should not be uploaded to the registry");
    }

    /**
     * Tests add banner by viewer.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Add banner by viewer",
            dependsOnMethods = "cancelAddingBannerByViewer")
    public void addBannerByViewer() throws MalformedURLException, XPathExpressionException {
        clickEditBannerButton(1);
        clickSaveBannerButton();
        assertTrue(isResourceExist(ROOT_RESOURCE_PATH + dashboardId + "/" + viewer.getUserName() + "/banner"),
                "Unable to find the resource");
    }

    /**
     * Tests reset banner by viewer.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Reset banner by viewer", dependsOnMethods = "addBannerByViewer")
    public void resetBannerByViewer() throws MalformedURLException, XPathExpressionException {
        clickRemoveBannerButton();
        assertFalse(isResourceExist(dashboardId + "/" + viewer.getUserName() + "/banner"),
                "Unable to remove the resource");
        logout();
    }

    /**
     * Tests remove banner by editor.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Remove banner by editor",
            dependsOnMethods = "resetBannerByViewer")
    public void removeBannerByEditor() throws XPathExpressionException, MalformedURLException {
        login(editor.getUserName(), editor.getPassword());
        goToDesigner();
        clickRemoveBannerButton();
        assertFalse(isResourceExist(ROOT_RESOURCE_PATH + dashboardId + "/banner"), "Unable to remove the resource");
        assertFalse(isBannerPresentInDesignerMode(),"Banner is not removed from the default mode");
        navigateToAnonView();
        assertFalse(isBannerPresentInDesignerMode(),"Banner is not removed from the anonymous mode");
        navigateToPage("page0");
        assertFalse(isBannerPresentInDesignerMode(),"Banner is not removed from page 0");
        logout();
    }

    /**
     * Clean up after running tests.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws MalformedURLException, XPathExpressionException {
        getDriver().quit();
    }

    /**
     * Creates a dashboard and change editor/viewer permissions.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void initDashboard() throws MalformedURLException, XPathExpressionException {
        getDriver().get(getBaseUrl() + "/portal/dashboards");
        // Create dashboard
        getDriver().findElement(By.cssSelector("a[href='create-dashboard']")).click();
        getDriver().findElement(By.id("ues-dashboard-title")).clear();
        getDriver().findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        getDriver().findElement(By.id("ues-dashboard-create")).click();
        selectLayout("banner");
        // Change permissions
        getDriver().findElement(By.id("dashboard-settings")).click();
        getDriver().executeScript("scroll(0, 200);");
        getDriver().findElement(By.id("ues-share-view")).clear();
        getDriver().findElement(By.id("ues-share-view")).sendKeys("view");
        getDriver().findElement(By.id("ues-share-view")).sendKeys(Keys.TAB);
        getDriver().findElement(By.id("ues-share-edit")).clear();
        getDriver().findElement(By.id("ues-share-edit")).sendKeys("edit");
        getDriver().findElement(By.id("ues-share-edit")).sendKeys(Keys.TAB);
        // Remove other permissions
        getDriver().findElement(By.cssSelector(
                ".ues-shared-view .ues-shared-role[data-role=\"Internal/everyone\"] span.remove-button")).click();
        getDriver().findElement(By.cssSelector(
                ".ues-shared-edit .ues-shared-role[data-role=\"Internal/everyone\"] span.remove-button")).click();
        getDriver().findElement(By.id("ues-dashboard-saveBtn")).click();
        redirectToLocation("portal", "dashboards");
    }

    /**
     * Go to the designer view from the home page.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void goToDesigner() throws MalformedURLException, XPathExpressionException {
        redirectToLocation("portal", "dashboards");
        getDriver().findElement(By.cssSelector(".ues-dashboard[data-id='" + dashboardId + "'] a.ues-edit")).click();
    }

    /**
     * Customize the dashboard.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void customizeDashboard() throws MalformedURLException, XPathExpressionException {
        getDriver().get(getBaseUrl() + "/portal/dashboards");
        getDriver().findElement(By.cssSelector(".ues-dashboard[data-id='" + dashboardId + "'] a" + ".ues-view"))
                .click();
        // Switch the driver to the new window and click on the edit/personalize link
        pushWindow();
        getDriver().findElement(By.cssSelector(".ues-copy")).click();
    }

    /**
     * Clicks the banner edit button. This pops up the file browser and upload the image for cropping.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void clickEditBannerButton(int imageIndex) throws MalformedURLException, XPathExpressionException {
        // Temporary display the HTML form which is used to upload the image
        getDriver().executeScript("document.getElementById('ues-dashboard-upload-banner-form').className='';");
        // Get the sample_images directory
        ClassLoader classLoader = BannerTest.class.getClassLoader();
        File classPathRoot = new File(classLoader.getResource("").getPath());
        String filePath = Paths.get(classPathRoot.getAbsolutePath(), "sample_images", IMAGES[imageIndex]).toString();
        if (LOG.isDebugEnabled()) {
            File file = new File(filePath);
            if (!file.exists()) {
                LOG.debug("Unable to find the sample image file at " + filePath);
            }
        }
        getDriver().findElement(By.id("file-banner")).sendKeys(Keys.DELETE);
        getDriver().findElement(By.id("file-banner")).sendKeys(filePath);
    }

    /**
     * Clicks the banner save button.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void clickSaveBannerButton() throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.id("btn-save-banner")).click();
    }

    /**
     * Clicks the cancel button when uploading a banner.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void clickCancelBannerButton() throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.id("btn-cancel-banner")).click();
    }

    /**
     * Clicks the remove/reset button for remove a banner.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void clickRemoveBannerButton() throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.id("btn-remove-banner")).click();
    }

    /**
     * Checks whether the banner is available in view mode.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private Boolean isBannerPresent () throws MalformedURLException, XPathExpressionException, InterruptedException {
        getDriver().findElement(By.cssSelector("a.ues-dashboard-preview")).click();
        pushWindow();
        WebElement bannerElem = getDriver().findElement(By.cssSelector("[data-banner=true]"));
        String imageUrl = bannerElem.getCssValue("background-image");
        getDriver().close();
        if(imageUrl != null && !imageUrl.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether the banner is available in designer mode.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private Boolean isBannerPresentInDesignerMode() throws MalformedURLException, XPathExpressionException{
        Boolean isBannerPresent = false;
        if (getDriver().isElementPresent(By.className("banner-image"))) {
            WebElement bannerElem = getDriver().findElement(By.className("banner-image"));
            String imageUrl = bannerElem.getCssValue("background-image");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                isBannerPresent = true;
            }
        } else {
            isBannerPresent = false;
        }
        return isBannerPresent;
    }

    /**
     * Create anonymous page from the designer.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void createAnonView() throws MalformedURLException, XPathExpressionException{
        popWindow();
        getDriver().findElement(By.cssSelector("a#btn-pages-sidebar")).click();
        getDriver().findElement(By.cssSelector("input[name='anon']")).click();
    }

    /**
     * Navigate to anonymous page from the designer.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void navigateToAnonView() throws MalformedURLException, XPathExpressionException{
        String fireEvent = "$('a[aria-controls=anonymousDashboardView]').click();";
        getDriver().executeScript(fireEvent);
    }

    /**
     * Add a page with banner layout.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void addPageWithBannerLayout() throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.cssSelector("button[rel='createPage']")).click();
        selectLayout("banner");
    }

    /**
     * Navigate to a particular page.
     *
     * @param page name of the page to be navigated
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void navigateToPage(String page) throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.cssSelector("a#btn-pages-sidebar")).click();
        switchPage(page);
    }



}