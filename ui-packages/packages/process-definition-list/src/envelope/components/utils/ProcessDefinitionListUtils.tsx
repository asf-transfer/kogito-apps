import React from 'react';
import { DataTableColumn } from '@kogito-apps/components-common';
import { PlayIcon } from '@patternfly/react-icons';
import { Tooltip } from '@patternfly/react-core';
import { ProcessDefinition } from '../../../api/ProcessDefinitionListEnvelopeApi';
export const getColumn = (
  columnPath: string,
  columnLabel: string
): DataTableColumn => {
  return {
    label: columnLabel,
    path: columnPath,
    bodyCellTransformer: value => <span>{value}</span>
  };
};

export const getActionColumn = (
  startProcess: (processDefinition: ProcessDefinition) => void
): DataTableColumn => {
  return {
    label: 'Actions',
    path: 'actions',
    bodyCellTransformer: (value, rowData: ProcessDefinition) => (
      <div onClick={() => startProcess(rowData)}>
        <Tooltip content="Start new process">
          <PlayIcon />
        </Tooltip>
      </div>
    )
  };
};
