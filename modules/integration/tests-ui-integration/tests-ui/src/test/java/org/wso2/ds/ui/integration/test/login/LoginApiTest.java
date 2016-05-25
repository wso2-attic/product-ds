/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Tests the login api`s functionality
 */
public class LoginApiTest extends DSUIIntegrationTest {
    /**
     * Initializes the class.
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public LoginApiTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     * @return user modes.
     */
    @DataProvider(name = "userMode")
    private static Object[][] userModeProvider() {
        return new Object[][] { { TestUserMode.SUPER_TENANT_ADMIN } };
    }

    /**
     * Checks whether the login api accepts the request from get, post methods
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.login", description = "verify login api only processes the post request and the "
            + "session is maintained")
    public void testLoginApi() throws IOException, XPathExpressionException, InterruptedException {
        redirectToLocation("portal",
                "apis/login?username=" + getCurrentUsername() + "&password=" + getCurrentPassword());
        String errorMessage = "We cannot process this request.";
        String bodyText = getDriver().findElement(By.tagName("body")).getText();
        Assert.assertTrue(bodyText.contains(errorMessage), "Login allowed using get method");
        String systemResourceLocation = FrameworkPathUtil.getSystemResourceLocation();

        // Open the html that makes the POST request to the login API
        String apiSourcePath = systemResourceLocation + "files/testLoginAPI.html";
        getDriver().get(apiSourcePath);
        Thread.sleep(3000);
        bodyText = getDriver().findElement(By.tagName("body")).getText();
        Assert.assertFalse(bodyText.contains(errorMessage), "Login is not allowed using post method");
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        Thread.sleep(3000);
        bodyText = getDriver().findElement(By.tagName("body")).getText();
        Assert.assertTrue(bodyText.contains("CREATE DASHBOARD"),
                "Session is not maintained. Redirected to login page again");
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
}
