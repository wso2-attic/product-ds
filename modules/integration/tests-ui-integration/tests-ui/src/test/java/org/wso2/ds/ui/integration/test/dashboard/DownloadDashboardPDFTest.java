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

package org.wso2.ds.ui.integration.test.dashboard;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;

public class DownloadDashboardPDFTest extends DSUIIntegrationTest {
    private static final String DASHBOARD_TITLE = "downloadDashboardPDF";
    private static final String DASHBOARD_DESCRIPTION = "This is description about " + DASHBOARD_TITLE;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @Test(groups = "wso2.ds.dashboard", description = "checking the download dashboard as a pdf feature")
    public void testDownloadDashboardPDF()
            throws Exception {
        login(ADMIN_USERNAME, ADMIN_PASSWORD);
        addDashBoard(DASHBOARD_TITLE, DASHBOARD_DESCRIPTION);
        redirectToLocation("portal", "dashboards");
        getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-edit")).click();
        String[][] gadgetMappings = {{"usa-map", "a"}};
        selectPane("gadgets");
        waitTillElementToBeClickable(By.id("usa-map"));
        dragDropGadget(gadgetMappings);
        clickViewButton();
        for (String winHandle : getDriver().getWindowHandles()) {
            getDriver().switchTo().window(winHandle);
        }
        getDriver().findElement(By.id("download-pdf-panel")).click();
        getDriver().findElement(By.id("generate-pdf")).click();
        Thread.sleep(10000);
        Assert.assertEquals(getDriver().findElement(By.id("download-pdf-panel")).getAttribute("val"), "success");
    }

    /**
     * Clean up after running tests.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws XPathExpressionException, MalformedURLException {
        try {
            logout();
        } finally {
            getDriver().quit();
        }
    }
}
