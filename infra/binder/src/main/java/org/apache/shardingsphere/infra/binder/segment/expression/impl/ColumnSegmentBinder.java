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

package org.apache.shardingsphere.infra.binder.segment.expression.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.groovy.util.Maps;
import org.apache.shardingsphere.infra.binder.enums.SegmentType;
import org.apache.shardingsphere.infra.binder.segment.from.TableSegmentBinderContext;
import org.apache.shardingsphere.infra.binder.statement.SQLStatementBinderContext;
import org.apache.shardingsphere.infra.exception.AmbiguousColumnException;
import org.apache.shardingsphere.infra.exception.UnknownColumnException;
import org.apache.shardingsphere.infra.exception.core.ShardingSpherePreconditions;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.column.ColumnSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.ColumnProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.ProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.bounded.ColumnSegmentBoundedInfo;
import org.apache.shardingsphere.sql.parser.sql.common.value.identifier.IdentifierValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

/**
 * Column segment binder.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ColumnSegmentBinder {
    
    private static final Collection<String> EXCLUDE_BIND_COLUMNS = new LinkedHashSet<>(Arrays.asList("ROWNUM", "ROW_NUMBER", "ROWNUM_", "SYSDATE", "SYSTIMESTAMP", "CURRENT_TIMESTAMP",
            "LOCALTIMESTAMP", "UID", "USER"));
    
    private static final Map<SegmentType, String> SEGMENT_TYPE_MESSAGES = Maps.of(SegmentType.PROJECTION, "field list", SegmentType.JOIN_ON, "on clause", SegmentType.JOIN_USING, "from clause",
            SegmentType.PREDICATE, "where clause", SegmentType.ORDER_BY, "order clause", SegmentType.GROUP_BY, "group statement");
    
    private static final String UNKNOWN_SEGMENT_TYPE_MESSAGE = "unknown clause";
    
    /**
     * Bind column segment with metadata.
     *
     * @param segment table segment
     * @param parentSegmentType parent segment type
     * @param statementBinderContext statement binder context
     * @param tableBinderContexts table binder contexts
     * @param outerTableBinderContexts outer table binder contexts
     * @return bounded column segment
     */
    public static ColumnSegment bind(final ColumnSegment segment, final SegmentType parentSegmentType, final SQLStatementBinderContext statementBinderContext,
                                     final Map<String, TableSegmentBinderContext> tableBinderContexts, final Map<String, TableSegmentBinderContext> outerTableBinderContexts) {
        if (EXCLUDE_BIND_COLUMNS.contains(segment.getIdentifier().getValue().toUpperCase())) {
            return segment;
        }
        ColumnSegment result = new ColumnSegment(segment.getStartIndex(), segment.getStopIndex(), segment.getIdentifier());
        segment.getOwner().ifPresent(result::setOwner);
        Collection<TableSegmentBinderContext> tableBinderContextValues =
                getTableSegmentBinderContexts(segment, parentSegmentType, statementBinderContext, tableBinderContexts, outerTableBinderContexts);
        Optional<ColumnSegment> inputColumnSegment = findInputColumnSegment(segment, parentSegmentType, tableBinderContextValues, statementBinderContext);
        inputColumnSegment.ifPresent(optional -> result.setVariable(optional.isVariable()));
        result.setColumnBoundedInfo(createColumnSegmentBoundedInfo(segment, inputColumnSegment.orElse(null)));
        return result;
    }
    
    private static Collection<TableSegmentBinderContext> getTableSegmentBinderContexts(final ColumnSegment segment, final SegmentType parentSegmentType,
                                                                                       final SQLStatementBinderContext statementBinderContext,
                                                                                       final Map<String, TableSegmentBinderContext> tableBinderContexts,
                                                                                       final Map<String, TableSegmentBinderContext> outerTableBinderContexts) {
        if (segment.getOwner().isPresent()) {
            return getTableBinderContextByOwner(segment.getOwner().get().getIdentifier().getValue(), tableBinderContexts, outerTableBinderContexts,
                    statementBinderContext.getExternalTableBinderContexts());
        }
        if (!statementBinderContext.getJoinTableProjectionSegments().isEmpty() && isNeedUseJoinTableProjectionBind(segment, parentSegmentType, statementBinderContext)) {
            return Collections.singleton(new TableSegmentBinderContext(statementBinderContext.getJoinTableProjectionSegments()));
        }
        return tableBinderContexts.values();
    }
    
    private static boolean isNeedUseJoinTableProjectionBind(final ColumnSegment segment, final SegmentType parentSegmentType, final SQLStatementBinderContext statementBinderContext) {
        return SegmentType.PROJECTION == parentSegmentType
                || SegmentType.PREDICATE == parentSegmentType && statementBinderContext.getUsingColumnNames().contains(segment.getIdentifier().getValue().toLowerCase());
    }
    
    private static Collection<TableSegmentBinderContext> getTableBinderContextByOwner(final String owner, final Map<String, TableSegmentBinderContext> tableBinderContexts,
                                                                                      final Map<String, TableSegmentBinderContext> outerTableBinderContexts,
                                                                                      final Map<String, TableSegmentBinderContext> externalTableBinderContexts) {
        if (tableBinderContexts.containsKey(owner)) {
            return Collections.singleton(tableBinderContexts.get(owner));
        }
        if (outerTableBinderContexts.containsKey(owner)) {
            return Collections.singleton(outerTableBinderContexts.get(owner));
        }
        if (externalTableBinderContexts.containsKey(owner)) {
            return Collections.singleton(externalTableBinderContexts.get(owner));
        }
        return Collections.emptyList();
    }
    
    private static Optional<ColumnSegment> findInputColumnSegment(final ColumnSegment segment, final SegmentType parentSegmentType, final Collection<TableSegmentBinderContext> tableBinderContexts,
                                                                  final SQLStatementBinderContext statementBinderContext) {
        ColumnSegment result = null;
        boolean isFindInputColumn = false;
        for (TableSegmentBinderContext each : tableBinderContexts) {
            ProjectionSegment projectionSegment = each.getProjectionSegmentByColumnLabel(segment.getIdentifier().getValue());
            if (projectionSegment instanceof ColumnProjectionSegment) {
                ShardingSpherePreconditions.checkState(null == result,
                        () -> new AmbiguousColumnException(segment.getExpression(), SEGMENT_TYPE_MESSAGES.getOrDefault(parentSegmentType, UNKNOWN_SEGMENT_TYPE_MESSAGE)));
                result = ((ColumnProjectionSegment) projectionSegment).getColumn();
            }
            if (!isFindInputColumn && null != projectionSegment) {
                isFindInputColumn = true;
            }
        }
        if (!isFindInputColumn) {
            result = findInputColumnSegmentFromExternalTables(segment, statementBinderContext.getExternalTableBinderContexts()).orElse(null);
            isFindInputColumn = result != null;
        }
        if (!isFindInputColumn) {
            result = findInputColumnSegmentByVariables(segment, tableBinderContexts).orElse(null);
            isFindInputColumn = result != null;
        }
        ShardingSpherePreconditions.checkState(isFindInputColumn,
                () -> new UnknownColumnException(segment.getExpression(), SEGMENT_TYPE_MESSAGES.getOrDefault(parentSegmentType, UNKNOWN_SEGMENT_TYPE_MESSAGE)));
        return Optional.ofNullable(result);
    }
    
    private static Optional<ColumnSegment> findInputColumnSegmentFromExternalTables(final ColumnSegment segment, final Map<String, TableSegmentBinderContext> externalTableBinderContexts) {
        for (TableSegmentBinderContext each : externalTableBinderContexts.values()) {
            ProjectionSegment projectionSegment = each.getProjectionSegmentByColumnLabel(segment.getIdentifier().getValue());
            if (projectionSegment instanceof ColumnProjectionSegment) {
                return Optional.of(((ColumnProjectionSegment) projectionSegment).getColumn());
            }
        }
        return Optional.empty();
    }
    
    private static Optional<ColumnSegment> findInputColumnSegmentByVariables(final ColumnSegment segment, final Collection<TableSegmentBinderContext> tableBinderContexts) {
        ColumnSegment result = null;
        for (TableSegmentBinderContext each : tableBinderContexts) {
            ProjectionSegment variableSegment = each.getProjectionSegmentByVariableLabel(segment.getIdentifier().getValue());
            if (variableSegment instanceof ColumnProjectionSegment) {
                result = ((ColumnProjectionSegment) variableSegment).getColumn();
                break;
            }
        }
        return Optional.ofNullable(result);
    }
    
    private static ColumnSegmentBoundedInfo createColumnSegmentBoundedInfo(final ColumnSegment segment, final ColumnSegment inputColumnSegment) {
        IdentifierValue originalDatabase = null == inputColumnSegment ? null : inputColumnSegment.getColumnBoundedInfo().getOriginalDatabase();
        IdentifierValue originalSchema = null == inputColumnSegment ? null : inputColumnSegment.getColumnBoundedInfo().getOriginalSchema();
        IdentifierValue originalTable =
                null == segment.getColumnBoundedInfo().getOriginalTable() ? Optional.ofNullable(inputColumnSegment).map(optional -> optional.getColumnBoundedInfo().getOriginalTable()).orElse(null)
                        : segment.getColumnBoundedInfo().getOriginalTable();
        IdentifierValue originalColumn =
                null == segment.getColumnBoundedInfo().getOriginalColumn() ? Optional.ofNullable(inputColumnSegment).map(optional -> optional.getColumnBoundedInfo().getOriginalColumn()).orElse(null)
                        : segment.getColumnBoundedInfo().getOriginalColumn();
        return new ColumnSegmentBoundedInfo(originalDatabase, originalSchema, originalTable, originalColumn);
    }
    
    /**
     * Bind using column segment with metadata.
     *
     * @param segment using column segment
     * @param parentSegmentType parent segment type
     * @param tableBinderContexts table binder contexts
     * @return bounded using column segment
     */
    public static ColumnSegment bindUsingColumn(final ColumnSegment segment, final SegmentType parentSegmentType, final Map<String, TableSegmentBinderContext> tableBinderContexts) {
        ColumnSegment result = new ColumnSegment(segment.getStartIndex(), segment.getStopIndex(), segment.getIdentifier());
        segment.getOwner().ifPresent(result::setOwner);
        Collection<TableSegmentBinderContext> tableBinderContextValues = tableBinderContexts.values();
        Collection<ColumnSegment> usingInputColumnSegments = findUsingInputColumnSegments(segment.getIdentifier().getValue(), tableBinderContextValues);
        ShardingSpherePreconditions.checkState(usingInputColumnSegments.size() >= 2,
                () -> new UnknownColumnException(segment.getExpression(), SEGMENT_TYPE_MESSAGES.getOrDefault(parentSegmentType, UNKNOWN_SEGMENT_TYPE_MESSAGE)));
        Iterator<ColumnSegment> iterator = usingInputColumnSegments.iterator();
        result.setColumnBoundedInfo(createColumnSegmentBoundedInfo(segment, iterator.next()));
        result.setOtherUsingColumnBoundedInfo(createColumnSegmentBoundedInfo(segment, iterator.next()));
        return result;
    }
    
    private static Collection<ColumnSegment> findUsingInputColumnSegments(final String columnName, final Collection<TableSegmentBinderContext> tableBinderContexts) {
        ProjectionSegment projectionSegment;
        Collection<ColumnSegment> result = new LinkedList<>();
        for (TableSegmentBinderContext each : tableBinderContexts) {
            projectionSegment = each.getProjectionSegmentByColumnLabel(columnName);
            if (projectionSegment instanceof ColumnProjectionSegment) {
                result.add(((ColumnProjectionSegment) projectionSegment).getColumn());
            }
        }
        return result;
    }
}
