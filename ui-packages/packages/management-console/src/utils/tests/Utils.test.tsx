import {
  ProcessInstanceIconCreator,
  setTitle,
  handleSkip,
  handleRetry,
  handleAbort,
  handleNodeInstanceRetrigger,
  handleNodeInstanceCancel,
  handleVariableUpdate,
  performMultipleAction,
  JobsIconCreator,
  handleJobReschedule
} from '../Utils';
import { GraphQL } from '@kogito-apps/common';
import ProcessInstanceState = GraphQL.ProcessInstanceState;
import JobStatus = GraphQL.JobStatus;
import axios from 'axios';
import wait from 'waait';
import { OperationType } from '../../components/Molecules/ProcessListToolbar/ProcessListToolbar';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
const children = 'children';
/* tslint:disable:no-string-literal */
describe('uitility function testing', () => {
  it('state icon creator tests', () => {
    const activeTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Active
    );
    const completedTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Completed
    );
    const errorTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Error
    );
    const suspendedTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Suspended
    );
    const abortedTestResult = ProcessInstanceIconCreator(
      ProcessInstanceState.Aborted
    );

    expect(activeTestResult.props[children][1]).toEqual('Active');
    expect(completedTestResult.props[children][1]).toEqual('Completed');
    expect(errorTestResult.props[children][1]).toEqual('Error');
    expect(suspendedTestResult.props[children][1]).toEqual('Suspended');
    expect(abortedTestResult.props[children][1]).toEqual('Aborted');
  });
  it('Jobs icon creator tests', () => {
    const jobsErrorResult = JobsIconCreator(JobStatus.Error);
    const jobsCanceledResult = JobsIconCreator(JobStatus.Canceled);
    const jobsScheduledResult = JobsIconCreator(JobStatus.Scheduled);
    const jobsExecutedResult = JobsIconCreator(JobStatus.Executed);
    const jobsRetryResult = JobsIconCreator(JobStatus.Retry);

    expect(jobsErrorResult.props[children][1]).toEqual('Error');
    expect(jobsCanceledResult.props[children][1]).toEqual('Canceled');
    expect(jobsScheduledResult.props[children][1]).toEqual('Scheduled');
    expect(jobsRetryResult.props[children][1]).toEqual('Retry');
    expect(jobsExecutedResult.props[children][1]).toEqual('Executed');
  });
  it('set title tests', () => {
    const successResult = setTitle('success', 'Abort operation');
    const failureResult = setTitle('failure', 'Skip operation');
    expect(successResult.props[children][2]).toEqual('Abort operation');
    expect(failureResult.props[children][2]).toEqual('Skip operation');
  });

  describe('handle skip tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };
    it('executes skip process successfully', async () => {
      mockedAxios.post.mockResolvedValue({});
      const onSkipSuccess = jest.fn();
      const onSkipFailure = jest.fn();
      await handleSkip(processInstanceData, onSkipSuccess, onSkipFailure);
      await wait(0);
      expect(onSkipSuccess).toHaveBeenCalled();
    });
    it('fails executing skip process', async () => {
      const onSkipSuccess = jest.fn();
      const onSkipFailure = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '403 error' });
      await handleSkip(processInstanceData, onSkipSuccess, onSkipFailure);
      await wait(0);
      expect(onSkipFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onSkipFailure).toHaveBeenCalled();
    });
  });

  describe('handle Retry tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };

    it('executes retry process successfully', async () => {
      const onRetrySuccess = jest.fn();
      const onRetryFailure = jest.fn();
      mockedAxios.post.mockResolvedValue({});
      await handleRetry(processInstanceData, onRetrySuccess, onRetryFailure);
      await wait(0);
      expect(onRetrySuccess).toHaveBeenCalled();
    });
    it('fails executing Retry process', async () => {
      const onRetrySuccess = jest.fn();
      const onRetryFailure = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '403 error' });
      await handleRetry(processInstanceData, onRetrySuccess, onRetryFailure);
      await wait(0);
      expect(onRetryFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onRetryFailure).toHaveBeenCalled();
    });
  });

  describe('handle Abort tests', () => {
    const processInstanceData = {
      id: '123',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active
    };
    it('executes Abort process successfully', async () => {
      const onAbortSuccess = jest.fn();
      const onAbortFailure = jest.fn();
      mockedAxios.delete.mockResolvedValue({});
      await handleAbort(processInstanceData, onAbortSuccess, onAbortFailure);
      await wait(0);
      expect(onAbortSuccess).toHaveBeenCalled();
    });
    it('fails executing Abort process', async () => {
      const onAbortSuccess = jest.fn();
      const onAbortFailure = jest.fn();
      mockedAxios.delete.mockRejectedValue({ message: '403 error' });
      await handleAbort(processInstanceData, onAbortSuccess, onAbortFailure);
      await wait(0);
      expect(onAbortFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onAbortFailure).toHaveBeenCalled();
    });
  });

  describe('retrigger click tests', () => {
    const processInstanceData = {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Active,
      nodes: [
        {
          nodeId: '2',
          name: 'Confirm travel',
          definitionId: 'UserTask_2',
          id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
          enter: '2019-10-22T04:43:01.148Z',
          exit: null,
          type: 'HumanTaskNode'
        }
      ]
    };
    const nodeObject = {
      nodeId: '2',
      name: 'Confirm travel',
      definitionId: 'UserTask_2',
      id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
      enter: '2019-10-22T04:43:01.148Z',
      exit: null,
      type: 'HumanTaskNode'
    };
    it('executes retrigger node process successfully', async () => {
      const onRetriggerSuccess = jest.fn();
      const onRetriggerFailure = jest.fn();
      mockedAxios.post.mockResolvedValue({});
      handleNodeInstanceRetrigger(
        processInstanceData,
        nodeObject,
        onRetriggerSuccess,
        onRetriggerFailure
      );
      await wait(0);
      expect(onRetriggerSuccess).toHaveBeenCalled();
    });
    it('fails executing retrigger node process', async () => {
      mockedAxios.post.mockRejectedValue({ message: '403 error' });
      const onRetriggerSuccess = jest.fn();
      const onRetriggerFailure = jest.fn();
      handleNodeInstanceRetrigger(
        processInstanceData,
        nodeObject,
        onRetriggerSuccess,
        onRetriggerFailure
      );
      await wait(0);
      expect(onRetriggerFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onRetriggerFailure).toHaveBeenCalled();
    });
  });

  describe('Cancel click tests', () => {
    const processInstanceData = {
      id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
      processId: 'trav',
      serviceUrl: 'http://localhost:4000',
      state: ProcessInstanceState.Error,
      nodes: [
        {
          nodeId: '2',
          name: 'Confirm travel',
          definitionId: 'UserTask_2',
          id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
          enter: '2019-10-22T04:43:01.148Z',
          exit: null,
          type: 'HumanTaskNode'
        }
      ]
    };
    const nodeObject = {
      nodeId: '2',
      name: 'Confirm travel',
      definitionId: 'UserTask_2',
      id: '843bd287-fb6e-4ee7-a304-ba9b430e52d8',
      enter: '2019-10-22T04:43:01.148Z',
      exit: null,
      type: 'HumanTaskNode'
    };
    it('executes cancel node process successfully', async () => {
      const onCancelSuccess = jest.fn();
      const onCancelFailure = jest.fn();
      mockedAxios.delete.mockResolvedValue({});
      handleNodeInstanceCancel(
        processInstanceData,
        nodeObject,
        onCancelSuccess,
        onCancelFailure
      );
      await wait(0);
      expect(onCancelSuccess).toHaveBeenCalled();
    });
    it('fails executing cancel node process', async () => {
      mockedAxios.delete.mockRejectedValue({ message: '403 error' });
      const onCancelSuccess = jest.fn();
      const onCancelFailure = jest.fn();
      handleNodeInstanceCancel(
        processInstanceData,
        nodeObject,
        onCancelSuccess,
        onCancelFailure
      );
      await wait(0);
      expect(onCancelFailure.mock.calls[0][0]).toEqual('"403 error"');
      expect(onCancelFailure).toHaveBeenCalled();
    });
  });

  describe('handle multiple abort click tests', () => {
    const instanceToBeActioned = {
      '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        processId: 'trav',
        serviceUrl: 'http://localhost:4000'
      }
    } as any;
    it('executes multi-abort process successfully', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.delete.mockResolvedValue({});
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.ABORT
      );
      await wait(0);
      expect(onMultiActionResult.mock.calls[0][0]).toBeDefined();
      expect(onMultiActionResult).toHaveBeenCalled();
    });
    it('catched an error in the instance(abort)', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.delete.mockRejectedValue({ message: '404 error' });
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.ABORT
      );
      await wait(0);
      expect(
        onMultiActionResult.mock.calls[0][1][
          '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
        ]['errorMessage']
      ).toEqual('"404 error"');
    });
  });

  describe('handle multiple skip click tests', () => {
    const instanceToBeActioned = {
      '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        processId: 'trav',
        serviceUrl: 'http://localhost:4000'
      }
    } as any;
    it('executes multi-skip process successfully', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.post.mockResolvedValue({});
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.SKIP
      );
      await wait(0);
      expect(onMultiActionResult.mock.calls[0][0]).toBeDefined();
      expect(onMultiActionResult).toHaveBeenCalled();
    });
    it('catched an error in the instance(skip)', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '404 error' });
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.SKIP
      );
      await wait(0);
      expect(
        onMultiActionResult.mock.calls[0][1][
          '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
        ]['errorMessage']
      ).toEqual('"404 error"');
    });
  });

  describe('handle multiple retry click tests', () => {
    const instanceToBeActioned = {
      '8035b580-6ae4-4aa8-9ec0-e18e19809e0b': {
        id: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        processId: 'trav',
        serviceUrl: 'http://localhost:4000'
      }
    } as any;
    it('executes multi-retry process successfully', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.post.mockResolvedValue({});
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.RETRY
      );
      await wait(0);
      expect(onMultiActionResult.mock.calls[0][0]).toBeDefined();
      expect(onMultiActionResult).toHaveBeenCalled();
    });
    it('catched an error in the instance(retry)', async () => {
      const onMultiActionResult = jest.fn();
      mockedAxios.post.mockRejectedValue({ message: '404 error' });
      await performMultipleAction(
        instanceToBeActioned,
        onMultiActionResult,
        OperationType.RETRY
      );
      await wait(0);
      expect(
        onMultiActionResult.mock.calls[0][1][
          '8035b580-6ae4-4aa8-9ec0-e18e19809e0b'
        ]['errorMessage']
      ).toEqual('"404 error"');
    });
  });
  describe('test utilities of process variables', () => {
    it('test put method that updates process variables', async () => {
      mockedAxios.put.mockResolvedValue({
        status: 200,
        statusText: 'OK',
        data: {
          flight: {
            flightNumber: 'MX555',
            seat: null,
            gate: null,
            departure: '2020-09-23T03:30:00.000+05:30',
            arrival: '2020-09-28T03:30:00.000+05:30'
          },
          hotel: {
            name: 'Perfect hotel',
            address: {
              street: 'street',
              city: 'Sydney',
              zipCode: '12345',
              country: 'Australia'
            },
            phone: '09876543',
            bookingNumber: 'XX-012345',
            room: null
          },
          traveller: {
            firstName: 'Saravana',
            lastName: 'Srinivasan',
            email: 'Saravana@gmai.com',
            nationality: 'US',
            address: {
              street: 'street',
              city: 'city',
              zipCode: '123156',
              country: 'US'
            }
          },
          trip: {
            city: 'Sydney',
            country: 'Australia',
            begin: '2020-09-23T03:30:00.000+05:30',
            end: '2020-09-28T03:30:00.000+05:30',
            visaRequired: false
          }
        }
      });
      const setUpdateJson = jest.fn();
      const setDisplayLabel = jest.fn();
      const setDisplaySuccess = jest.fn();
      const setVariableError = jest.fn();
      const processInstance = {
        id: '0e5f1dde-cc5a-4b1f-8e06-dbb27bc489b4',
        endpoint: 'http://localhost:8080/travels'
      };
      const updateJson = {
        flight: {
          flightNumber: 'MX5555',
          seat: null,
          gate: null,
          departure: '2020-09-23T03:30:00.000+05:30',
          arrival: '2020-09-28T03:30:00.000+05:30'
        },
        hotel: {
          name: 'Perfect hotel',
          address: {
            street: 'street',
            city: 'Sydney',
            zipCode: '12345',
            country: 'Australia'
          },
          phone: '09876543',
          bookingNumber: 'XX-012345',
          room: null
        },
        traveller: {
          firstName: 'Saravana',
          lastName: 'Srinivasan',
          email: 'Saravana@gmai.com',
          nationality: 'US',
          address: {
            street: 'street',
            city: 'city',
            zipCode: '123156',
            country: 'US'
          }
        },
        trip: {
          city: 'Sydney',
          country: 'Australia',
          begin: '2020-09-23T03:30:00.000+05:30',
          end: '2020-09-28T03:30:00.000+05:30',
          visaRequired: false
        }
      };
      await handleVariableUpdate(
        processInstance,
        updateJson,
        setUpdateJson,
        setDisplaySuccess,
        setDisplayLabel,
        setVariableError
      );
      expect(setDisplaySuccess).toHaveBeenCalled();
      expect(setUpdateJson).toHaveBeenCalled();
    });
  });
  describe('test utilities of jobs', () => {
    it('test reschedule function', async () => {
      mockedAxios.patch.mockResolvedValue({
        status: 200,
        statusText: 'OK',
        data: {
          callbackEndpoint:
            'http://localhost:8080/management/jobs/travels/instances/9865268c-64d7-3a44-8972-7325b295f7cc/timers/58180644-2fdf-4261-83f2-f4e783d308a3_0',
          executionCounter: 0,
          executionResponse: null,
          expirationTime: '2020-10-16T10:17:22.879Z',
          id: '58180644-2fdf-4261-83f2-f4e783d308a3_0',
          lastUpdate: '2020-10-07T07:41:31.467Z',
          priority: 0,
          processId: 'travels',
          processInstanceId: '9865268c-64d7-3a44-8972-7325b295f7cc',
          repeatInterval: null,
          repeatLimit: null,
          retries: 0,
          rootProcessId: null,
          rootProcessInstanceId: null,
          scheduledId: null,
          status: 'SCHEDULED'
        }
      });
      const job = {
        id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
        processId: 'travels',
        processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
        rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
        status: GraphQL.JobStatus.Executed,
        priority: 0,
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
        repeatInterval: 1,
        repeatLimit: 3,
        scheduledId: '0',
        retries: 0,
        lastUpdate: '2020-08-27T03:35:50.147Z',
        expirationTime: '2020-08-27T03:35:50.147Z'
      };
      const repeatInterval = 2;
      const repeatLimit = 1;
      const rescheduleClicked = false;
      const setRescheduleClicked = jest.fn();
      const scheduleDate = '2020-08-27T03:35:50.147Z';
      const refetch = jest.fn();
      const setErrorMessage = jest.fn();
      await handleJobReschedule(
        job,
        repeatInterval,
        repeatLimit,
        rescheduleClicked,
        setErrorMessage,
        setRescheduleClicked,
        scheduleDate,
        refetch
      );
      expect(setRescheduleClicked).toHaveBeenCalled();
    });
    it('test error response for reschedule function', async () => {
      mockedAxios.patch.mockRejectedValue({ message: '403 error' });
      const job = {
        id: '6e74a570-31c8-4020-bd70-19be2cb625f3_0',
        processId: 'travels',
        processInstanceId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
        rootProcessId: '5c56eeff-4cbf-3313-a325-4c895e0afced',
        status: GraphQL.JobStatus.Executed,
        priority: 0,
        callbackEndpoint:
          'http://localhost:8080/management/jobs/travels/instances/5c56eeff-4cbf-3313-a325-4c895e0afced/timers/6e74a570-31c8-4020-bd70-19be2cb625f3_0',
        repeatInterval: 1,
        repeatLimit: 3,
        scheduledId: '0',
        retries: 0,
        lastUpdate: '2020-08-27T03:35:50.147Z',
        expirationTime: '2020-08-27T03:35:50.147Z'
      };
      const repeatInterval = null;
      const repeatLimit = null;
      const rescheduleClicked = false;
      const setRescheduleClicked = jest.fn();
      const scheduleDate = '2020-08-27T03:35:50.147Z';
      const refetch = jest.fn();
      const setErrorMessage = jest.fn();
      await handleJobReschedule(
        job,
        repeatInterval,
        repeatLimit,
        rescheduleClicked,
        setErrorMessage,
        setRescheduleClicked,
        scheduleDate,
        refetch
      );
      expect(setRescheduleClicked).toHaveBeenCalled();
    });
  });
});
