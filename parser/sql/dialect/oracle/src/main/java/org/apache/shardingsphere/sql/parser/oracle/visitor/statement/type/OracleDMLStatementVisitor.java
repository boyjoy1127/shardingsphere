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

package org.apache.shardingsphere.sql.parser.oracle.visitor.statement.type;

import org.antlr.v4.runtime.misc.Interval;
import org.apache.shardingsphere.sql.parser.api.ASTNode;
import org.apache.shardingsphere.sql.parser.api.visitor.statement.type.DMLStatementVisitor;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.AliasContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.AssignmentValueContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.AssignmentValuesContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.CellAssignmentContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.CollectionExprContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ColumnNameContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ColumnNamesContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ConditionalInsertClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ConditionalInsertElsePartContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ConditionalInsertWhenPartContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ContainersClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.CrossOuterApplyClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.DeleteContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.DeleteSpecificationContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.DeleteWhereClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.DimensionColumnContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.DmlSubqueryClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.DmlTableClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.DuplicateSpecificationContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ExprContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ExpressionListContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ForUpdateClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ForUpdateClauseListContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ForUpdateClauseOptionContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.FromClauseListContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.FromClauseOptionContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.GroupByClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.GroupByItemContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.GroupingExprListContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.GroupingSetsClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.HavingClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.InnerCrossJoinClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.InsertContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.InsertIntoClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.InsertMultiTableContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.InsertSingleTableContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.InsertValuesClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.IntoClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.JoinClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.LockTableContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.MergeAssignmentContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.MergeAssignmentValueContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.MergeContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.MergeSetAssignmentsClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.MergeUpdateClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ModelClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.MultiColumnForLoopContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.MultiTableElementContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.OrderByClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.OuterJoinClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ParenthesisSelectSubqueryContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.QueryBlockContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.QueryNameContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.QueryTableExprClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.QueryTableExprContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ReferenceModelContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.RollupCubeClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectCombineClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectFromClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectJoinOptionContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectJoinSpecificationContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectListContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectProjectionContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectProjectionExprClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectSubqueryContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SelectTableReferenceContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.ShardsClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SingleColumnForLoopContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SubqueryContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.SubqueryFactoringClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.TableCollectionExprContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.TableNameContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.UpdateContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.UpdateSetClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.UpdateSetColumnClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.UpdateSetValueClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.UpdateSpecificationContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.UsingClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.WhereClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.WithClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser.XmlTableContext;
import org.apache.shardingsphere.sql.parser.oracle.visitor.statement.OracleStatementVisitor;
import org.apache.shardingsphere.sql.parser.sql.common.enums.JoinType;
import org.apache.shardingsphere.sql.parser.sql.common.enums.OrderDirection;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.assignment.AssignmentSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.assignment.ColumnAssignmentSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.assignment.InsertValuesSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.assignment.SetAssignmentSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.column.ColumnSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.column.InsertColumnsSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.BinaryOperationExpression;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.DatetimeExpression;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.ExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.FunctionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.XmlPiFunctionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.XmlQueryAndExistsFunctionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.XmlSerializeFunctionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.XmlTableFunctionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.complex.CommonExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.complex.CommonTableExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.simple.LiteralExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.subquery.SubqueryExpressionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.expr.subquery.SubquerySegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.ColumnProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.DatetimeProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.ExpressionProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.ProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.ProjectionsSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.ShorthandProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.SubqueryProjectionSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.order.GroupBySegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.order.OrderBySegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.order.item.ColumnOrderByItemSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.order.item.ExpressionOrderByItemSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.order.item.IndexOrderByItemSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.order.item.OrderByItemSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.predicate.HavingSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.predicate.LockSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.predicate.WhereSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.AliasAvailable;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.AliasSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.InsertMultiTableElementSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.ModelSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.OwnerSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.WithSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.table.JoinTableSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.table.SimpleTableSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.table.SubqueryTableSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.table.TableSegment;
import org.apache.shardingsphere.sql.parser.sql.common.segment.generic.table.XmlTableSegment;
import org.apache.shardingsphere.sql.parser.sql.common.util.SQLUtils;
import org.apache.shardingsphere.sql.parser.sql.common.value.collection.CollectionValue;
import org.apache.shardingsphere.sql.parser.sql.common.value.identifier.IdentifierValue;
import org.apache.shardingsphere.sql.parser.sql.common.value.literal.impl.BooleanLiteralValue;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.oracle.dml.OracleDeleteStatement;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.oracle.dml.OracleInsertStatement;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.oracle.dml.OracleLockTableStatement;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.oracle.dml.OracleMergeStatement;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.oracle.dml.OracleSelectStatement;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.oracle.dml.OracleUpdateStatement;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DML statement visitor for Oracle.
 */
public final class OracleDMLStatementVisitor extends OracleStatementVisitor implements DMLStatementVisitor {
    
    @Override
    public ASTNode visitUpdate(final UpdateContext ctx) {
        OracleUpdateStatement result = new OracleUpdateStatement();
        result.setTable((TableSegment) visit(ctx.updateSpecification()));
        if (null != ctx.alias()) {
            result.getTable().setAlias((AliasSegment) visit(ctx.alias()));
        }
        result.setSetAssignment((SetAssignmentSegment) visit(ctx.updateSetClause()));
        if (null != ctx.whereClause()) {
            result.setWhere((WhereSegment) visit(ctx.whereClause()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitUpdateSpecification(final UpdateSpecificationContext ctx) {
        if (null != ctx.dmlTableExprClause().dmlTableClause()) {
            return visit(ctx.dmlTableExprClause().dmlTableClause());
        }
        if (null != ctx.dmlTableExprClause().dmlSubqueryClause()) {
            SubquerySegment subquerySegment = (SubquerySegment) visit(ctx.dmlTableExprClause().dmlSubqueryClause());
            return new SubqueryTableSegment(subquerySegment);
        }
        SubquerySegment subquerySegment = (SubquerySegment) visit(ctx.dmlTableExprClause().tableCollectionExpr());
        return new SubqueryTableSegment(subquerySegment);
    }
    
    @Override
    public ASTNode visitUpdateSetClause(final UpdateSetClauseContext ctx) {
        Collection<AssignmentSegment> assignments = new LinkedList<>();
        if (null != ctx.updateSetColumnList()) {
            for (UpdateSetColumnClauseContext each : ctx.updateSetColumnList().updateSetColumnClause()) {
                assignments.add((AssignmentSegment) visit(each));
            }
        } else {
            assignments.add((AssignmentSegment) visit(ctx.updateSetValueClause()));
        }
        return new SetAssignmentSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), assignments);
    }
    
    @Override
    public ASTNode visitUpdateSetColumnClause(final UpdateSetColumnClauseContext ctx) {
        return 1 == ctx.columnName().size() ? createAssignmentSegmentFromSingleColumnAssignment(ctx) : createAssignmentSegmentFromMultiColumnAssignment(ctx);
    }
    
    private AssignmentSegment createAssignmentSegmentFromSingleColumnAssignment(final UpdateSetColumnClauseContext ctx) {
        ColumnSegment column = (ColumnSegment) visitColumnName(ctx.columnName(0));
        List<ColumnSegment> columnSegments = new LinkedList<>();
        columnSegments.add(column);
        if (null != ctx.expr()) {
            ExpressionSegment value = (ExpressionSegment) visit(ctx.expr());
            return new ColumnAssignmentSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), columnSegments, value);
        }
        if (null != ctx.selectSubquery()) {
            SubquerySegment subquerySegment = new SubquerySegment(ctx.selectSubquery().start.getStartIndex(), ctx.selectSubquery().stop.getStopIndex(),
                    (OracleSelectStatement) visit(ctx.selectSubquery()));
            SubqueryExpressionSegment value = new SubqueryExpressionSegment(subquerySegment);
            AssignmentSegment result = new ColumnAssignmentSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), columnSegments, value);
            result.getColumns().add(column);
            return result;
        }
        CommonExpressionSegment value = new CommonExpressionSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), ctx.DEFAULT().getText());
        AssignmentSegment result = new ColumnAssignmentSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), columnSegments, value);
        result.getColumns().add(column);
        return result;
    }
    
    private AssignmentSegment createAssignmentSegmentFromMultiColumnAssignment(final UpdateSetColumnClauseContext ctx) {
        List<ColumnSegment> columnSegments = new LinkedList<>();
        for (ColumnNameContext each : ctx.columnName()) {
            columnSegments.add((ColumnSegment) visit(each));
        }
        SubquerySegment subquerySegment =
                new SubquerySegment(ctx.selectSubquery().start.getStartIndex(), ctx.selectSubquery().stop.getStopIndex(), (OracleSelectStatement) visit(ctx.selectSubquery()));
        SubqueryExpressionSegment value = new SubqueryExpressionSegment(subquerySegment);
        return new ColumnAssignmentSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), columnSegments, value);
    }
    
    @Override
    public ASTNode visitUpdateSetValueClause(final UpdateSetValueClauseContext ctx) {
        ColumnSegment column = new ColumnSegment(ctx.alias().start.getStartIndex(), ctx.alias().stop.getStopIndex(), (IdentifierValue) visit(ctx.alias().identifier()));
        List<ColumnSegment> columnSegments = new LinkedList<>();
        columnSegments.add(column);
        if (null != ctx.expr()) {
            ExpressionSegment value = (ExpressionSegment) visit(ctx.expr());
            AssignmentSegment result = new ColumnAssignmentSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), columnSegments, value);
            result.getColumns().add(column);
            return result;
        } else {
            SubquerySegment subquerySegment =
                    new SubquerySegment(ctx.selectSubquery().start.getStartIndex(), ctx.selectSubquery().stop.getStopIndex(), (OracleSelectStatement) visit(ctx.selectSubquery()));
            SubqueryExpressionSegment value = new SubqueryExpressionSegment(subquerySegment);
            AssignmentSegment result = new ColumnAssignmentSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), columnSegments, value);
            result.getColumns().add(column);
            return result;
        }
    }
    
    @Override
    public ASTNode visitInsert(final InsertContext ctx) {
        // TODO :FIXME, since there is no segment for insertValuesClause, InsertStatement is created by sub rule.
        return null == ctx.insertSingleTable() ? visit(ctx.insertMultiTable()) : visit(ctx.insertSingleTable());
    }
    
    @Override
    public ASTNode visitInsertSingleTable(final InsertSingleTableContext ctx) {
        OracleInsertStatement result = (OracleInsertStatement) visit(ctx.insertIntoClause());
        if (null != ctx.insertValuesClause()) {
            result.getValues().addAll(createInsertValuesSegments(ctx.insertValuesClause().assignmentValues()));
        }
        if (null != ctx.selectSubquery()) {
            OracleSelectStatement subquery = (OracleSelectStatement) visit(ctx.selectSubquery());
            SubquerySegment subquerySegment = new SubquerySegment(ctx.selectSubquery().start.getStartIndex(), ctx.selectSubquery().stop.getStopIndex(), subquery);
            result.setSelectSubquery(subquerySegment);
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    private Collection<InsertValuesSegment> createInsertValuesSegments(final AssignmentValuesContext ctx) {
        Collection<InsertValuesSegment> result = new LinkedList<>();
        result.add((InsertValuesSegment) visit(ctx));
        return result;
    }
    
    @Override
    public ASTNode visitInsertMultiTable(final InsertMultiTableContext ctx) {
        OracleInsertStatement result = new OracleInsertStatement();
        result.setInsertMultiTableElementSegment(null == ctx.conditionalInsertClause()
                ? createInsertMultiTableElementSegment(ctx.multiTableElement())
                : (InsertMultiTableElementSegment) visit(ctx.conditionalInsertClause()));
        OracleSelectStatement subquery = (OracleSelectStatement) visit(ctx.selectSubquery());
        SubquerySegment subquerySegment = new SubquerySegment(ctx.selectSubquery().start.getStartIndex(), ctx.selectSubquery().stop.getStopIndex(), subquery);
        result.setSelectSubquery(subquerySegment);
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    private InsertMultiTableElementSegment createInsertMultiTableElementSegment(final List<MultiTableElementContext> ctx) {
        Collection<OracleInsertStatement> insertStatements = new LinkedList<>();
        for (MultiTableElementContext each : ctx) {
            insertStatements.add((OracleInsertStatement) visit(each));
        }
        InsertMultiTableElementSegment result = new InsertMultiTableElementSegment(ctx.get(0).getStart().getStartIndex(), ctx.get(ctx.size() - 1).getStop().getStopIndex());
        result.getInsertStatements().addAll(insertStatements);
        return result;
    }
    
    @Override
    public ASTNode visitInsertValuesClause(final InsertValuesClauseContext ctx) {
        OracleInsertStatement result = new OracleInsertStatement();
        result.getValues().addAll(createInsertValuesSegments(ctx.assignmentValues()));
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitInsertIntoClause(final InsertIntoClauseContext ctx) {
        OracleInsertStatement result = new OracleInsertStatement();
        if (null != ctx.dmlTableExprClause().dmlTableClause()) {
            result.setTable((SimpleTableSegment) visit(ctx.dmlTableExprClause().dmlTableClause()));
        } else if (null != ctx.dmlTableExprClause().dmlSubqueryClause()) {
            result.setInsertSelect((SubquerySegment) visit(ctx.dmlTableExprClause().dmlSubqueryClause()));
        } else {
            result.setInsertSelect((SubquerySegment) visit(ctx.dmlTableExprClause().tableCollectionExpr()));
        }
        if (null != ctx.columnNames()) {
            ColumnNamesContext columnNames = ctx.columnNames();
            CollectionValue<ColumnSegment> columnSegments = (CollectionValue<ColumnSegment>) visit(columnNames);
            result.setInsertColumns(new InsertColumnsSegment(columnNames.start.getStartIndex(), columnNames.stop.getStopIndex(), columnSegments.getValue()));
        } else {
            result.setInsertColumns(new InsertColumnsSegment(ctx.stop.getStopIndex() + 1, ctx.stop.getStopIndex() + 1, Collections.emptyList()));
        }
        return result;
    }
    
    @Override
    public ASTNode visitDelete(final DeleteContext ctx) {
        OracleDeleteStatement result = new OracleDeleteStatement();
        result.setTable((TableSegment) visit(ctx.deleteSpecification()));
        if (null != ctx.alias()) {
            result.getTable().setAlias((AliasSegment) visit(ctx.alias()));
        }
        if (null != ctx.whereClause()) {
            result.setWhere((WhereSegment) visit(ctx.whereClause()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitDeleteSpecification(final DeleteSpecificationContext ctx) {
        if (null != ctx.dmlTableExprClause().dmlTableClause()) {
            return visit(ctx.dmlTableExprClause().dmlTableClause());
        }
        if (null != ctx.dmlTableExprClause().dmlSubqueryClause()) {
            SubquerySegment subquerySegment = (SubquerySegment) visit(ctx.dmlTableExprClause().dmlSubqueryClause());
            return new SubqueryTableSegment(subquerySegment);
        }
        SubquerySegment subquerySegment = (SubquerySegment) visit(ctx.dmlTableExprClause().tableCollectionExpr());
        return new SubqueryTableSegment(subquerySegment);
    }
    
    @Override
    public ASTNode visitMultiTableElement(final MultiTableElementContext ctx) {
        OracleInsertStatement result = (OracleInsertStatement) visit(ctx.insertIntoClause());
        if (null != ctx.insertValuesClause()) {
            result.getValues().addAll(createInsertValuesSegments(ctx.insertValuesClause().assignmentValues()));
        }
        return result;
    }
}
