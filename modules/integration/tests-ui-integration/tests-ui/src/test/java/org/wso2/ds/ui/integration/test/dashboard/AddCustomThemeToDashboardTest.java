package org.wso2.ds.ui.integration.test.dashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.net.MalformedURLException;

import static org.testng.Assert.assertEquals;

/**
 * Created by prabushi on 5/25/16.
 */
public class AddCustomThemeToDashboardTest extends DSUIIntegrationTest {
    private static final Log log = LogFactory.getLog(AddCustomThemeToDashboardTest.class);
    private static final String USER_NAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String dashboardTitle = "sample-dashboard";
    private static final String description = "This is a sample dashboard";
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

    @Factory(dataProvider = "userMode") public AddCustomThemeToDashboardTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return user modes
     */
    @DataProvider(name = "userMode") public static Object[][] userModeProvider() {
        return new Object[][] { { TestUserMode.SUPER_TENANT_ADMIN } };
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

    @Test(groups = "wso2.ds.dashboard", description = "Adding a custom theme when creating a dashboard") public void addCustomThemeToDashboard()
            throws XPathExpressionException, MalformedURLException, InterruptedException {
        //log in to portal
        login(getCurrentUsername(), getCurrentPassword());

        //create dashboard
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.cssSelector("[href='create-dashboard']")).click();
        getDriver().findElement(By.id("ues-dashboard-title")).clear();
        getDriver().findElement(By.id("ues-dashboard-title")).sendKeys(dashboardTitle);
        getDriver().findElement(By.id("ues-dashboard-description")).clear();
        getDriver().findElement(By.id("ues-dashboard-description")).sendKeys(description);
        new Select(getDriver().findElement(By.id("ues-theme-list"))).selectByVisibleText("custom-theme-sample");
        getDriver().findElement(By.id("ues-dashboard-create")).click();
        selectLayout("default-grid");

        //add a page to the dashboard
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        WebElement webElement = getDriver().findElement(By.id(dashboardTitle.toLowerCase()));
        webElement.findElement(By.cssSelector(".ues-edit")).click();
        addPageToDashboard();

        redirectToLocation(DS_HOME_CONTEXT, "dashboards/" + dashboardTitle + "/landing");
        String x = getDriver().findElement(By.cssSelector(".product-name")).getText();
        log.info("$$$$" + x);
        assertEquals(getDriver().findElement(By.cssSelector(".product-name")).getText(),
                "This is a custom theme title");

        redirectToLocation(DS_HOME_CONTEXT, "dashboard-settings/" + dashboardTitle);
        //edirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        //bElement = getDriver().findElement(By.id(dashboardTitle.toLowerCase()));
        //getDriver().findElement(By.id("dashboard-settings")).click();
        //getDriver().findElement(By.xpath("(//button[@type='button'])[2]")).click();
        //change custom theme to default theme through dashboard settings
        //getDriver().findElement(By.linkText("Default Theme")).click();
        //getDriver().findElement(By.id("ues-dashboard-theme")).sendKeys("Default Theme");
        getDriver().findElement(By.xpath("(//button[@type='button'])[2]")).click();
        getDriver().findElement(By.linkText("Default Theme")).click();
        getDriver().findElement(By.id("ues-dashboard-saveBtn")).click();
        Thread.sleep(10000);
        //redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        redirectToLocation(DS_HOME_CONTEXT, "dashboards/" + dashboardTitle + "/landing");
        //        getDriver().findElement(By.linkText("Dashboards")).click();
        //        getDriver().findElement(By.id("ues-view")).click();
        String y = getDriver().findElement(By.cssSelector(".product-name")).getText();
        log.info("$$$$" + y);
        assertEquals(getDriver().findElement(By.cssSelector(".product-name")).getText(), dashboardTitle);

    }

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
            logout();
            //logoutFromAdminConsole();
        } finally {
            getDriver().quit();
        }
    }
}
