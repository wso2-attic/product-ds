package org.wso2.ues.ui.integration.util;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.carbon.automation.engine.configurations.UrlGenerationUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.Tenant;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;
import org.wso2.ues.integration.common.clients.ResourceAdminServiceClient;
import ues.integration.tests.common.domain.UESIntegrationTest;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;

public abstract class BaseUITestCase extends UESIntegrationTest {

    protected static final String PRODUCT_GROUP_NAME = "UES";
    protected static final String DASHBOARD_REGISTRY_BASE_PATH = "/_system/config/ues/dashboards/";
    private static final Log LOG = LogFactory.getLog(BaseUITestCase.class);
    protected Tenant tenantDetails;
    protected String resourcePath;
    protected ResourceAdminServiceClient resourceAdminServiceClient;
    private UESWebDriver driver = null;
    private String baseUrl = null;
    private WebDriverWait wait = null;


    public BaseUITestCase() {
        super();
    }

    public BaseUITestCase(TestUserMode userMode) {
        super(userMode);

    }

    /**
     * This method check whether the given element is present in the current driver instance
     *
     * @param by By element to be present
     * @return boolean true/false
     */
    protected static boolean isElementPresent(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Requested element is not present", e);
            }
            return false;
        } catch (TimeoutException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Requested element is not present", e);
            }
            return false;
        }
    }

    /**
     * This method check whether a alert is present
     *
     * @return boolean true/false
     */
    protected static boolean isAlertPresent(WebDriver driver) {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No alert found", e);
            }
            return false;
        }
    }

    /**
     * This method close the alert and return its text
     *
     * @return String - the text of the alert
     */
    protected static String closeAlertAndGetItsText(WebDriver driver, boolean acceptAlert) {
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        if (acceptAlert) {
            alert.accept();
        } else {
            alert.dismiss();
        }
        return alertText;
    }
    /**
     * This method returns the we driver instance
     *
     * @return UESWEbDriver - the driver instance of UESWebDriver
     */
    public UESWebDriver getDriver() throws MalformedURLException, XPathExpressionException {
        if (driver == null) {
            driver = new UESWebDriver(BrowserManager.getWebDriver(), getMaxWaitTime());
        }
        return driver;
    }
    /**
     * This method returns the baseUrl
     *
     * @return baseUrl - the baseUrl of webApp
     */
    public String getBaseUrl() throws MalformedURLException, XPathExpressionException {
        if (baseUrl == null) {
            baseUrl = UrlGenerationUtil.getWebAppURL(getUesContext().getContextTenant(), getUesContext().getInstance());
        }
        return baseUrl;
    }
    /**
     * This method returns the we driver wait instance
     *
     * @return UESWEbDriverWait - the webDriverWait instance of UESWebDriverWait
     */
    public WebDriverWait getWebDriverWait() throws MalformedURLException, XPathExpressionException {
        if (wait == null) {
            wait = new WebDriverWait(getDriver(), getMaxWaitTime());
        }
        return wait;
    }

    protected void buildTenantDetails(TestUserMode userMode) throws Exception {
        AutomationContext automationContext = new AutomationContext(PRODUCT_GROUP_NAME, userMode);
        tenantDetails = automationContext.getContextTenant();
    }

    protected String getLoginURL() throws XPathExpressionException {
        return UrlGenerationUtil.getLoginURL(getUesContext().getInstance());
    }

    protected String getWebAppURL() throws XPathExpressionException {
        return UrlGenerationUtil.getWebAppURL(getUesContext().getContextTenant(), getUesContext().getInstance());
    }

    protected String getStorePublisherUrl() throws XPathExpressionException {
        return UrlGenerationUtil.getWebAppURL(getUesContext().getContextTenant(), getUesContext().getInstance()).split("\\/t\\/")
                [0];
    }
}
