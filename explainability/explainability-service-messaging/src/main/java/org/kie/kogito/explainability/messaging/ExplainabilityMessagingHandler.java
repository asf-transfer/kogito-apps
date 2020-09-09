/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability.messaging;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.kie.kogito.explainability.ExplanationService;
import org.kie.kogito.explainability.PredictionProviderFactory;
import org.kie.kogito.explainability.api.ExplainabilityRequestDto;
import org.kie.kogito.explainability.api.ExplainabilityResultDto;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.models.ExplainabilityRequest;
import org.kie.kogito.tracing.decision.event.CloudEventUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ExplainabilityMessagingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplainabilityMessagingHandler.class);

    private static final URI URI_PRODUCER = URI.create("explainabilityService/ExplainabilityMessagingHandler");

    private final PublishSubject<String> eventSubject = PublishSubject.create();

    protected ExplanationService explanationService;
    protected PredictionProviderFactory predictionProviderFactory;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    public ExplainabilityMessagingHandler(
            ExplanationService explanationService,
            PredictionProviderFactory predictionProviderFactory) {
        this.explanationService = explanationService;
        this.predictionProviderFactory = predictionProviderFactory;
    }

    // Incoming
    @Incoming("trusty-explainability-request")
    public CompletionStage<Void> handleMessage(Message<String> message) {
        try {
            Optional<CloudEvent> cloudEventOpt = decodeCloudEvent(message.getPayload());
            if (!cloudEventOpt.isPresent()) {
                return message.ack();
            }

            CloudEvent cloudEvent = cloudEventOpt.get();
            return handleCloudEvent(cloudEvent)
                    .thenAccept(x -> message.ack());
        } catch (Exception e) {
            LOGGER.error("Something unexpected happened during the processing of an Event. The event is discarded.", e);
        }
        return message.ack();
    }

    private Optional<CloudEvent> decodeCloudEvent(String payload) {
        try {
            return Optional.of(CloudEventUtils.decode(payload));
        } catch (IllegalStateException e) {
            LOGGER.error(String.format("Can't decode message to CloudEvent: %s", payload), e);
            return Optional.empty();
        }
    }

    private CompletionStage<Void> handleCloudEvent(CloudEvent cloudEvent) {
        ExplainabilityRequestDto requestDto;
        try {
            requestDto = objectMapper.readValue(cloudEvent.getData(), ExplainabilityRequestDto.class);
        } catch (IOException e) {
             LOGGER.error("Unable to deserialize CloudEvent data as ExplainabilityRequest", e);
             return CompletableFuture.completedFuture(null);
        }
        if (requestDto == null) {
            LOGGER.error("Received CloudEvent with id {} from {} with empty data", cloudEvent.getId(), cloudEvent.getSource());
            return CompletableFuture.completedFuture(null);
        }

        LOGGER.info("Received CloudEvent with id {} from {}", cloudEvent.getId(), cloudEvent.getSource());

        ExplainabilityRequest request = ExplainabilityRequest.from(requestDto);
        PredictionProvider provider = predictionProviderFactory.createPredictionProvider(request);

        return explanationService
                .explainAsync(request, provider)
                .thenApply(this::sendEvent);
    }

    // Outgoing
    public Void sendEvent(ExplainabilityResultDto result) {
        LOGGER.info("Explainability service emits explainability for execution with ID {}", result.getExecutionId());
        String payload = CloudEventUtils.encode(
                CloudEventUtils.build(result.getExecutionId(),
                                      URI_PRODUCER,
                                      result,
                                      ExplainabilityResultDto.class)
        );
        eventSubject.onNext(payload);
        return null;
    }

    @Outgoing("trusty-explainability-result")
    public Publisher<String> getEventPublisher() {
        return eventSubject.toFlowable(BackpressureStrategy.BUFFER);
    }
}