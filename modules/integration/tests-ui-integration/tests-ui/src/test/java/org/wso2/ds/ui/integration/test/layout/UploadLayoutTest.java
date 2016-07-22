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

package org.wso2.ds.ui.integration.test.layout;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.testng.Assert.assertTrue;

/**
 * This is used to test the upload layout functionality using portal
 */
public class UploadLayoutTest extends DSUIIntegrationTest {
    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public UploadLayoutTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return user modes
     */
    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN}};
    }

    /**
     * Setup the testing environment.
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws AutomationUtilException
     */
    @BeforeClass(alwaysRun = true)
    public void setUp()
            throws AutomationUtilException, XPathExpressionException, IOException {
        login(getCurrentUsername(), getCurrentPassword());
    }

    /**
     * Clean up after running tests.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws XPathExpressionException, MalformedURLException {
        logout();
        getDriver().quit();
    }

    /**
     * Upload wrong files using the file upload functionality and check whether the required messages are shown to the user
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.layout", description = "Verifying the validation check when uploading the layout zip file from portal")
    public void testValidation() throws MalformedURLException, XPathExpressionException {
        String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();
        String wrongFilePath = systemResourceLocation + "files" + File.separator + "testLoginAPI.html";
        String bodyText = null;
        String errorMessage = "Please select a zip file to upload.";

        getDriver().findElement(By.xpath("//nav/div/div/a/span/i")).click();
        getDriver().findElement(By.cssSelector("i.fw.fw-layout")).click();
        getDriver().findElement(By.cssSelector("a[href*='upload-layout']")).click();

        // Without selecting the layout zip file, just click the upload button and verify the error message
        getDriver().findElement(By.xpath("(//button[@type='button'])[4]")).click();
        bodyText = getDriver().findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains(errorMessage), "Zip file not found error message is not correctly displayed");

        // Select a html file and validate the error message
        WebElement inputElement = getDriver().findElement(By.id("selected-file"));
        ((JavascriptExecutor) getDriver())
                .executeScript("document.getElementById('selected-file').style.display='block';");
        inputElement.sendKeys(wrongFilePath);
        getDriver().findElement(By.xpath("(//button[@type='button'])[4]")).click();
        bodyText = getDriver().findElement(By.tagName("body")).getText();
        errorMessage = "file format is not supported";
        assertTrue(bodyText.contains(errorMessage), "File format not supported is not correctly displayed");

        // Select a dummy zip file without layout.json and verify the error message
        inputElement = getDriver().findElement(By.id("selected-file"));
        wrongFilePath = systemResourceLocation + "files" + File.separator + "dummy.zip";
        inputElement.sendKeys(wrongFilePath);
        getDriver().findElement(By.xpath("(//button[@type='button'])[4]")).click();
        bodyText = getDriver().findElement(By.tagName("body")).getText();
        errorMessage = "Configuration file is not found in the zip. Please make sure your zip file contains layout.json";
        assertTrue(bodyText.contains(errorMessage), "Configuration file missing in zip is not correctly displayed");

        // Select a layout which already exists and verify the error message
        inputElement = getDriver().findElement(By.id("selected-file"));
        ((JavascriptExecutor) getDriver())
                .executeScript("document.getElementById('selected-file').style.display='block';");
        wrongFilePath = systemResourceLocation + "layouts" + File.separator + "banner.zip";
        inputElement.sendKeys(wrongFilePath);
        getDriver().findElement(By.xpath("(//button[@type='button'])[4]")).click();
        bodyText = getDriver().findElement(By.tagName("body")).getText();
        errorMessage = "A layout with same id already exists.";
        assertTrue(bodyText.contains(errorMessage), "Error message for having the same layout in store is not correctly "
                + "displayed");
        ((JavascriptExecutor) getDriver())
                .executeScript("document.getElementById('selected-file').style.display='none';");
    }

    /**
     * Upload the correct layout zip fle and verify whether it is uploaded correctly
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.layout", description = "Verifying the validation check when uploading the layout zip file from portal",
            dependsOnMethods = "testValidation")
    public void testLayoutUpload() throws MalformedURLException, XPathExpressionException, InterruptedException {
        String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();
        String layoutFilePath = systemResourceLocation + "layouts" + File.separator + "banner-copy.zip";
        String bodyText;
        String successMessage = "You have successfully uploaded the layout.";

        // Select the correct layout zip file and upload
        WebElement inputElement = getDriver().findElement(By.id("selected-file"));
        ((JavascriptExecutor) getDriver())
                .executeScript("document.getElementById('selected-file').style.display='block';");
        inputElement.sendKeys(layoutFilePath);
        getDriver().findElement(By.xpath("(//button[@type='button'])[4]")).click();
        bodyText = getDriver().findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains(successMessage), "Layout upload failed");
        Thread.sleep(30000);
        getDriver().get(getBaseUrl() + "/portal/layout");
        assertTrue(getDriver().findElement(By.id("banner-copy")).isDisplayed(),
                "Uploaded layout is not displayed in the listing page");
    }
}
