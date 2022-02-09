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
package org.kie.kogito.persistence.reporting.database.sqlbuilders;

import java.util.List;

import org.kie.kogito.persistence.reporting.model.Field;
import org.kie.kogito.persistence.reporting.model.Mapping;
import org.kie.kogito.persistence.reporting.model.MappingDefinition;
import org.kie.kogito.persistence.reporting.model.PartitionField;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;

public interface Context<T, F extends Field<T>, P extends PartitionField<T>, M extends Mapping<T, F>> extends MappingDefinition<T, F, P, M> {

    List<PathSegment> getMappingPaths();
}
