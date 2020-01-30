import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Grid,
  GridItem,
  PageSection
} from '@patternfly/react-core';
import _ from 'lodash';
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import DataListTitleComponent from '../../Molecules/DataListTitleComponent/DataListTitleComponent';
import DataToolbarComponent from '../../Molecules/DataToolbarComponent/DataToolbarComponent';
import './DataList.css';
import DataListComponent from '../../Organisms/DataListComponent/DataListComponent';
import EmptyStateComponent from '../../Atoms/EmptyStateComponent/EmptyStateComponent';
import PaginationComponent from '../../Atoms/PaginationComponent/PaginationComponent';
import {
  useGetProcessInstancesLazyQuery,
  useGetAllProcessInstancesLazyQuery
} from '../../../graphql/types';

const DataListContainer: React.FC<{}> = () => {
  const [initData, setInitData] = useState<any>([]);
  const [checkedArray, setCheckedArray] = useState<any>(['ACTIVE']);
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);
  const [isStatusSelected, setIsStatusSelected] = useState(true);
  const [filters, setFilters] = useState(checkedArray);
  const [totalProcesses, setTotalProcesses] = useState(0);
  const [limit, setLimit] = useState(10);
  const [offset, setOffset] = useState(0);
  const [page, setPage] = useState(0);

  const [
    getProcessInstances,
    { loading, data }
  ] = useGetProcessInstancesLazyQuery({ fetchPolicy: 'network-only' });

  const [getAllProcesses, allProcessData] = useGetAllProcessInstancesLazyQuery({
    fetchPolicy: 'network-only'
  });

  const onFilterClick = async (arr = checkedArray) => {
    setIsLoading(true);
    setIsError(false);
    setIsStatusSelected(true);
    getProcessInstances({ variables: { state: arr, offset, limit } });
    getAllProcesses({ variables: { state: arr } });
  };

  useEffect(() => {
    if (!allProcessData.loading && allProcessData.data) {
      setTotalProcesses(allProcessData.data.ProcessInstances.length);
    }
  }, [allProcessData.data]);

  useEffect(() => {
    setIsLoading(loading);
    setInitData(data);
  }, [data]);

  useEffect(() => {
    setOffset(0);
    setPage(0);
  }, [checkedArray]);

  return (
    <React.Fragment>
      <PageSection variant="light">
        <DataListTitleComponent />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          <BreadcrumbItem isActive>Process instances</BreadcrumbItem>
        </Breadcrumb>
      </PageSection>
      <PageSection>
        <Grid gutter="md">
          <GridItem span={12}>
            <Card className="dataList">
              {!isError && (
                <>
                  {' '}
                  <DataToolbarComponent
                    checkedArray={checkedArray}
                    filterClick={onFilterClick}
                    setCheckedArray={setCheckedArray}
                    setIsStatusSelected={setIsStatusSelected}
                    filters={filters}
                    setFilters={setFilters}
                    setOffset={setOffset}
                    setPage={setPage}
                    getProcessInstances={getProcessInstances}
                    setLimit={setLimit}
                  />
                  <PaginationComponent
                    totalProcesses={totalProcesses}
                    offset={offset}
                    limit={limit}
                    setOffset={setOffset}
                    setLimit={setLimit}
                    checkedArray={checkedArray}
                    getProcessInstances={getProcessInstances}
                    page={page}
                    setIsLoading={setIsLoading}
                    setPage={setPage}
                  />
                </>
              )}
              {isStatusSelected ? (
                <DataListComponent
                  initData={initData}
                  setInitData={setInitData}
                  isLoading={isLoading}
                  setIsError={setIsError}
                  setTotalProcesses={setTotalProcesses}
                />
              ) : (
                  <EmptyStateComponent
                    iconType="warningTriangleIcon1"
                    title="No status is selected"
                    body="Try selecting at least one status to see results"
                    filterClick={onFilterClick}
                    setFilters={setFilters}
                    setCheckedArray={setCheckedArray}
                  />
                )}
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default DataListContainer;
