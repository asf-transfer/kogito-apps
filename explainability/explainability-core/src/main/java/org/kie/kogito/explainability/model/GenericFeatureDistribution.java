/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Feature distribution based on list of {@code Values}.
 */
public class GenericFeatureDistribution implements FeatureDistribution {

    private final Logger LOGGER = LoggerFactory.getLogger(GenericFeatureDistribution.class);

    private final Feature feature;
    private final List<Value<?>> values;

    public GenericFeatureDistribution(Feature feature, List<Value<?>> values) {
        this.feature = feature;
        this.values = Collections.unmodifiableList(values);
    }

    @Override
    public Feature getFeature() {
        return feature;
    }

    @Override
    public Value<?> sample() {
        return sample(1).get(0);
    }

    @Override
    public List<Value<?>> sample(int sampleSize) {
        if (sampleSize >= values.size()) {
            LOGGER.warn("required {} samples, but only {} are available", sampleSize, values.size());
            return getAllSamples();
        } else {
            List<Value<?>> copy = new java.util.ArrayList<>(values);
            Collections.shuffle(copy);
            List<Value<?>> samples = new ArrayList<>(sampleSize);
            for (int i = 0; i < sampleSize; i++) {
                samples.add(copy.get(i));
            }
            return samples;
        }
    }

    @Override
    public List<Value<?>> getAllSamples() {
        return values;
    }
}
