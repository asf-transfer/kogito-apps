import '@patternfly/patternfly/patternfly.css';
import React from 'react';
import ReactDOM from 'react-dom';
import { ApolloClient } from 'apollo-client';
import { setContext } from 'apollo-link-context';
import { ApolloProvider } from 'react-apollo';
import { Nav, NavList, NavItem } from '@patternfly/react-core';
import {
  ServerUnavailable,
  appRenderWithAxiosInterceptorConfig,
  getToken,
  isAuthEnabled,
  KogitoAppContextProvider
} from '@kogito-apps/common';
import { HttpLink } from 'apollo-link-http';
import { onError } from 'apollo-link-error';
import { InMemoryCache, NormalizedCacheObject } from 'apollo-cache-inmemory';
import managementConsoleLogo from './static/managementConsoleLogo.svg';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import PageLayout from './components/Templates/PageLayout/PageLayout';

const httpLink = new HttpLink({
  // @ts-ignore
  uri: window.DATA_INDEX_ENDPOINT || process.env.KOGITO_DATAINDEX_HTTP_URL
});

const PageNav = (
  <Nav aria-label="Nav" theme="dark">
    <NavList>
      <NavItem>Process Instances</NavItem>
      <NavItem>Domain Explorer</NavItem>
    </NavList>
  </Nav>
);

const fallbackUI = onError(({ networkError }: any) => {
  if (networkError && networkError.stack === 'TypeError: Failed to fetch') {
    return ReactDOM.render(
      <ApolloProvider client={client}>
        <KogitoAppContextProvider>
          <ServerUnavailable
            PageNav={PageNav}
            src={managementConsoleLogo}
            alt={'Management Console Logo'}
          />
        </KogitoAppContextProvider>
      </ApolloProvider>,
      document.getElementById('root')
    );
  }
});

const setGQLContext = setContext((_, { headers }) => {
  if (isAuthEnabled()) {
    const token = getToken();
    return {
      headers: {
        ...headers,
        authorization: token ? `Bearer ${token}` : ''
      }
    };
  }
});

const cache = new InMemoryCache();
const client: ApolloClient<NormalizedCacheObject> = new ApolloClient({
  cache,
  link: setGQLContext.concat(fallbackUI.concat(httpLink))
});

const appRender = () => {
  ReactDOM.render(
    <ApolloProvider client={client}>
      <KogitoAppContextProvider>
        <BrowserRouter>
          <Switch>
            <Route path="/" component={PageLayout} />
          </Switch>
        </BrowserRouter>
      </KogitoAppContextProvider>
    </ApolloProvider>,
    document.getElementById('root')
  );
};

appRenderWithAxiosInterceptorConfig(() => appRender());
