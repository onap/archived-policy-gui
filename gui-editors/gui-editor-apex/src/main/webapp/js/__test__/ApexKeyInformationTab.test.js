/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021-2022 Nordix Foundation
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

const ApexUtils = require("../ApexUtils");
const ApexKeyInformationTab = require("../ApexKeyInformationTab");

test("Test keyInformationTab_activate", () => {
    document.body.innerHTML = '<div id ="keyInformationTab"></div>';
    const data = {
        useHttps: 'useHttps',
        hostname: 'hostname',
        port: 'port',
        username: 'username',
        password: 'password',
        messages: [
            '{"apexKeyInfo": {"UUID": "UUID1", "description": "description1", "key":{"name": "name1", "version":' +
            ' "version1"}}, "objectType": {"key": {"name": "name1", "version": "version1"}}}'
        ],
        content: ['01', '02'],
        result: 'SUCCESS'
    };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, null);
    });
    ApexKeyInformationTab.keyInformationTab_activate();

    const actual = document.getElementById("keyInformationTabContent");
    const expected = /<td>name1:version1<\/td><td><uuid>UUID1<\/uuid><\/td><td><desc>description1<\/desc><\/td>/;
    expect(actual.innerHTML).toMatch(expected);
});

test("Test keyInformationTab_deactivate", (done) => {
    ApexUtils.apexUtils_removeElement = jest.fn((id) => {
        expect(id).toBe("keyInformationTabContent");
        done();
    });
    ApexKeyInformationTab.keyInformationTab_deactivate()
})

test("Test keyInformationTab_create, key information tab exists", () => {
    document.body.innerHTML = '<div id ="keyInformationTabContent"></div>';

    ApexKeyInformationTab.keyInformationTab_create();
    const actual = document.getElementById("keyInformationTab");
    expect(actual).toBeNull();
});

test("Test keyInformationTab_create, ", () => {
    document.body.innerHTML = '<div id ="keyInformationTab"></div>';

    ApexKeyInformationTab.keyInformationTab_create();
    const actual = document.getElementById("keyInformationTabContent");
    const expected = '<table id="keyInformationTableBody" class="apexTable ebTable elTablelib-Table-table ebTable_striped"><thead id="keyInformationTableHeader"><tr id="keyInformationTableHeaderRow"><th id="keyInformationTableKeyHeader">Key Information</th><th id="keyInformationTableUUIDHeader">UUID</th><th id="keyInformationTableDescriptionHeader">Description</th></tr></thead><tbody></tbody></table>'
    expect(actual.innerHTML).toBe(expected);
});


