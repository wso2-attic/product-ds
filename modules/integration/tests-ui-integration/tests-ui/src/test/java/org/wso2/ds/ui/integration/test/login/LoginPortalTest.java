/*
*Copyright (c) 2015​, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.ds.ui.integration.test.login;

import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

public class LoginPortalTest extends DSUIIntegrationTest {

    @Factory(dataProvider = "userMode")
    public LoginPortalTest(TestUserMode userMode) throws Exception {
        super(userMode);
    }

    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][]{{TestUserMode.SUPER_TENANT_ADMIN}, {TestUserMode.SUPER_TENANT_USER}};
    }

    @Test(groups = "wso2.ds.login", description = "login to DS Portal")
    public void testLoginToPortal() throws Exception {
        login(getCurrentUsername(), getCurrentPassword());
    }

    @Test(groups = "wso2.ds.logout", description = "logout from DS Portal", dependsOnMethods = "testLoginToPortal")
    public void testLogoutPortal() throws Exception {
        logout();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        getDriver().quit();
    }

}
