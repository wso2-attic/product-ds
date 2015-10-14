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
import org.wso2.carbon.automation.engine.configurations.UrlGenerationUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.Tenant;
import org.wso2.carbon.automation.engine.context.beans.User;

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

    /**
     * This method returns the automation Context
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    private AutomationContext getUesContext() throws XPathExpressionException {
        if (uesContext == null) {
            uesContext = new AutomationContext(UESIntegrationTestConstants.UES_PRODUCT_NAME, this.userMode);
        }
        return uesContext;
    }

    /**
     * This method returns the maximum waiting time from automation context
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public int getMaxWaitTime() throws XPathExpressionException {
        return Integer.parseInt(getUesContext().getConfigurationValue("//maximumWaitingTime"));
    }

    /**
     * This method returns the current tenant Information
     *
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public Tenant getCurrentTenantInfo() throws XPathExpressionException {
        if (tenantInfo == null) {
            tenantInfo = getUesContext().getContextTenant();
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

    /**
     * This method returns the backendUrl
     *
     * @return backendUrl - the backendUrl
     */
    protected String getBackEndUrl() throws Exception {
        return getUesContext().getContextUrls().getBackEndUrl();
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

