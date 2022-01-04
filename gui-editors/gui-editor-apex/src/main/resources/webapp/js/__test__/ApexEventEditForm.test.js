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

const mod = require('../ApexEventEditForm');
const eventTab_reset = require('../ApexEventTab');
const apexUtils = require('../ApexUtils');
const formUtils_generateDescription = require('../ApexFormUtils');
const keyInformationTab_reset = require('../ApexKeyInformationTab');

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

const contextSchema = {
   name: 'testName',
   version: '0.0.1',
   schemaFlavour: 'testFlav',
   schemaDefinition: 'testDef',
   uuid: 'testUUID',
   description: 'testDesc'
}

test('Test Activate', () => {
   const mock_activate = jest.fn(mod.editEventForm_activate);
   mock_activate(null, 'CREATE', null, contextSchema);
   expect(mock_activate).toBeCalled();
});

test('Test Activate Edit', () => {

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

   const mock_activate = jest.fn(mod.editEventForm_activate);
   mock_activate(null, 'EDIT', event, contextSchema);
   expect(mock_activate).toBeCalled();
});

test('Test Activate !=Create/Edit', () => {

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

   const mock_activate = jest.fn(mod.editEventForm_activate);
   mock_activate(null, 'TEST', event, contextSchema);
   expect(mock_activate).toBeCalled();
});

test('Test Delete Event', () => {
   global.confirm = () => true
   global.window.restRootURL = () => 'http://localhost'
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
   jest.spyOn(eventTab_reset, 'eventTab_reset').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.editEventForm_deleteEvent);
   mock_activate('parentTest', 'name', 'version');
   expect(mock_activate).toBeCalled();
});

test('Test Event Edit Form Inner', () => {
   global.window.restRootURL = () => 'http://localhost'
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
   jest.spyOn(apexUtils, 'apexUtils_emptyElement').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.editEventForm_editEvent_inner);
   mock_activate('parentTest', 'name', 'version', 'edit');
   expect(mock_activate).toBeCalled();
});

test('Test View Event', () => {
   const mock_activate = jest.fn(mod.editEventForm_viewEvent);
   mock_activate('parentTest', 'name', 'version');
   expect(mock_activate).toBeCalled();
});

test('Test Edit Event', () => {
   const mock_activate = jest.fn(mod.editEventForm_editEvent);
   mock_activate('parentTest', 'name', 'version');
   expect(mock_activate).toBeCalled();
});

test('Test Create Event', () => {
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   const mock_activate = jest.fn(mod.editEventForm_createEvent);
   mock_activate('parentTest');
   expect(mock_activate).toBeCalled();
});

test('Test Edit Event Generate UUID Pressed', () => {
   let documentSpy = jest.spyOn(document, 'getElementById');
   let elementMock = document.createElement("editContextSchemaFormDescriptionTextArea");
   documentSpy.mockReturnValue(elementMock);
   const mock_activate = jest.fn(mod.editEventForm_generateUUIDPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test Edit Event Generate Description Pressed', () => {
   jest.spyOn(formUtils_generateDescription, 'formUtils_generateDescription').mockReturnValueOnce(null);
   let documentSpy = jest.spyOn(document, 'getElementById');
   let elementMock = document.createElement("editContextSchemaFormDescriptionTextArea");
   documentSpy.mockReturnValue(elementMock);
   const mock_activate = jest.fn(mod.editEventForm_generateDescriptionPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});
