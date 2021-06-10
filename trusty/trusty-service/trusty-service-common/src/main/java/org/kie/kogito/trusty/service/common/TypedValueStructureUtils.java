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
package org.kie.kogito.trusty.service.common;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

public class TypedValueStructureUtils {

    private TypedValueStructureUtils() {
        //Prevent instantiation of this utility class
    }

    private interface Check<D extends TypedVariable<D>, C extends TypedVariable<C>> {

        boolean check(Collection<D> structure1,
                Collection<C> structure2);
    }

    private static abstract class BaseCheck<D extends TypedVariable<D>, C extends TypedVariable<C>> implements Check<D, C> {

        @Override
        public boolean check(Collection<D> structure1, Collection<C> structure2) {
            if (Objects.isNull(structure1) && Objects.isNull(structure2)) {
                return true;
            }
            if (Objects.isNull(structure1)) {
                return false;
            }
            if (Objects.isNull(structure2)) {
                return false;
            }
            if (structure1.isEmpty() && structure2.isEmpty()) {
                return true;
            }

            Map<String, StructureHolder<D>> structure1Map = structure1.stream().map(StructureHolder::new).collect(Collectors.toMap(ih -> ih.name, ih -> ih));
            Map<String, StructureHolder<C>> structure2Map = structure2.stream().map(StructureHolder::new).collect(Collectors.toMap(ih -> ih.name, ih -> ih));
            if (!checkMembership(structure1Map, structure2Map)) {
                return false;
            }

            //Check direct descendents
            Collection<D> structure1ChildStructures =
                    structure1Map.values()
                            .stream()
                            .filter(e -> e.kind == TypedValue.Kind.STRUCTURE)
                            .map(ih -> ih.original.getComponents())
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
            Collection<C> structure2ChildStructures =
                    structure2Map.values()
                            .stream()
                            .filter(e -> e.kind == TypedValue.Kind.STRUCTURE)
                            .map(ih -> ih.original.getComponents())
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());

            return check(structure1ChildStructures, structure2ChildStructures);
        }

        protected abstract boolean checkMembership(Map<String, StructureHolder<D>> structure1Map,
                Map<String, StructureHolder<C>> structure2Map);

    }

    private static class IdenticalCheck extends BaseCheck<TypedVariableWithValue, CounterfactualSearchDomain> {

        @Override
        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        protected boolean checkMembership(Map<String, StructureHolder<TypedVariableWithValue>> structure1Map,
                Map<String, StructureHolder<CounterfactualSearchDomain>> structure2Map) {
            boolean validSize = structure2Map.size() == structure1Map.size();
            boolean validEntries = structure2Map.entrySet().stream().allMatch(e -> Objects.equals(e.getValue(), structure1Map.get(e.getKey())));
            return validSize && validEntries;
        }
    }

    private static class ComparableCheck extends BaseCheck<TypedVariableWithValue, TypedVariableWithValue> {

        @Override
        protected boolean checkMembership(Map<String, StructureHolder<TypedVariableWithValue>> structure1Map,
                Map<String, StructureHolder<TypedVariableWithValue>> structure2Map) {
            boolean validSize = structure2Map.size() <= structure1Map.size();
            boolean validEntries = structure2Map.entrySet().stream().allMatch(e -> Objects.equals(e.getValue(), structure1Map.get(e.getKey())));
            return validSize && validEntries;
        }

    }

    private static final IdenticalCheck IDENTICAL = new IdenticalCheck();
    private static final ComparableCheck COMPARABLE = new ComparableCheck();

    public static boolean isStructureIdentical(Collection<TypedVariableWithValue> inputs, Collection<CounterfactualSearchDomain> searchDomains) {
        return IDENTICAL.check(inputs, searchDomains);
    }

    public static boolean isStructureComparable(Collection<TypedVariableWithValue> outcomes, Collection<TypedVariableWithValue> goals) {
        return COMPARABLE.check(outcomes, goals);
    }

    private static class StructureHolder<T extends TypedVariable<T>> {

        private final TypedValue.Kind kind;
        private final String name;
        private final String typeRef;
        private final T original;

        public StructureHolder(T typedVariableWithValue) {
            this.kind = typedVariableWithValue.getKind();
            this.name = typedVariableWithValue.getName();
            this.typeRef = typedVariableWithValue.getTypeRef();
            this.original = typedVariableWithValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StructureHolder<?> that = (StructureHolder<?>) o;
            return kind == that.kind && Objects.equals(name, that.name) && Objects.equals(typeRef, that.typeRef);
        }

        @Override
        public int hashCode() {
            return Objects.hash(kind, name, typeRef);
        }
    }
}
