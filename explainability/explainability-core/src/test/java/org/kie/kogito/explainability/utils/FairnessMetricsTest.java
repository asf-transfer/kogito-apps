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
package org.kie.kogito.explainability.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionInputsDataDistribution;
import org.kie.kogito.explainability.model.PredictionProvider;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FairnessMetricsTest {

    @Test
    void testIndividualConsistencyTextClassifier() throws ExecutionException, InterruptedException {
        BiFunction<PredictionInput, List<PredictionInput>, List<PredictionInput>> proximityFunction = (predictionInput, predictionInputs) -> {
            String reference = DataUtils.textify(predictionInput);
            return predictionInputs.stream().sorted(
                    (o1, o2) -> (StringUtils.getFuzzyDistance(DataUtils.textify(o2), reference, Locale.getDefault())
                            - StringUtils.getFuzzyDistance(DataUtils.textify(o1), reference, Locale.getDefault())))
                    .collect(Collectors.toList()).subList(1, 3);
        };
        List<PredictionInput> testInputs = getTestInputs();
        DataDistribution dataDistribution = new PredictionInputsDataDistribution(testInputs);
        PredictionProvider model = TestUtils.getDummyTextClassifier();
        double individualConsistency = FairnessMetrics.individualConsistency(proximityFunction, dataDistribution, model);
        assertThat(individualConsistency).isBetween(0d, 1d);
    }

    private List<PredictionInput> getTestInputs() {
        List<PredictionInput> inputs = new ArrayList<>();
        Function<String, List<String>> tokenizer = s -> Arrays.asList(s.split(" ").clone());
        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newFulltextFeature("subject", "urgent inquiry", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("text", "please give me some money", tokenizer));
        inputs.add(new PredictionInput(features));
        features = new ArrayList<>();
        features.add(FeatureFactory.newFulltextFeature("subject", "please reply", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("text", "we got urgent matter! please reply", tokenizer));
        inputs.add(new PredictionInput(features));
        features = new ArrayList<>();
        features.add(FeatureFactory.newFulltextFeature("subject", "please reply", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("text", "we got money matter! please reply", tokenizer));
        inputs.add(new PredictionInput(features));
        features = new ArrayList<>();
        features.add(FeatureFactory.newFulltextFeature("subject", "inquiry", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("text", "would you like to get a 100% secure way to invest your money?", tokenizer));
        inputs.add(new PredictionInput(features));
        features = new ArrayList<>();
        features.add(FeatureFactory.newFulltextFeature("subject", "you win", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("text", "you just won an incredible 1M $ prize !", tokenizer));
        inputs.add(new PredictionInput(features));
        features = new ArrayList<>();
        features.add(FeatureFactory.newFulltextFeature("subject", "prize waiting", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("text", "you are the lucky winner of a 100k $ prize", tokenizer));
        inputs.add(new PredictionInput(features));
        features = new ArrayList<>();
        features.add(FeatureFactory.newFulltextFeature("subject", "urgent matter", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("text", "we got an urgent inquiry for you to answer.", tokenizer));
        inputs.add(new PredictionInput(features));
        features = new ArrayList<>();
        features.add(FeatureFactory.newFulltextFeature("subject", "password change", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("text", "you just requested to change your password", tokenizer));
        inputs.add(new PredictionInput(features));
        features = new ArrayList<>();
        features.add(FeatureFactory.newFulltextFeature("subject", "password stolen", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("text", "we stole your password, if you want it back, send some money .", tokenizer));
        inputs.add(new PredictionInput(features));
        return inputs;
    }
}