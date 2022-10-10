/*
 *  Copyright 10/10/22, 12:32 PM Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.jobs.service.repository.postgresql;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.model.JobServiceManagementInfo;
import org.kie.kogito.jobs.service.repository.JobServiceManagementRepository;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class PostgreSqlJobServiceManagementRepositoryTest {

    @Inject
    JobServiceManagementRepository tested;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetAndUpdate() {
        String id = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        create(id, token);

        AtomicReference<OffsetDateTime> date = new AtomicReference<>();
        JobServiceManagementInfo updated = tested.getAndUpdate(id, info -> {
            date.set(OffsetDateTime.now());
            info.setLastHeartbeat(date.get());
            return info;
        }).await().indefinitely();
        assertThat(updated.getId()).isEqualTo(id);
        assertThat(date.get()).isNotNull();
        assertThat(updated.getLastHeartbeat()).isEqualTo(date.get());
        assertThat(updated.getToken()).isEqualTo(token);
    }

    @Test
    void testGetAndUpdateNotExisting() {
        String id = UUID.randomUUID().toString();

        AtomicReference<JobServiceManagementInfo> found = new AtomicReference<>(new JobServiceManagementInfo());
        JobServiceManagementInfo updated = tested.getAndUpdate(id, info -> {
            found.set(info);
            return info;
        }).await().indefinitely();
        assertThat(updated).isNull();
        assertThat(found.get()).isNull();
    }

    private JobServiceManagementInfo create(String id, String token) {
        JobServiceManagementInfo created = tested.set(new JobServiceManagementInfo(id, token, null)).await().indefinitely();
        assertThat(created.getId()).isEqualTo(id);
        assertThat(created.getToken()).isEqualTo(token);
        assertThat(created.getLastHeartbeat()).isNull();
        return created;
    }

    @Test
    void testHeartbeat() {
        String id = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        ;
        JobServiceManagementInfo created = create(id, token);

        JobServiceManagementInfo updated = tested.heartbeat(created).await().indefinitely();
        assertThat(updated.getLastHeartbeat()).isNotNull();
        assertThat(updated.getLastHeartbeat()).isBefore(OffsetDateTime.now().plusSeconds(1));
    }

    @Test
    void testConflictHeartbeat() {
        String id = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();
        ;
        create(id, token);

        JobServiceManagementInfo updated = tested.heartbeat(new JobServiceManagementInfo(id, "differentToken", null)).await().indefinitely();
        assertThat(updated).isNull();
    }
}