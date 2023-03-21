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

package org.apache.shardingsphere.db.protocol.postgresql.packet.command.query.extended.bind.protocol;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class PostgreSQLByteConverterTest {
    
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    public void assertNumeric(final BigDecimal input, final byte[] expected) {
        assertThat(PostgreSQLByteConverter.numeric(input), is(expected));
    }
    
    private static class TestCaseArgumentsProvider implements ArgumentsProvider {
        
        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) {
            return Stream.of(Arguments.of(new BigDecimal("0"), new byte[]{0, 0, -1, -1, 0, 0, 0, 0}),
                    Arguments.of(new BigDecimal("0.00"), new byte[]{0, 0, -1, -1, 0, 0, 0, 2}),
                    Arguments.of(new BigDecimal("0.0001"), new byte[]{0, 1, -1, -1, 0, 0, 0, 4, 0, 1}),
                    Arguments.of(new BigDecimal("9999"), new byte[]{0, 1, 0, 0, 0, 0, 0, 0, 39, 15}),
                    Arguments.of(new BigDecimal("9999.0"), new byte[]{0, 1, 0, 0, 0, 0, 0, 1, 39, 15}),
                    Arguments.of(new BigDecimal("9999.9999"), new byte[]{0, 2, 0, 0, 0, 0, 0, 4, 39, 15, 39, 15}));
        }
    }
}
