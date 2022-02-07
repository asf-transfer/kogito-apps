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
package org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostgresContextTest {

    @Test
    void testPostgresContext() {
        final PostgresField field = new PostgresField("field1",
                JsonType.STRING);
        final PostgresPartitionField partitionField = new PostgresPartitionField("partitionField1",
                JsonType.STRING,
                "value");
        final PostgresMapping mapping = new PostgresMapping("sourceJsonPath",
                new PostgresField("targetField1",
                        JsonType.STRING));
        final PathSegment pathSegment = new PathSegment("segment",
                null);

        final PostgresContext context = new PostgresContext("mappingId",
                "sourceTableName",
                "sourceTableJsonFieldName",
                List.of(field),
                List.of(partitionField),
                "targetTableName",
                List.of(mapping),
                List.of(pathSegment));

        assertEquals("mappingId",
                context.getMappingId());
        assertEquals("sourceTableName",
                context.getSourceTableName());
        assertEquals("sourceTableJsonFieldName",
                context.getSourceTableJsonFieldName());
        assertEquals(List.of(field),
                context.getSourceTableIdentityFields());
        assertEquals(List.of(partitionField),
                context.getSourceTablePartitionFields());
        assertEquals("targetTableName",
                context.getTargetTableName());
        assertEquals(List.of(mapping),
                context.getFieldMappings());
        assertEquals(List.of(pathSegment),
                context.getMappingPaths());
    }
}
