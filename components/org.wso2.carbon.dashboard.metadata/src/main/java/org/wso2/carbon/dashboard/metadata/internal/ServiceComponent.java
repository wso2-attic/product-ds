/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.dashboard.metadata.internal;

import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.wso2.carbon.dashboard.metadata.bean.Metadata;
import org.wso2.carbon.dashboard.metadata.exception.MetadataException;
import org.wso2.carbon.dashboard.metadata.internal.dao.utils.DAOUtils;
import org.wso2.carbon.dashboard.metadata.internal.provider.impl.MetaDataProviderFactory;
import org.wso2.carbon.dashboard.metadata.provider.MetadataProvider;
import org.wso2.carbon.datasource.core.api.DataSourceService;

import java.util.Date;
import java.util.Map;

@Component(name = "org.wso2.carbon.dashboard.metadata.internal.ServiceComponent",
           immediate = true)
public class ServiceComponent {

    private static final Logger log = LoggerFactory.getLogger(ServiceComponent.class);

    private BundleContext bundleContext;

    /**
     * Get called when this osgi component get registered.
     *
     * @param bundleContext Context of the osgi component.
     */
    @Activate
    public void registerCarbonSecurityConnectors(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        log.info("ServiceComponent activated.");
        try {
            test();
        } catch (MetadataException e) {
            e.printStackTrace();
        }
    }

    private void test() throws MetadataException {
        log.info("test()");
        DAOUtils.getInstance().initialize("WSO2_DASHBOARD_DB");
        Metadata metadata = new Metadata();
        metadata.setName("Test");
        metadata.setContent("sdda fadsf dsf dsf dsaadsf1234353543b543 5");
        metadata.setDescription("fdsfsdfsdfsdfds");
        metadata.setOwner("Chandana");
        metadata.setLastUpdatedBy("Chandana");
        metadata.setLastUpdatedTime((new Date().getTime()));
        metadata.setCreatedTime((new Date().getTime()));
        metadata.setVersion("1.2.3");

        MetadataProvider provider = MetaDataProviderFactory.getInstance().getProvider();
        provider.add(metadata);


        log.info("end test()");
    }

    /**
     * Get called when this osgi component get unregistered.
     */
    @Deactivate
    protected void deactivate() {
        this.bundleContext = null;
        log.info("ServiceComponent deactivated.");
    }

    @Reference(
            name = "org.wso2.carbon.datasource.DataSourceService",
            service = DataSourceService.class,
            cardinality = ReferenceCardinality.AT_LEAST_ONE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unregisterDataSourceService"
    )
    protected void registerDataSourceService(DataSourceService service, Map<String, String> properties) {

        if (service == null) {
            log.error("Data source service is null. Registering data source service is unsuccessful.");
            return;
        }

        DAOUtils.getInstance().setDataSourceService(service);

        if (log.isInfoEnabled()) {
            log.info("Data source service registered successfully.");
        }
    }

    protected void unregisterDataSourceService(DataSourceService service) {

        if (log.isInfoEnabled()) {
            log.info("Data source service unregistered.");
        }
        DAOUtils.getInstance().setDataSourceService(null);
    }

}
