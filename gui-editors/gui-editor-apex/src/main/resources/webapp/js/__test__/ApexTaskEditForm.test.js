/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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
