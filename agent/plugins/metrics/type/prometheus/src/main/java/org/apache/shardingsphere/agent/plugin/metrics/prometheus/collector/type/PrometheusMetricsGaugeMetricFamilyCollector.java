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

package org.apache.shardingsphere.agent.plugin.metrics.prometheus.collector.type;

import io.prometheus.client.GaugeMetricFamily;
import org.apache.shardingsphere.agent.plugin.metrics.core.collector.type.GaugeMetricFamilyMetricsCollector;
import org.apache.shardingsphere.agent.plugin.metrics.core.config.MetricConfiguration;

import java.util.List;

/**
 * Prometheus metrics gauge metric family collector.
 */
public final class PrometheusMetricsGaugeMetricFamilyCollector implements GaugeMetricFamilyMetricsCollector {
    
    private final GaugeMetricFamily gaugeMetricFamily;
    
    public PrometheusMetricsGaugeMetricFamilyCollector(final MetricConfiguration config) {
        gaugeMetricFamily = new GaugeMetricFamily(config.getId(), config.getHelp(), config.getLabels());
    }
    
    @Override
    public void addMetric(final List<String> labelValues, final double value) {
        gaugeMetricFamily.addMetric(labelValues, value);
    }
    
    @Override
    public Object getRawMetricFamilyObject() {
        return gaugeMetricFamily;
    }
    
    @Override
    public void cleanMetrics() {
        gaugeMetricFamily.samples.clear();
    }
}
