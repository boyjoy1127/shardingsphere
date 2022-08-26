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

package org.apache.shardingsphere.sharding.exception;

import org.apache.shardingsphere.infra.util.exception.sql.ShardingSphereSQLException;
import org.apache.shardingsphere.infra.util.exception.sql.sqlstate.XOpenSQLState;

import java.util.Collection;

/**
 * Sharding route exception.
 */
public final class ShardingRouteException extends ShardingSphereSQLException {
    
    private static final long serialVersionUID = 1611718269096645289L;
    
    public ShardingRouteException(final String operation, final String objectType, final Collection<String> objectNames) {
        super(XOpenSQLState.GENERAL_ERROR, 20007, "`%s %s` can not route correctly for %s `%s`", operation, objectType, objectType, objectNames.toString());
    }
}
