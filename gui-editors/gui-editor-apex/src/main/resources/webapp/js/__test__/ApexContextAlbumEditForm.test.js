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

const mod = require('../ApexContextAlbumEditForm');

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