import React from 'react';
import { mount } from 'enzyme';
import DataTable from '../DataTable';
import { gql } from 'apollo-boost';
import { MockedProvider } from '@apollo/react-testing';

jest.mock('uuid', () => {
  let value = 1;
  return () => value++;
});

const data = [
  {
    id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
    description: null,
    name: 'Apply for visa',
    priority: '1',
    processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-02-19T11:11:56.282Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}"',
    outputs: '{}',
    referenceName: 'VisaApplication'
  },
  {
    id: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
    description: '',
    name: 'Confirm travel',
    priority: '1',
    processInstanceId: '9ae407dd-cdfa-4722-8a49-0a6d2e14550d',
    processId: 'travels',
    rootProcessInstanceId: null,
    rootProcessId: null,
    state: 'Ready',
    actualOwner: null,
    adminGroups: [],
    adminUsers: [],
    completed: null,
    started: '2020-02-19T10:59:34.185Z',
    excludedUsers: [],
    potentialGroups: [],
    potentialUsers: [],
    inputs:
      '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null}}',
    outputs: '{"ActorId":""}',
    referenceName: 'ConfirmTravel'
  }
];
const columns = ['ProcessId', 'Name', 'Priority', 'ProcessInstanceId', 'State'];
const GET_USER_TASKS_BY_STATE = gql`
  query getUserTasksByState($state: String) {
    ProcessInstances(where: { state: { equal: $state } }) {
      id
      description
      name
      priority
      processInstanceId
      processId
      rootProcessInstanceId
      rootProcessId
      state
      actualOwner
      adminGroups
      adminUsers
      completed
      started
      excludedUsers
      potentialGroups
      potentialUsers
      inputs
      outputs
      referenceName
    }
  }
`;
const mocks = [
  {
    request: {
      query: GET_USER_TASKS_BY_STATE,
      variables: {
        state: ['Ready']
      }
    },
    result: {
      data: {
        UserTaskInstances: [
          {
            id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
            description: null,
            name: 'Apply for visa',
            priority: '1',
            processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
            processId: 'travels',
            rootProcessInstanceId: null,
            rootProcessId: null,
            state: 'Ready',
            actualOwner: null,
            adminGroups: [],
            adminUsers: [],
            completed: null,
            started: '2020-02-19T11:11:56.282Z',
            excludedUsers: [],
            potentialGroups: [],
            potentialUsers: [],
            inputs:
              '{"Skippable":"true","trip":{"city":"Boston","country":"US","begin":"2020-02-19T23:00:00.000+01:00","end":"2020-02-26T23:00:00.000+01:00","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}"',
            outputs: '{}',
            referenceName: 'VisaApplication'
          },
          {
            id: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
            description: '',
            name: 'Confirm travel',
            priority: '1',
            processInstanceId: '9ae407dd-cdfa-4722-8a49-0a6d2e14550d',
            processId: 'travels',
            rootProcessInstanceId: null,
            rootProcessId: null,
            state: 'Ready',
            actualOwner: null,
            adminGroups: [],
            adminUsers: [],
            completed: null,
            started: '2020-02-19T10:59:34.185Z',
            excludedUsers: [],
            potentialGroups: [],
            potentialUsers: [],
            inputs:
              '{"flight":{"flightNumber":"MX555","seat":null,"gate":null,"departure":"2019-12-09T23:00:00.000+01:00","arrival":"2019-12-14T23:00:00.000+01:00"},"TaskName":"ConfirmTravel","NodeName":"Confirm travel","Priority":"1","Skippable":"true","hotel":{"name":"Perfect hotel","address":{"street":"street","city":"New York","zipCode":"12345","country":"US"},"phone":"09876543","bookingNumber":"XX-012345","room":null}}',
            outputs: '{"ActorId":""}',
            referenceName: 'ConfirmTravel'
          }
        ]
      }
    }
  }
];
const props1 = {
  data,
  isLoading: false,
  columns,
  networkStatus: 1,
  error: undefined,
  refetch: jest.fn(),
  LoadingComponent: undefined,
  ErrorComponent: undefined
};

describe('DataTable component tests', () => {
  it('Snapshot tests', async () => {
    const wrapper = mount(
      <MockedProvider mocks={mocks} addTypename={false}>
        <DataTable {...props1} />
      </MockedProvider>
    );
    await new Promise(resolve => setTimeout(resolve));
    wrapper.update();
    expect(wrapper.find(DataTable)).toMatchSnapshot();
  });
});
