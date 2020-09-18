import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import ApolloClient from 'apollo-client';
import { ApolloProvider } from 'react-apollo';
import '@patternfly/patternfly/patternfly.css';
import { Nav, NavItem, NavList } from '@patternfly/react-core';
import { HttpLink } from 'apollo-link-http';
import { setContext } from 'apollo-link-context';
import { onError } from 'apollo-link-error';
import { InMemoryCache, NormalizedCacheObject } from 'apollo-cache-inmemory';
import {
  appRenderWithAxiosInterceptorConfig,
  getToken,
  isAuthEnabled,
  KogitoAppContextProvider,
  ServerUnavailable
} from '@kogito-apps/common';
import PageLayout from './components/Templates/PageLayout/PageLayout';
import TaskConsoleContextProvider from './context/TaskConsoleContext/TaskConsoleContextProvider';
import taskConsoleLogo from './static/taskConsoleLogo.svg';

const httpLink = new HttpLink({
  // @ts-ignore
  uri: window.DATA_INDEX_ENDPOINT || process.env.KOGITO_DATAINDEX_HTTP_URL
});

const PageNav = (
  <Nav aria-label="Nav" theme="dark">
    <NavList>
      <NavItem>User Tasks</NavItem>
      <NavItem>User tasks with filters</NavItem>
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
            src={taskConsoleLogo}
            alt={'Task Console Logo'}
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
        <TaskConsoleContextProvider>
          <BrowserRouter>
            <Switch>
              <Route path="/" component={PageLayout} />
            </Switch>
          </BrowserRouter>
        </TaskConsoleContextProvider>
      </KogitoAppContextProvider>
    </ApolloProvider>,
    document.getElementById('root')
  );
};

appRenderWithAxiosInterceptorConfig(() => appRender());
