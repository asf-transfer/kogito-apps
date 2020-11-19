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
package org.kie.kogito.explainability.global;

import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.PredictionProviderMetadata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * A global explainability method.
 *
 * @param <T> the type of global explanation generated
 */
public interface GlobalExplainer<T> {

    /**
     * Generate a global explanation on a given model, using existing metadata about the model.
     *
     * @param model    the model to explain
     * @param metadata information about the model
     * @return a global explanation
     */
    T explain(PredictionProvider model, PredictionProviderMetadata metadata) throws InterruptedException, ExecutionException, TimeoutException;
}
