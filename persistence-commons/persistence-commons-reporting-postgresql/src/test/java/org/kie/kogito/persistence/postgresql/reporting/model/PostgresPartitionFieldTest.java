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
package org.kie.kogito.persistence.postgresql.reporting.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PostgresPartitionFieldTest {

    static final PostgresPartitionField PARTITION_FIELD = new PostgresPartitionField("field1", JsonType.STRING, "value1");
    static final int PARTITION_FIELD_HASHCODE = PARTITION_FIELD.hashCode();

    @Test
    void testEquality() {
        assertEquals(PARTITION_FIELD,
                PARTITION_FIELD);
        assertNotEquals(PARTITION_FIELD,
                new PostgresPartitionField("different", JsonType.STRING, "value1"));
        assertNotEquals(PARTITION_FIELD,
                new PostgresPartitionField("field1", JsonType.NUMBER, "value1"));
        assertNotEquals(PARTITION_FIELD,
                new PostgresPartitionField("field1", JsonType.STRING, "different"));
    }

    @Test
    void testHashCode() {
        assertEquals(PARTITION_FIELD_HASHCODE,
                PARTITION_FIELD.hashCode());
        assertNotEquals(PARTITION_FIELD_HASHCODE,
                new PostgresPartitionField("different", JsonType.STRING, "value1").hashCode());
        assertNotEquals(PARTITION_FIELD_HASHCODE,
                new PostgresPartitionField("field1", JsonType.NUMBER, "value1").hashCode());
        assertNotEquals(PARTITION_FIELD_HASHCODE,
                new PostgresPartitionField("field1", JsonType.STRING, "different").hashCode());
    }
}
