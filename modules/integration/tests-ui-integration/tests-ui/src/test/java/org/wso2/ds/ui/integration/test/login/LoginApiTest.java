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
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;

/**
 * Tests the login api`s functionality
 */
public class LoginApiTest extends DSUIIntegrationTest {
    @Factory(dataProvider = "userMode")
    public LoginApiTest(TestUserMode userMode) throws Exception {
        super(userMode);
    }

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
    @Test(groups = "wso2.ds.login", description = "verify login api only processes the post request")
    public void testLoginApi() throws MalformedURLException, XPathExpressionException {
        loginToApi("get");
        String errorMessage = "We cannot process this request.";
        String bodyText = getDriver().findElement(By.tagName("body")).getText();
        Assert.assertTrue(bodyText.contains(errorMessage), "Login allowed using get method");

        loginToApi("post");
        Assert.assertTrue(bodyText.contains(errorMessage), "Login not allowed using post method");
    }

    /**
     * Clean up after running tests.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws XPathExpressionException, MalformedURLException {
        getDriver().quit();
    }
}
