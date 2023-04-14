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

const mod = require('../ApexModelHandling');
const ApexResultForm = require("../ApexResultForm");

afterEach(() => {
   document.body.innerHTML = '';
});

test('Test modelHandling_analyse', (done) => {
   const data = {
      useHttps: 'useHttps',
      hostname: 'hostname',
      port: 'port',
      username: 'username',
      password: 'password',
      messages: [
         '{"apexKeyInfo": null}'
      ],
      content: ['01'],
      result: 'SUCCESS'
   };
   const expectedMessage  = '{"apexKeyInfo": null}';
   document.body.innerHTML = '<div id="mainArea"></div>';

   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, null);
   });

   ApexResultForm.resultForm_activate = jest.fn((element, heading, message) => {
      expect(element).not.toBeNull();
      expect(heading).toBe('Model Analysis Result');
      expect(message).toBe(expectedMessage);
      done();
   });

   mod.modelHandling_analyse();
});

test('Test modelHandling_validate', (done) => {
   const data = {
      useHttps: 'useHttps',
      hostname: 'hostname',
      port: 'port',
      username: 'username',
      password: 'password',
      messages: [
         '{"apexKeyInfo": null}',
         '{"apexPolicy": null}',
         '{"apexEvent": null}'
      ],
      content: ['01'],
      result: 'SUCCESS'
   };

   const expectedMessage  = '{"apexPolicy": null}\n{"apexEvent": null}\n';

   document.body.innerHTML = '<div id="mainArea"></div>';

   $.ajax = jest.fn().mockImplementation((args) => {
      args.success(data, null, null);
   });

   ApexResultForm.resultForm_activate = jest.fn((element, heading, message) => {
      expect(element).not.toBeNull();
      expect(heading).toBe('Model Validation Result');
      expect(message).toBe(expectedMessage);
      done();
   });

   mod.modelHandling_validate();
});
