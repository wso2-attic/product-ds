package org.wso2.ds.ui.integration.test.dashboard;

import java.util.List;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

public class HierarchicalPagesTest extends DSUIIntegrationTest {
	private static final String USERNAME_EDITOR = "pageeditor";
	private static final String PASSWORD_EDITOR = "editor123";
	private static final String EDITOR_ROLE = "dashboardEditorRole";

	/**
	 * Initialize the class
	 */
	@Factory(dataProvider = "userMode")
	public HierarchicalPagesTest(TestUserMode userMode) throws Exception {
		super(userMode);
	}

	/**
	 * Provides user modes
	 *
	 * @return
	 */
	@DataProvider(name = "userMode")
	private static Object[][] userModeProvider() {
		return new Object[][] { { TestUserMode.SUPER_TENANT_ADMIN } };
	}

	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {
		login(getCurrentUsername(), getCurrentPassword());
		deleteDashboards();
		logout();
		loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
		addUser(USERNAME_EDITOR, PASSWORD_EDITOR, PASSWORD_EDITOR);
		addRole(EDITOR_ROLE);
		assignRoleToUser(new String[] { USERNAME_EDITOR });
		logoutFromAdminConsole();
		login(USERNAME_EDITOR, PASSWORD_EDITOR);
	}

	/**
	 * Tests availability of all pages within the menu creator
	 *
	 * @throws Exception
	 */
	@Test(groups = "wso2.ds.dashboard.pages", description = "Tests availability of all pages within menu creator.")
	public void testAllPagesVisibilityInMenuCreator() throws Exception {
		DSWebDriver driver = getDriver();
		String dashboardTitle = "dashboardmenu";
		String gridId = "default-grid";
		addDashBoard(dashboardTitle, "This is a test dashboard");
		Thread.sleep(500);
		WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
		webElement.findElement(By.cssSelector(".ues-edit")).click();

		addPageToDashboard(gridId); // page0
		// deselect pages pane, first
		selectPane("pages");
		addPageToDashboard(gridId); // page1
		selectPane("pages");
		addPageToDashboard(gridId); // page2
		selectPane("pages");
		addPageToDashboard(gridId); // page3

		// goto menu creation page
		selectPane("menu");

		// .menu-customize should have 5 + 2 (7 in total) <li>'s
		List<WebElement> totalLinks = driver.findElements(By.cssSelector(".menu-customize li"));
		int totalLinkSize = totalLinks.size();
		assertEquals(totalLinkSize, 7);
	}

	/**
	 * Tests creating a page hierachy
	 *
	 * @throws Exception
	 */
	@Test(groups = "wso2.ds.dashboard.pages", description = "Tests creating a page hierachy.", dependsOnMethods = "testAllPagesVisibilityInMenuCreator")
	public void testHierachicalMenuCreation() throws Exception {
		DSWebDriver driver = getDriver();
		String dashboardTitle = "dashboardmenu1";
		String gridId = "default-grid";
		addDashBoard(dashboardTitle, "This is a test dashboard");
		Thread.sleep(500);
		WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
		webElement.findElement(By.cssSelector(".ues-edit")).click();

		addPageToDashboard(gridId); // page0
		// deselect pages pane, first
		selectPane("pages");
		addPageToDashboard(gridId); // page1
		selectPane("pages");
		addPageToDashboard(gridId); // page2
		selectPane("pages");
		addPageToDashboard(gridId); // page3

		// goto menu creation page
		selectPane("menu");

		// drag li with page id page1 and drop on to page id page0

		WebElement element = driver.findElement(By.id("page1"));
		WebElement target = driver.findElement(By.id("page2"));

		(new Actions(driver)).dragAndDrop(element, target).perform();

		// check page1 li inside page2 ul

		List<WebElement> totalLinks = driver.findElements(By.cssSelector("ul#page2"));
		int totalLinkSize = totalLinks.size();
		assertEquals(totalLinkSize, 1);
	}

	/**
	 * Tests adding a page to root level
	 *
	 * @throws Exception
	 */
	@Test(groups = "wso2.ds.dashboard.pages", description = "Tests adding a page to root level.", dependsOnMethods = "testHierachicalMenuCreation")
	public void testHierachicalMenuCreateRoot() throws Exception {
		DSWebDriver driver = getDriver();
		String dashboardTitle = "dashboardmenu2";
		String gridId = "default-grid";
		addDashBoard(dashboardTitle, "This is a test dashboard");
		Thread.sleep(500);
		WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
		webElement.findElement(By.cssSelector(".ues-edit")).click();

		addPageToDashboard(gridId); // page0
		// deselect pages pane, first
		selectPane("pages");
		addPageToDashboard(gridId); // page1
		selectPane("pages");
		addPageToDashboard(gridId); // page2

		// goto menu creation page
		selectPane("menu");

		// drag li with page id page1 and drop on to page id page0

		WebElement element = driver.findElement(By.id("page1"));
		WebElement target = driver.findElement(By.id("page2"));

		(new Actions(driver)).dragAndDrop(element, target).perform();

		// undo above dragDrop

		WebElement element1 = driver.findElement(By.id("page2"));
		WebElement target1 = driver.findElement(By.id("ds-menu-root"));

		(new Actions(driver)).dragAndDrop(element1, target1).perform();

		List<WebElement> totalLinks = driver.findElements(By.cssSelector(".menu-customize li"));
		int totalLinkSize = totalLinks.size();
		assertEquals(totalLinkSize, 6);
	}

	/**
	 * Test hide all menu items (excepts landing page)
	 *
	 * @throws Exception
	 */
	@Test(groups = "wso2.ds.dashboard.pages", description = "Test hide all menu items.", dependsOnMethods = "testHierachicalMenuCreateRoot")
	public void testHideAllMenuItems() throws Exception {
		DSWebDriver driver = getDriver();
		String dashboardTitle = "dashboardmenu3";
		String gridId = "default-grid";
		addDashBoard(dashboardTitle, "This is a test dashboard");
		Thread.sleep(500);
		WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
		webElement.findElement(By.cssSelector(".ues-edit")).click();

		addPageToDashboard(gridId); // home and landing page
		// deselect pages pane, first
		selectPane("pages");
		addPageToDashboard(gridId); // page0
		selectPane("pages");
		addPageToDashboard(gridId); // page1
		selectPane("pages");
		addPageToDashboard(gridId); // page2

		// goto menu creation page
		selectPane("menu");

		driver.findElement(By.id("ds-menu-hide-all")).click();

		// goto dashboard view and check the availability of landing page
		clickViewButton();
		pushWindow();

		List<WebElement> totalLinks = driver.findElements(By.cssSelector("#ues-pages .menu-customize li"));
		int totalLinkSize = totalLinks.size();
		assertEquals(totalLinkSize, 1);
		driver.close();
		popWindow();
	}

	/**
	 * Test hide one menu item
	 *
	 * @throws Exception
	 */
	@Test(groups = "wso2.ds.dashboard.pages", description = "Test hide one menu item.", dependsOnMethods = "testHideAllMenuItems")
	public void testHideSingleMenuItem() throws Exception {
		DSWebDriver driver = getDriver();
		String dashboardTitle = "dashboardmenu4";
		String gridId = "default-grid";
		addDashBoard(dashboardTitle, "This is a test dashboard");
		Thread.sleep(500);
		WebElement webElement = driver.findElement(By.id(dashboardTitle.toLowerCase()));
		webElement.findElement(By.cssSelector(".ues-edit")).click();

		addPageToDashboard(gridId); // home and landing page
		// deselect pages pane, first
		selectPane("pages");
		addPageToDashboard(gridId); // page0
		selectPane("pages");
		addPageToDashboard(gridId); // page1
		selectPane("pages");
		addPageToDashboard(gridId); // page2

		// goto menu creation page
		selectPane("menu");

		// hide page2's menu
		driver.findElement(By.cssSelector("li#page2 .hide-menu-item")).click();

		clickViewButton();
		pushWindow();

		List<WebElement> totalLinks = driver.findElements(By.cssSelector("#ues-pages .menu-customize li"));
		int totalLinkSize = totalLinks.size();
		assertEquals(totalLinkSize, 4);
		driver.close();
		popWindow();
	}

	/**
	 * Clean up after running tests
	 *
	 * @throws Exception
	 */
	@AfterClass(alwaysRun = true)
	public void tearDown() throws Exception {
		deleteDashboards();
		getDriver().quit();
	}
}
