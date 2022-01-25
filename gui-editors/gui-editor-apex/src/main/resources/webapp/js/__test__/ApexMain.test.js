/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation
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

const mod = require('../ApexMain');


test('Test main_getRestRootURL', () => {
    const mock_main_getRestRootURL = jest.fn(mod.main_getRestRootURL);
    mock_main_getRestRootURL();
    expect(mock_main_getRestRootURL).toBeCalled();
});

test('Test clearLocalStorage', () => {
    const mock_clearLocalStorage = jest.fn(mod.clearLocalStorage);
    mock_clearLocalStorage();
    expect(mock_clearLocalStorage).toBeCalled();
});