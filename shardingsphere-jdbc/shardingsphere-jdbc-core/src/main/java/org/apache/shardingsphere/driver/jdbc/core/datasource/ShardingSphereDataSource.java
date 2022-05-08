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

package org.apache.shardingsphere.driver.jdbc.core.datasource;

import lombok.Getter;
import org.apache.shardingsphere.driver.jdbc.adapter.AbstractDataSourceAdapter;
import org.apache.shardingsphere.driver.state.DriverStateContext;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.checker.RuleConfigurationCheckerFactory;
import org.apache.shardingsphere.infra.config.mode.ModeConfiguration;
import org.apache.shardingsphere.infra.config.database.impl.DataSourceProvidedDatabaseConfiguration;
import org.apache.shardingsphere.infra.config.scope.GlobalRuleConfiguration;
import org.apache.shardingsphere.infra.instance.definition.InstanceDefinition;
import org.apache.shardingsphere.infra.instance.definition.InstanceType;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.mode.manager.ContextManagerBuilderFactory;
import org.apache.shardingsphere.mode.manager.ContextManagerBuilderParameter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * ShardingSphere data source.
 */
@Getter
public final class ShardingSphereDataSource extends AbstractDataSourceAdapter implements AutoCloseable {
    
    private final String databaseName;
    
    private final ContextManager contextManager;
    
    public ShardingSphereDataSource(final String databaseName, final ModeConfiguration modeConfig) throws SQLException {
        this.databaseName = databaseName;
        contextManager = createContextManager(databaseName, modeConfig, new HashMap<>(), new LinkedList<>(), new Properties());
    }
    
    public ShardingSphereDataSource(final String databaseName, final ModeConfiguration modeConfig, final Map<String, DataSource> dataSourceMap,
                                    final Collection<RuleConfiguration> ruleConfigs, final Properties props) throws SQLException {
        checkRuleConfiguration(databaseName, ruleConfigs);
        this.databaseName = databaseName;
        contextManager = createContextManager(databaseName, modeConfig, dataSourceMap, ruleConfigs, null == props ? new Properties() : props);
    }
    
    @SuppressWarnings("unchecked")
    private void checkRuleConfiguration(final String databaseName, final Collection<RuleConfiguration> ruleConfigs) {
        ruleConfigs.forEach(each -> RuleConfigurationCheckerFactory.newInstance(each).ifPresent(optional -> optional.check(databaseName, each)));
    }
    
    private ContextManager createContextManager(final String databaseName, final ModeConfiguration modeConfig, final Map<String, DataSource> dataSourceMap,
                                                final Collection<RuleConfiguration> ruleConfigs, final Properties props) throws SQLException {
        Collection<RuleConfiguration> globalRuleConfigs = ruleConfigs.stream().filter(each -> each instanceof GlobalRuleConfiguration).collect(Collectors.toList());
        ContextManagerBuilderParameter parameter = ContextManagerBuilderParameter.builder()
                .modeConfig(modeConfig)
                .databaseConfigs(Collections.singletonMap(databaseName, new DataSourceProvidedDatabaseConfiguration(dataSourceMap, ruleConfigs)))
                .globalRuleConfigs(globalRuleConfigs)
                .props(props)
                .instanceDefinition(new InstanceDefinition(InstanceType.JDBC)).build();
        return ContextManagerBuilderFactory.newInstance(modeConfig).build(parameter);
    }
    
    @Override
    public Connection getConnection() {
        return DriverStateContext.getConnection(databaseName, contextManager);
    }
    
    @Override
    public Connection getConnection(final String username, final String password) {
        return getConnection();
    }
    
    /**
     * Close data sources.
     *
     * @param dataSourceNames data source names to be closed
     * @throws Exception exception
     */
    public void close(final Collection<String> dataSourceNames) throws Exception {
        Map<String, DataSource> dataSourceMap = contextManager.getDataSourceMap(databaseName);
        for (String each : dataSourceNames) {
            close(dataSourceMap.get(each));
        }
        contextManager.close();
    }
    
    private void close(final DataSource dataSource) throws Exception {
        if (dataSource instanceof AutoCloseable) {
            ((AutoCloseable) dataSource).close();
        }
    }
    
    @Override
    public void close() throws Exception {
        close(contextManager.getDataSourceMap(databaseName).keySet());
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        Map<String, DataSource> dataSourceMap = contextManager.getDataSourceMap(databaseName);
        return dataSourceMap.isEmpty() ? 0 : dataSourceMap.values().iterator().next().getLoginTimeout();
    }
    
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        for (DataSource each : contextManager.getDataSourceMap(databaseName).values()) {
            each.setLoginTimeout(seconds);
        }
    }
}
