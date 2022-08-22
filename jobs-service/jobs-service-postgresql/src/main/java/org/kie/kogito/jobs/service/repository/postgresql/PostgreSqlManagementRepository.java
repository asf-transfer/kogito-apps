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
package org.kie.kogito.jobs.service.repository.postgresql;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.kie.kogito.jobs.service.model.JobServiceManagementInfo;
import org.kie.kogito.jobs.service.repository.JobServiceManagementRepository;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.context.api.ThreadContextConfig;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;

@ApplicationScoped
public class PostgreSqlManagementRepository implements JobServiceManagementRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlManagementRepository.class);
    private PgPool client;

    @Inject
    @ThreadContextConfig
    ThreadContext sharedConfiguredThreadContext;

    @Inject
    ManagedExecutor managedExecutor;
    private Supplier<SqlConnection> connectionSupplier;

    @Inject
    public PostgreSqlManagementRepository(PgPool client) {
        this.client = client;
    }

    public Uni<JobServiceManagementInfo> getAndUpdate(String id, Function<JobServiceManagementInfo, JobServiceManagementInfo> computeUpdate) {
        LOGGER.info("get {}", id);
        return client.withTransaction(conn -> conn
                .preparedQuery("SELECT id, token, last_heartbeat FROM job_service_management WHERE id = $1 FOR UPDATE ")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null)
                .onItem().invoke(r -> LOGGER.info("got {}", r))
                .onItem().transformToUni(r -> update(conn, computeUpdate.apply(r))));
    }

    JobServiceManagementInfo from(Row row) {
        return new JobServiceManagementInfo(row.getString("id"), row.getString("token"),
                Optional.ofNullable(row.getOffsetDateTime("last_heartbeat")).map(t -> t.atZoneSameInstant(DateUtil.DEFAULT_ZONE)).orElse(null));
    }

    @Override
    public Uni<JobServiceManagementInfo> get(String id) {
        LOGGER.info("get {}", id);
        return client.preparedQuery("SELECT id, token, last_heartbeat FROM job_service_management FOR UPDATE")
                .execute()
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : new JobServiceManagementInfo(null, null, null))
                .onItem().invoke(r -> LOGGER.info("got {}", r));
    }

    @Override
    public Uni<JobServiceManagementInfo> set(JobServiceManagementInfo info) {
        LOGGER.info("set {}", info);
        return update(client, info);
    }

    private Uni<JobServiceManagementInfo> update(SqlClient conn, JobServiceManagementInfo info) {
        if (Objects.isNull(info)) {
            return Uni.createFrom().nullItem();
        }
        return conn.preparedQuery("INSERT INTO job_service_management (id, token, last_heartbeat) " +
                "VALUES ($1, $2, $3) " +
                "ON CONFLICT (id) DO " +
                "UPDATE SET token = $2, last_heartbeat = $3 " +
                "RETURNING id, token, last_heartbeat")
                .execute(Tuple.tuple(Stream.of(
                        Optional.ofNullable(info.getId()).map(UUID::toString).orElse(null),
                        Optional.ofNullable(info.getToken()).map(UUID::toString).orElse(null),
                        Optional.ofNullable(info.getLastHeartbeat()).map(ZonedDateTime::toOffsetDateTime).orElse(null)).collect(Collectors.toList())))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null)
                .onItem().ignore().andContinueWith(new JobServiceManagementInfo(null, null, null));
    }

    @Override
    public Uni<JobServiceManagementInfo> heartbeat(JobServiceManagementInfo info) {
        info.setLastHeartbeat(DateUtil.now());
        return set(info);
    }
}
