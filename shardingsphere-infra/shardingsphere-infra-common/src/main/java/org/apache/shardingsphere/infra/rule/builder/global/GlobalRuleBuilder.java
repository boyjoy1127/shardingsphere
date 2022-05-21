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

package org.apache.shardingsphere.infra.rule.builder.global;

import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.metadata.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.rule.builder.RuleBuilder;
import org.apache.shardingsphere.infra.rule.identifier.scope.GlobalRule;
import org.apache.shardingsphere.spi.annotation.SingletonSPI;

import java.util.Map;

/**
 * Global rule builder.
 * 
 * @param <T> type of rule configuration
 */
@SingletonSPI
public interface GlobalRuleBuilder<T extends RuleConfiguration> extends RuleBuilder<T> {
    
    /**
     * Build global rule.
     *
     * @param ruleConfig rule configuration
     * @param databaseMap database map
     * @return global rule
     */
    GlobalRule build(T ruleConfig, Map<String, ShardingSphereDatabase> databaseMap);
}
