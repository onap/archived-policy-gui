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

const mod = require('../ApexPolicyEditForm_State');

const contextAlbum = {
    name: 'testName',
    version: '0.0.1',
    scope: 'test',
    uuid: 'testUUID',
    description: 'testDesc',
    writeable: true
};
const contextSchema = {
    name: 'testName',
    version: '0.0.1',
    schemaFlavour: 'testFlav',
    schemaDefinition: 'testDef',
    uuid: 'testUUID',
    description: 'testDesc'
}

const task = {
    key: {
        name: 'testName',
        version: 'testVersion'
    },
    uuid: 'testUUID'
};

const event = {
    key: {
        name: 'testName',
        version: '0.0.1',
    },
    nameSpace: 'test',
    source: 'test',
    target: 'test',
    uuid: 'test',
    description: 'test',
}

const policy = {
    policyKey: {
        name: 'testName',
        version: 'testVersion',
        uuid: 'testUUID'
    },
    uuid: 'testUUID'
}

const state = {
    trigger: {
        name: 'testName',
        version: '0.0.1',
    }
}

const parentTBody = document.createElement('table');

test('Test editPolicyForm_State_generateStateDiv CREATE', () => {
    const mock_editPolicyForm_State_generateStateDiv = jest.fn(mod.editPolicyForm_State_generateStateDiv);
    mock_editPolicyForm_State_generateStateDiv('CREATE', policy, 'stateName', state, task, event, contextAlbum, contextSchema);
    expect(mock_editPolicyForm_State_generateStateDiv).toBeCalledWith('CREATE', policy, 'stateName', state, task, event, contextAlbum, contextSchema);
});

test('Test editPolicyForm_State_generateStateDiv VIEW', () => {
    const mock_editPolicyForm_State_generateStateDiv = jest.fn(mod.editPolicyForm_State_generateStateDiv);
    mock_editPolicyForm_State_generateStateDiv('VIEW', policy, 'stateName', state, task, event, contextAlbum, contextSchema);
    expect(mock_editPolicyForm_State_generateStateDiv).toBeCalledWith('VIEW', policy, 'stateName', state, task, event, contextAlbum, contextSchema);
});

test('Test editPolicyForm_State_generateStateDiv EDIT', () => {
    const mock_editPolicyForm_State_generateStateDiv = jest.fn(mod.editPolicyForm_State_generateStateDiv);
    mock_editPolicyForm_State_generateStateDiv('EDIT', policy, 'stateName', state, task, event, contextAlbum, contextSchema);
    expect(mock_editPolicyForm_State_generateStateDiv).toBeCalledWith('EDIT', policy, 'stateName', state, task, event, contextAlbum, contextSchema);
});

test('Test editPolicyForm_State_addStateLogicOutput', () => {
    const mock_editPolicyForm_State_addStateLogicOutput = jest.fn(mod.editPolicyForm_State_addStateLogicOutput);
    mock_editPolicyForm_State_addStateLogicOutput(parentTBody, false, 'stateName', state, 'outputName', 'logic', 'flavour');
    expect(mock_editPolicyForm_State_addStateLogicOutput).toBeCalledWith(parentTBody, false, 'stateName', state, 'outputName', 'logic', 'flavour');
});

test('Test editPolicyForm_State_addStateDirectOutput', () => {
    const mock_editPolicyForm_State_addStateDirectOutput = jest.fn(mod.editPolicyForm_State_addStateDirectOutput);
    mock_editPolicyForm_State_addStateDirectOutput(parentTBody, false, 'stateName', state, 'outputName', state, event, 'options', event);
    expect(mock_editPolicyForm_State_addStateDirectOutput).toBeCalledWith(parentTBody, false, 'stateName', state, 'outputName', state, event, 'options', event);
});

test('Test editPolicyForm_State_addPolicyContext', () => {
    const mock_editPolicyForm_State_addPolicyContext = jest.fn(mod.editPolicyForm_State_addPolicyContext);
    mock_editPolicyForm_State_addPolicyContext(parentTBody, false, 'stateName', 'contextName', 'ref', contextAlbum);
    expect(mock_editPolicyForm_State_addPolicyContext).toBeCalledWith(parentTBody, false, 'stateName', 'contextName', 'ref', contextAlbum);
});

test('Test editPolicyForm_State_addPolicyTask', () => {
    const mock_editPolicyForm_State_addPolicyTask = jest.fn(mod.editPolicyForm_State_addPolicyTask);
    mock_editPolicyForm_State_addPolicyTask(parentTBody, false, false, state, 'stateName', 'ref', task, 'options');
    expect(mock_editPolicyForm_State_addPolicyTask).toBeCalledWith(parentTBody, false, false, state, 'stateName', 'ref', task, 'options');

});

test.todo('Test editPolicyForm_State_getDirectOutputMappingOptions');
test.todo('Test editPolicyForm_State_getStateBean');
test.todo('Test editPolicyForm_State_getLogicOutputMappingOptions');