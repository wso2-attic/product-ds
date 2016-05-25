package org.wso2.ds.ui.integration.test.dashboard;

import ds.integration.tests.common.domain.DSIntegrationTestConstants;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * Tests the getCarbonServerAddress method.
 */
public class CarbonServerAddressTest extends DSUIIntegrationTest {

    private static final String CARBON_SERVER_ADDRESS_API_PATH = "controllers" + File.separator + "apis" +
            File.separator + "carbonserveraddresstest.jag";
    private static final String DESIGNER_JSON_PATH = "configs" + File.separator + "designer.json";
    private static final String BACKUP_DESIGNER_JSON_PATH = "configs" + File.separator + "designer.json.backup";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public CarbonServerAddressTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return
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
    public void setUp() throws XPathExpressionException, IOException, AutomationUtilException {
        String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();

        // Copy the endpoint files
        String apiSourcePath = systemResourceLocation + "files/carbonserveraddresstest.jag";
        String apiDestPath = getPortalFilePath(CARBON_SERVER_ADDRESS_API_PATH);
        FileUtils.copyFile(new File(apiSourcePath), new File(apiDestPath));

        // Backup the designer.json
        String designerJsonPath = getPortalFilePath(DESIGNER_JSON_PATH);
        String designerJsonBackupPath = getPortalFilePath(BACKUP_DESIGNER_JSON_PATH);
        FileUtils.copyFile(new File(designerJsonPath), new File(designerJsonBackupPath));

        // Copy the designer.json
        String designerJsonSourcePath = systemResourceLocation + "files/designer.json";
        FileUtils.copyFile(new File(designerJsonSourcePath), new File(designerJsonPath));

        // Restart the server after designer.json is modified
        AutomationContext automationContext =
                new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME, this.userMode);
        ServerConfigurationManager serverConfigurationManager = new ServerConfigurationManager(automationContext);
        serverConfigurationManager.restartGracefully();
    }

    /**
     * Tests carbon server address when the designer.json host details are changed.
     *
     * @throws IOException
     * @throws XPathExpressionException
     * @throws AutomationUtilException
     */
    @Test(groups = "wso2.ds.dashboard",
            description = "Tests carbon server address when the designer.json host details are changed")
    public void testCarbonServerAddress() throws IOException, XPathExpressionException, AutomationUtilException {
        login(getCurrentUsername(), getCurrentPassword());
        getDriver().get(getBaseUrl() + "/portal/apis/carbonserveraddresstest");
        String carbonServerAddress = getDriver().findElement(By.tagName("body")).getText();
        assertEquals(carbonServerAddress, "https://ds.wso2.com:443", "Invalid carbon server address");
    }

    /**
     * Cleanup the testing environment.
     *
     * @throws XPathExpressionException
     * @throws IOException
     */
    @AfterClass
    public void tearDown() throws XPathExpressionException, IOException {
        String designerJsonPath = getPortalFilePath(DESIGNER_JSON_PATH);
        String designerJsonBackupPath = getPortalFilePath(BACKUP_DESIGNER_JSON_PATH);

        // Restore the backup and remove the backup copy
        FileUtils.forceDelete(new File(designerJsonPath));
        FileUtils.copyFile(new File(designerJsonBackupPath), new File(designerJsonPath));
        FileUtils.forceDelete(new File(designerJsonBackupPath));

        try {
            logout();
        } finally {
            getDriver().quit();
        }
    }
}
