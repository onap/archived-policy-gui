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

const mod = require('../ApexTaskTab');

let data = {
   messages: [
      JSON.stringify({
         key: {name: "name1", version: "version1"},
         taskLogic: {logicFlavour: "logicFlavour"},
         taskParameters: {entry: [{key: "", value: {fieldSchemaKey: {name: "name"}}}]},
         contextAlbumReference: [{name: "name", version: "version"}],
         policyKey: {name: "name1", version: "version1"},
      })
   ],
   result: 'SUCCESS'
};

test('test dom : taskTab_activate', () => {
   const jqXHR = { status: 200, responseText: "" };
   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, jqXHR);
   });
   const activate_mock = jest.fn(mod.taskTab_activate);
   activate_mock();
   expect(activate_mock).toBeCalled();
});

test('test reset', () => {
   const reset_mock = jest.fn(mod.taskTab_reset);
   reset_mock();
   expect(reset_mock).toBeCalled();
});
