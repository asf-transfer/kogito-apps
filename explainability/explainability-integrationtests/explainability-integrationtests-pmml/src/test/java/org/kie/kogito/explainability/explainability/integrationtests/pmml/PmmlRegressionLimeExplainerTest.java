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
package org.kie.kogito.explainability.explainability.integrationtests.pmml;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;
import org.kie.kogito.explainability.utils.ValidationUtils;
import org.kie.pmml.api.runtime.PMMLRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.kie.pmml.evaluator.assembler.factories.PMMLRuntimeFactoryInternal.getPMMLRuntime;
import static org.kie.test.util.filesystem.FileUtils.getFile;

class PmmlRegressionLimeExplainerTest {

    private static PMMLRuntime logisticRegressionIrisRuntime;

    @BeforeAll
    static void setUpBefore() {
        logisticRegressionIrisRuntime = getPMMLRuntime(getFile("logisticRegressionIrisData.pmml"));
        Config.INSTANCE.setAsyncTimeout(5000);
        Config.INSTANCE.setAsyncTimeUnit(TimeUnit.MILLISECONDS);
    }

    @Test
    void testPMMLRegression() throws Exception {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            LimeConfig limeConfig = new LimeConfig()
                    .withSamples(1000)
                    .withPerturbationContext(new PerturbationContext(random, 1));
            LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
            List<Feature> features = new ArrayList<>();
            features.add(FeatureFactory.newNumericalFeature("sepalLength", 6.9));
            features.add(FeatureFactory.newNumericalFeature("sepalWidth", 3.1));
            features.add(FeatureFactory.newNumericalFeature("petalLength", 5.1));
            features.add(FeatureFactory.newNumericalFeature("petalWidth", 2.3));
            PredictionInput input = new PredictionInput(features);

            PredictionProvider model = inputs -> CompletableFuture.supplyAsync(() -> {
                List<PredictionOutput> outputs = new ArrayList<>();
                for (PredictionInput input1 : inputs) {
                    List<Feature> features1 = input1.getFeatures();
                    LogisticRegressionIrisDataExecutor pmmlModel = new LogisticRegressionIrisDataExecutor(
                            features1.get(0).getValue().asNumber(), features1.get(1).getValue().asNumber(),
                            features1.get(2).getValue().asNumber(), features1.get(3).getValue().asNumber());
                    PMML4Result result = pmmlModel.execute(logisticRegressionIrisRuntime);
                    String species = result.getResultVariables().get("Species").toString();
                    PredictionOutput predictionOutput = new PredictionOutput(List.of(new Output("species", Type.TEXT, new Value<>(species), 1d)));
                    outputs.add(predictionOutput);
                }
                return outputs;
            });
            List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input))
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            assertThat(predictionOutputs).isNotNull();
            assertThat(predictionOutputs).isNotEmpty();
            PredictionOutput output = predictionOutputs.get(0);
            assertThat(output).isNotNull();
            Prediction prediction = new Prediction(input, output);
            Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            for (Saliency saliency : saliencyMap.values()) {
                assertThat(saliency).isNotNull();
                double v = ExplainabilityMetrics.impactScore(model, prediction, saliency.getTopFeatures(2));
                assertThat(v).isEqualTo(1d);
            }
            assertDoesNotThrow(() -> ValidationUtils.validateLocalSaliencyStability(model, prediction, limeExplainer, 1,
                    0.0, 0.0));
        }
    }

}