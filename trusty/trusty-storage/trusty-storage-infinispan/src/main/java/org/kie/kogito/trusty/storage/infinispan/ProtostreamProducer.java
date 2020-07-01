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

package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.MessageMarshaller;

@ApplicationScoped
public class ProtostreamProducer {

    @Inject
    ObjectMapper mapper;

    @Produces
    FileDescriptorSource kogitoTypesDescriptor() throws IOException {
        FileDescriptorSource source = new FileDescriptorSource();
        source.addProtoFile("decision.proto", Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/decision.proto"));
        return source;
    }

    @Produces
    MessageMarshaller decisionModelMarshaller() {
        return new DecisionModelMarshaller(mapper);
    }

    @Produces
    MessageMarshaller decisionOutcomeModelMarshaller() {
        return new DecisionOutcomeModelMarshaller(mapper);
    }

    @Produces
    MessageMarshaller messageExceptionFieldModelMarshaller() {
        return new MessageExceptionFieldModelMarshaller(mapper);
    }

    @Produces
    MessageMarshaller messageModelMarshaller() {
        return new MessageModelMarshaller(mapper);
    }

    @Produces
    MessageMarshaller typedValueModelMarshaller() {
        return new TypedValueModelMarshaller(mapper);
    }
}
