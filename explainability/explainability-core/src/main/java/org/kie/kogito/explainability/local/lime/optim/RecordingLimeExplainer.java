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
package org.kie.kogito.explainability.local.lime.optim;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link LimeExplainer} wrapper that, while producing a {@link Saliency} explanation, records a fixed amount
 * of {@link Prediction}s.
 * <p>
 * {@link RecordingLimeExplainer} leverages a blocking queue with a fixed capacity. If the no. of {@link Prediction}s in
 * the queue reaches the capacity limit, inserting a new {@link Prediction} will cause the first item in the queue to
 * be evicted.
 */
public class RecordingLimeExplainer extends LimeExplainer {

    private final static Logger LOGGER = LoggerFactory.getLogger(RecordingLimeExplainer.class);

    private final Queue<Prediction> recordedPredictions;

    public RecordingLimeExplainer(int capacity) {
        this(new LimeConfig(), capacity);
    }

    public RecordingLimeExplainer(LimeConfig limeConfig, int capacity) {
        super(limeConfig);
        recordedPredictions = new FixedSizeConcurrentLinkedDeque<>(capacity);
    }

    @Override
    public CompletableFuture<Map<String, Saliency>> explainAsync(Prediction prediction, PredictionProvider model) {
        if (!recordedPredictions.offer(prediction) && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Prediction {} not recorded", prediction);
        }
        return super.explainAsync(prediction, model);
    }

    public List<Prediction> getRecordedPredictions() {
        Prediction[] a = recordedPredictions.toArray(new Prediction[0]);
        return Collections.unmodifiableList(Arrays.asList(a));
    }

    static class FixedSizeConcurrentLinkedDeque<T> extends ConcurrentLinkedDeque<T> {

        private final int capacity;

        FixedSizeConcurrentLinkedDeque(int capacity) {
            this.capacity = capacity;
        }

        @Override
        public boolean offer(T o) {
            return !contains(o) && (super.offer(o) && (size() <= capacity || super.pop() != null));
        }
    }
}
