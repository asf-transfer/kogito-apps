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
import { useHistory } from 'react-router-dom';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/components-common';
import { EmbeddedTaskInbox } from '@kogito-apps/task-inbox';
import { TaskInboxGatewayApi } from '../../../../../channel/inbox';
import { useTaskInboxGatewayApi } from '../../../../../channel/inbox/TaskInboxContext';
import {
  getActiveTaskStates,
  getAllTaskStates
} from '../../../../../utils/Utils';
import { GraphQL } from '@kogito-apps/consoles-common';
import UserTaskInstance = GraphQL.UserTaskInstance;

const TaskInboxContainer: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const history = useHistory();
  const gatewayApi: TaskInboxGatewayApi = useTaskInboxGatewayApi();

  useEffect(() => {
    const unsubscriber = gatewayApi.onOpenTaskListen({
      onOpen(task: UserTaskInstance) {
        history.push(`/TaskDetails/${task.id}`);
      }
    });

    return () => {
      unsubscriber.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedTaskInbox
      {...componentOuiaProps(ouiaId, 'task-inbox-container', ouiaSafe)}
      initialState={gatewayApi.taskInboxState}
      driver={gatewayApi}
      allTaskStates={getAllTaskStates()}
      activeTaskStates={getActiveTaskStates()}
      targetOrigin={window.location.origin}
    />
  );
};

export default TaskInboxContainer;
