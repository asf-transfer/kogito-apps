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
package org.kie.kogito.explainability.local.lime;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Objects;

import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.EncodingParams;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.PredictionInputsDataDistribution;

/**
 * Lime explainer configuration parameters.
 */
public class LimeConfig {

    private static final int DEFAULT_NO_OF_SAMPLES = 300;
    private static final double DEFAULT_SEPARABLE_DATASET_RATIO = 0.9;
    public static final int DEFAULT_NO_OF_RETRIES = 3;
    private static final boolean DEFAULT_ADAPT_DATASET_VARIANCE = true;
    private static final boolean DEFAULT_PENALIZE_BALANCE_SPARSE = true;
    private static final boolean DEFAULT_PROXIMITY_FILTER = true;
    private static final double DEFAULT_PROXIMITY_THRESHOLD = 0.83;
    private static final Number DEFAULT_PROXIMITY_FILTERED_DATASET_MIN = 10;
    private static final double DEFAULT_PROXIMITY_KERNEL_WIDTH = 0.5;
    private static final double DEFAULT_ENCODING_CLUSTER_THRESHOLD = 0.07;
    private static final double DEFAULT_ENCODING_GAUSSIAN_FILTER_WIDTH = 0.07;
    private static final boolean DEFAULT_NORMALIZE_WEIGHTS = false;
    private static final boolean DEFAULT_HIGH_SCORE_ZONES = true;
    private static final int DEFAULT_BOOSTRAP_INPUTS = 50;
    private static final boolean DEFAULT_FEATURE_SELECTION = true;
    private static final int DEFAULT_NO_OF_FEATURES = 6;

    private double separableDatasetRatio = DEFAULT_SEPARABLE_DATASET_RATIO;

    /**
     * No. of samples to be generated for the local linear model training.
     */
    private int noOfSamples = DEFAULT_NO_OF_SAMPLES;

    /**
     * No. of retries while trying to find a (linearly) separable dataset.
     */
    private int noOfRetries = DEFAULT_NO_OF_RETRIES;

    /**
     * Context object for perturbing features.
     */
    private PerturbationContext perturbationContext = new PerturbationContext(new SecureRandom(), 1);

    /**
     * Whether the explainer should adapt the variance in the generated (perturbed) data when it's not separable.
     */
    private boolean adaptDatasetVariance = DEFAULT_ADAPT_DATASET_VARIANCE;

    private DataDistribution dataDistribution = new PredictionInputsDataDistribution(Collections.emptyList());

    /**
     * Whether to use high score feature zones for more accurate numeric features sampling
     */
    private boolean highScoreFeatureZones = DEFAULT_HIGH_SCORE_ZONES;

    /**
     * Whether to operate feature selection
     */
    private boolean featureSelection = DEFAULT_FEATURE_SELECTION;

    public LimeConfig withFeatureSelection(boolean featureSelection) {
        this.featureSelection = featureSelection;
        return this;
    }

    public boolean isFeatureSelection() {
        return featureSelection;
    }

    /**
     * Number of features to use
     */
    private int noOfFeatures = DEFAULT_NO_OF_FEATURES;

    public LimeConfig withNoOfFeatures(int noOfFeatures) {
        this.noOfFeatures = noOfFeatures;
        return this;
    }

    public int getNoOfFeatures() {
        return noOfFeatures;
    }

    public LimeConfig withDataDistribution(DataDistribution dataDistribution) {
        this.dataDistribution = dataDistribution;
        return this;
    }

    /**
     * Whether to penalize weights whose sparse features encoding is balanced with respect to target output
     */
    private boolean penalizeBalanceSparse = DEFAULT_PENALIZE_BALANCE_SPARSE;

    /**
     * Whether to prefer filtering by proximity over weighting by proximity when generating samples for the linear model.
     */
    private boolean proximityFilter = DEFAULT_PROXIMITY_FILTER;

    /**
     * The proximity threshold used to filter samples when {@code proximityFilter == true}.
     */
    private double proximityThreshold = DEFAULT_PROXIMITY_THRESHOLD;

    /**
     * Minimum "cut" from the original sparse encoded dataset required in order to apply the proximity filter.
     * It this is an {@code int} then it would be used as hard minimum number of samples, if it's a {@code double}
     * (it has to be in the range {@code (0, 1)}, otherwise it will be ignored) it will be used as minimum percentage
     * from the original sparse encoded dataset.
     */
    private Number proximityFilteredDatasetMinimum = DEFAULT_PROXIMITY_FILTERED_DATASET_MIN;

    /**
     * The width of the kernel used to calculate proximity of sparse vector instances.
     */
    private double proximityKernelWidth = DEFAULT_PROXIMITY_KERNEL_WIDTH;

    /**
     * {@link EncodingParams} used to perform sparse encoding for LIME.
     */
    private EncodingParams encodingParams = new EncodingParams(DEFAULT_ENCODING_GAUSSIAN_FILTER_WIDTH,
            DEFAULT_ENCODING_CLUSTER_THRESHOLD);

    /**
     * Whether to normalize weights generated by LIME or not.
     */
    private boolean normalizeWeights = DEFAULT_NORMALIZE_WEIGHTS;

    /**
     * Max number of inputs used to bootstrap numeric features
     */
    private int boostrapInputs = DEFAULT_BOOSTRAP_INPUTS;

    public LimeConfig withSeparableDatasetRatio(double separableDatasetRatio) {
        this.separableDatasetRatio = separableDatasetRatio;
        return this;
    }

    public LimeConfig withPerturbationContext(PerturbationContext perturbationContext) {
        this.perturbationContext = perturbationContext;
        return this;
    }

    public LimeConfig withAdaptiveVariance(boolean adaptDatasetVariance) {
        this.adaptDatasetVariance = adaptDatasetVariance;
        return this;
    }

    public LimeConfig withPenalizeBalanceSparse(boolean penalizeBalanceSparse) {
        this.penalizeBalanceSparse = penalizeBalanceSparse;
        return this;
    }

    public LimeConfig withRetries(int noOfRetries) {
        this.noOfRetries = noOfRetries;
        return this;
    }

    public LimeConfig withSamples(int noOfSamples) {
        this.noOfSamples = noOfSamples;
        return this;
    }

    public LimeConfig withProximityFilter(boolean proximityFilter) {
        this.proximityFilter = proximityFilter;
        return this;
    }

    public LimeConfig withProximityThreshold(double proximityThreshold) {
        this.proximityThreshold = proximityThreshold;
        return this;
    }

    public LimeConfig withProximityKernelWidth(double proximityKernelWidth) {
        this.proximityKernelWidth = proximityKernelWidth;
        return this;
    }

    public int getNoOfRetries() {
        return noOfRetries;
    }

    public int getNoOfSamples() {
        return noOfSamples;
    }

    public PerturbationContext getPerturbationContext() {
        return perturbationContext;
    }

    public boolean isAdaptDatasetVariance() {
        return adaptDatasetVariance;
    }

    public double getSeparableDatasetRatio() {
        return separableDatasetRatio;
    }

    public boolean isPenalizeBalanceSparse() {
        return penalizeBalanceSparse;
    }

    public boolean isProximityFilter() {
        return proximityFilter;
    }

    public double getProximityThreshold() {
        return proximityThreshold;
    }

    public Number getProximityFilteredDatasetMinimum() {
        return proximityFilteredDatasetMinimum;
    }

    public LimeConfig withProximityFilteredDatasetMinimum(Number proximityFilteredDatasetMinimum) {
        this.proximityFilteredDatasetMinimum = proximityFilteredDatasetMinimum;
        return this;
    }

    public double getProximityKernelWidth() {
        return proximityKernelWidth;
    }

    public EncodingParams getEncodingParams() {
        return encodingParams;
    }

    public LimeConfig withEncodingParams(EncodingParams encodingParams) {
        this.encodingParams = encodingParams;
        return this;
    }

    public LimeConfig withNormalizeWeights(boolean normalizeWeights) {
        this.normalizeWeights = normalizeWeights;
        return this;
    }

    public boolean isNormalizeWeights() {
        return normalizeWeights;
    }

    public DataDistribution getDataDistribution() {
        return dataDistribution;
    }

    public LimeConfig withHighScoreFeatureZones(boolean highScoreFeatureZones) {
        this.highScoreFeatureZones = highScoreFeatureZones;
        return this;
    }

    public int getBoostrapInputs() {
        return boostrapInputs;
    }

    public boolean isHighScoreFeatureZones() {
        return highScoreFeatureZones;
    }

    public LimeConfig witBootstrapInputs(int boostrapInputs) {
        this.boostrapInputs = boostrapInputs;
        return this;
    }

    public LimeConfig copy() {
        return new LimeConfig()
                .withSeparableDatasetRatio(separableDatasetRatio)
                .withSamples(noOfSamples)
                .withRetries(noOfRetries)
                .withPerturbationContext(perturbationContext)
                .withAdaptiveVariance(adaptDatasetVariance)
                .withDataDistribution(dataDistribution)
                .withPenalizeBalanceSparse(penalizeBalanceSparse)
                .withProximityFilter(proximityFilter)
                .withProximityThreshold(proximityThreshold)
                .withProximityFilteredDatasetMinimum(proximityFilteredDatasetMinimum)
                .withProximityKernelWidth(proximityKernelWidth)
                .withEncodingParams(encodingParams)
                .withNormalizeWeights(normalizeWeights);
    }

    @Override
    public String toString() {
        return "LimeConfig{" +
                "separableDatasetRatio=" + separableDatasetRatio +
                ", noOfSamples=" + noOfSamples +
                ", noOfRetries=" + noOfRetries +
                ", perturbationContext=" + perturbationContext +
                ", adaptDatasetVariance=" + adaptDatasetVariance +
                ", dataDistribution=" + dataDistribution +
                ", penalizeBalanceSparse=" + penalizeBalanceSparse +
                ", proximityFilter=" + proximityFilter +
                ", proximityThreshold=" + proximityThreshold +
                ", proximityFilteredDatasetMinimum=" + proximityFilteredDatasetMinimum +
                ", proximityKernelWidth=" + proximityKernelWidth +
                ", encodingParams=" + encodingParams +
                ", normalizeWeights=" + normalizeWeights +
                ", highScoreFeatureZones=" + highScoreFeatureZones +
                ", featureSelection=" + featureSelection +
                ", noOfFeatures=" + noOfFeatures +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LimeConfig that = (LimeConfig) o;
        return Double.compare(that.separableDatasetRatio, separableDatasetRatio) == 0 && noOfSamples == that.noOfSamples && noOfRetries == that.noOfRetries
                && adaptDatasetVariance == that.adaptDatasetVariance && highScoreFeatureZones == that.highScoreFeatureZones && penalizeBalanceSparse == that.penalizeBalanceSparse
                && proximityFilter == that.proximityFilter && Double.compare(that.proximityThreshold, proximityThreshold) == 0 && Double.compare(that.proximityKernelWidth, proximityKernelWidth) == 0
                && normalizeWeights == that.normalizeWeights && boostrapInputs == that.boostrapInputs && Objects.equals(perturbationContext, that.perturbationContext)
                && Objects.equals(dataDistribution, that.dataDistribution) && Objects.equals(proximityFilteredDatasetMinimum, that.proximityFilteredDatasetMinimum)
                && Objects.equals(encodingParams, that.encodingParams) && featureSelection == that.featureSelection && noOfFeatures == that.noOfFeatures;
    }

    @Override
    public int hashCode() {
        return Objects.hash(separableDatasetRatio, noOfSamples, noOfRetries, perturbationContext, adaptDatasetVariance, dataDistribution, highScoreFeatureZones, penalizeBalanceSparse, proximityFilter,
                proximityThreshold, proximityFilteredDatasetMinimum, proximityKernelWidth, encodingParams, normalizeWeights, boostrapInputs, featureSelection, noOfFeatures);
    }
}
