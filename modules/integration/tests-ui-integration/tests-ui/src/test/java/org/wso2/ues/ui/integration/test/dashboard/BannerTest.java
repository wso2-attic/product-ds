/*
 * Copyright 2005-2015 WSO2, Inc. (http://wso2.com)
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

package org.wso2.ues.ui.integration.test.dashboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.ues.integration.common.clients.ResourceAdminServiceClient;
import org.wso2.ues.ui.integration.util.UESUIIntegrationTest;
import org.wso2.ues.ui.integration.util.UESWebDriver;
import ues.integration.tests.common.domain.UESIntegrationTestConstants;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;

/**
 * Created by lasanthas on 10/8/15.
 */
public class BannerTest extends UESUIIntegrationTest {

    private static final Log LOG = LogFactory.getLog(BannerTest.class);

    private static final String DASHBOARD_TITLE = "Banner Dashboard";
    private static final String DASHBOARD_ID = "banner-dashboard";
    private static final String ROOT_RESOURCE_PATH = "/_system/config/ues/customizations/";
    private static final String[] IMAGES = {"orange.png", "silver.png"};

    private static final String EDITOR_ROLE = "editor";
    private static final String VIEWER_ROLE = "viewer";
    private static final String ADMIN_ROLE = "admin";

    private ResourceAdminServiceClient resourceAdminServiceClient;
    private User editor, viewer;

    /**
     * Initialize the class
     */
    public BannerTest() throws Exception {
        super(TestUserMode.SUPER_TENANT_ADMIN);

        AutomationContext automationContext = new AutomationContext(UESIntegrationTestConstants
                .UES_PRODUCT_NAME, this.userMode);

        editor = automationContext.getContextTenant().getTenantUser("editor");
        viewer = automationContext.getContextTenant().getTenantUser("viewer");

        resourceAdminServiceClient = new ResourceAdminServiceClient(getBackEndUrl(),
                getCurrentUsername(), getCurrentPassword());

        UserManagementClient userManagementClient = new UserManagementClient(getBackEndUrl(),
                getCurrentUsername(), getCurrentPassword());

        // Create user roles
        String[] editorsRoles = new String[]{editor.getUserName()};
        String[] viewersRoles = new String[]{viewer.getUserName()};

        userManagementClient.addRole(EDITOR_ROLE, editorsRoles, null);
        userManagementClient.addRole(VIEWER_ROLE, viewersRoles, null);

        // Remove unnecessary roles from users
        String[] rolesToRemove = new String[]{ADMIN_ROLE};

        userManagementClient.addRemoveRolesOfUser(editor.getUserName(), null, rolesToRemove);
        userManagementClient.addRemoveRolesOfUser(viewer.getUserName(), null, rolesToRemove);
    }

    /**
     * Setup the testing environment
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {

        // login as admin and create the dashboard
        login(getDriver(), getBaseUrl(), getCurrentUsername(), getCurrentPassword());

        initDashboard();

        logout(getDriver(), getBaseUrl(), getCurrentUsername());
    }

    /**
     * Cancel adding banner by editor
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ues.dashboard", description = "Cancel adding banner by editor")
    public void cancelAddingBannerByEditor() throws Exception {

        login(getDriver(), getBaseUrl(), editor.getUserName(), editor.getPassword());

        goToDesigner();

        clickEditBannerButton(0);
        clickCancelBannerButton();
        assertEquals(isResourceExist(DASHBOARD_ID + "/banner"), false,
                "Resource should not be uploaded to the registry");
    }

    /**
     * Add banner by editor
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ues.dashboard", description = "Add banner by editor",
            dependsOnMethods = "cancelAddingBannerByEditor")
    public void addBannerByEditor() throws Exception {

        clickEditBannerButton(0);
        clickSaveBannerButton();
        assertEquals(isResourceExist(DASHBOARD_ID + "/banner"), true,
                "Unable to find the resource");

        logout(getDriver(), getBaseUrl(), editor.getUserName());
    }

    /**
     * Cancel adding banner by viewer
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ues.dashboard", description = "Cancel adding banner by viewer",
            dependsOnMethods = "addBannerByEditor")
    public void cancelAddingBannerByViewer() throws Exception {

        login(getDriver(), getBaseUrl(), viewer.getUserName(), viewer.getPassword());

        customizeDashboard();

        clickEditBannerButton(1);
        clickCancelBannerButton();
        assertEquals(isResourceExist(DASHBOARD_ID + "/" + viewer.getUserName() + "/banner"), false,
                "Resource should not be uploaded to the registry");
    }

    /**
     * Add banner by viewer
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ues.dashboard", description = "Add banner by viewer", dependsOnMethods =
            "cancelAddingBannerByViewer")
    public void addBannerByViewer() throws Exception {

        clickEditBannerButton(1);
        clickSaveBannerButton();

        assertEquals(isResourceExist(DASHBOARD_ID + "/" + viewer.getUserName() + "/banner"), true,
                "Unable to find the resource");
    }

    /**
     * Reset banner by viewer
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ues.dashboard", description = "Reset banner by viewer", dependsOnMethods
            = "addBannerByViewer")
    public void resetBannerByViewer() throws Exception {

        clickRemoveBannerButton();
        assertEquals(isResourceExist(DASHBOARD_ID + "/" + viewer.getUserName() + "/banner"), false,
                "Unable to remove the resource");

        logout(getDriver(), getBaseUrl(), viewer.getUserName());
    }

    /**
     * Remove banner by editor
     *
     * @throws Exception
     */
    @Test(groups = "wso2.ues.dashboard", description = "Remove banner by editor",
            dependsOnMethods = "resetBannerByViewer")
    public void removeBannerByEditor() throws Exception {

        login(getDriver(), getBaseUrl(), editor.getUserName(), editor.getPassword());

        goToDesigner();

        clickRemoveBannerButton();
        assertEquals(isResourceExist(DASHBOARD_ID + "/banner"), false,
                "Unable to remove the resource");

        logout(getDriver(), getBaseUrl(), editor.getUserName());
    }

    /**
     * Clean up after running tests
     *
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        getDriver().quit();
    }

    /**
     * Check whether an specific resource under the '/_system/config/ues/customizations/' exists
     *
     * @param resourcePath relative path from the '/_system/config/ues/customizations/' node
     * @return boolean
     */
    private boolean isResourceExist(String resourcePath) {

        boolean resourceExists;

        try {
            resourceAdminServiceClient.getResourceContent(ROOT_RESOURCE_PATH + resourcePath);
            resourceExists = true;
        } catch (Exception ex) {
            resourceExists = false;
        }

        return resourceExists;
    }

    /**
     * initialize the dashboard
     */
    private void initDashboard() throws MalformedURLException, XPathExpressionException {

        UESWebDriver driver = getDriver();

        // create dashboard
        driver.findElement(By.cssSelector("a[href='create-dashboard']")).click();
        driver.findElement(By.id("ues-dashboard-title")).clear();
        driver.findElement(By.id("ues-dashboard-title")).sendKeys(DASHBOARD_TITLE);
        driver.findElement(By.id("ues-dashboard-create")).click();
        driver.findElement(By.cssSelector("button[data-id='layout-banner']")).click();

        // change permissions
        driver.findElement(By.id("settings-link")).click();

        driver.findElement(By.id("ues-share-view")).clear();
        driver.findElement(By.id("ues-share-view")).sendKeys("viewer");
        driver.findElement(By.cssSelector("div.tt-suggestion.tt-selectable")).click();

        driver.findElement(By.id("ues-share-edit")).clear();
        driver.findElement(By.id("ues-share-edit")).sendKeys("editor");
        driver.findElement(By.cssSelector("div.tt-suggestion.tt-selectable")).click();

        // remove permissions from others
        driver.findElement(By.cssSelector(".ues-shared-view " +
                ".ues-shared-role[data-role=\"Internal/everyone\"] span.remove-button")).click();
        driver.findElement(By.cssSelector(".ues-shared-edit " +
                ".ues-shared-role[data-role=\"Internal/everyone\"] span.remove-button")).click();

        driver.findElement(By.id("ues-designer-link")).click();
    }

    /**
     * Go to the designer view from the home page
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void goToDesigner() throws MalformedURLException, XPathExpressionException {

        getDriver().findElement(By.cssSelector(".ues-dashboard[data-id='" + DASHBOARD_ID + "'] a" +
                ".ues-edit")).click();
    }

    /**
     * Customize the dashboard
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void customizeDashboard() throws MalformedURLException, XPathExpressionException {

        UESWebDriver driver = getDriver();

        driver.findElement(By.cssSelector(".ues-dashboard[data-id='" + DASHBOARD_ID + "'] a" +
                ".ues-view")).click();

        // switch the driver to the new window and click on the edit/personalize link
        String theParent = driver.getWindowHandle();
        Iterator windowIterator = driver.getWindowHandles().iterator();

        while (windowIterator.hasNext()) {

            String theChild = windowIterator.next().toString();

            if (!theChild.contains(theParent)) {
                driver.switchTo().window(theChild);
                driver.findElement(By.cssSelector(".ues-copy")).click();
                break;
            }
        }
    }

    /**
     * Clicks the banner edit button. This pops up the file browser and upload the image for
     * cropping
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void clickEditBannerButton(int imageIndex) throws MalformedURLException,
            XPathExpressionException {

        UESWebDriver driver = getDriver();

        // temporary display the html form which is used to upload the image
        driver.executeScript("document.getElementById('ues-dashboard-upload-banner-form')" +
                ".className='';");

        // get the sample_images directory
        ClassLoader classLoader = BannerTest.class.getClassLoader();
        File classPathRoot = new File(classLoader.getResource("").getPath());

        String filePath = Paths.get(classPathRoot.getAbsolutePath(), "sample_images",
                IMAGES[imageIndex]).toString();

        if (LOG.isDebugEnabled()) {
            File file = new File(filePath);
            if (!file.exists()) {
                LOG.debug("Unable to find the sample image file at " + filePath);
            }
        }

        driver.findElement(By.id("file-banner")).sendKeys(Keys.DELETE);
        driver.findElement(By.id("file-banner")).sendKeys(filePath);
    }

    /**
     * Clicks the banner save button
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void clickSaveBannerButton() throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.id("btn-save-banner")).click();
    }

    /**
     * Clicks the cancel button when uploading a banner
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void clickCancelBannerButton() throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.id("btn-cancel-banner")).click();
    }

    /**
     * Clicks the remove/reset button for remove a banner
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void clickRemoveBannerButton() throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.id("btn-remove-banner")).click();
    }
}