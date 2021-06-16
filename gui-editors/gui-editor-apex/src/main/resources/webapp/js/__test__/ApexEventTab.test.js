/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation.
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

const mod = require('../ApexEventTab');
const ApexUtils = require('../ApexUtils');

afterEach(() => {
    document.body.innerHTML = '';
});

test('Test activate', () => {
    document.body.innerHTML = '<div id="eventsTab"></div>';

    const data = {
        useHttps: 'useHttps',
        hostname: 'hostname',
        port: 'port',
        username: 'username',
        password: 'password',
        messages: {
            message: [
                '{"apexEvent" : {"key": {"name": "name1", "version":"version1"}, "nameSpace":"nameSpace1",' +
                ' "source":"source1", "target":"target1", "parameter": ' +
                '{"entry": [{"key": "key1", "value": {"optional":"optional", "fieldSchemaKey": ' +
                '{"name": "name2", "version":"version2"}}}]}}}'
            ]
        },
        content: ['01', '02'],
        result: 'ok',
        ok: true
    };

    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, null);
    });

    const expected = '<div id="eventsTab"><eventtabcontent id="eventTabContent">' +
        '<table id="eventTableBody" class="apexTable ebTable elTablelib-Table-table ebTable_striped">' +
        '<thead id="eventTableHeader"><tr id="eventTableHeaderRow">' +
        '<th id="eventTableKeyHeader">Event</th>' +
        '<th id="eventTableNamespaceHeader">Name Space</th>' +
        '<th id="eventTableSourceHeader">Source</th>' +
        '<th id="eventTableTargetHeader">Target</th>' +
        '<th id="eventTableParameterHeader">Parameters</th>' +
        '</tr></thead>' +
        '<tbody><tr>' +
        '<td>name1:version1</td>' +
        '<td>nameSpace1</td>' +
        '<td>source1</td>' +
        '<td>target1</td>' +
        '<td><table class="ebTable">' +
        '<thead><tr><th>Parameter</th><th>Parameter Type/Schema</th><th>Optional</th></tr></thead>' +
        '<tbody><tr><td>key1</td><td>name2:version2</td><td>optional</td></tr></tbody></table></td></tr></tbody>' +
        '</table></eventtabcontent></div>';

    mod.eventTab_activate();
    expect(document.body.innerHTML).toBe(expected);
});

test('Test deactivate', (done) => {
    ApexUtils.apexUtils_removeElement = jest.fn(id => {
        expect(id).toBe('eventTabContent');
        done()
    });
    mod.eventTab_deactivate();
});


test('Test create eventTabContent exists', () => {
    document.body.innerHTML = '<div id="eventsTab"><div id="eventTabContent"></div></div>';
    mod.eventTab_create();
    expect(document.body.innerHTML).toBe('<div id="eventsTab"><div id="eventTabContent"></div></div>');
});

test('Test create eventsTab does not exist', () => {
    document.body.innerHTML = '<div></div>';
    mod.eventTab_create();
    expect(document.body.innerHTML).toBe('<div></div>');
});

test('Test create', () => {
    document.body.innerHTML = '<div id="eventsTab"></div>';

    const expected = '<div id="eventsTab"><eventtabcontent id="eventTabContent">' +
        '<table id="eventTableBody" class="apexTable ebTable elTablelib-Table-table ebTable_striped">' +
        '<thead id="eventTableHeader"><tr id="eventTableHeaderRow">' +
        '<th id="eventTableKeyHeader">Event</th>' +
        '<th id="eventTableNamespaceHeader">Name Space</th>' +
        '<th id="eventTableSourceHeader">Source</th>' +
        '<th id="eventTableTargetHeader">Target</th>' +
        '<th id="eventTableParameterHeader">Parameters</th>' +
        '</tr></thead><tbody></tbody></table>' +
        '</eventtabcontent></div>';

    mod.eventTab_create();
    expect(document.body.innerHTML).toBe(expected);
});


// These are being tested indirectly
// But could be tested individually here if needed
test.todo('Test reset');
