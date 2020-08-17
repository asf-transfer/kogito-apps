import React from 'react';
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow
} from '@patternfly/react-core';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import './SkeletonDataList.scss';

type SkeletonDataListProps = {
  rowsNumber: number;
  colsNumber: number;
  hasHeader?: boolean;
};

const SkeletonDataList = (props: SkeletonDataListProps) => {
  const { rowsNumber, colsNumber, hasHeader } = props;

  const rows = [];
  for (let i = 0; i < rowsNumber; i++) {
    const row = [];
    for (let j = 0; j < colsNumber; j++) {
      const size = (i + j) % 2 ? 'lg' : 'md';
      row.push(
        <DataListCell key={`content-${j}`}>
          <SkeletonStripe size={size} />
        </DataListCell>
      );
    }
    const skeletonRow = {
      cells: row,
      key: 'skeleton-row-' + i
    };
    rows.push(skeletonRow);
  }

  return (
    <DataList aria-label="Loading content">
      <DataListItem aria-labelledby="">
        {rows.map((item, index) => {
          let headerClass;
          if (hasHeader && index === 0) {
            headerClass = 'skeleton-datalist__header';
          }
          return (
            <DataListItemRow className={headerClass} key={item.key}>
              <DataListItemCells dataListCells={item.cells} />
            </DataListItemRow>
          );
        })}
      </DataListItem>
    </DataList>
  );
};

export default SkeletonDataList;
