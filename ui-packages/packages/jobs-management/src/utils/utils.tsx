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

import React from 'react';
import { Job, JobStatus } from '../types';
import { BulkListItem } from '@kogito-apps/management-console-shared';
import {
  CheckCircleIcon,
  BanIcon,
  ErrorCircleOIcon,
  UndoIcon,
  ClockIcon
} from '@patternfly/react-icons';

export const JobsIconCreator = (state: JobStatus): JSX.Element => {
  switch (state) {
    case JobStatus.Error:
      return (
        <>
          <ErrorCircleOIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--danger-color--100)"
          />
          Error
        </>
      );
    case JobStatus.Canceled:
      return (
        <>
          <BanIcon className="pf-u-mr-sm" />
          Canceled
        </>
      );
    case JobStatus.Executed:
      return (
        <>
          <CheckCircleIcon
            className="pf-u-mr-sm"
            color="var(--pf-global--success-color--100)"
          />
          Executed
        </>
      );
    case JobStatus.Retry:
      return (
        <>
          <UndoIcon className="pf-u-mr-sm" />
          Retry
        </>
      );
    case JobStatus.Scheduled:
      return (
        <>
          <ClockIcon className="pf-u-mr-sm" />
          Scheduled
        </>
      );
  }
};

export const formatForBulkListJob = (
  jobsList: (Job & { errorMessage?: string })[]
): BulkListItem[] => {
  const formattedItems: BulkListItem[] = [];
  jobsList.forEach((item: Job & { errorMessage?: string }) => {
    const formattedObj: BulkListItem = {
      id: item.id,
      name: item.processId,
      description: item.id,
      errorMessage: item.errorMessage ? item.errorMessage : null
    };
    formattedItems.push(formattedObj);
  });
  return formattedItems;
};
