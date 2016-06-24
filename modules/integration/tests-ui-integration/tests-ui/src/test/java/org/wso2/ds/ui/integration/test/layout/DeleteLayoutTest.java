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

package org.wso2.ds.ui.integration.test.layout;

import org.openqa.selenium.By;
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
 * This is used to test the delete layout functionality
 */
public class DeleteLayoutTest extends DSUIIntegrationTest {
    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public DeleteLayoutTest(TestUserMode userMode) {
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
     * Delete a layout from layout listing page and check whether that layout exists in that page.
     * Delete button of another layout and click cancel button and check whether that layout still exists.
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     * @throws InterruptedException
     */
    @Test(groups = "wso2.ds.layout", description = "Deleting a layout in layout listing page")
    public void testDeletelayout() throws MalformedURLException, XPathExpressionException, InterruptedException {
        getDriver().get(getBaseUrl() + "/portal/layout/");
        getDriver().findElement(By.cssSelector("#blank > a.ds-asset-trash-handle")).click();
        getDriver().findElement(By.cssSelector("span.ladda-label")).click();
        Thread.sleep(3000);
        assertFalse(getDriver().isElementPresent(By.cssSelector("#blank")), "layout is not deleted");
        getDriver().findElement(By.cssSelector("#default-grid > a.ds-asset-trash-handle")).click();
        getDriver().findElement(By.cssSelector("a.btn.btn-default.ds-asset-trash-cancel")).click();
        assertTrue(getDriver().isElementPresent(By.cssSelector("#default-grid")),
                "layout is deleted without confirm");
    }
}
