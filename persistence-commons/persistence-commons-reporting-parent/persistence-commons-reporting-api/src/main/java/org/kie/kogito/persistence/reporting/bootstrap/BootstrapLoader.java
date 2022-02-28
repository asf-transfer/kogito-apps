/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.persistence.reporting.bootstrap;

import java.util.Optional;

import org.kie.kogito.persistence.reporting.model.Field;
import org.kie.kogito.persistence.reporting.model.Mapping;
import org.kie.kogito.persistence.reporting.model.MappingDefinition;
import org.kie.kogito.persistence.reporting.model.MappingDefinitions;
import org.kie.kogito.persistence.reporting.model.PartitionField;

public interface BootstrapLoader<T, F extends Field<T>, P extends PartitionField<T>, M extends Mapping<T, F>, D extends MappingDefinition<T, F, P, M>, S extends MappingDefinitions<T, F, P, M, D>> {

    /**
     * Loads the Mapping Definitions present at start-up in "bootstrap.json".
     *
     * @return Mapping Definitions or empty.
     */
    Optional<S> load();

    /**
     * Returns the type of mapping definitions.
     * 
     * @return
     */
    Class<S> getMappingDefinitionsType();
}
