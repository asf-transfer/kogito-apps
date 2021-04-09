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

import java.util.List;
import java.util.Optional;

/**
 * The output generated by a {@link PredictionProvider}.
 * A prediction output is composed by at least one {@link Output}.
 */
public class PredictionOutput {

    private final List<Output> outputs;

    public PredictionOutput(List<Output> outputs) {
        this.outputs = outputs;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public Optional<Output> getByName(String name) {
        return outputs.stream()
                .filter(output -> name.equalsIgnoreCase(output.getName()))
                .findFirst();
    }
}
