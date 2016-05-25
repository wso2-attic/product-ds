package org.wso2.ds.ui.integration.test.dashboard;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by prabushi on 5/25/16.
 */
public class AddCustomThemeToDashboardTest extends DSUIIntegrationTest {
    private static final String USER_NAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String CUSTOM_THEME_NAME = "custom-theme-sample";
    //get the path to custom theme carbon archive file
    String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();
    String carFilePath = systemResourceLocation + "files/Custom_Theme.car";

    //get the path to store location of custom themes
    String carbonHome = FrameworkPathUtil.getCarbonHome();
    String themeStoredLocation = carbonHome + File.separator + "repository" + File.separator + "deployment" +
            File.separator + "server" + File.separator + "jaggeryapps" + File.separator + "portal" +
            File.separator + "store" + File.separator + "carbon.super" + File.separator + "fs" +
            File.separator + "theme" + File.separator + CUSTOM_THEME_NAME;

    @Factory(dataProvider = "userMode")
    public AddCustomThemeToDashboardTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Setup the testing environment.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @BeforeClass(alwaysRun = true) public void setUp()
            throws MalformedURLException, XPathExpressionException, InterruptedException {
        //log in to admin console
        loginToAdminConsole(USER_NAME, PASSWORD);

        //deploy the custom theme file
        getDriver().findElement(By.xpath("(//a[contains(text(),'Add')])[7]")).click();
        getDriver().findElement(By.id("filename")).sendKeys(carFilePath);
        getDriver().findElement(By.name("upload")).click();
        getDriver().findElement(By.cssSelector("button[type=\"button\"]")).click();
        Thread.sleep(15000);
        //logout from admin console
        logoutFromAdminConsole();
    }

    @Test(groups = "wso2.ds.dashboard", description = "Adding a custom theme when creating a dashboard")
    public void addCustomThemeToDashboard() throws XPathExpressionException, MalformedURLException {
        //log in to portal
        login(getCurrentUsername(), getCurrentPassword());

    }

//    driver.findElement(By.name("username")).clear();
//    driver.findElement(By.name("username")).sendKeys("admin");
//    driver.findElement(By.name("password")).clear();
//    driver.findElement(By.name("password")).sendKeys("admin");
//    driver.findElement(By.xpath("//button[@type='submit']")).click();
    driver.findElement(By.xpath("//div[@id='navbar']/ul/li/a/span/i[2]")).click();
    driver.findElement(By.id("ues-dashboard-title")).clear();
    driver.findElement(By.id("ues-dashboard-title")).sendKeys("sample-dashboard");
    driver.findElement(By.id("ues-dashboard-description")).clear();
    driver.findElement(By.id("ues-dashboard-description")).sendKeys("This is a sample dashboard");
    new Select(driver.findElement(By.id("ues-theme-list"))).selectByVisibleText("custom-theme-sample");
    driver.findElement(By.id("ues-dashboard-create")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.xpath("//div[@id='ues-page-layouts']/div[2]/img")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.xpath("//a[@id='btn-sidebar-gadgets']/i")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonlanding")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.xpath("//a[@id='btn-pages-sidebar']/i")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.xpath("//div[@id='ues-page-layouts']/div[2]/img")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonpage0")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonpage0")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("pagesButtonpage0")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.xpath("//a[@id='btn-sidebar-gadgets']/i")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.linkText("View")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    // ERROR: Caught exception [ERROR: Unsupported command [waitForPopUp | _blank | 30000]]
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.id("dashboard-settings")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=opensearch | ]]
    driver.findElement(By.cssSelector("input[type=\"checkbox\"]")).click();
    driver.findElement(By.cssSelector("span.helper")).click();


    driver.get(baseUrl + "/portal/login-controller?destination=%2Fportal%2F");
    driver.findElement(By.name("username")).clear();
    driver.findElement(By.name("username")).sendKeys("admin");
    driver.findElement(By.name("password")).clear();
    driver.findElement(By.name("password")).sendKeys("admin");
    driver.findElement(By.xpath("//button[@type='submit']")).click();
    driver.findElement(By.xpath("//div[@id='navbar']/ul/li/a/span/i[2]")).click();
    driver.findElement(By.id("ues-dashboard-title")).clear();
    driver.findElement(By.id("ues-dashboard-title")).sendKeys("sample-dashboard");
    driver.findElement(By.id("ues-dashboard-description")).clear();
    driver.findElement(By.id("ues-dashboard-description")).sendKeys("This is a sample dashboard");
    new Select(driver.findElement(By.id("ues-theme-list"))).selectByVisibleText("custom-theme-sample");
    driver.findElement(By.id("ues-dashboard-create")).click();
    driver.findElement(By.xpath("//div[@id='ues-page-layouts']/div[2]/img")).click();
    driver.findElement(By.id("pagesButtonlanding")).click();
    driver.findElement(By.id("pagesButtonlanding")).click();
    driver.findElement(By.id("pagesButtonlanding")).click();
    driver.findElement(By.id("pagesButtonlanding")).click();
    driver.findElement(By.xpath("//a[@id='btn-sidebar-gadgets']/i")).click();
    driver.findElement(By.id("pagesButtonlanding")).click();
    driver.findElement(By.id("pagesButtonlanding")).click();
    driver.findElement(By.id("btn-pages-sidebar")).click();
    driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();
    driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();
    driver.findElement(By.xpath("//div[@id='ues-page-layouts']/div[2]/img")).click();
    driver.findElement(By.id("pagesButtonpage0")).click();
    driver.findElement(By.id("pagesButtonpage0")).click();
    driver.findElement(By.id("pagesButtonpage0")).click();
    driver.findElement(By.xpath("//a[@id='btn-sidebar-gadgets']/i")).click();
    driver.findElement(By.linkText("View")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [waitForPopUp | _blank | 30000]]
    driver.findElement(By.linkText("Home")).click();
    driver.findElement(By.linkText("Page 0")).click();
    driver.findElement(By.id("dashboard-settings")).click();
    driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();
    driver.findElement(By.linkText("Default Theme")).click();
    driver.findElement(By.id("ues-dashboard-saveBtn")).click();
    driver.findElement(By.linkText("Dashboards")).click();
    driver.findElement(By.id("ues-view")).click();
    // ERROR: Caught exception [ERROR: Unsupported command [waitForPopUp | _blank | 30000]]
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | null | ]]
    // ERROR: Caught exception [Error: locator strategy either id or name must be specified explicitly.]
    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=toolbox-panel-iframe-inspector | ]]
    // ERROR: Caught exception [Error: locator strategy either id or name must be specified explicitly.]



    /**
     * Clean up after running tests
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @AfterClass(alwaysRun = true) public void tearDown()
            throws MalformedURLException, XPathExpressionException, InterruptedException {
        //log in to admin console
        loginToAdminConsole(USER_NAME, PASSWORD);

        //undeploy custom theme file
        getDriver().findElement(By.xpath("(//a[contains(text(),'List')])[6]")).click();
        getDriver().findElement(By.linkText("Delete")).click();
        getDriver().findElement(By.cssSelector("button[type=\"button\"]")).click();
        Thread.sleep(15000);
        try {
            logoutFromAdminConsole();
        } finally {
            getDriver().quit();
        }
    }
}
