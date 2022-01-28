/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2022 Nordix Foundation
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

const mod = require('../ApexAjax');

const requestURL = "http://localhost:7979";
let data = {};

beforeEach(() => {
    data = {
        useHttps: 'useHttps',
        hostname: 'hostname',
        port: 'port',
        username: 'username',
        password: 'password',
        messages: [''],
        content: ['01', '02'],
        result: 'SUCCESS'
    };
});

test('Test ajax_get error', () => {
   const callback = jest.fn();
   $.ajax = jest.fn().mockImplementation((args) => {
       args.error(data, null, null);
   });
   const mock_get_error = jest.fn(mod.ajax_get(requestURL, callback));
    mock_get_error();
   expect(mock_get_error).toHaveBeenCalled();
});

test('Test ajax_get success', (done) => {
    const callback = jest.fn((actualData) => {
        expect(actualData).toEqual(data);
        done();
    });
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    mod.ajax_get(requestURL, callback);
});

test('Test ajax_getWithKeyInfo success', (done) => {
    const myCallback = jest.fn((actual) => {
        expect(actual).toEqual({
            key: {
                name: "name1",
                version: "version1"
            },
            uuid: "UUID1",
            description: "description1"
        });
        done();
    });
    data.messages = [
        '{"apexKeyInfo": {"UUID": "UUID1", "description": "description1", "key":{"name": "name1", "version":' +
        ' "version1"}}, "objectType": {"key": {"name": "name1", "version": "version1"}}}'
    ];
    const jqXHR = {status: 200, responseText: ""};

    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    mod.ajax_getWithKeyInfo("requestUrl", "objectType", myCallback, undefined);
});

test('Test ajax_getWithKeyInfo with custom key success', (done) => {
    const myCallback = jest.fn((actual) => {
        expect(actual).toEqual({
            customKey: {
                name: "name1",
                version: "version1"
            },
            uuid: "UUID1",
            description: "description1"
        });
        done();
    });
    data.messages = [
        '{"apexKeyInfo": {"UUID": "UUID1", "description": "description1", "key":{"name": "name1",' +
        ' "version": "version1"}}, "objectType": {"customKey": {"name": "name1", "version": "version1"}}}'
    ];
    const jqXHR = {status: 200, responseText: ""};

    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    mod.ajax_getWithKeyInfo("requestUrl", "objectType", myCallback, "customKey");
});

test('Test ajax_delete error', () => {
    const callback = jest.fn();
    const jqXHR = { status: 500, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.error(jqXHR, null, null);
    });
    const mock_delete_error = jest.fn(mod.ajax_delete(requestURL, callback));
    mock_delete_error();
    expect(mock_delete_error).toHaveBeenCalled();
});

test('Test ajax_delete success', (done) => {
    const callback = jest.fn((actualData) => {
        expect(actualData).toEqual(data);
        done();
    });
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    mod.ajax_delete(requestURL, callback);
});

test('Test ajax_post error', () => {
    const callback = jest.fn();
    const jqXHR = { status: 500, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.error(jqXHR, null, null);
    });
    const mock_post_error = jest.fn(mod.ajax_post(requestURL, data, callback));
    mock_post_error();
    expect(mock_post_error).toHaveBeenCalled();
});

test('Test ajax_post success', (done) => {
    const callback = jest.fn((actualData) => {
        expect(actualData).toEqual(data);
        done();
    });
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    mod.ajax_post(requestURL, data, callback);
});

test('Test ajax_put error', () => {
    const callback = jest.fn();
    const jqXHR = { status: 500, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.error(jqXHR, null, null);
    });
    const mock_put_error = jest.fn(mod.ajax_put(requestURL, callback));
    mock_put_error();
    expect(mock_put_error).toHaveBeenCalled();
});

test('Test ajax_put success', (done) => {
    const callback = jest.fn((actualData) => {
        expect(actualData).toEqual(data);
        done();
    });
    const jqXHR = { status: 200, responseText: "" };

    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    mod.ajax_put(requestURL, data, callback);
});

test('Test ajax_getOKOrFail error', () => {
    const callback = jest.fn();
    const jqXHR = { status: 500, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.error(jqXHR, null, null);
    });
    const mock_getOKOrFail_error = jest.fn(mod.ajax_getOKOrFail(requestURL, callback));
    mock_getOKOrFail_error();
    expect(mock_getOKOrFail_error).toHaveBeenCalled();
});

test('Test ajax_getOKOrFail success', (done) => {
    const callback = jest.fn((actualData) => {
        expect(actualData).toEqual(data);
        done();
    });
    const jqXHR = { status: 200, responseText: "" };

    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    mod.ajax_getOKOrFail(requestURL, callback);
});
