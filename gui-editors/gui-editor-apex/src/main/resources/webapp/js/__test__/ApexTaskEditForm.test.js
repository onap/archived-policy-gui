/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation.
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
   uuid: 'testUUID'
};

let data = {
   messages: {
      message: [
         '{"apexContextSchema": {"key":{"name": "name1", "version": "version1"}}, "apexTask":{"key":{"name": "name1", "version": "version1"}},' +
         '"apexContextAlbum":{"key":{"name": "name1", "version": "version1"}},"apexEvent":{"key":{"name": "name1", "version": "version1"}},' +
         '"apexPolicy":{"policyKey":{"name": "name1", "version": "version1"}}, "apexKeyInfo":{"key":{"name": "name1", "version": "version1"}}}'
      ]
   },
   ok: true
};

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
   const mock_activate = jest.fn(mod.editTaskForm_editTask_inner);
   mock_activate('test', 'name', 'version', 'Edit');
   expect(mock_activate).toBeCalled();
});




