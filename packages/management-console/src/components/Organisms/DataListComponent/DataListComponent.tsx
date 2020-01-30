import React, { useEffect } from 'react';
import { DataList, Bullseye } from '@patternfly/react-core';
import DataListItemComponent from '../../Molecules/DataListItemComponent/DataListItemComponent';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import EmptyStateComponent from '../../Atoms/EmptyStateComponent/EmptyStateComponent';
import '@patternfly/patternfly/patternfly-addons.css';
import {
  useGetProcessInstancesQuery,
  ProcessInstanceState,
  useGetAllProcessInstancesQuery
} from '../.././../graphql/types';

interface IOwnProps {
  setInitData: any;
  initData: any;
  isLoading: boolean;
  setIsError: any;
  setTotalProcesses: any;
}

const DataListComponent: React.FC<IOwnProps> = ({
  initData,
  setInitData,
  isLoading,
  setIsError,
  setTotalProcesses
}) => {
  const {
    loading,
    error,
    data,
    refetch,
    networkStatus
  } = useGetProcessInstancesQuery({
    variables: {
      state: [ProcessInstanceState.Active],
      offset: 0,
      limit: 10
    },
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });

  const totalProcessInstances = useGetAllProcessInstancesQuery({
    variables: { state: [ProcessInstanceState.Active] },
    fetchPolicy: 'network-only'
  });

  useEffect(() => {
    if (!totalProcessInstances.loading) {
      setTotalProcesses(totalProcessInstances.data.ProcessInstances.length);
    }
  }, [totalProcessInstances.data]);

  useEffect(() => {
    setIsError(false);
    setInitData(data);
  }, [data]);

  if (loading || isLoading) {
    return (
      <Bullseye>
        <SpinnerComponent spinnerText="Loading process instances..." />
      </Bullseye>
    );
  }

  if (networkStatus === 4) {
    return (
      <Bullseye>
        <SpinnerComponent spinnerText="Loading process instances..." />
      </Bullseye>
    );
  }

  if (error) {
    setIsError(true);
    return (
      <div className=".pf-u-my-xl">
        <EmptyStateComponent
          iconType="warningTriangleIcon"
          title="Oops... error while loading"
          body="Try using the refresh action to reload process instances"
          refetch={refetch}
          refetchAll={totalProcessInstances.refetch}
        />
      </div>
    );
  }

  return (
    <DataList aria-label="Process instance list">
      {!loading &&
        initData !== undefined &&
        initData.ProcessInstances.map((item, index) => {
          return (
            <DataListItemComponent
              id={index}
              key={item.id}
              processInstanceData={item}
            />
          );
        })}
      {initData !== undefined &&
        !isLoading &&
        initData.ProcessInstances.length === 0 && (
          <EmptyStateComponent
            iconType="searchIcon"
            title="No results found"
            body="Try using different filters"
          />
        )}
    </DataList>
  );
};

export default DataListComponent;
