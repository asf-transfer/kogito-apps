/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React from 'react';
import {
  DevUIAppContext,
  useDevUIAppContext
} from '../../components/contexts/DevUIAppContext';
import CloudEventFormContext from './CloudEventFormContext';
import { CloudEventFormGatewayApiImpl } from './CloudEventFormGatewayApi';

interface IOwnProps {
  children;
}

const CloudEventFormContextProvider: React.FC<IOwnProps> = ({ children }) => {
  const runtimeToolsApi: DevUIAppContext = useDevUIAppContext();
  return (
    <CloudEventFormContext.Provider
      value={new CloudEventFormGatewayApiImpl(runtimeToolsApi.getDevUIUrl())}
    >
      {children}
    </CloudEventFormContext.Provider>
  );
};

export default CloudEventFormContextProvider;
