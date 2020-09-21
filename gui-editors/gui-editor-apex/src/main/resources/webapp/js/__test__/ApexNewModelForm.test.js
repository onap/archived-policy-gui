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

const mod = require('../ApexNewModelForm');

test('test activate', () => {
    document.innerHTML =
        '<div id="newModelFormDiv">' +
        '</div>' +
        '<div id="elementTest">' +
        '</div>';

    const mock_activate = jest.fn(mod.newModelForm_activate);
    mock_activate(document.createElement("elementTest"));
    expect(mock_activate).toBeCalled();
});

test('Test generateUUIDPressed', () => {
    document.innerHTML =
        '<div id="newModelFormUuidInput"></div>';

    const mock_generateUuid = jest.fn(mod.newModelForm_generateUUIDPressed);
    mock_generateUuid.mockImplementation(() => {
        document.createElement("newModelFormUuidInput");
        document.getElementsByTagName("newModelFormUuidInput").value = 'test';
    });
    mock_generateUuid();
    expect(mock_generateUuid).toBeCalled();
});