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

package org.apache.shardingsphere.test.sql.parser.internal.asserts.statement.dal.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.mysql.dal.MySQLShowEventsStatement;
import org.apache.shardingsphere.test.sql.parser.internal.asserts.SQLCaseAssertContext;
import org.apache.shardingsphere.test.sql.parser.internal.asserts.segment.SQLSegmentAssert;
import org.apache.shardingsphere.test.sql.parser.internal.asserts.segment.database.DatabaseAssert;
import org.apache.shardingsphere.test.sql.parser.internal.asserts.segment.show.ShowFilterAssert;
import org.apache.shardingsphere.test.sql.parser.internal.cases.parser.domain.statement.dal.ShowEventsStatementTestCase;

/**
 * Show events statement assert.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShowEventsStatementAssert {
    
    /**
     * Assert show events statement is correct with expected parser result.
     *
     * @param assertContext assert context
     * @param actual actual show events statement
     * @param expected expected show events statement test case
     */
    public static void assertIs(final SQLCaseAssertContext assertContext, final MySQLShowEventsStatement actual, final ShowEventsStatementTestCase expected) {
        if (actual.getFromSchema().isPresent()) {
            DatabaseAssert.assertIs(assertContext, actual.getFromSchema().get().getSchema(), expected.getFromSchema().getSchema());
            SQLSegmentAssert.assertIs(assertContext, actual.getFromSchema().get(), expected.getFromSchema());
        }
        if (actual.getFilter().isPresent()) {
            ShowFilterAssert.assertIs(assertContext, actual.getFilter().get(), expected.getFilter());
        }
    }
}
