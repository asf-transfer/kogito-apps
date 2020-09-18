import React from 'react';
import KogitoPageLayout from '../KogitoPageLayout';
import { getWrapper } from '../../../../utils/OuiaUtils';
import { act } from 'react-dom/test-utils';
import * as Keycloak from "../../../../utils/KeycloakClient";


const props = {
  children: <React.Fragment>children rendered</React.Fragment>,
  BrandSrc: '../../../../static/kogito.png',
  PageNav: <React.Fragment>page Navigation elements</React.Fragment>,
  BrandAltText: 'Kogito logo',
  BrandClick: jest.fn()
};

jest.mock('../../../Molecules/PageToolbar/PageToolbar');

describe('KogitoPageLayout component tests', () => {
  const isAuthEnabledMock = jest.spyOn(Keycloak, 'isAuthEnabled');
  isAuthEnabledMock.mockReturnValue(false);
  it('snapshot tests', () => {
    const wrapper = getWrapper(
      <KogitoPageLayout {...props} />,
      'KogitoPageLayout'
    );
    expect(wrapper).toMatchSnapshot();
  });
  it('check isNavOpen boolean', () => {
    const wrapper = getWrapper(
      <KogitoPageLayout {...props} />,
      'KogitoPageLayout'
    );
    const event = {
      target: {}
    } as React.MouseEvent<HTMLInputElement>;
    act(() => {
      wrapper.find('Button').prop('onClick')(event);
      wrapper.update();
    });
    expect(wrapper.find('PageSidebar').prop('isNavOpen')).toBeTruthy();
  });
});
