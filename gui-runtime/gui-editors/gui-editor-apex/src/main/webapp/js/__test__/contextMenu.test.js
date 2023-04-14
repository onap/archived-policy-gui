/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

const mod = require('../contextMenu');
const apexContextEdit = require('../ApexContextSchemaEditForm');
const apexEventEdit = require('../ApexEventEditForm');
const apexTaskEdit = require('../ApexTaskEditForm');
const apexPolicyEdit = require('../ApexPolicyEditForm');
const apexContextAlbumEdit = require('../ApexContextAlbumEditForm');

test('Test rightClickMenu', () => {
   let documentSpy = jest.spyOn(document, 'getElementById');
   let elementMock = document.createElement("rightClickMenu");
   documentSpy.mockReturnValue(elementMock);
   const event = new MouseEvent('click');
   const mock_activate = jest.fn(mod.rightClickMenu);
   mock_activate(event, 'type', 'name', 'version');
   expect(mock_activate).toBeCalled();
});

test('Test rightClickMenuCreate when Type is CONTEXTSCHEMA', () => {
   global.confirm = () => true
   jest.spyOn(apexContextEdit, 'editContextSchemaForm_createContextSchema').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.rightClickMenuCreate);
   mock_activate('parent', 'CONTEXTSCHEMA');
   expect(mock_activate).toBeCalled();
});

test('Test rightClickMenuCreate when Type is EVENT or TASK or POLICY or CONTEXTALBUM or EMPTY', () => {
   global.confirm = () => true
   jest.spyOn(apexEventEdit, 'editEventForm_createEvent').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.rightClickMenuCreate);
   mock_activate('parent', 'EVENT');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'TASK');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'POLICY');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'CONTEXTALBUM');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', '');
   expect(mock_activate).toBeCalled();

});

test('Test rightClickMenuView when Type is CONTEXTSCHEMA or EVENT or TASK or POLICY or CONTEXTALBUM or EMPTY', () => {
   global.confirm = () => true
   jest.spyOn(apexContextEdit, 'editContextSchemaForm_viewContextSchema').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.rightClickMenuView);
   mock_activate('parent', 'CONTEXTSCHEMA');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'EVENT');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'TASK');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'POLICY');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'CONTEXTALBUM');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', '');
   expect(mock_activate).toBeCalled();
});

test('Test rightClickMenuEdit when Type is CONTEXTSCHEMA or EVENT or TASK or POLICY or CONTEXTALBUM or EMPTY', () => {
   global.confirm = () => true
   jest.spyOn(apexContextEdit, 'editContextSchemaForm_viewContextSchema').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.rightClickMenuEdit);
   mock_activate('parent', 'CONTEXTSCHEMA');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'EVENT');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'TASK');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'POLICY');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'CONTEXTALBUM');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', '');
   expect(mock_activate).toBeCalled();
});

test('Test rightClickMenuDelete when Type is CONTEXTSCHEMA or EVENT or TASK or POLICY or CONTEXTALBUM or EMPTY', () => {
   jest.spyOn(apexContextEdit, 'editContextSchemaForm_deleteContextSchema').mockReturnValueOnce(null);
   jest.spyOn(apexEventEdit, 'editEventForm_deleteEvent').mockReturnValueOnce(null);
   jest.spyOn(apexTaskEdit, 'editTaskForm_deleteTask').mockReturnValueOnce(null);
   jest.spyOn(apexPolicyEdit, 'editPolicyForm_deletePolicy').mockReturnValueOnce(null);
   jest.spyOn(apexContextAlbumEdit, 'editContextAlbumForm_deleteContextAlbum').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.rightClickMenuDelete);
   mock_activate('parent', 'CONTEXTSCHEMA');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'EVENT');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'TASK');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'POLICY');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', 'CONTEXTALBUM');
   expect(mock_activate).toBeCalled();
   mock_activate('parent', '');
   expect(mock_activate).toBeCalled();
});