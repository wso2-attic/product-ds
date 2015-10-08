package ues.integration.tests.common.domain;

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
import org.testng.Assert;
import org.wso2.carbon.automation.engine.configurations.UrlGenerationUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.Tenant;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import javax.xml.xpath.XPathExpressionException;

public abstract class UESIntegrationTest {
    private static final Log LOG = LogFactory.getLog(UESIntegrationTest.class);
    protected final TestUserMode userMode;
    private User userInfo = null;
    private AutomationContext uesContext = null;
    private Tenant tenantInfo = null;
    private String baseUrl = null;

    public UESIntegrationTest(TestUserMode userMode) {
        this.userMode = userMode;
    }

    public UESIntegrationTest() {
        this(TestUserMode.SUPER_TENANT_ADMIN);
    }

    private AutomationContext getUesContext() throws XPathExpressionException {
        if (uesContext == null) {
            uesContext = new AutomationContext(UESIntegrationTestConstants.UES_PRODUCT_NAME, this.userMode);
        }
        return uesContext;
    }

    public int getMaxWaitTime() throws XPathExpressionException {
        return Integer.parseInt(getUesContext().getConfigurationValue("//maximumWaitingTime"));
    }

    public Tenant getCurrentTenantInfo() throws XPathExpressionException {
        if (tenantInfo == null) {
            tenantInfo = getUesContext().getContextTenant();
        }
        return tenantInfo;
    }

    public User getCurrentUserInfo() throws XPathExpressionException {
        if (userInfo == null) {
            userInfo = getCurrentTenantInfo().getContextUser();
        }
        return userInfo;
    }

    public String getCurrentUsername() throws XPathExpressionException {
        return getCurrentUserInfo().getUserName();
    }

    public String getCurrentPassword() throws XPathExpressionException {
        return getCurrentUserInfo().getPassword();
    }

    protected String getServiceUrlHttp(String serviceName) throws XPathExpressionException {
        String serviceUrl = uesContext.getContextUrls().getServiceUrl() + "/" + serviceName;
        validateServiceUrl(serviceUrl, tenantInfo);
        return serviceUrl;
    }

    protected String getServiceUrlHttps(String serviceName) throws XPathExpressionException {
        String serviceUrl = uesContext.getContextUrls().getSecureServiceUrl() + "/" + serviceName;
        validateServiceUrl(serviceUrl, tenantInfo);
        return serviceUrl;
    }

    protected String getResourceLocation() throws XPathExpressionException {
        return TestConfigurationProvider.getResourceLocation(UESIntegrationTestConstants.UES_PRODUCT_NAME);
    }

    protected boolean isTenant() throws Exception {
        if (userMode == null) {
            throw new Exception("UserMode Not Initialized. Can not identify user type");
        }
        return (userMode == TestUserMode.TENANT_ADMIN || userMode == TestUserMode.TENANT_USER);
    }

    private void validateServiceUrl(String serviceUrl, Tenant tenant) {
        //if user mode is null can not validate the service url
        if (userMode != null) {
            if ((userMode == TestUserMode.TENANT_ADMIN || userMode == TestUserMode.TENANT_USER)) {
                Assert.assertTrue(serviceUrl.contains("/t/" + tenant.getDomain() + "/"), "invalid service url for " +
                        "tenant. " + serviceUrl);
            } else {
                Assert.assertFalse(serviceUrl.contains("/t/"), "Invalid service url for user. " + serviceUrl);
            }
        }
    }

    protected String getBackEndUrl() throws Exception {
        return  getUesContext().getContextUrls().getBackEndUrl();
    }

    /**
     * This method returns the baseUrl of webApp
     *
     * @return baseUrl - the baseUrl of webApp
     */
    public String getBaseUrl() throws Exception {
        if (baseUrl == null) {
            baseUrl = UrlGenerationUtil.getWebAppURL(getUesContext().getContextTenant(), getUesContext().getInstance());
        }
        return baseUrl;
    }
}

