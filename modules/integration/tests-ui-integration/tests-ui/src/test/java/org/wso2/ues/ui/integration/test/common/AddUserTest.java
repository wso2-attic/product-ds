/*
 * <!--
 *   ~  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *   ~
 *   ~  WSO2 Inc. licenses this file to you under the Apache License,
 *   ~  Version 2.0 (the "License"); you may not use this file except
 *   ~  in compliance with the License.
 *   ~  You may obtain a copy of the License at
 *   ~
 *   ~  http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~  Unless required by applicable law or agreed to in writing,
 *   ~  software distributed under the License is distributed on an
 *   ~  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   ~  KIND, either express or implied.  See the License for the
 *   ~  specific language governing permissions and limitations
 *   ~  under the License.
 *   -->
 */

package org.wso2.ues.ui.integration.test.common;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.wso2.ues.ui.integration.util.UESUIIntegrationTest;

import static org.testng.Assert.assertEquals;

public class AddUserTest extends UESUIIntegrationTest {
    private static final String USER_NAME = "sampleuser1";
    private static final String PASSWORD = "sampleuser1";
    private static final String RETYPE_PASSWORD = "sampleuser1";

    public AddUserTest() {
        super();
    }

    @Test(groups = "wso2.ues.common", description = "Adding user to Dashboard server and trying to login with newly " +
            "created user to portal")
    public void testAddUserToUES() throws Exception {
        UESUIIntegrationTest.loginToAdminConsole(getDriver(), getBaseUrl(), getCurrentUsername(), getCurrentPassword());
        getDriver().findElement(By.cssSelector("a[href=\"../userstore/add-user-role" +
                ".jsp?region=region1&item=user_mgt_menu_add\"]")).click();
        getDriver().findElement(By.cssSelector("a[href=\"../user/add-step1.jsp\"]")).click();
        getDriver().findElement(By.name("username")).clear();
        getDriver().findElement(By.name("username")).sendKeys(USER_NAME);
        getDriver().findElement(By.name("password")).clear();
        getDriver().findElement(By.name("password")).sendKeys(PASSWORD);
        getDriver().findElement(By.name("retype")).clear();
        getDriver().findElement(By.name("retype")).sendKeys(RETYPE_PASSWORD);
        //get a way to next button
        getDriver().findElement(By.cssSelector("input.button")).click();
        getDriver().findElement(By.cssSelector("td.buttonRow > input.button")).click();
        getDriver().findElement(By.cssSelector("button[type=\"button\"]")).click();
        getDriver().findElement(By.cssSelector(".right > a")).click();
        UESUIIntegrationTest.login(getDriver(), getBaseUrl(), USER_NAME, PASSWORD);
        assertEquals(USER_NAME, getDriver().findElement(By.cssSelector(".dropdown-toggle")).getText(), "Expected " +
                "Username is not matched");

    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        try {
            UESUIIntegrationTest.logout(getDriver(), getBaseUrl(), USER_NAME);
        } finally {
            getDriver().quit();
        }
    }

}