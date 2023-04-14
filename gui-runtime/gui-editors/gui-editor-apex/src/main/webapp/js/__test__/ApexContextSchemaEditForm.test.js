/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2022 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

const mod = require('../ApexContextSchemaEditForm');
const apexUtils = require('../ApexUtils');
const apexContextSchemaTab = require('../ApexContextSchemaTab');
const keyInformationTab_reset = require('../ApexKeyInformationTab');
const apexAjax = require('../ApexAjax');
const formUtils_generateDescription = require('../ApexFormUtils');

let data = {
    messages: [],
    result: 'SUCCESS'
};
const contextSchema = {
    name: 'testName',
    version: '0.0.1',
    schemaFlavour: 'testFlav',
    schemaDefinition: 'testDef',
    uuid: 'testUUID',
    description: 'testDesc'
}

test('Test editContextSchemaForm_createContextSchema', () => {
    const mock_editContextSchemaForm_createContextSchema = jest.fn(mod.editContextSchemaForm_createContextSchema);
    mock_editContextSchemaForm_createContextSchema('parentTest', 'CREATE', contextSchema);
    expect(mock_editContextSchemaForm_createContextSchema).toBeCalled();
});

test('Test Delete Context Schema', () => {
    global.confirm = () => true
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
    jest.spyOn(apexContextSchemaTab, 'contextSchemaTab_reset').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editContextSchemaForm_deleteContextSchema);
    mock_activate('parent', 'name', 'version');
    expect(mock_activate).toBeCalled();
});

test('Test View Context Schema', () => {
    jest.spyOn(apexAjax, 'ajax_getWithKeyInfo').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editContextSchemaForm_viewContextSchema);
    mock_activate('parent', 'name', 'version');
    expect(mock_activate).toBeCalled();
});

test('Test Activate Context Schema', () => {
    const mock_activate = jest.fn(mod.editContextSchemaForm_activate);
    mock_activate('parent', 'operation', contextSchema);
    expect(mock_activate).toBeCalled();
});

test('Test Generate UUID Pressed', () => {
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("editContextSchemaFormUuidInput");
    elementMock.value = 'one'
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editContextSchemaForm_generateUUIDPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test Generate Description Pressed', () => {
    jest.spyOn(formUtils_generateDescription, 'formUtils_generateDescription').mockReturnValueOnce(null);
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("editContextSchemaFormDescriptionTextArea");
    elementMock.value = 'one'
    documentSpy.mockReturnValue(elementMock);
    const mock_activate = jest.fn(mod.editContextSchemaForm_generateDescriptionPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test Cancel Pressed', () => {
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    jest.spyOn(apexContextSchemaTab, 'contextSchemaTab_reset').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editContextSchemaForm_cancelPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test Submit Pressed', () => {
    jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
    jest.spyOn(apexContextSchemaTab, 'contextSchemaTab_reset').mockReturnValueOnce(null);
    const mock_activate = jest.fn(mod.editContextSchemaForm_submitPressed);
    mock_activate();
    expect(mock_activate).toBeCalled();
});
