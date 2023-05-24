/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.service.messaging;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceEventBody;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceEventBody;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.MessageConverter;
import io.vertx.core.buffer.Buffer;

import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

/**
 * Converts the message payload into an indexable object. The conversion takes into account that the
 * message can be coded in the structured or binary format.
 */
@ApplicationScoped
public class KogitoIndexEventConverter implements MessageConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoIndexEventConverter.class);

    @Override
    public boolean canConvert(Message<?> message, Type type) {
        return isIndexable(type) &&
                (message.getPayload() instanceof Buffer);
    }

    private boolean isIndexable(Type type) {
        return type == ProcessInstanceDataEvent.class
                || type == UserTaskInstanceDataEvent.class
                || type == KogitoJobCloudEvent.class;
    }

    @Override
    public Message<?> convert(Message<?> message, Type type) {
        try {
            if (type.getTypeName().equals(ProcessInstanceDataEvent.class.getTypeName())) {
                ProcessInstanceDataEvent processInstanceDataEvent = new ProcessInstanceDataEvent();
                processInstanceDataEvent.setData(getObjectMapper().readValue(message.getPayload().toString(), ProcessInstanceEventBody.class));
                return message.withPayload(processInstanceDataEvent);
            } else if (type.getTypeName().equals(KogitoJobCloudEvent.class.getTypeName())) {
                return message.withPayload(KogitoJobCloudEvent.builder().data(getObjectMapper().readValue(message.getPayload().toString(), Job.class)).build());
            } else if (type.getTypeName().equals(UserTaskInstanceDataEvent.class.getTypeName())) {
                UserTaskInstanceDataEvent userTaskInstanceDataEvent = new UserTaskInstanceDataEvent();
                userTaskInstanceDataEvent.setData(getObjectMapper().readValue(message.getPayload().toString(), UserTaskInstanceEventBody.class));
                return message.withPayload(userTaskInstanceDataEvent);
            }
            return message;
        } catch (IOException e) {
            LOGGER.error("Error converting message payload to " + type.getTypeName(), e);
            throw new RuntimeException(e);
        }
    }
}
