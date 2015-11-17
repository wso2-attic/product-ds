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
package ds.integration.tests.common.domain;

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

public abstract class DSIntegrationTest {
    private static final Log LOG = LogFactory.getLog(DSIntegrationTest.class);
    protected final TestUserMode userMode;
    private User userInfo = null;
    private AutomationContext dsContext = null;
    private Tenant tenantInfo = null;
    private String baseUrl = null;

    public DSIntegrationTest(TestUserMode userMode) {
        this.userMode = userMode;
    }

    public DSIntegrationTest() {
        this(TestUserMode.SUPER_TENANT_ADMIN);
    }

    /**
     * This method returns the automation Context
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    private AutomationContext getDsContext() throws XPathExpressionException {
        if (dsContext == null) {
            dsContext = new AutomationContext(DSIntegrationTestConstants.DS_PRODUCT_NAME, this.userMode);
        }
        return dsContext;
    }

    /**
     * This method returns the maximum waiting time from automation context
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public int getMaxWaitTime() throws XPathExpressionException {
        return Integer.parseInt(getDsContext().getConfigurationValue("//maximumWaitingTime"));
    }

    /**
     * This method returns the current tenant Information
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public Tenant getCurrentTenantInfo() throws XPathExpressionException {
        if (tenantInfo == null) {
            tenantInfo = getDsContext().getContextTenant();
        }
        return tenantInfo;
    }

    /**
     * This method returns the current user information
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public User getCurrentUserInfo() throws XPathExpressionException {
        if (userInfo == null) {
            userInfo = getCurrentTenantInfo().getContextUser();
        }
        return userInfo;
    }

    /**
     * This method returns the current username of user from userInfo
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public String getCurrentUsername() throws XPathExpressionException {
        return getCurrentUserInfo().getUserName();
    }

    /**
     * This method returns the current password of user from userInfo
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public String getCurrentPassword() throws XPathExpressionException {
        return getCurrentUserInfo().getPassword();
    }

    protected String getServiceUrlHttp(String serviceName) throws XPathExpressionException {
        String serviceUrl = getDsContext().getContextUrls().getServiceUrl() + "/" + serviceName;
        validateServiceUrl(serviceUrl, tenantInfo);
        return serviceUrl;
    }

    protected String getServiceUrlHttps(String serviceName) throws XPathExpressionException {
        String serviceUrl = getDsContext().getContextUrls().getSecureServiceUrl() + "/" + serviceName;
        validateServiceUrl(serviceUrl, tenantInfo);
        return serviceUrl;
    }

    protected String getResourceLocation() throws XPathExpressionException {
        return TestConfigurationProvider.getResourceLocation(DSIntegrationTestConstants.DS_PRODUCT_NAME);
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

    /**
     * This method returns the backendUrl
     *
     * @return backendUrl - the backendUrl
     */
    protected String getBackEndUrl() throws Exception {
        return getDsContext().getContextUrls().getBackEndUrl();
    }

    /**
     * This method returns the baseUrl of webApp
     *
     * @return baseUrl - the baseUrl of webApp
     */
    public String getBaseUrl() throws Exception {
        if (baseUrl == null) {
            baseUrl = UrlGenerationUtil.getWebAppURL(getDsContext().getContextTenant(), getDsContext().getInstance());
        }
        return baseUrl;
    }
}

