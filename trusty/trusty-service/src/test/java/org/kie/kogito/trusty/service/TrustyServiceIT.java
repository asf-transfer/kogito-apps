package org.kie.kogito.trusty.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.storage.api.TrustyStorageService;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.Execution;

@QuarkusTest
@QuarkusTestResource(TrustyInfinispanServerTestResource.class)
public class TrustyServiceIT {

    @Inject
    TrustyService trustyService;

    @Inject
    TrustyStorageService trustyStorageService;

    @BeforeEach
    public void setup() {
        trustyStorageService.getDecisionsStorage().clear();
    }

    @Test
    public void testStoreAndRetrieveExecution() {
        storeExecution("myExecution", 1591692958000L);

        OffsetDateTime from = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1591692957000L), ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1591692959000L), ZoneOffset.UTC);
        List<Execution> result = trustyService.getExecutionHeaders(from, to, 100, 0, "");
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("myExecution", result.get(0).getExecutionId());
    }

    @Test
    public void GivenTwoExecutions_WhenTheQueryExcludesOneExecution_ThenOnlyOneExecutionIsReturned() {
        storeExecution("myExecution", 1591692950000L);
        storeExecution("executionId2", 1591692958000L);

        OffsetDateTime from = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1591692940000L), ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1591692955000L), ZoneOffset.UTC);
        List<Execution> result = trustyService.getExecutionHeaders(from, to, 100, 0, "");
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("myExecution", result.get(0).getExecutionId());
    }

    @Test
    public void GivenTwoExecutions_WhenThePrefixIsUsed_ThenOnlyOneExecutionIsReturned() {
        storeExecution("myExecution", 1591692950000L);
        storeExecution("executionId2", 1591692958000L);

        OffsetDateTime from = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1591692940000L), ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1591692959000L), ZoneOffset.UTC);
        List<Execution> result = trustyService.getExecutionHeaders(from, to, 100, 0, "my");
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("myExecution", result.get(0).getExecutionId());
    }

    @Test
    public void GivenAnExecution_WhenGetDecisionByIdIsCalled_ThenTheExecutionIsReturned() {
        String executionId = "myExecution";
        storeExecution(executionId, 1591692950000L);

        Decision result = trustyService.getDecisionById(executionId);
        Assertions.assertEquals(executionId, result.getExecutionId());
    }

    @Test
    public void GivenADuplicatedDecision_WhenTheDecisionIsStored_ThenAnExceptionIsRaised() {
        String executionId = "myExecution";
        storeExecution(executionId, 1591692950000L);
        Assertions.assertThrows(IllegalArgumentException.class, () -> storeExecution(executionId, 1591692950000L));
    }

    @Test
    public void GivenNoExecutions_WhenADecisionIsRetrieved_ThenAnExceptionIsRaised() {
        String executionId = "myExecution";
        Assertions.assertThrows(IllegalArgumentException.class, () -> trustyService.getDecisionById(executionId));
    }

    private Decision storeExecution(String executionId, Long timestamp) {
        Decision decision = new Decision();
        decision.setExecutionId(executionId);
        decision.setExecutionTimestamp(timestamp);
        trustyService.storeDecision(decision.getExecutionId(), decision);
        return decision;
    }
}