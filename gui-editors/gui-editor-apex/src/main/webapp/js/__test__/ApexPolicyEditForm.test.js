/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2022 Nordix Foundation
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
const apexUtils = require('../ApexUtils');
const apexPageControl = require('../ApexPageControl');
const apexPolicyTab = require('../ApexPolicyTab');
const keyInformationTab_reset = require('../ApexKeyInformationTab');
const mod = require('../ApexPolicyEditForm');
const policy = {
    policyKey: {
        name: 'testName',
        version: 'testVersion',
        uuid: 'testUUID'
    },
    uuid: 'testUUID'
}
let data = {
    messages: [
        JSON.stringify({
            key: {name: "name1", version: "0.0.1"},
            policyKey: {name: "name1", version: "0.0.1"},
        })
    ],
    result: 'SUCCESS'
};

test('Test Create Policy', () => {
    const mock_activate = jest.fn(mod.editPolicyForm_createPolicy);
    mock_activate('test');
    expect(mock_activate).toBeCalled();
});

test('Test Delete Policy', () => {
    global.confirm = () => true
    global.window.restRootURL = () => 'http://localhost'
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    jest.spyOn(apexPageControl, 'pageControl_successStatus').mockReturnValueOnce(policy);
    jest.spyOn(apexPolicyTab, 'policyTab_reset').mockReturnValueOnce(null);
    jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editPolicyForm_deletePolicy);
    mock_activate('test', policy.policyKey.name, policy.policyKey.version);
    expect(mock_activate).toBeCalled();
});

test('Test View Policy', () => {
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    jest.spyOn(apexPageControl, 'pageControl_successStatus').mockReturnValueOnce(policy);
    const mock_activate = jest.fn(mod.editPolicyForm_viewPolicy);
    mock_activate('test', policy.policyKey.name, policy.policyKey.version);
    expect(mock_activate).toBeCalled();
});

test('Test Edit Policy', () => {
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    const mock_activate = jest.fn(mod.editPolicyForm_editPolicy);
    mock_activate('test', policy.policyKey.name, policy.policyKey.version);
    expect(mock_activate).toBeCalled();
});

test('Test Edit Policy Inner', () => {
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    jest.spyOn(apexPageControl, 'pageControl_successStatus').mockReturnValueOnce(policy);
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editPolicyForm_editPolicy_inner);
    mock_activate('test', policy, 'view');
    expect(mock_activate).toBeCalled();
});

test('Test Add New State Policy', () => {
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("editEventFormNewStateInput");
    elementMock.value = 'one'
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_addNewState);
    const node = document.body.parentNode;
    mock_activate(node, 'New State', policy, 'tasks', 'events', 'contextS', 'contextI');
    expect(mock_activate).toBeCalled();
});

test('Test Add State Policy', () => {
    const mock_activate = jest.fn(mod.editPolicyForm_addState);
    mock_activate('state','test', 'CREATE', policy, 'tasks', 'events', 'contextS', 'contextI');
    expect(mock_activate).toBeCalled();
});

test('Test Generate Description', () => {
    const mock_activate = jest.fn(mod.editPolicyForm_generateDescriptionPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test Submit Pressed', () => {
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    jest.spyOn(apexPolicyTab, 'policyTab_reset').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editPolicyForm_submitPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test Submit Pressed When createEditOrView is CREATE', () => {
    let documentSpy = jest.spyOn(document, 'getElementById');
    let editPolicyFormElementMock = document.createElement("editPolicyForm");
    editPolicyFormElementMock.setAttribute("createEditOrView", "CREATE")
    documentSpy.mockReturnValue(editPolicyFormElementMock)
    const mock_activate = jest.fn(mod.editPolicyForm_submitPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test Update Trigger Event', () => {
    const mock_activate = jest.fn(mod.editPolicyForm_updateTriggerEventOptions);
    mock_activate('events');
    expect(mock_activate).toBeCalled();
});

test('Test Update Trigger Event with firststate', () => {
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<div id="editEventFormSelectFirstState_dropdownList"></div>' +
    '</body></html>';
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.getElementById("editEventFormSelectFirstState_dropdownList");
    elementMock.setAttribute("createEditOrView", "CREATE")
    elementMock.setAttribute("disabled", "false")
    elementMock.selectedOption = {"name": "name1", "version": "version1", "displaytext": "PeriodicEvent"};
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_updateTriggerEventOptions);
    mock_activate('events');
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_getNextStateOptions', () => {
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<div id="editEventFormStates"><div id="#editEventFormStates"><li stateName="state" value="v1">a1</li><li stateName="state" value="v2">a2</li></div></div>' +
    '</body></html>';
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.getElementById("editEventFormStates");
    elementMock.value = 'notNullValue';
    elementMock.setAttribute("stateName", "state");
    documentSpy.mockReturnValue(elementMock);
    let options = mod.editPolicyForm_getNextStateOptions();
    let expected = [{"name" : "NULL", "displaytext" : "None"}];
    expect(options).toStrictEqual(expected);
});

test('Test activate CREATE', () => {
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editPolicyForm_activate);

    mock_activate('test', 'CREATE', policy, 'tasks', 'events', 'contextS', 'contextI');
    expect(mock_activate).toBeCalled();
});

test('Test activate EDIT', () => {
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editPolicyForm_activate);

    mock_activate('test', 'EDIT', policy, 'tasks', 'events', 'contextS', 'contextI');
    expect(mock_activate).toBeCalled();
});

test('Test activate !CREATE/EDIT', () => {
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editPolicyForm_activate);

    mock_activate('test', 'TEST', policy, 'tasks', 'events', 'contextS', 'contextI');
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_editPolicy_inner', () => {
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    const mock_editPolicyForm_editPolicy_inner = jest.fn(mod.editPolicyForm_editPolicy_inner);
    mock_editPolicyForm_editPolicy_inner('formParent', policy, 'VIEW');
    expect(mock_editPolicyForm_editPolicy_inner).toBeCalled();
});

test('Test editPolicyForm_submitPressed CREATE with page', () => {
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    jest.spyOn(apexPolicyTab, 'policyTab_reset').mockReturnValueOnce(null);
    jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<div id="editEventFormStates"><div id="#editEventFormStates"><li stateName="state" value="v1">a1</li><li stateName="state" value="v2">a2</li></div></div>' +
    '</body></html>';
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.getElementById("#editEventFormStates");
    elementMock.value = 'notNullValue';
    elementMock.setAttribute("stateName", "state");
    elementMock.selectedOption = {"name": "name1", "version": "version1"};
    elementMock.setAttribute("createEditOrView", "CREATE")
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_submitPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_submitPressed EDIT with page', () => {
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    jest.spyOn(apexPolicyTab, 'policyTab_reset').mockReturnValueOnce(null);
    jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<div id="editEventFormStates"><div id="#editEventFormStates"><li stateName="state" value="v1">a1</li><li stateName="state" value="v2">a2</li></div></div>' +
    '</body></html>';
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.getElementById("#editEventFormStates");
    elementMock.value = 'notNullValue';
    elementMock.setAttribute("stateName", "state");
    elementMock.selectedOption = {"name": "name1", "version": "version1"};
    elementMock.setAttribute("createEditOrView", "EDIT")
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editPolicyForm_submitPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_cancelPressed', () => {
    jest.spyOn(apexPolicyTab, 'policyTab_reset').mockReturnValueOnce(null);
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editPolicyForm_cancelPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test editPolicyForm_generateUUIDPressed', () => {
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<div id="editPolicyFormUuidInput">test</div>' +
    '</body></html>';
    mod.editPolicyForm_generateUUIDPressed();
    expect(document.getElementById("editPolicyFormUuidInput").value).not.toBeNull();
});
