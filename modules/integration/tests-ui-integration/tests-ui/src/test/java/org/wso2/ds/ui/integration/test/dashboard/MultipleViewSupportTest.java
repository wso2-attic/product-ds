package org.wso2.ds.ui.integration.test.dashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This is the class to test the multiple view support for page based on roles
 */
public class MultipleViewSupportTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "multipleviewdashboard";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public MultipleViewSupportTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return user modes
     */
    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][] { { TestUserMode.SUPER_TENANT_ADMIN } };
    }

    /**
     * Setup the testing environment.
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws AutomationUtilException
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws AutomationUtilException, XPathExpressionException, IOException {
        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, "This is a test dashboard");
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
     * Checks for single view
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking a dashboard with single view")
    public void testSingleView() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        String[][] gadgetMappings = { { "publisher", "b" }, { "usa-map", "c" } };
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().executeScript(script);
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("usa-map-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
    }

    /**
     * Checks for multiple view by copying the views
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking a dashboard with multiple view by copying views", dependsOnMethods = "testSingleView")
    public void testCopyView() throws MalformedURLException, XPathExpressionException, InterruptedException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        copyView(1);
        clickOnView("view0");
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("usa-map-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        copyView(2);
        clickOnView("view0");
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("usa-map-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
    }

    /**
     * Checks for multiple views by creating
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking a dashboard with multiple view by creating new views", dependsOnMethods = "testCopyView")
    public void testMultipleNewViews() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        createNewView("default-grid");
        clickOnView("view2");
        String[][] gadgetMappings = { { "textbox", "a" }, { "subscriber", "d" } };
        String[][] gadgetMappingForNewView = { { "publisher", "a" }, { "subscriber", "b" } };
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().executeScript(script);
        assertTrue(getDriver().findElement(By.id("textbox-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("subscriber-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        createNewView("single-column");
        clickOnView("view3");
        script = generateAddGadgetScript(gadgetMappingForNewView);
        getDriver().executeScript(script);
        assertTrue(getDriver().findElement(By.id("publisher-0")).isDisplayed(),
                "Publisher gadget is not displayed in the page");
        assertTrue(getDriver().findElement(By.id("subscriber-0")).isDisplayed(),
                "USA map gadget is not displayed in the page");
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
    }

    /**
     * To test the functionality of getting new layout for a page
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the functionality of replacing layout of a particular view", dependsOnMethods = "testMultipleNewViews")
    public void testReplaceLayout() throws MalformedURLException, XPathExpressionException {
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        clickOnView("default");
        getDriver().findElement(By.id("btn-sidebar-dashboard-layout")).click();
        selectViewLayout("single-column");
        getDriver().findElement(By.id("ues-modal-confirm-yes")).click();
        assertFalse(getDriver().isElementPresent(By.id("publisher-0")), "Gadgets that are on existing layout is not replaced");
        assertFalse(getDriver().isElementPresent(By.id("usa-map-0")), "Gadgets that are on existing layout is not replaced");
        clickOnView("view0");
        assertTrue(getDriver().isElementPresent(By.id("publisher-0")), "When replacing the layout of a view other views are affected");
        assertTrue(getDriver().isElementPresent(By.id("usa-map-0")), "When replacing the layout of a view other views are affected");
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
    }

    /**
     * To test the functionality of deleting a particular view
     */
    @Test(groups = "wso2.ds.dashboard", description = "Checking the functionality of replacing layout of a particular view", dependsOnMethods = "testReplaceLayout")
    public void testDeleteView() throws MalformedURLException, XPathExpressionException{
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();

    }

    /**
     * To create a new view by copying existing view
     *
     * @param index Index of the view to be copied
     */
    private void copyView(int index) throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.id("add-view")).click();
        getDriver().findElement(By.id("copy-view")).click();
        Select dropdown = new Select(getDriver().findElement(By.id("page-views-menu")));
        dropdown.selectByIndex(index);
    }

    /**
     * To click on the particular view
     * @param viewId View id of the view to be clicked
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void clickOnView(String viewId) throws MalformedURLException, XPathExpressionException {
        getDriver().findElement(By.id(viewId)).click();
    }

    /**
     * To create a new view with new layout
     * @param layout Layout to be added to new view
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    private void createNewView (String layout) throws MalformedURLException, XPathExpressionException{
        getDriver().findElement(By.id("add-view")).click();
        getDriver().findElement(By.id("new-view")).click();
        selectViewLayout(layout);
    }

}
