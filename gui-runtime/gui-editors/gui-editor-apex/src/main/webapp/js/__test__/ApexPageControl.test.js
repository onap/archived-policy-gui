/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021-2022 Nordix Foundation.
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

const mod = require('../ApexPageControl');
const apexContextSchemaTab = require('../ApexContextSchemaTab');
const apexEventTab = require('../ApexEventTab');
const apexAlbumTab = require('../ApexContextAlbumTab');
const apexTaskTab = require('../ApexTaskTab');
const apexPolicyTab = require('../ApexPolicyTab');
const keyInformationTab = require('../ApexKeyInformationTab');

test('Test showPlaceholder show', () => {
   const mock_activate = jest.fn(mod.showPlaceholder);
   mock_activate('show');
   expect(mock_activate).toBeCalled();
});

test('Test showPlaceholder hide', () => {
   const mock_activate = jest.fn(mod.showPlaceholder);
   mock_activate('');
   expect(mock_activate).toBeCalled();
});

test('Test pageControl_status', () => {
   let data = {
      messages: [],
      result: 'SUCCESS'
   };
   const mock_activate = jest.fn(mod.pageControl_status);
   mock_activate(data);
   expect(mock_activate).toBeCalled();
});

test('Test pageControl_status when data is not present', () => {
   let data = {
      messages: []
   };
   const mock_activate = jest.fn(mod.pageControl_status);
   mock_activate(data);
   expect(mock_activate).toBeCalled();
});

test('Test pageControl_restError', () => {
   const mock_activate = jest.fn(mod.pageControl_restError);
   mock_activate('', '', '', '');
   expect(mock_activate).toBeCalled();
});

test('Test pageControl_modelMode', () => {
   jest.spyOn(apexContextSchemaTab, 'contextSchemaTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexEventTab, 'eventTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexAlbumTab, 'contextAlbumTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexTaskTab, 'taskTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexPolicyTab, 'policyTab_activate').mockReturnValueOnce(null);
   jest.spyOn(keyInformationTab, 'keyInformationTab_activate').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.pageControl_modelMode);
   mock_activate('name', 'version', 'fileName');
   expect(mock_activate).toBeCalled();
});

test('Test pageControl_noModelMode', () => {
   jest.spyOn(apexContextSchemaTab, 'contextSchemaTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexEventTab, 'eventTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexAlbumTab, 'contextAlbumTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexTaskTab, 'taskTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexPolicyTab, 'policyTab_activate').mockReturnValueOnce(null);
   jest.spyOn(keyInformationTab, 'keyInformationTab_activate').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.pageControl_noModelMode);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test pageControl_busyMode', () => {
   jest.spyOn(apexContextSchemaTab, 'contextSchemaTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexEventTab, 'eventTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexAlbumTab, 'contextAlbumTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexTaskTab, 'taskTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexPolicyTab, 'policyTab_activate').mockReturnValueOnce(null);
   jest.spyOn(keyInformationTab, 'keyInformationTab_activate').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.pageControl_busyMode);
   mock_activate();
   expect(mock_activate).toBeCalled();
});

test('Test pageControl_readyMode', () => {
   jest.spyOn(apexContextSchemaTab, 'contextSchemaTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexEventTab, 'eventTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexAlbumTab, 'contextAlbumTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexTaskTab, 'taskTab_activate').mockReturnValueOnce(null);
   jest.spyOn(apexPolicyTab, 'policyTab_activate').mockReturnValueOnce(null);
   jest.spyOn(keyInformationTab, 'keyInformationTab_activate').mockReturnValueOnce(null);
   const mock_activate = jest.fn(mod.pageControl_readyMode);
   mock_activate();
   expect(mock_activate).toBeCalled();
});
