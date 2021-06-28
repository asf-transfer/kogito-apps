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

package org.kie.kogito.explainability.local.shap;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PredictionInput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapConfigTest {
    Random rn = new Random();
    List<Feature> fs = Arrays.asList(
            FeatureFactory.newNumericalFeature("f", 1.),
            FeatureFactory.newNumericalFeature("f", 2.));
    PredictionInput pi = new PredictionInput(fs);
    List<PredictionInput> pis = Arrays.asList(pi, pi);

    // Test that everything recovers as expected
    @Test
    void testRecovery() {
        ExecutorService executor = ForkJoinPool.commonPool();
        ShapConfig skConfig = ShapConfig.builder()
                .withLink(ShapConfig.LinkType.IDENTITY)
                .withBackground(pis)
                .withRN(rn)
                .withExecutor(executor)
                .withNSamples(100)
                .build();
        assertEquals(ShapConfig.LinkType.IDENTITY, skConfig.getLink());
        assertTrue(skConfig.getNSamples().isPresent());
        assertEquals(100, skConfig.getNSamples().get());
        assertSame(rn, skConfig.getRN());
        assertSame(executor, skConfig.getExecutor());
        assertSame(pis, skConfig.getBackground());
    }

    // Test that the default arguments recover as expected
    @Test
    void testNullRecovery() {
        ShapConfig skConfig = ShapConfig.builder()
                .withLink(ShapConfig.LinkType.IDENTITY)
                .withBackground(pis)
                .build();
        assertEquals(ShapConfig.LinkType.LOGIT, skConfig.getLink());
        assertFalse(skConfig.getNSamples().isPresent());
        assertSame(rn, skConfig.getRN());
        assertSame(pis, skConfig.getBackground());
        assertSame(SecureRandom.class, skConfig.getRN().getClass());
        assertSame(ForkJoinPool.commonPool(), skConfig.getExecutor());
        assertFalse(skConfig.getNSamples().isPresent());
    }

    // Test that not setting mandatory arguments throws errors
    @Test
    void testMandatoryErrors() {
        ShapConfig.Builder linkNoBG = ShapConfig.builder().withLink(ShapConfig.LinkType.IDENTITY);
        ShapConfig.Builder bgNoLink = ShapConfig.builder().withBackground(pis);
        assertThrows(IllegalArgumentException.class, linkNoBG::build);
        assertThrows(IllegalArgumentException.class, bgNoLink::build);

    }
}
