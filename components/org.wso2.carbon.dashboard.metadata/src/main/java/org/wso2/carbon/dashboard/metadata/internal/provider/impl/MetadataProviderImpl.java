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

import org.wso2.carbon.dashboard.metadata.bean.Metadata;
import org.wso2.carbon.dashboard.metadata.bean.PaginationContext;
import org.wso2.carbon.dashboard.metadata.bean.Query;

import org.wso2.carbon.dashboard.metadata.exception.MetadataException;
import org.wso2.carbon.dashboard.metadata.internal.dao.MetadataDAO;
import org.wso2.carbon.dashboard.metadata.provider.MetadataProvider;

import java.util.List;

/**
 * This is a core class of the Metadata business logic implementation.
 */
public class MetadataProviderImpl implements MetadataProvider {
    private MetadataDAO dao;

    public MetadataProviderImpl(MetadataDAO dao) {
        this.dao = dao;
    }

    @Override
    public boolean isExists(Query query) throws MetadataException {
        validateQuery(query);
        if (query.getUuid() != null) {
            return dao.isExists(query.getUuid());
        } else if (query.getOwner() != null && query.getName() != null && query.getVersion() != null) {
            return dao.isExists(query.getOwner(), query.getName(), query.getVersion());
        } else if (query.getOwner() != null && query.getName() != null) {
            return dao.isExistsOwner(query.getOwner(), query.getName());
        } else if (query.getName() != null && query.getVersion() != null) {
            return dao.isExistsByVersion(query.getName(), query.getVersion());
        } else {
            throw new MetadataException("Insufficient parameters supplied to the command");
        }
    }

    @Override
    public void update(Metadata metadata) throws MetadataException {
        dao.update(metadata);
    }

    @Override
    public void add(Metadata metadata) throws MetadataException {
        dao.add(metadata);
    }

    @Override
    public void delete(Query query) throws MetadataException {
        validateQuery(query);
        if (query.getUuid() != null) {
            dao.delete(query.getUuid());
        } else if (query.getOwner() != null && query.getName() != null && query.getVersion() != null) {
            dao.delete(query.getOwner(), query.getName(), query.getVersion());
        } else if (query.getOwner() != null && query.getName() != null) {
            dao.delete(query.getOwner(), query.getName());
        } else {
            throw new MetadataException("Insufficient parameters supplied to the command");
        }
    }

    @Override
    public Metadata get(Query query) throws MetadataException {
        validateQuery(query);
        if (query.getUuid() != null) {
            return dao.get(query.getUuid());
        } else {
            throw new MetadataException("Insufficient parameters supplied to the command");
        }
    }

    @Override
    public List<Metadata> get(Query query, PaginationContext paginationContext) throws MetadataException {
        validateQuery(query);
        if (query.getOwner() != null && query.getName() != null && query.getVersion() != null) {
            return dao.list(query.getOwner(), query.getName(), query.getVersion(), paginationContext);
        } else if (query.getOwner() != null && query.getName() != null) {
            return dao.listByOwner(query.getOwner(), query.getName(), paginationContext);
        } else if (query.getName() != null && query.getVersion() != null) {
            return dao.list(query.getName(), query.getVersion(), paginationContext);
        } else if (query.getName() != null) {
            return dao.listByName(query.getName(), paginationContext);
        } else {
            throw new MetadataException("Insufficient parameters supplied to the command");
        }
    }

    private void validateQuery(Query query) throws MetadataException {
        if (query == null) {
            throw new MetadataException("Unable to find Metadata. The query is empty");
        }
    }
}
