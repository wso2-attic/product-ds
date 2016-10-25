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
package org.wso2.carbon.dashboard.metadata.internal.provider.impl;


import org.wso2.carbon.dashboard.metadata.internal.dao.MetadataDAO;
import org.wso2.carbon.dashboard.metadata.internal.dao.impl.MetadataDAOImpl;
import org.wso2.carbon.dashboard.metadata.internal.dao.impl.SolrBackedMetadataDAOImpl;
import org.wso2.carbon.dashboard.metadata.provider.MetadataProvider;

/**
 * Factory class to provide instance of MetadataProvider
 */
public class MetaDataProviderFactory {

    private static MetaDataProviderFactory instance;
    private MetadataProvider provider;

    private MetaDataProviderFactory() {
        MetadataDAO dao = new SolrBackedMetadataDAOImpl(new MetadataDAOImpl());
        provider = new MetadataProviderImpl(dao);

    }

    public static MetaDataProviderFactory getInstance() {
        synchronized (MetaDataProviderFactory.class) {
            if (instance == null) {
                instance = new MetaDataProviderFactory();
            }
        }
        return instance;
    }

    public MetadataProvider getProvider() {
        return this.provider;
    }
}
