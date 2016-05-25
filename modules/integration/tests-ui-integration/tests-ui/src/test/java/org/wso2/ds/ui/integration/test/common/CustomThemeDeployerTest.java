package org.wso2.ds.ui.integration.test.common;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.MalformedURLException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test cases related to deploying a custom theme files using a carbon archive file
 */
public class CustomThemeDeployerTest extends DSUIIntegrationTest {
    private static final String USER_NAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String CUSTOM_THEME_NAME = "custom-theme-sample";

    /**
     * Initializes class
     */
    public CustomThemeDeployerTest() {
        super();
    }

    /**
     * This test case checks for the availability of custom theme files before deploying, after deploying and after
     * un deploying the .car file
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.common", description = "Deploying and un-deploying a custom theme in the admin console")
    public void addDeleteCustomTheme() throws MalformedURLException, XPathExpressionException, InterruptedException {

        //log in to admin console
        loginToAdminConsole(USER_NAME, PASSWORD);

        //get the path to custom theme carbon archive file
        String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();
        String carFilePath = systemResourceLocation + "files/Custom_Theme.car";

        //get the path to store location of custom themes
        String carbonHome = FrameworkPathUtil.getCarbonHome();
        String themeStoredLocation = carbonHome + File.separator + "repository" + File.separator + "deployment" +
                File.separator + "server" + File.separator + "jaggeryapps" + File.separator + "portal" +
                File.separator + "store" + File.separator + "carbon.super" + File.separator + "fs" +
                File.separator + "theme" + File.separator + CUSTOM_THEME_NAME;

        //check for custom theme availability before deploying the .car file
        File folder = new File(themeStoredLocation);
        assertFalse(folder.exists(),
                "Before deploying the theme, the " + CUSTOM_THEME_NAME + " folder should not " + "exist in "
                        + themeStoredLocation);

        //check for custom theme availability after deploying the .car file
        getDriver().findElement(By.xpath("(//a[contains(text(),'Add')])[7]")).click();
        getDriver().findElement(By.id("filename")).sendKeys(carFilePath);
        getDriver().findElement(By.name("upload")).click();
        getDriver().findElement(By.cssSelector("button[type=\"button\"]")).click();
        Thread.sleep(15000);
        folder = new File(themeStoredLocation);
        assertTrue(folder.exists(), "After deploying the theme, the " + CUSTOM_THEME_NAME + " folder should exist in "
                + themeStoredLocation);

        //check for custom theme availability after un deploying the .car file
        getDriver().findElement(By.xpath("(//a[contains(text(),'List')])[6]")).click();
        getDriver().findElement(By.linkText("Delete")).click();
        getDriver().findElement(By.cssSelector("button[type=\"button\"]")).click();
        Thread.sleep(15000);
        folder = new File(themeStoredLocation);
        assertFalse(folder.exists(),
                "After un-deploying the theme, the " + CUSTOM_THEME_NAME + " folder should not " + "exist in "
                        + themeStoredLocation);
    }

    /**
     * Clean up after running tests
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @AfterClass(alwaysRun = true) public void tearDown() throws MalformedURLException, XPathExpressionException {
        try {
            logoutFromAdminConsole();
        } finally {
            getDriver().quit();
        }
    }
}
