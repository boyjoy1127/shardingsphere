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

package org.apache.shardingsphere.parser.spring.boot.condition;

import org.apache.shardingsphere.spring.boot.util.PropertyUtil;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * SQL parser spring boot condition.
 */
public final class SQLParserSpringBootCondition extends SpringBootCondition {
    
    private static final String SQL_PARSER_PREFIX = "spring.shardingsphere.rules.sql-parser";
    
    @Override
    public ConditionOutcome getMatchOutcome(final ConditionContext conditionContext, final AnnotatedTypeMetadata annotatedTypeMetadata) {
        return PropertyUtil.containsPropertyPrefix(conditionContext.getEnvironment(), SQL_PARSER_PREFIX)
                ? ConditionOutcome.match()
                : ConditionOutcome.noMatch("Can't find ShardingSphere sql-parser rule configuration in local file.");
    }
}
