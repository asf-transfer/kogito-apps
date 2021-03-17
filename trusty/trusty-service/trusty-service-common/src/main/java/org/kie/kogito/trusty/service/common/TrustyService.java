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

package org.kie.kogito.trusty.service.common;

import java.time.OffsetDateTime;
import java.util.List;

import org.kie.kogito.trusty.service.common.messaging.incoming.ModelIdentifier;
import org.kie.kogito.trusty.service.common.models.MatchedExecutionHeaders;
import org.kie.kogito.trusty.service.common.requests.CounterfactualGoal;
import org.kie.kogito.trusty.service.common.requests.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.CounterfactualRequestResult;
import org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;

/**
 * The trusty service interface.
 * <p>
 * The service exposes the api to CRUD the executions.
 */
public interface TrustyService {

    /**
     * Gets all the headers of the executions that were evaluated within a specified time range.
     *
     * @param from The start datetime.
     * @param to The end datetime.
     * @param limit The maximum (non-negative) number of items to be returned.
     * @param offset The non-negative pagination offset.
     * @param prefix The executionId prefix to be matched in the search.
     * @return The execution headers that satisfy the time range, pagination and prefix conditions and the total number of available results.
     */
    MatchedExecutionHeaders getExecutionHeaders(OffsetDateTime from, OffsetDateTime to, int limit, int offset, String prefix);

    /**
     * Gets a decision by execution ID.
     *
     * @param executionId The execution ID.
     * @return The decision.
     * @throws IllegalArgumentException Throws IllegalArgumentException in case the executionId is not present in the system.
     */
    Decision getDecisionById(String executionId);

    /**
     * Stores a decision.
     *
     * @param executionId The unique execution ID
     * @param decision The decision object.
     * @throws IllegalArgumentException Throws IllegalArgumentException in case the executionId is already present in the system.
     */
    void storeDecision(String executionId, Decision decision);

    /**
     * Updates a decision. If the decision is not present in the storage, then it is created.
     *
     * @param executionId The execution ID
     * @param decision The decision object.
     */
    void updateDecision(String executionId, Decision decision);

    /**
     * Process a decision. Stores the decision and then send an explainability request if it is enabled.
     *
     * @param executionId The execution ID
     * @param serviceUrl The service URL
     * @param decision The decision object.
     */
    void processDecision(String executionId, String serviceUrl, Decision decision);

    /**
     * Gets a explainability result by execution ID.
     *
     * @param executionId The execution ID.
     * @return The explainability result.
     */
    ExplainabilityResult getExplainabilityResultById(String executionId);

    /**
     * Store the explainability result.
     *
     * @param executionId The execution ID.
     */
    void storeExplainabilityResult(String executionId, ExplainabilityResult result);

    /**
     * Stores a Model definition.
     *
     * @param modelIdentifier The model identifier.
     * @param dmnModelWithMetadata The DMNModel to be stored.
     * @throws IllegalArgumentException Throws IllegalArgumentException in case the model is already present in the system.
     */
    void storeModel(ModelIdentifier modelIdentifier, DMNModelWithMetadata dmnModelWithMetadata);

    /**
     * Gets a model by model id.
     *
     * @param modelIdentifier The model identifier.
     * @return The model definition.
     * @throws IllegalArgumentException Throws IllegalArgumentException in case the modelId is not present in the system.
     */
    DMNModelWithMetadata getModelById(ModelIdentifier modelIdentifier);

    /**
     * Requests calculation of the Counterfactuals for an execution.
     *
     * @param executionId The execution ID.
     * @param goals The outputs that are desired from the Counterfactual calculation.
     * @param searchDomains The domains that the Counterfactual calculation can search.
     * @return A wrapper for the calculation request.
     * @throws IllegalArgumentException Throws IllegalArgumentException the executionId is not present in the system.
     */
    CounterfactualRequestResult requestCounterfactuals(String executionId,
            List<CounterfactualGoal> goals,
            List<CounterfactualSearchDomain> searchDomains);
}
