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

import React, { ReactElement } from 'react';

import KogitoAppContext, {
  AppContextImpl,
  EnvironmentMode
} from '../../../environment/context/KogitoAppContext';
import { TestUserSystemImpl } from '../../../environment/auth/TestUserSystem';
import { isAuthEnabled } from '../../../utils/KeycloakClient';
import { KeycloakUserSystem } from '../../../environment/auth/KeycloakUserSystem';

interface IOwnProps {
  children: ReactElement;
}

const KogitoAppContextProvider: React.FC<IOwnProps> = ({ children }) => {
  const authEnabled: boolean = isAuthEnabled();

  const getUserSystem = () => {
    const userSystem = authEnabled
      ? new KeycloakUserSystem()
      : new TestUserSystemImpl(user => {
          window.location.href = '/';
        });
    return userSystem;
  };

  const appUserSystem = getUserSystem();

  return (
    <KogitoAppContext.Provider
      value={
        new AppContextImpl(appUserSystem, {
          mode: authEnabled ? EnvironmentMode.PROD : EnvironmentMode.TEST
        })
      }
    >
      {children}
    </KogitoAppContext.Provider>
  );
};

export default KogitoAppContextProvider;
