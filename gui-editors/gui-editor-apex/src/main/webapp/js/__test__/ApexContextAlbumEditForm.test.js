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

const mod = require('../ApexContextAlbumEditForm');
const apexUtils = require('../ApexUtils');
const contextAlbumTab_reset = require('../ApexContextAlbumTab');
const keyInformationTab_reset = require('../ApexKeyInformationTab');
const formUtils_generateDescription = require('../ApexFormUtils');

let data = {
   messages: [
      JSON.stringify({key: {name: "name1", version: "0.0.1"}, itemSchema: {}})
   ],
   result: 'SUCCESS'
};

test('Test mock_editContextAlbumForm_activate', () => {
   const mock_editContextAlbumForm_activate = jest.fn(mod.editContextAlbumForm_activate);

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

   mock_editContextAlbumForm_activate('parentTest', 'CREATE', contextAlbum, contextSchema);
   expect(mock_editContextAlbumForm_activate).toHaveBeenCalledTimes(1);
});

test('Test Create Context Album', () => {
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   const mock_activate = jest.fn(mod.editContextAlbumForm_createContextAlbum);
   mock_activate('parentTest');
   expect(mock_activate).toBeCalled();
});

test('Test Delete Context Album', () => {
   global.confirm = () => true
   global.window.restRootURL = () => 'http://localhost'
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
   jest.spyOn(contextAlbumTab_reset, 'contextAlbumTab_reset').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.editContextAlbumForm_deleteContextAlbum);
   mock_activate('parentTest', 'name', 'version');
   expect(mock_activate).toBeCalled();
});

test('Test View Context Album', () => {
   global.window.restRootURL = () => 'http://localhost'
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   const mock_activate = jest.fn(mod.editContextAlbumForm_viewContextAlbum);
   mock_activate('parentTest', 'name', 'version');
   expect(mock_activate).toBeCalled();
});

test('Test Edit Context Album', () => {
   global.window.restRootURL = () => 'http://localhost'
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   const mock_activate = jest.fn(mod.editContextAlbumForm_editContextAlbum);
   mock_activate('parentTest', 'name', 'version');
   expect(mock_activate).toBeCalled();
});

test('Test Submit Pressed', () => {
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValueOnce(null);
   jest.spyOn(contextAlbumTab_reset, 'contextAlbumTab_reset').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.editContextAlbumForm_submitPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test Generate UUID Pressed', () => {
   let documentSpy = jest.spyOn(document, 'getElementById');
   let elementMock = document.createElement("editContextAlbumFormUuidInput");
   elementMock.value = 'one'
   documentSpy.mockReturnValue(elementMock);
   const mock_activate = jest.fn(mod.editContextAlbumForm_generateUUIDPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test Generate Description Pressed', () => {
   jest.spyOn(formUtils_generateDescription, 'formUtils_generateDescription').mockReturnValueOnce(null);
   let documentSpy = jest.spyOn(document, 'getElementById');
   let elementMock = document.createElement("editContextAlbumFormDescriptionTextArea");
   elementMock.value = 'one'
   documentSpy.mockReturnValue(elementMock);
   const mock_activate = jest.fn(mod.editContextAlbumForm_generateDescriptionPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test editContextAlbumForm_cancelPressed', () => {
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValue(null);
   const mock = jest.spyOn(document, 'getElementById').mockReturnValue(null);
   jest.spyOn(contextAlbumTab_reset, 'contextAlbumTab_reset').mockReturnValue(null);
   const mock_activate = jest.fn(mod.editContextAlbumForm_cancelPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
   mock.mockRestore();
});

test('Test Submit Pressed with page', () => {
   global.window.restRootURL = () => 'http://localhost'
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   jest.spyOn(keyInformationTab_reset, 'keyInformationTab_reset').mockReturnValueOnce(null);
   jest.spyOn(apexUtils, 'apexUtils_removeElement').mockReturnValue(null);
   jest.spyOn(contextAlbumTab_reset, 'contextAlbumTab_reset').mockReturnValueOnce(null);

   document.documentElement.innerHTML = '<html><head></head><body>' +
   '<div id="editContextAlbumFormDiv"></div>' +
   '</body></html>';
   let documentSpy = jest.spyOn(document, 'getElementById');
   let elementMock = document.getElementById("editContextAlbumFormDiv");
   elementMock.value = 'APPLICATION';
   elementMock.selectedOption = {"name": "schemaName", "version": "schemaVers"};
   elementMock.checked = false;
   elementMock.setAttribute("createEditOrView", "CREATE");
   documentSpy.mockReturnValue(elementMock);
   const mock_activate = jest.fn(mod.editContextAlbumForm_submitPressed);
   mock_activate();
   expect(mock_activate).toBeCalled();
});
