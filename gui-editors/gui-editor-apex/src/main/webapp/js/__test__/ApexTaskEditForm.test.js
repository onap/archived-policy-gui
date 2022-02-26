/*
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

const mod = require('../ApexTaskEditForm');
const apexUtils = require('../ApexUtils');
const apexTaskTab = require('../ApexTaskTab');
const keyInformationTab_reset = require('../ApexKeyInformationTab');

const contextSchema = {
   name: 'testName',
   version: '0.0.1',
   schemaFlavour: 'testFlav',
   schemaDefinition: 'testDef',
   uuid: 'testUUID',
   description: 'testDesc'
};

const task = {
   key: {
      name: 'testName',
      version: 'testVersion'
   },
   uuid: 'testUUID',
   description: 'Description of task',
   taskLogic: {
      logicFlavour: 'testFlav'
   },
   inputFields : {entry: [{key: "key1", value: {fieldSchemaKey: { name : "name2",  version : "version2"}}}]},
   outputFields : {entry: [{key: "key01", value: {fieldSchemaKey: { name : "name02",  version : "version02"}}}]},
   taskParameters: {entry: [{key: 'testKey',value: {defaultValue: 'testValue'}}]},
   contextAlbumReference : [{name : 'contextEntry.name',version : 'contextEntry.version', displaytext : 'contextName'},
      {name : 'contextEntry.name2',version : 'contextEntry.version2', displaytext : 'contextName2'},
      {name : 'contextEntry.name3',version : 'contextEntry.version3', displaytext : 'contextName3'}]
};

let data = {
   messages: [
      '{"apexContextSchema": {"key":{"name": "name1", "version": "version1"}}, "apexTask":{"key":{"name": "name1", "version": "version1"}},' +
      '"apexContextAlbum":{"key":{"name": "name1", "version": "version1"}},"apexEvent":{"key":{"name": "name1", "version": "version1"}},' +
      '"apexPolicy":{"policyKey":{"name": "name1", "version": "version1"}}, "apexKeyInfo":{"key":{"name": "name1", "version": "version1"}}}'
   ],
   result: 'SUCCESS'
};

 let contextAlbumReference = {
   "name" : "TestAlbum",
   "version" : "0.0.1"
};

const parentTBody = document.createElement('table');

test('Test editTaskForm_activate CREATE', () => {
   const mock_activate = jest.fn(mod.editTaskForm_activate);
   mock_activate('test', 'CREATE', 'task', contextSchema, 'album');
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_activate EDIT', () => {
   const mock_activate = jest.fn(mod.editTaskForm_activate);
   mock_activate('test', 'EDIT', task, contextSchema, 'album');
   expect(mock_activate).toBeCalled();
});

test('Test Create Task', () => {
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   const mock_activate = jest.fn(mod.editTaskForm_createTask);
   mock_activate('test');
   expect(mock_activate).toBeCalled();
});

test('Test Delete Task', () => {
   global.confirm = () => true
   global.window.restRootURL = () => 'http://localhost'
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   jest.spyOn(apexTaskTab, 'taskTab_reset').mockReturnValueOnce(null);
   jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.editTaskForm_deleteTask);
   mock_activate('test');
   expect(mock_activate).toBeCalled();
});

test('Test Edit Task Inner', () => {
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   jest.spyOn(apexTaskTab, 'taskTab_reset').mockReturnValueOnce(null);
   jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.editTaskForm_editTask_inner);
   mock_activate('test', 'name', 'version', 'Edit');
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_addTaskInputField', () => {
   const mock_activate = jest.fn(mod.editTaskForm_addTaskInputField);
   let contextSchemas = new Array();
   contextSchemas.push(contextSchema);
   mock_activate(parentTBody, true, 'name', null, contextSchema, contextSchemas);
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_addTaskOutputField', () => {
   const mock_activate = jest.fn(mod.editTaskForm_addTaskOutputField);
   let contextSchemas = new Array();
   contextSchemas.push(contextSchema);
   mock_activate(parentTBody, true, 'name', null, contextSchema, contextSchemas);
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_addTaskParameter', () => {
   const mock_activate = jest.fn(mod.editTaskForm_addTaskParameter);
   mock_activate(parentTBody, true, 'name', null);
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_addTaskContext', () => {
   const mock_activate = jest.fn(mod.editTaskForm_addTaskContext);
   let contextAlbums = new Array();
   contextAlbums.push(contextAlbumReference);
   mock_activate(parentTBody, true, 'name', null, contextAlbumReference, contextAlbums);
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_generateUUIDPressed', () => {
   let documentSpy = jest.spyOn(document, 'getElementById');
   let editTaskFormUuidInput = document.createElement("editTaskFormUuidInput");
   documentSpy.mockReturnValue(editTaskFormUuidInput);
   const mock_activate = jest.fn(mod.editTaskForm_generateUUIDPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_generateDescriptionPressed', () => {
   let documentSpy = jest.spyOn(document, 'getElementById');
   let editTaskFormDescriptionTextArea = document.createElement("editTaskFormDescriptionTextArea");
   documentSpy.mockReturnValue(editTaskFormDescriptionTextArea);
   const mock_activate = jest.fn(mod.editTaskForm_generateDescriptionPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_cancelPressed', () => {
   jest.spyOn(apexTaskTab, 'taskTab_reset').mockReturnValueOnce(null);
   jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.editTaskForm_cancelPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_submitPressed', () => {
   jest.spyOn(apexTaskTab, 'taskTab_reset').mockReturnValueOnce(null);
   jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);

   let documentSpy = jest.spyOn(document, 'getElementById');
   let elementMock = document.createElement("editTaskForm");
   elementMock.setAttribute("createEditOrView", "CREATE");
   elementMock.value = 'logictype'
   documentSpy.mockReturnValue(elementMock);

   const mock_activate = jest.fn(mod.editTaskForm_submitPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test editTaskForm_submitPressed with page', () => {
   const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
   jest.spyOn(apexTaskTab, 'taskTab_reset').mockReturnValueOnce(null);
   jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);

   document.documentElement.innerHTML = '<html><head></head><body>' +
   '<table id="editTaskFormInputFieldsTable" value="v0">' +
   '<tr class="table" inputfield_id="a1" outputfield_id="b1" param_id="c1" context_id="d1" value="v1"><td>cell1</td><td>cell2</td></tr>' +
   '<tr class="table" inputfield_id="a2" outputfield_id="b2" param_id="c2" context_id="d2" value="v2"><td>cell3</td><td>cell4</td></tr>' +
   '<tr class="table" inputfield_id="a3" outputfield_id="b3" param_id="c3" context_id="d3" value="v3"><td>cell5</td><td>cell6</td></tr>' +
   '</table>' +
   '</body></html>';
   let documentSpy = jest.spyOn(document, 'getElementById');
   let elementMock = document.createElement("editTaskFormInputFieldsTable");
   elementMock.value = 'notNullValue';
   elementMock.selectedOption = {"name": "name1", "version": "version1", "displaytext": "t"};
   elementMock.checked = {"name": "nameOpt", "version": "versionOpt"};
   elementMock.setAttribute("createEditOrView", "EDIT")
   elementMock.rows = document.getElementById("editTaskFormInputFieldsTable").rows;
   console.log(elementMock.rows);
   documentSpy.mockReturnValue(elementMock);

   const mock_activate = jest.fn(mod.editTaskForm_submitPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});
