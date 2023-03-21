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

package org.apache.shardingsphere.test.e2e.engine.dql;

import org.apache.shardingsphere.test.e2e.cases.SQLCommandType;
import org.apache.shardingsphere.test.e2e.cases.SQLExecuteType;
import org.apache.shardingsphere.test.e2e.cases.value.SQLValue;
import org.apache.shardingsphere.test.e2e.engine.SingleE2EITContainerComposer;
import org.apache.shardingsphere.test.e2e.framework.E2EITExtension;
import org.apache.shardingsphere.test.e2e.framework.param.array.E2ETestParameterFactory;
import org.apache.shardingsphere.test.e2e.framework.param.model.AssertionTestParameter;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(E2EITExtension.class)
public final class GeneralDQLE2EIT extends BaseDQLE2EIT {
    
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    public void assertExecuteQuery(final AssertionTestParameter testParam) throws SQLException, ParseException, IOException, JAXBException {
        // TODO make sure DQL test case can not be null
        if (null == testParam.getTestCaseContext()) {
            return;
        }
        try (SingleE2EITContainerComposer containerComposer = new SingleE2EITContainerComposer(testParam)) {
            init(testParam, containerComposer);
            if (isUseXMLAsExpectedDataset()) {
                assertExecuteQueryWithXmlExpected(testParam, containerComposer);
            } else {
                assertExecuteQueryWithExpectedDataSource(containerComposer);
            }
        }
        
    }
    
    private void assertExecuteQueryWithXmlExpected(final AssertionTestParameter testParam, final SingleE2EITContainerComposer containerComposer) throws SQLException, ParseException {
        // TODO Fix jdbc adapter
        if ("jdbc".equals(testParam.getAdapter())) {
            return;
        }
        try (
                Connection connection = containerComposer.getContainerComposer().getTargetDataSource().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(containerComposer.getSQL())) {
            assertResultSet(containerComposer, resultSet);
        }
    }
    
    private void assertExecuteQueryWithExpectedDataSource(final SingleE2EITContainerComposer containerComposer) throws SQLException, ParseException {
        try (
                Connection actualConnection = containerComposer.getContainerComposer().getTargetDataSource().getConnection();
                Connection expectedConnection = getExpectedDataSource().getConnection()) {
            if (SQLExecuteType.Literal == containerComposer.getSqlExecuteType()) {
                assertExecuteQueryForStatement(containerComposer, actualConnection, expectedConnection);
            } else {
                assertExecuteQueryForPreparedStatement(containerComposer, actualConnection, expectedConnection);
            }
        }
    }
    
    private void assertExecuteQueryForStatement(final SingleE2EITContainerComposer containerComposer,
                                                final Connection actualConnection, final Connection expectedConnection) throws SQLException, ParseException {
        try (
                Statement actualStatement = actualConnection.createStatement();
                ResultSet actualResultSet = actualStatement.executeQuery(containerComposer.getSQL());
                Statement expectedStatement = expectedConnection.createStatement();
                ResultSet expectedResultSet = expectedStatement.executeQuery(containerComposer.getSQL())) {
            assertResultSet(actualResultSet, expectedResultSet);
        }
    }
    
    private void assertExecuteQueryForPreparedStatement(final SingleE2EITContainerComposer containerComposer,
                                                        final Connection actualConnection, final Connection expectedConnection) throws SQLException, ParseException {
        try (
                PreparedStatement actualPreparedStatement = actualConnection.prepareStatement(containerComposer.getSQL());
                PreparedStatement expectedPreparedStatement = expectedConnection.prepareStatement(containerComposer.getSQL())) {
            for (SQLValue each : containerComposer.getAssertion().getSQLValues()) {
                actualPreparedStatement.setObject(each.getIndex(), each.getValue());
                expectedPreparedStatement.setObject(each.getIndex(), each.getValue());
            }
            try (
                    ResultSet actualResultSet = actualPreparedStatement.executeQuery();
                    ResultSet expectedResultSet = expectedPreparedStatement.executeQuery()) {
                assertResultSet(actualResultSet, expectedResultSet);
            }
        }
    }
    
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    public void assertExecute(final AssertionTestParameter testParam) throws SQLException, ParseException, JAXBException, IOException {
        // TODO make sure DQL test case can not be null
        if (null == testParam.getTestCaseContext()) {
            return;
        }
        try (SingleE2EITContainerComposer containerComposer = new SingleE2EITContainerComposer(testParam)) {
            init(testParam, containerComposer);
            if (isUseXMLAsExpectedDataset()) {
                assertExecuteWithXmlExpected(testParam, containerComposer);
            } else {
                assertExecuteWithExpectedDataSource(containerComposer);
            }
        }
    }
    
    private void assertExecuteWithXmlExpected(final AssertionTestParameter testParam, final SingleE2EITContainerComposer containerComposer) throws SQLException, ParseException {
        // TODO Fix jdbc adapter
        if ("jdbc".equals(testParam.getAdapter())) {
            return;
        }
        try (
                Connection connection = containerComposer.getContainerComposer().getTargetDataSource().getConnection();
                Statement statement = connection.createStatement()) {
            assertTrue(statement.execute(containerComposer.getSQL()), "Not a query statement.");
            ResultSet resultSet = statement.getResultSet();
            assertResultSet(containerComposer, resultSet);
        }
    }
    
    private void assertExecuteWithExpectedDataSource(final SingleE2EITContainerComposer containerComposer) throws SQLException, ParseException {
        try (
                Connection actualConnection = containerComposer.getContainerComposer().getTargetDataSource().getConnection();
                Connection expectedConnection = getExpectedDataSource().getConnection()) {
            if (SQLExecuteType.Literal == containerComposer.getSqlExecuteType()) {
                assertExecuteForStatement(containerComposer, actualConnection, expectedConnection);
            } else {
                assertExecuteForPreparedStatement(containerComposer, actualConnection, expectedConnection);
            }
        }
    }
    
    private void assertExecuteForStatement(final SingleE2EITContainerComposer containerComposer,
                                           final Connection actualConnection, final Connection expectedConnection) throws SQLException, ParseException {
        try (
                Statement actualStatement = actualConnection.createStatement();
                Statement expectedStatement = expectedConnection.createStatement()) {
            assertTrue(actualStatement.execute(containerComposer.getSQL()) && expectedStatement.execute(containerComposer.getSQL()), "Not a query statement.");
            try (
                    ResultSet actualResultSet = actualStatement.getResultSet();
                    ResultSet expectedResultSet = expectedStatement.getResultSet()) {
                assertResultSet(actualResultSet, expectedResultSet);
            }
        }
    }
    
    private void assertExecuteForPreparedStatement(final SingleE2EITContainerComposer containerComposer,
                                                   final Connection actualConnection, final Connection expectedConnection) throws SQLException, ParseException {
        try (
                PreparedStatement actualPreparedStatement = actualConnection.prepareStatement(containerComposer.getSQL());
                PreparedStatement expectedPreparedStatement = expectedConnection.prepareStatement(containerComposer.getSQL())) {
            for (SQLValue each : containerComposer.getAssertion().getSQLValues()) {
                actualPreparedStatement.setObject(each.getIndex(), each.getValue());
                expectedPreparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertTrue(actualPreparedStatement.execute() && expectedPreparedStatement.execute(), "Not a query statement.");
            try (
                    ResultSet actualResultSet = actualPreparedStatement.getResultSet();
                    ResultSet expectedResultSet = expectedPreparedStatement.getResultSet()) {
                assertResultSet(actualResultSet, expectedResultSet);
            }
        }
    }
    
    private static boolean isEnabled() {
        return E2ETestParameterFactory.containsTestParameter();
    }
    
    private static class TestCaseArgumentsProvider implements ArgumentsProvider {
        
        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) {
            Collection<AssertionTestParameter> result = E2ETestParameterFactory.getAssertionTestParameters(SQLCommandType.DQL);
            // TODO make sure DQL test case can not be null
            return result.isEmpty() ? Stream.of(Arguments.of(new AssertionTestParameter(null, null, null, null, null, null, null))) : result.stream().map(Arguments::of);
        }
    }
}
