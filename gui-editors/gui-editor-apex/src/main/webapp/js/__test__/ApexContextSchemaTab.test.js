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

const mod = require('../ApexContextSchemaTab');

let data = {
    messages: [
        '{"apexContextSchema": {"key":{"name": "name1", "version": "version1"}}, "apexTask":{"key":{"name": "name1", "version": "version1"}},' +
        '"apexContextAlbum":{"key":{"name": "name1", "version": "version1"}},"apexEvent":{"key":{"name": "name1", "version": "version1"}},' +
        '"apexPolicy":{"policyKey":{"name": "name1", "version": "version1"}}, "apexKeyInfo":{"key":{"name": "name1", "version": "version1"}}}'
    ],
    ok: true
};

test('Test activateContextSchema', () => {
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    const mock_activate = jest.fn(mod.contextSchemaTab_activate);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('Test deactivate', () => {
    const mock_deactivate = jest.fn(mod.contextSchemaTab_deactivate);
    mock_deactivate();
    expect(mock_deactivate).toBeCalledWith();
});

test('Test reset', () => {
    const mock_deactivate = jest.fn(mod.contextSchemaTab_reset);
    mock_deactivate();
    expect(mock_deactivate).toBeCalledWith();
});
