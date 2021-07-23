/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.index.TestUtils;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessInstance;
import org.mockito.junit.jupiter.MockitoExtension;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClientOptions;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.index.TestUtils.readFileContent;
import static org.kie.kogito.index.api.utils.RuntimeServiceWiremock.OK_RESPONSE_WITH_ID;
import static org.kie.kogito.index.api.utils.RuntimeServiceWiremock.PROCESS_ID_FAIL;
import static org.kie.kogito.index.api.utils.RuntimeServiceWiremock.PROCESS_INSTANCE_ID;
import static org.kie.kogito.index.api.utils.RuntimeServiceWiremock.PROCESS_INSTANCE_ID_FAIL;
import static org.kie.kogito.index.api.utils.RuntimeServiceWiremock.RUNTIME_SERVICE_URL;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractDataIndexApiTestIT {

    private static int ACTIVE = 1;
    private static int ABORTED = 4;
    private static int ERROR = 5;

    @Inject
    public Vertx vertx;

    private DataIndexApiImpl client;

    @BeforeEach
    public void setup() {
        client = new DataIndexApiImpl(vertx);
    }

    @Test
    public void testAbortProcessInstance() throws InterruptedException, ExecutionException {
        String serviceUrl = ConfigProvider.getConfig().getValue(RUNTIME_SERVICE_URL, String.class);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);

        CompletableFuture<String> future = client.abortProcessInstance(serviceUrl, pI);
        checkSuccessfulResults(future, format(OK_RESPONSE_WITH_ID, PROCESS_INSTANCE_ID));
        ProcessInstance pI2 = createProcessInstance(PROCESS_INSTANCE_ID, ABORTED);
        future = client.abortProcessInstance(serviceUrl, pI2);
        checkFailedResults(future, "ProcessInstance can't be aborted regarding is not in ACTIVE State");

        ProcessInstance pI3 = createProcessInstance(PROCESS_INSTANCE_ID_FAIL, ACTIVE);
        future = client.abortProcessInstance(serviceUrl, pI3);
        checkFailedResults(future, "FAILED: ABORT ProcessInstance with id: " + PROCESS_INSTANCE_ID_FAIL +
                " errorCode:404 errorStatus:Not Found");
    }

    @Test
    public void testRetryProcessInstance() throws InterruptedException, ExecutionException {
        String serviceUrl = ConfigProvider.getConfig().getValue(RUNTIME_SERVICE_URL, String.class);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);
        CompletableFuture<String> future = client.retryProcessInstance(serviceUrl, pI);
        checkSuccessfulResults(future, format(OK_RESPONSE_WITH_ID, PROCESS_INSTANCE_ID));

        ProcessInstance pI2 = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        future = client.retryProcessInstance(serviceUrl, pI2);
        checkFailedResults(future, "ProcessInstance can't be retried regarding is not in ERROR State");

        ProcessInstance pI3 = createProcessInstance(PROCESS_INSTANCE_ID_FAIL, ERROR);
        future = client.retryProcessInstance(serviceUrl, pI3);
        checkFailedResults(future, "FAILED: RETRY ProcessInstance with id: " + PROCESS_INSTANCE_ID_FAIL +
                " errorCode:404 errorStatus:Not Found");
    }

    @Test
    public void testSkipProcessInstance() throws InterruptedException, ExecutionException {
        String serviceUrl = ConfigProvider.getConfig().getValue(RUNTIME_SERVICE_URL, String.class);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);
        CompletableFuture<String> future = client.retryProcessInstance(serviceUrl, pI);
        checkSuccessfulResults(future, format(OK_RESPONSE_WITH_ID, PROCESS_INSTANCE_ID));

        ProcessInstance pI2 = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        future = client.skipProcessInstance(serviceUrl, pI2);
        checkFailedResults(future, "ProcessInstance can't be skipped regarding is not in ERROR State");

        ProcessInstance pI3 = createProcessInstance(PROCESS_INSTANCE_ID_FAIL, ERROR);
        future = client.skipProcessInstance(serviceUrl, pI3);
        checkFailedResults(future, "FAILED: SKIP ProcessInstance with id: " + PROCESS_INSTANCE_ID_FAIL +
                " errorCode:404 errorStatus:Not Found");
    }

    @Test
    public void testGetProcessInstanceDiagram() throws InterruptedException, ExecutionException {
        String serviceUrl = ConfigProvider.getConfig().getValue(RUNTIME_SERVICE_URL, String.class);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);
        CompletableFuture<String> future = client.getProcessInstanceDiagram(serviceUrl, pI);
        checkSuccessfulResults(future, getTravelsSVGFile());
        ProcessInstance pI2 = createProcessInstance(PROCESS_INSTANCE_ID_FAIL, ERROR);

        future = client.getProcessInstanceDiagram(serviceUrl, pI2);
        checkFailedResults(future, "FAILED: Get Process Instance diagram with id: " + PROCESS_INSTANCE_ID_FAIL +
                " errorCode:404 errorStatus:Not Found");
    }

    @Test
    public void testGetProcessInstanceNodeDefinitions() throws InterruptedException, ExecutionException {
        String serviceUrl = ConfigProvider.getConfig().getValue(RUNTIME_SERVICE_URL, String.class);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ERROR);
        CompletableFuture<List<Node>> future = client.getProcessInstanceNodeDefinitions(serviceUrl, pI);
        assertThat(future.isDone());
        assertThat(future.isCompletedExceptionally()).isFalse();
        assertThat(((Map) ((List) future.get()).get(0)).get("id")).isEqualTo(1);
        assertThat(((Map) ((List) future.get()).get(0)).get("nodeDefinitionId")).isEqualTo("_CCB6F569-A925-4F03-93BA-BD9CAA1843C5");

        ProcessInstance pI2 = createProcessInstance(PROCESS_INSTANCE_ID_FAIL, ERROR);
        pI2.setProcessId(PROCESS_ID_FAIL);
        future = client.getProcessInstanceNodeDefinitions(serviceUrl, pI2);

        checkFailedResults(future, "FAILED: Get Process Instance available nodes with id: " + PROCESS_INSTANCE_ID_FAIL +
                " errorCode:404 errorStatus:Not Found");
    }

    @Test
    public void testWebClientToURLOptions() {
        String defaultHost = "localhost";
        int defaultPort = 8180;
        WebClientOptions webClientOptions = client.getWebClientToURLOptions("http://" + defaultHost + ":" + defaultPort);
        assertThat(webClientOptions.getDefaultHost()).isEqualTo(defaultHost);
        assertThat(webClientOptions.getDefaultPort()).isEqualTo(defaultPort);
    }

    @Test
    public void testWebClientToURLOptionsWithoutPort() {
        String dataIndexUrl = "http://service.com";
        WebClientOptions webClientOptions = client.getWebClientToURLOptions(dataIndexUrl);
        assertThat(webClientOptions.getDefaultPort()).isEqualTo(80);
        assertThat(webClientOptions.getDefaultHost()).isEqualTo("service.com");
        assertFalse(webClientOptions.isSsl());
    }

    @Test
    public void testWebClientToURLOptionsWithoutPortSSL() {
        String dataIndexurl = "https://service.com";
        WebClientOptions webClientOptions = client.getWebClientToURLOptions(dataIndexurl);
        assertThat(webClientOptions.getDefaultPort()).isEqualTo(443);
        assertThat(webClientOptions.getDefaultHost()).isEqualTo("service.com");
        assertTrue(webClientOptions.isSsl());
    }

    @Test
    public void testMalformedURL() {
        assertThat(client.getWebClientToURLOptions("malformedURL")).isNull();
    }

    public String getTravelsSVGFile() {
        try {
            return readFileContent("travels.svg");
        } catch (Exception e) {
            return "Not Found";
        }
    }

    private void checkSuccessfulResults(CompletableFuture future, Object expectedReturnValue) throws ExecutionException, InterruptedException {
        assertThat(future.isDone());
        assertThat(future.isCompletedExceptionally()).isFalse();
        assertThat(future.get().toString()).isEqualTo(expectedReturnValue.toString());
    }

    private void checkFailedResults(CompletableFuture future, String errorMessage) throws InterruptedException {
        assertThat(future.isDone());
        try {
            future.get();
        } catch (ExecutionException e) {
            assertThat(e.getCause().getMessage()).isEqualTo(errorMessage);
        }
        assertThat(future.isCompletedExceptionally()).isTrue();
    }

    private ProcessInstance createProcessInstance(String processInstanceId, int status) {
        return TestUtils.getProcessInstance("travels", processInstanceId, status, null, null);
    }

    protected abstract String getTestProtobufFileContent() throws Exception;
}
