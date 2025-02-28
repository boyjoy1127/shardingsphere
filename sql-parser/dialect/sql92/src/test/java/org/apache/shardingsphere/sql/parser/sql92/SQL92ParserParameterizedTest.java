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

package org.apache.shardingsphere.sql.parser.sql92;

import org.apache.shardingsphere.test.runner.ShardingSphereParallelTestParameterized;
import org.apache.shardingsphere.test.sql.parser.internal.engine.SQLParserParameterizedTest;
import org.apache.shardingsphere.test.sql.parser.internal.jaxb.sql.SQLCaseType;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;

@RunWith(ShardingSphereParallelTestParameterized.class)
public final class SQL92ParserParameterizedTest extends SQLParserParameterizedTest {
    
    public SQL92ParserParameterizedTest(final String sqlCaseId, final String databaseType, final SQLCaseType sqlCaseType) {
        super(sqlCaseId, databaseType, sqlCaseType);
    }
    
    @Parameters(name = "{0} ({2}) -> {1}")
    public static Collection<Object[]> getTestParameters() {
        return SQLParserParameterizedTest.getTestParameters("SQL92");
    }
}
