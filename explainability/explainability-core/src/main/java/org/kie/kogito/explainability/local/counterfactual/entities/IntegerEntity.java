/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.local.counterfactual.entities;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Mapping between an integer feature an OptaPlanner {@link PlanningEntity}
 */

@PlanningEntity
public class IntegerEntity implements CounterfactualEntity {
    @PlanningVariable(valueRangeProviderRefs = {"intRange"})
    public Integer value;

    int intRangeMinimum;
    int intRangeMaximum;
    private Feature feature;
    private boolean constrained;

    public IntegerEntity() {
    }

    /**
     * Creates a {@link IntegerEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is constrained or not.
     *
     * @param feature     Original input {@link Feature}
     * @param constrained Whether this entity's value should be fixed or not
     */
    public IntegerEntity(Feature feature, int minimum, int maximum, boolean constrained) {
        this.value = (int) feature.getValue().asNumber();
        this.feature = feature;
        this.intRangeMinimum = minimum;
        this.intRangeMaximum = maximum;
        this.constrained = constrained;
    }

    /**
     * Creates an unconstrained {@link IntegerEntity}, taking the original input value from the
     * provided {@link Feature}.
     *
     * @param feature feature Original input {@link Feature}
     */
    public IntegerEntity(Feature feature, int minimum, int maximum) {
        this(feature, minimum, maximum, false);
    }

    @ValueRangeProvider(id = "intRange")
    public ValueRange getValueRange() {
        return ValueRangeFactory.createIntValueRange(intRangeMinimum, intRangeMaximum);
    }

    @Override
    public String toString() {
        return "IntegerFeature{"
                + "value="
                + value
                + ", intRangeMinimum="
                + intRangeMinimum
                + ", intRangeMaximum="
                + intRangeMaximum
                + ", id='"
                + feature.getName()
                + '\''
                + '}';
    }

    /**
     * Calculates the distance between the current planning value and the reference value
     * for this feature.
     *
     * @return Numerical distance
     */
    @Override
    public double distance() {
        return Math.abs(this.value - (int) this.feature.getValue().asNumber());
    }

    /**
     * Returns the {@link IntegerEntity} as a {@link Feature}
     *
     * @return {@link Feature}
     */
    @Override
    public Feature asFeature() {
        return FeatureFactory.newNumericalFeature(feature.getName(), this.value);
    }


    @Override
    public boolean isConstrained() {
        return constrained;
    }

    /**
     * Returns whether the {@link DoubleEntity} new value is different from the reference
     * {@link Feature} value.
     *
     * @return boolean
     */
    @Override
    public boolean isChanged() {
        return !this.feature.getValue().getUnderlyingObject().equals(this.value);
    }
}
