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

package org.apache.shardingsphere.infra.binder.segment.projection.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.binder.segment.from.TableSegmentBinderContext;
import org.apache.shardingsphere.sql.parser.sql.common.segment.dml.item.ShorthandProjectionSegment;

import java.util.Map;

/**
 * Column projection segment binder.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ShorthandProjectionSegmentBinder {
    
    /**
     * Bind column projection segment with metadata.
     *
     * @param segment table segment
     * @param tableBinderContexts table binder contexts
     * @return bounded column projection segment
     */
    public static ShorthandProjectionSegment bind(final ShorthandProjectionSegment segment, final Map<String, TableSegmentBinderContext> tableBinderContexts) {
        if (segment.getOwner().isPresent()) {
            TableSegmentBinderContext tableBinderContext = tableBinderContexts.get(segment.getOwner().get().getIdentifier().getValue());
            segment.getActualProjectionSegments().addAll(tableBinderContext.getColumnLabelProjectionSegments().values());
        } else {
            tableBinderContexts.values().forEach(each -> segment.getActualProjectionSegments().addAll(each.getColumnLabelProjectionSegments().values()));
        }
        return segment;
    }
}
