/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.jobs.embedded;

import jakarta.enterprise.context.ApplicationScoped;

import org.kie.kogito.Application;
import org.kie.kogito.Config;
import org.kie.kogito.KogitoEngine;
import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.mockito.Mockito;

@ApplicationScoped
public class TestApplication implements Application {

    @Override
    public Config config() {
        return Mockito.mock(Config.class);
    }

    @Override
    public <T extends KogitoEngine> T get(Class<T> clazz) {
        return (T) Mockito.mock(KogitoEngine.class);
    }

    @Override
    public UnitOfWorkManager unitOfWorkManager() {
        UnitOfWorkManager uowm = Mockito.mock(UnitOfWorkManager.class);
        Mockito.when(uowm.newUnitOfWork()).thenReturn(Mockito.mock(UnitOfWork.class));
        return uowm;
    }

}
