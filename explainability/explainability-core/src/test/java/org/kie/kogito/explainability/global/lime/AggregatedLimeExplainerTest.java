package org.kie.kogito.explainability.global.lime;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.PredictionProviderMetadata;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.DataUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AggregatedLimeExplainerTest {

    @Test
    void testExplain() throws ExecutionException, InterruptedException {
        PredictionProvider sumSkipModel = TestUtils.getSumSkipModel(1);
        PredictionProviderMetadata metadata = new PredictionProviderMetadata() {
            @Override
            public DataDistribution getDataDistribution() {
                return DataUtils.generateRandomDataDistribution(3, 100, new Random());
            }

            @Override
            public PredictionInput getInputShape() {
                List<Feature> features = new LinkedList<>();
                features.add(FeatureFactory.newNumericalFeature("f0", 0));
                features.add(FeatureFactory.newNumericalFeature("f1", 0));
                features.add(FeatureFactory.newNumericalFeature("f2", 0));
                return new PredictionInput(features);
            }

            @Override
            public PredictionOutput getOutputShape() {
                List<Output> outputs = new LinkedList<>();
                outputs.add(new Output("sum-but1", Type.BOOLEAN, new Value<>(false), 0d));
                return new PredictionOutput(outputs);
            }
        };

        AggregatedLimeExplainer aggregatedLimeExplainer = new AggregatedLimeExplainer(new LimeExplainer(100, 1));
        Map<String, Saliency> explain = aggregatedLimeExplainer.explain(sumSkipModel, metadata);
        assertNotNull(explain);
        assertEquals(1, explain.size());
        assertTrue(explain.containsKey("sum-but1"));
        Saliency saliency = explain.get("sum-but1");
        assertNotNull(saliency);
        List<String> collect = saliency.getPositiveFeatures(2).stream()
                .map(FeatureImportance::getFeature).map(Feature::getName).collect(Collectors.toList());
        assertFalse(collect.contains("f1")); // skipped feature should not appear in top two positive features
    }
}