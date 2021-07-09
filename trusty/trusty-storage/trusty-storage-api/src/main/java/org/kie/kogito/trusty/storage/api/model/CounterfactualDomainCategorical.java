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
package org.kie.kogito.trusty.storage.api.model;

import java.util.Collection;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CounterfactualDomainCategorical extends CounterfactualDomain {

    public static final String TYPE = "CATEGORICAL";
    public static final String CATEGORIES = "categories";

    @JsonProperty(CATEGORIES)
    @NotNull(message = "categories object must be provided.")
    private Collection<JsonNode> categories;

    public CounterfactualDomainCategorical() {
    }

    public CounterfactualDomainCategorical(@NotNull Collection<JsonNode> categories) {
        this.categories = Objects.requireNonNull(categories);
    }

    @JsonIgnore
    public Collection<JsonNode> getCategories() {
        return this.categories;
    }

    //-------------
    // Test methods
    //-------------

    @JsonIgnore
    public void setCategories(Collection<JsonNode> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "DomainCategorical{" +
                "categories=" + categories +
                "}";
    }
}
