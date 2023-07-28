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

package org.apache.shardingsphere.infra.database.core.connection;

import java.util.Properties;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DataSourceMetaDataBuilderFixture implements DataSourceMetaDataBuilder {
    
    @Override
    public DataSourceMetaData build(final String url, final String username, final String catalog) {
        DataSourceMetaData result = mock(DataSourceMetaData.class, RETURNS_DEEP_STUBS);
        when(result.getQueryProperties()).thenReturn(new Properties());
        return result;
    }
    
    @Override
    public String getDatabaseType() {
        return "FIXTURE";
    }
}
