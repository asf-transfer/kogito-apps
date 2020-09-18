/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { TestUserSystemImpl } from '../TestUserSystem';
import { TEST_USERS, TestUserManagerImpl } from '../TestUserManager';

let userSystem: TestUserSystemImpl;
let onUserChange;

describe('TestUserSystemImpl tests', () => {
  beforeEach(() => {
    onUserChange = jest.fn();
    userSystem = new TestUserSystemImpl(onUserChange);
  });

  it('Test getCurrentUser', () => {
    expect(userSystem.getCurrentUser()).toStrictEqual(TEST_USERS[0]);
  });

  it('Test get UserManager', () => {
    expect(userSystem.getUserManager()).not.toBeNull();
    expect(userSystem.getUserManager()).toBeInstanceOf(TestUserManagerImpl);
  });

  it('Test switch user', () => {
    userSystem.su(TEST_USERS[1].id);

    expect(userSystem.getCurrentUser()).toStrictEqual(TEST_USERS[1]);

    expect(onUserChange).toBeCalledWith(TEST_USERS[1]);
    expect(onUserChange).toBeCalledTimes(1);
  });

  it('Test logout', () => {
    userSystem.su(TEST_USERS[1].id);

    expect(userSystem.getCurrentUser()).toStrictEqual(TEST_USERS[1]);

    userSystem.logout();

    expect(onUserChange).toBeCalledWith(TEST_USERS[0]);
    expect(onUserChange).toBeCalledTimes(2);
  });
});
