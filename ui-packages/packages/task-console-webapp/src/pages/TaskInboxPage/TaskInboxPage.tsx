/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import React, { useEffect } from 'react';
import { Card, Grid, GridItem, PageSection } from '@patternfly/react-core';
import {
  OUIAProps,
  ouiaPageTypeAndObjectId
} from '@kogito-apps/components-common';

const TaskInboxPage: React.FC<OUIAProps> = () => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId('task-inbox');
  });

  return (
    <React.Fragment>
      {'header goes here'}
      <PageSection>
        <Grid hasGutter md={1}>
          <GridItem span={12}>
            <Card>{'Task Inbox goes here!'}</Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default TaskInboxPage;
