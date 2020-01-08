import { TimeAgo } from '@n1ru4l/react-time-ago';
import React, { useCallback, useState, useEffect } from 'react';
import gql from 'graphql-tag';
import axios from 'axios';
import {
  Alert,
  AlertActionCloseButton,
  Button,
  DataListAction,
  DataListCell,
  DataListCheck,
  DataListContent,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  DataListToggle,
  Dropdown,
  DropdownItem,
  DropdownPosition,
  Bullseye,
  KebabToggle,
  Modal
} from '@patternfly/react-core';
import { Link } from 'react-router-dom';
import { useApolloClient } from 'react-apollo';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';

/* tslint:disable:no-string-literal */

export interface IProcessInstanceError {
  nodeDefinitionId: string;
  message: string;
}
interface IProcessInstance {
  lastUpdate: string;
  id: string;
  processId: string;
  parentProcessInstanceId: string | null;
  rootProcessInstanceId: string | null;
  processName: string;
  start: string;
  state: string;
  addons: [string];
  endpoint: string;
  error: IProcessInstanceError;
  isChecked: boolean;
}
export interface IOwnProps {
  id: number;
  processInstanceData: IProcessInstance;
  initData: any;
  setInitData: any;
  loading: boolean;
  abortedArray: any;
  setAbortedArray: any;
  completedAndAbortedArray: any;
  setCompletedAndAbortedArray: any;
}

const DataListItemComponent: React.FC<IOwnProps> = ({
  processInstanceData,
  initData,
  setInitData,
  loading,
  abortedArray,
  setAbortedArray,
  completedAndAbortedArray,
  setCompletedAndAbortedArray
}) => {
  const [expanded, setexpanded] = useState([]);
  const [isOpen, setisOpen] = useState(false);
  const [isLoaded, setisLoaded] = useState(false);
  const [openModal, setOpenModal] = useState(false);
  const [alertVisible, setAlertVisible] = useState(false);
  const [alertTitle, setAlertTitle] = useState('');
  const [alertType, setAlertType] = useState(null);
  const [alertMessage, setAlertMessage] = useState('');

  const client = useApolloClient();

  const GET_CHILD_INSTANCES = gql`
    query getChildInstances($rootProcessInstanceId: String) {
      ProcessInstances(
        where: { rootProcessInstanceId: { equal: $rootProcessInstanceId } }
      ) {
        id
        processId
        processName
        parentProcessInstanceId
        rootProcessInstanceId
        roles
        state
        start
        lastUpdate
        endpoint
        addons
        error {
          nodeDefinitionId
          message
        }
      }
    }
  `;
  const handleViewError = useCallback(
    async (_processID, _instanceID, _endpoint) => {
      setOpenModal(true);
    },
    []
  );

  const handleSkip = useCallback(async (_processID, _instanceID, _endpoint) => {
    const processInstanceId = processInstanceData.id;
    const processId = processInstanceData.processId;

    try {
      const result = await axios.get(
        `${processInstanceData.endpoint}/management/processes/${processId}/instances/${processInstanceId}/skip`
      );
      setAlertTitle('Skip operation');
      setAlertType('success');
      setAlertMessage(
        'Process execution has successfully skipped node which was in error state.'
      );
      setAlertVisible(true);
    } catch (error) {
      setAlertTitle('Skip operation');
      setAlertType('danger');
      setAlertMessage(
        'Process execution failed to skip node which in error state. Message: ' +
          JSON.stringify(error.message)
      );
      setAlertVisible(true);
    }
  }, []);

  const handleRetry = useCallback(
    async (_processID, _instanceID, _endpoint) => {
      const processInstanceId = processInstanceData.id;
      const processId = processInstanceData.processId;
      try {
        const result = await axios.get(
          `${processInstanceData.endpoint}/management/processes/${processId}/instances/${processInstanceId}/retrigger`
        );
        setAlertTitle('Retry operation');
        setAlertType('success');
        setAlertMessage(
          'Process execution has successfully re executed node which was in error state.'
        );
        setAlertVisible(true);
      } catch (error) {
        setAlertTitle('Retry operation');
        setAlertType('danger');
        setAlertMessage(
          'Process execution failed to re executed node which is error state. Message: ' +
            JSON.stringify(error.message)
        );
        setAlertVisible(true);
      }
    },
    []
  );
  const onSelect = event => {
    setisOpen(isOpen ? false : true);
  };

  const onCheckBoxClick = () => {
    const copyOfInitData = { ...initData };
    copyOfInitData.ProcessInstances.map(instance => {
      if (processInstanceData.rootProcessInstanceId === null) {
        if (processInstanceData.id === instance.id) {
          if (instance.isChecked) {
            if (abortedArray.indexOf(instance.id) !== -1) {
              abortedArray.splice(abortedArray.indexOf(instance.id), 1);
            }
            instance.isChecked = false;
            if (
              instance.childDataList !== undefined &&
              instance.childDataList.length !== 0
            ) {
              instance.childDataList.map(child => {
                if (abortedArray.indexOf(child.id) !== -1) {
                  abortedArray.splice(abortedArray.indexOf(child.id), 1);
                }
                child.isChecked = false;
              });
            }
          } else {
            instance.isChecked = true;
            const childIds = [];
            if (
              instance.childDataList !== undefined &&
              instance.childDataList.length !== 0
            ) {
              instance.childDataList.map(child => {
                childIds.push(child.id);
                child.isChecked = true;
              });
            }
            setAbortedArray([...abortedArray, instance.id, ...childIds]);
          }
        }
      } else {
        if (
          instance.childDataList !== undefined &&
          instance.childDataList.length !== 0
        ) {
          instance.childDataList.map(child => {
            if (child.id === processInstanceData.id) {
              if (child.isChecked) {
                if (abortedArray.indexOf(child.id) !== -1) {
                  abortedArray.splice(abortedArray.indexOf(child.id), 1);
                }
                child.isChecked = false;
              } else {
                setAbortedArray([...abortedArray, child.id]);
                child.isChecked = true;
              }
            }
          });
        }
      }
    });
    copyOfInitData.ProcessInstances.map(instance => {
      if (instance.id === processInstanceData.rootProcessInstanceId) {
        const totalCheckedChildren = instance.childDataList.length;
        const checkedChildren = instance.childDataList.filter(child => {
          return child.isChecked === true;
        });
        if (totalCheckedChildren === checkedChildren.length) {
          setAbortedArray([
            ...abortedArray,
            instance.id,
            processInstanceData.id
          ]);
          instance.isChecked = true;
        } else {
          if (abortedArray.indexOf(instance.id) !== -1) {
            abortedArray.splice(abortedArray.indexOf(instance.id), 1);
          }
          instance.isChecked = false;
        }
      }
    });
    setInitData(copyOfInitData);
  };

  const onToggle = _isOpen => {
    setisOpen(_isOpen);
  };

  const handleModalToggle = () => {
    setOpenModal(!openModal);
  };

  const closeAlert = () => {
    setAlertVisible(false);
  };

  const toggle = async _id => {
    const index = expanded.indexOf(_id);
    const newExpanded =
      index >= 0
        ? [
            ...expanded.slice(0, index),
            ...expanded.slice(index + 1, expanded.length)
          ]
        : [...expanded, _id];
    setexpanded(newExpanded);
    if (!isLoaded) {
      await client
        .query({
          query: GET_CHILD_INSTANCES,
          variables: {
            rootProcessInstanceId: processInstanceData.id
          },
          fetchPolicy: 'network-only'
        })
        .then(result => {
          if (!result.loading) {
            result.data.ProcessInstances.map(instance => {
              if (processInstanceData.isChecked) {
                instance.isChecked = true;
              } else {
                instance.isChecked = false;
              }
            });
          }
          const copyOfInitData = { ...initData };
          copyOfInitData.ProcessInstances.map(instanceData => {
            if (instanceData.id === processInstanceData.id) {
              instanceData.childDataList = result.data.ProcessInstances;
            }
          });
          setInitData(copyOfInitData);
          setisLoaded(true);
        });
    }
  };

  const handleSkipButton = async () => {
    setOpenModal(!openModal);
    await handleSkip(
      processInstanceData.processId,
      processInstanceData.id,
      processInstanceData.endpoint
    );
  };

  const handleRetryButton = async () => {
    setOpenModal(!openModal);
    await handleRetry(
      processInstanceData.processId,
      processInstanceData.id,
      processInstanceData.endpoint
    );
  };

  return (
    <React.Fragment>
      {alertVisible && (
        <Alert
          variant={alertType}
          title={alertTitle}
          action={<AlertActionCloseButton onClose={() => closeAlert()} />}
        >
          {alertMessage}
        </Alert>
      )}
      <DataListItem
        aria-labelledby="kie-datalist-item"
        isExpanded={expanded.includes('kie-datalist-toggle')}
      >
        <DataListItemRow>
          {processInstanceData.parentProcessInstanceId === null && (
            <DataListToggle
              onClick={() => toggle('kie-datalist-toggle')}
              isExpanded={expanded.includes('kie-datalist-toggle')}
              id="kie-datalist-toggle"
              aria-controls="kie-datalist-expand"
            />
          )}
          <DataListCheck
            aria-labelledby="width-kie-datalist-item"
            name="width-kie-datalist-item"
            checked={processInstanceData.isChecked}
            onChange={() => {
              onCheckBoxClick();
            }}
          />
          <DataListItemCells
            dataListCells={[
              <DataListCell key={1}>
                {processInstanceData.processName}
              </DataListCell>,
              <DataListCell key={2}>
                {processInstanceData.start ? (
                  <TimeAgo
                    date={new Date(`${processInstanceData.start}`)}
                    render={({ _error, value }) => <span>{value}</span>}
                  />
                ) : (
                  ''
                )}
              </DataListCell>,
              <DataListCell key={3}>
                {processInstanceData.lastUpdate ? (
                  <TimeAgo
                    date={new Date(`${processInstanceData.lastUpdate}`)}
                    render={({ _error, value }) => <span>{value}</span>}
                  />
                ) : (
                  ''
                )}
              </DataListCell>,
              <DataListCell key={4}>{processInstanceData.state}</DataListCell>
            ]}
          />

          <DataListAction
            aria-labelledby="kie-datalist-item kie-datalist-action"
            id="kie-datalist-action"
            aria-label="Actions"
          >
            <Link to={'/ProcessInstances/' + processInstanceData.id}>
              <Button variant="secondary">Details</Button>
            </Link>
          </DataListAction>
          <DataListAction
            aria-labelledby="kie-datalist-item kie-datalist-action"
            id="kie-datalist-action"
            aria-label="Actions"
          >
            {processInstanceData.state === 'ERROR' ? (
              <Dropdown
                isPlain
                position={DropdownPosition.right}
                isOpen={isOpen}
                onSelect={onSelect}
                toggle={<KebabToggle onToggle={onToggle} />}
                dropdownItems={
                  processInstanceData.addons.includes('process-management')
                    ? [
                        <DropdownItem
                          key={1}
                          onClick={() =>
                            handleRetry(
                              processInstanceData.processId,
                              processInstanceData.id,
                              processInstanceData.endpoint
                            )
                          }
                        >
                          Retry
                        </DropdownItem>,
                        <DropdownItem
                          key={2}
                          onClick={() =>
                            handleSkip(
                              processInstanceData.processId,
                              processInstanceData.id,
                              processInstanceData.endpoint
                            )
                          }
                        >
                          Skip
                        </DropdownItem>,
                        <DropdownItem
                          key={3}
                          onClick={() =>
                            handleViewError(
                              processInstanceData.processId,
                              processInstanceData.id,
                              processInstanceData.endpoint
                            )
                          }
                        >
                          View Error
                        </DropdownItem>
                      ]
                    : [
                        <DropdownItem
                          key={1}
                          onClick={() =>
                            handleViewError(
                              processInstanceData.processId,
                              processInstanceData.id,
                              processInstanceData.endpoint
                            )
                          }
                        >
                          View Error
                        </DropdownItem>
                      ]
                }
              />
            ) : (
              <Dropdown
                isPlain
                position={DropdownPosition.right}
                isOpen={isOpen}
                onSelect={onSelect}
                toggle={<KebabToggle isDisabled onToggle={onToggle} />}
                dropdownItems={[]}
              />
            )}
            <Modal
              isLarge
              title="Error"
              isOpen={openModal}
              onClose={handleModalToggle}
              actions={
                processInstanceData.addons.includes('process-management')
                  ? [
                      <Button
                        key="confirm1"
                        variant="secondary"
                        onClick={handleSkipButton}
                      >
                        Skip
                      </Button>,
                      <Button
                        key="confirm2"
                        variant="secondary"
                        onClick={handleRetryButton}
                      >
                        Retry
                      </Button>,
                      <Button
                        key="confirm3"
                        variant="primary"
                        onClick={handleModalToggle}
                      >
                        Close
                      </Button>
                    ]
                  : [
                      <Button
                        key="confirm3"
                        variant="primary"
                        onClick={handleModalToggle}
                      >
                        Close
                      </Button>
                    ]
              }
            >
              {processInstanceData.error
                ? processInstanceData.error.message
                : 'No error message found'}
            </Modal>
          </DataListAction>
        </DataListItemRow>
        <DataListContent
          aria-label="Primary Content Details"
          id="kie-datalist-expand1"
          isHidden={!expanded.includes('kie-datalist-toggle')}
        >
          {!loading &&
            isLoaded &&
            initData.ProcessInstances.map(instance => {
              if (instance.id === processInstanceData.id) {
                return instance.childDataList.map((child, index) => {
                  return (
                    <DataListItemComponent
                      id={index}
                      key={child.id}
                      processInstanceData={child}
                      initData={initData}
                      setInitData={setInitData}
                      loading={loading}
                      abortedArray={abortedArray}
                      setAbortedArray={setAbortedArray}
                      completedAndAbortedArray={completedAndAbortedArray}
                      setCompletedAndAbortedArray={setCompletedAndAbortedArray}
                    />
                  );
                });
              }
            })}
          {!isLoaded && (
            <Bullseye>
              <SpinnerComponent spinnerText="Loading process instances..." />
            </Bullseye>
          )}
        </DataListContent>
      </DataListItem>
    </React.Fragment>
  );
};

export default DataListItemComponent;
