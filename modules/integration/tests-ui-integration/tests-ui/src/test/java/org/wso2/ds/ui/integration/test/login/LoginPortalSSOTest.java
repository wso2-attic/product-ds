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

package org.wso2.ds.ui.integration.test.login;

import ds.integration.tests.common.domain.DSIntegrationTestConstants;
import org.json.JSONObject;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class LoginPortalSSOTest extends DSUIIntegrationTest {

    private String carbonHome;
    private String systemResourceLocation;
    private String designerFilePath;

    /**
     * Initialize the class
     */
    @Factory(dataProvider = "userMode")
    public LoginPortalSSOTest(TestUserMode userMode) throws Exception {
        super(userMode);
    }

    /**
     * Provides user modes
     * @return
     */
    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN}, {TestUserMode.SUPER_TENANT_USER}};
    }

    /**
     * Setup the testing environment
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        this.carbonHome = FrameworkPathUtil.getCarbonHome();
        this.systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();
        this.designerFilePath = carbonHome + File.separator + "repository" + File.separator + "deployment" +
                File.separator + "server" + File.separator + "jaggeryapps" + File.separator + "portal" +
                File.separator + "configs" + File.separator + "designer.json";
    }

    /**
     * Test login when sso is enabled
     * @throws Exception
     */
    @Test(groups = "wso2.ds.login", description = "login to DS portal when SSO is enabled")
    public void testSSOLoginToPortal() throws Exception {
        // register portal application by using configuration files
        registerPortalApplication();

        // setting authentication method to sso in designer json file
        enableSSOLogin();

        loginWithSSO(getCurrentUsername(), getCurrentPassword());
    }

    /**
     * Test logout when sso is enabled
     * @throws Exception
     */
    @Test(groups = "wso2.ds.logout", description = "logout from DS Portal when SSO is enabled", dependsOnMethods = "testSSOLoginToPortal")
    public void testSSOLogoutPortal() throws Exception {
        logout();
    }

    /**
     * Clean up after running tests
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        // set authentication method back to basic
        enableBasicLogin();

        getDriver().quit();
    }

    /**
     * Register portal as a service provider, by using configuration files
     * @throws Exception
     */
    private void registerPortalApplication() throws Exception {
        String pathToSSOIdpConfig = systemResourceLocation + "identity" + File.separator + "sso-idp-config.xml";
        String targetSSOIdpConfig = carbonHome + File.separator + "repository" + File.separator + "conf" + File.separator +
                "identity" + File.separator + "sso-idp-config.xml";

        String pathToPortal = systemResourceLocation + "identity" + File.separator + "service-providers" +
                File.separator + "portal.xml";
        String targetToPortal = carbonHome + File.separator + "repository" + File.separator + "conf" + File.separator +
                "identity" + File.separator + "service-providers" + File.separator + "portal.xml";

        AutomationContext automationContext = new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME, this.userMode);
        ServerConfigurationManager serverConfigurationManager = new ServerConfigurationManager(automationContext);

        // copy files
        serverConfigurationManager.applyConfigurationWithoutRestart(new File(pathToSSOIdpConfig),
                new File(targetSSOIdpConfig), false);
        serverConfigurationManager.applyConfigurationWithoutRestart(new File(pathToPortal), new File(targetToPortal), false);

        // restart the server to activate configuration files
        serverConfigurationManager.restartGracefully();
    }

    /**
     * Enable sso in portal by configuring designer json file
     * @throws Exception
     */
    private void enableSSOLogin() throws Exception {
        File f = new File(designerFilePath);
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();

        JSONObject designerJson;
        PrintWriter pw;

        while(br.ready()){
            sb.append(br.readLine());
        }
        br.close();

        // convert json string to json object
        designerJson = new JSONObject(sb.toString());
        // set active method to sso
        designerJson.getJSONObject("authentication").put("activeMethod", "sso");

        pw = new PrintWriter(f);
        pw.println(designerJson.toString());
        pw.flush();
        pw.close();
    }

    /**
     * Enable basic login in portal by configuring designer json file
     * @throws Exception
     */
    public void enableBasicLogin() throws Exception{
        File f = new File(designerFilePath);
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();

        JSONObject designerJson;
        PrintWriter pw;

        while(br.ready()){
            sb.append(br.readLine());
        }
        br.close();

        // convert json string to json object
        designerJson = new JSONObject(sb.toString());
        // set active method back to basic
        designerJson.getJSONObject("authentication").put("activeMethod", "basic");

        pw = new PrintWriter(f);
        pw.println(designerJson.toString());
        pw.flush();
        pw.close();
    }
}
