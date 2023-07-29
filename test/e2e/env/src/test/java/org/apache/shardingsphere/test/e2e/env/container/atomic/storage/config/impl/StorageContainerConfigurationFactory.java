/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.e2e.env.container.atomic.storage.config.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.test.e2e.env.container.atomic.storage.config.StorageContainerConfiguration;
import org.apache.shardingsphere.test.e2e.env.container.atomic.storage.config.impl.h2.H2ContainerConfigurationFactory;
import org.apache.shardingsphere.test.e2e.env.container.atomic.storage.config.impl.mysql.MySQLContainerConfigurationFactory;
import org.apache.shardingsphere.test.e2e.env.container.atomic.storage.config.impl.opengauss.OpenGaussContainerConfigurationFactory;
import org.apache.shardingsphere.test.e2e.env.container.atomic.storage.config.impl.postgresql.PostgreSQLContainerConfigurationFactory;

/**
 * Storage container configuration factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StorageContainerConfigurationFactory {
    
    /**
     * Create new instance of storage container configuration.
     *
     * @param databaseType database type
     * @param scenario scenario
     * @return created instance
     * @throws RuntimeException runtime exception
     */
    public static StorageContainerConfiguration newInstance(final DatabaseType databaseType, final String scenario) {
        switch (databaseType.getType()) {
            case "MySQL":
                return MySQLContainerConfigurationFactory.newInstance(scenario);
            case "PostgreSQL":
                return PostgreSQLContainerConfigurationFactory.newInstance(scenario);
            case "openGauss":
                return OpenGaussContainerConfigurationFactory.newInstance(scenario);
            case "H2":
                return H2ContainerConfigurationFactory.newInstance(scenario);
            default:
                throw new RuntimeException(String.format("Database `%s` is unknown.", databaseType.getType()));
        }
    }
    
    /**
     * Create new instance of storage container configuration.
     *
     * @param databaseType database type
     * @param majorVersion majorVersion
     * @return created instance
     * @throws RuntimeException runtime exception
     */
    public static StorageContainerConfiguration newInstance(final DatabaseType databaseType, final int majorVersion) {
        switch (databaseType.getType()) {
            case "MySQL":
                return MySQLContainerConfigurationFactory.newInstance(majorVersion);
            case "PostgreSQL":
                return PostgreSQLContainerConfigurationFactory.newInstance();
            case "openGauss":
                return OpenGaussContainerConfigurationFactory.newInstance();
            case "H2":
                return H2ContainerConfigurationFactory.newInstance();
            default:
                throw new RuntimeException(String.format("Database `%s` is unknown.", databaseType.getType()));
        }
    }
}
