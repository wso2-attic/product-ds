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

package org.wso2.ues.ui.integration.test.login;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.wso2.ues.ui.integration.util.UESUIIntegrationTest;

public class LoginAdminTest extends UESUIIntegrationTest {

    @Test(groups = "wso2.ues.login", description = "verify login to admin console")
    public void testLoginAdminTestcaseUES() throws Exception {
        UESUIIntegrationTest.loginToAdminConsole(getDriver(), getBaseUrl(), getCurrentUsername(), getCurrentPassword());
    }

    @Test(groups = "wso2.ues.login", description = "verify logout from admin console", dependsOnMethods =
            "testLoginAdminTestcaseUES")
    public void testLogoutAdminTestcaseUES() throws Exception {
        UESUIIntegrationTest.logoutFromAdminConsole(getDriver(), getBaseUrl());
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        getDriver().quit();
    }

}
