/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

const mod = require('../ApexPolicyEditForm');
const policy = {
    policyKey: {
        name: 'testName',
        version: 'testVersion',
        uuid: 'testUUID'
    },
    uuid: 'testUUID'
}

test('Test activate CREATE', () => {
    const mock_activate = jest.fn(mod.editPolicyForm_activate);

    mock_activate('test', 'CREATE', policy, 'tasks', 'events', 'contextS', 'contextI');
    expect(mock_activate).toBeCalled();
});

test('Test activate EDIT', () => {
    const mock_activate = jest.fn(mod.editPolicyForm_activate);

    mock_activate('test', 'EDIT', policy, 'tasks', 'events', 'contextS', 'contextI');
    expect(mock_activate).toBeCalled();
});

test('Test activate !CREATE/EDIT', () => {
    const mock_activate = jest.fn(mod.editPolicyForm_activate);

    mock_activate('test', 'TEST', policy, 'tasks', 'events', 'contextS', 'contextI');
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_editPolicy_inner', () => {
    const  mock_editPolicyForm_editPolicy_inner = jest.fn(mod.editPolicyForm_editPolicy_inner);
    mock_editPolicyForm_editPolicy_inner('formParent', policy, 'VIEW');
    expect(mock_editPolicyForm_editPolicy_inner).toBeCalled();
})