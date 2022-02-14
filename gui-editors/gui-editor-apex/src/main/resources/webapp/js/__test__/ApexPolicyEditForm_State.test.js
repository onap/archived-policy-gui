/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation
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

test('Test editPolicyForm_State_getDirectOutputMappingOptions', () => {
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("editPolicyFormDirOutputsTable_stateName");
    elementMock.rows = '1'
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_State_getDirectOutputMappingOptions);
    mock_activate('stateName');
    expect(mock_activate).toBeCalled();

});

test('Test editPolicyForm_State_getStateBean', () => {
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("editPolicyFormDirOutputsTable_stateName");
    elementMock.rows = '1'
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_State_getStateBean);
    mock_activate('stateName');
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_State_getStateBean StateName is Null', () => {
    jest.spyOn(global.console, 'error').mockImplementation(() => {});
    mod.editPolicyForm_State_getStateBean(null);
    expect(console.error).toHaveBeenCalledTimes(2);
    mod.editPolicyForm_State_getStateBean('FakeState');
    expect(console.error).toHaveBeenCalledTimes(4);
    global.console.error.mockRestore();
});

test('Test editPolicyForm_State_getStateBean with mock', () => {
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("editPolicyFormDirOutputsTable_stateName");
    elementMock.rows = '1'
    elementMock.key = {"name": "name1", "version": "version1"}
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_State_getStateBean);
    mock_activate('stateName');
    expect(mock_activate).toBeCalled();
});


test('Test editPolicyForm_State_getStateBean with page', () => {
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<div id="editPolicyFormStateDiv_stateName"></div>' +
    '<div id="editPolicyFormTrigger_stateName_dropdownList" selectedOption="1"></div>' +
    '<table id="editPolicyFormContextsTable_stateName"context_id="a0">' +
    '<tr class="table" context_id="a1" output_id="b1" finalizer_id="c1" task_id="d1"><td>cell 1</td><td>cell 2</td></tr>' +
    '<tr class="table" context_id="a2" output_id="b2" finalizer_id="c2" task_id="d2"><td>cell 3</td><td>cell4</td></tr>' +
    '<tr class="table" context_id="a3" output_id="b3" finalizer_id="c3" task_id="d3"><td>cell 5</td><td>cell6</td></tr>' +
    '</table>' +
    '</body></html>';
    
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("editPolicyFormDirOutputsTable_stateName");
    elementMock.setAttribute("context_id", "a0");
    elementMock.setAttribute("task_id", "d0");
    elementMock.rows = document.getElementById("editPolicyFormContextsTable_stateName").rows;
    elementMock.key = {"name": "name1", "version": "version1"};
    elementMock.selectedOption = {"album": { "key": { "name": 'testAlbumName', "version": '0.0.1'}}, "name": "name1", "version": "version1", "event": { "key": { "name": 'testEventName', "version": '0.1.1'}}};
    elementMock.value = "localName";
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_State_getStateBean);
    mock_activate('stateName');
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_State_getLogicOutputMappingOptions', () => {
    const mock_activate = jest.fn(mod.editPolicyForm_State_getLogicOutputMappingOptions);
    mock_activate(null);
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_State_getLogicOutputMappingOptions with page', () => {
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<table id="editPolicyFormLogicOutputsTable_stateName"context_id="a0">' +
    '<tr class="table" context_id="a1" output_id="b1" finalizer_id="c1" task_id="d1"><td>cell 1</td><td>cell 2</td></tr>' +
    '<tr class="table" context_id="a2" output_id="b2" finalizer_id="c2" task_id="d2"><td>cell 3</td><td>cell4</td></tr>' +
    '<tr class="table" context_id="a3" output_id="b3" finalizer_id="c3" task_id="d3"><td>cell 5</td><td>cell6</td></tr>' +
    '</table>' +
    '</body></html>';
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("editPolicyFormLogicOutputsTable_stateName");
    elementMock.value = "localName";
    elementMock.rows = document.getElementById("editPolicyFormLogicOutputsTable_stateName").rows;
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_State_getLogicOutputMappingOptions);
    mock_activate('stateName');
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_State_getDirectOutputMappingOptions with page', () => {
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<table id="editPolicyFormDirOutputsTable_stateName"context_id="a0">' +
    '<tr class="table" context_id="a1" output_id="b1" finalizer_id="c1" task_id="d1"><td>cell 1</td><td>cell 2</td></tr>' +
    '<tr class="table" context_id="a2" output_id="b2" finalizer_id="c2" task_id="d2"><td>cell 3</td><td>cell4</td></tr>' +
    '<tr class="table" context_id="a3" output_id="b3" finalizer_id="c3" task_id="d3"><td>cell 5</td><td>cell6</td></tr>' +
    '</table>' +
    '</body></html>';
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("editPolicyFormDirOutputsTable_stateName");
    elementMock.value = "localName";
    elementMock.rows = document.getElementById("editPolicyFormDirOutputsTable_stateName").rows;
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_State_getDirectOutputMappingOptions);
    mock_activate('stateName');
    expect(mock_activate).toBeCalled();
});