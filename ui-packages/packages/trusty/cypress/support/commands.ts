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

declare namespace Cypress {
  interface Chainable {

    /**
     * Return DOM Element which match the value of data-ouia-component-it attribute.
     */
    ouiaId(valueStr: String): Chainable<JQuery<HTMLBodyElement>>;
  }
}

Cypress.Commands.add('ouiaId', (valueStr) => {
  return cy.get("[data-ouia-component-id='" + valueStr + "']");
});
