package org.wso2.ds.ui.integration.test.dashboard;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import static org.testng.Assert.assertEquals;

public class DashboardAccessLevelTest extends DSUIIntegrationTest {

    private static final String USER_NAME_WITH_LOGINPERMISSION = "sampleuserWithLogin";
    private static final String USER_NAME_WITHOUT_LOGINPERMISSION = "sampleuserWithOutLogin";
    private static final String PASSWORD = "sampleuserDALTest";
    private static final String RETYPE_PASSWORD = "sampleuserDALTest";
    private static final String DASHBOARD_TITLE = "sampledashboardDALTest";

    public DashboardAccessLevelTest() {
        super();
    }

    @Test(priority = 0, groups = "wso2.ds.common", description = "trying to login without login permission"
            + "created user to portal")
    public void loginWithoutLoginPermission() throws Exception {
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
        getDriver().findElement(
                By.cssSelector("a[href=\"../userstore/add-user-role" + ".jsp?region=region1&item=user_mgt_menu_add\"]"))
                .click();
        getDriver().findElement(By.cssSelector("a[href=\"../user/add-step1.jsp\"]")).click();
        getDriver().findElement(By.name("username")).clear();
        getDriver().findElement(By.name("username")).sendKeys(USER_NAME_WITHOUT_LOGINPERMISSION);
        getDriver().findElement(By.name("password")).clear();
        getDriver().findElement(By.name("password")).sendKeys(PASSWORD);
        getDriver().findElement(By.name("retype")).clear();
        getDriver().findElement(By.name("retype")).sendKeys(RETYPE_PASSWORD);
        getDriver().findElement(By.cssSelector("input.button")).click();
        getDriver().findElement(By.cssSelector("td.buttonRow > input.button")).click();
        getDriver().findElement(By.cssSelector("button[type=\"button\"]")).click();
        getDriver().findElement(By.linkText("Sign-out")).click();
        login(USER_NAME_WITHOUT_LOGINPERMISSION, PASSWORD);
        Assert.assertTrue(getDriver().findElement(By.className("alert-danger")).isDisplayed());
    }

    @Test(priority = 1, groups = "wso2.ds.common", description = "trying to login with login permission"
            + "created user to portal")
    public void loginWithLoginPermission() throws Exception {
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
        getDriver().findElement(
                By.cssSelector("a[href=\"../userstore/add-user-role" + ".jsp?region=region1&item=user_mgt_menu_add\"]"))
                .click();
        getDriver().findElement(By.cssSelector("a[href=\"../user/add-step1.jsp\"]")).click();
        getDriver().findElement(By.name("username")).clear();
        getDriver().findElement(By.name("username")).sendKeys(USER_NAME_WITH_LOGINPERMISSION);
        getDriver().findElement(By.name("password")).clear();
        getDriver().findElement(By.name("password")).sendKeys(PASSWORD);
        getDriver().findElement(By.name("retype")).clear();
        getDriver().findElement(By.name("retype")).sendKeys(RETYPE_PASSWORD);
        getDriver().findElement(By.cssSelector("input.button")).click();
        getDriver().findElement(By.cssSelector("td.buttonRow > input.button")).click();
        getDriver().findElement(By.cssSelector("button[type=\"button\"]")).click();
        getDriver().findElement(By.linkText("Sign-out")).click();
        addLoginRole(USER_NAME_WITH_LOGINPERMISSION);
        login(USER_NAME_WITH_LOGINPERMISSION, PASSWORD);
        assertEquals(USER_NAME_WITH_LOGINPERMISSION, getDriver().findElement(By.cssSelector(".dropdown")).getText(),
                "Login Failed using " + USER_NAME_WITH_LOGINPERMISSION);
        Assert.assertTrue(getDriver().findElements(By.cssSelector("a[href='../create-dashboard']")).isEmpty(),
                "Create Dashboard is available");
    }

    @Test(priority = 2, groups = "wso2.ds.common", description = "trying to login with login permission"
            + "created user to portal")
    public void checkCreateDashboard() throws Exception {
        logout();
        addCreateRole(USER_NAME_WITH_LOGINPERMISSION);
        login(USER_NAME_WITH_LOGINPERMISSION, PASSWORD);
        Assert.assertFalse((getDriver().findElements(By.cssSelector("a[href='../create-dashboard']"))).size() == 0,
                "Create Dashboard is not available");
    }

    @Test(priority = 3, groups = "wso2.ds.common", description = "trying to login with login permission"
            + "created user to portal")
    public void checkSettingsDashboard() throws Exception {
        logout();
        initDashboard();
        Assert.assertTrue(
                (getDriver().findElements(By.cssSelector("a[href='../dashboard-settings/" + DASHBOARD_TITLE + "']")))
                        .size() == 0, "Settings Dashboard is not available");
    }

    private void initDashboard() throws Exception {
        login(USER_NAME_WITH_LOGINPERMISSION, PASSWORD);
        getDriver().findElement(By.cssSelector("a[href='../create-dashboard']")).click();
        getDriver().findElement(By.id("ues-dashboard-title")).clear();
        getDriver().findElement(By.id("ues-dashboard-title")).sendKeys(DASHBOARD_TITLE);
        getDriver().findElement(By.id("ues-dashboard-create")).click();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        getDriver().quit();
    }
}
