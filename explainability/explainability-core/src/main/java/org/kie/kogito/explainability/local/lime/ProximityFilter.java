/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.local.lime;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Filter sparse training set generated by {@link DatasetEncoder}, if possible, based on the sample weights
 * calculated by {@link SampleWeighter}.
 */
class ProximityFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProximityFilter.class);

    private final double proximityThreshold;
    private final double proximityFilteredDatasetMinimum;

    public ProximityFilter(double proximityThreshold, double proximityFilteredDatasetMinimum) {
        this.proximityThreshold = proximityThreshold;
        this.proximityFilteredDatasetMinimum = proximityFilteredDatasetMinimum;
    }

    /**
     * Apply proximity filter on given sparse training set and weights.
     *
     * @param trainingSet   the sparse training set
     * @param sampleWeights the sample weights
     */
    void apply(List<Pair<double[], Double>> trainingSet, double[] sampleWeights) {
        if (trainingSet != null && sampleWeights != null) {
            if (trainingSet.size() == sampleWeights.length) {
                List<Integer> toRemove = new ArrayList<>();
                for (int i = trainingSet.size() - 1; i >= 0; i--) {
                    if (sampleWeights[i] < proximityThreshold) {
                        toRemove.add(i);
                    }
                }
                boolean enoughSamples;
                double v = proximityFilteredDatasetMinimum;
                if (v % 1 == 0) {
                    enoughSamples = trainingSet.size() - toRemove.size() > v;
                } else {
                    if (v > 1) {
                        LOGGER.warn("unexpected value for 'Minimum dataset cut' {}, not filtering", v);
                        enoughSamples = false;
                    } else {
                        enoughSamples = (double) toRemove.size() / (double) trainingSet.size() >= v;
                    }
                }
                if (!toRemove.isEmpty() && enoughSamples) {
                    for (Integer r : toRemove) {
                        trainingSet.remove(r.intValue());
                    }
                    Arrays.fill(sampleWeights, 1);
                }
            } else {
                LOGGER.warn("training set size {} ≠ weights size {}, not filtering", trainingSet.size(), sampleWeights.length);
            }
        } else {
            LOGGER.error("applied filter on null training set / weights");
        }
    }
}
