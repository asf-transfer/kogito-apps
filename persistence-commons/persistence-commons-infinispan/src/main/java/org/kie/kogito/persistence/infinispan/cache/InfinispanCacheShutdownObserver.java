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

package org.kie.kogito.persistence.infinispan.cache;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.persistence.api.StorageService;

import io.quarkus.runtime.ShutdownEvent;

import static org.kie.kogito.persistence.api.factory.Constants.PERSISTENCE_TYPE_PROPERTY;
import static org.kie.kogito.persistence.infinispan.Constants.INFINISPAN_STORAGE;

@ApplicationScoped
public class InfinispanCacheShutdownObserver {

    @ConfigProperty(name = PERSISTENCE_TYPE_PROPERTY)
    Optional<String> storageType;

    @Inject
    StorageService cacheService;

    public void stop(@Observes ShutdownEvent event) {
        if (storageType.isPresent() && INFINISPAN_STORAGE.equals(storageType.get()) && cacheService instanceof InfinispanStorageService) {
            ((InfinispanStorageService) cacheService).destroy();
        }
    }
}
