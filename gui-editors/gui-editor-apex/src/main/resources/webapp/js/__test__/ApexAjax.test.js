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

const mod = require('../ApexAjax');

const requestURL = "http://localhost:7979";
const data = {
    useHttps: 'useHttps',
    hostname: 'hostname',
    port: 'port',
    username: 'username',
    password: 'password',
    messages: {
        message: ''
    },
    content: ['01', '02'],
    result: 'ok',
    ok: true
}

test('Test ajax_get error', () => {
   const callback = jest.fn();
   $.ajax = jest.fn().mockImplementation((args) => {
       args.error(data, null, null);
   });
   const mock_get_error = jest.fn(mod.ajax_get(requestURL, callback));
    mock_get_error();
   expect(mock_get_error).toHaveBeenCalled();
});

test('Test ajax_get success', () => {
    const callback = jest.fn();
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    const mock_get_success = jest.fn(mod.ajax_get(requestURL, callback));
    mock_get_success();
    expect(mock_get_success).toHaveBeenCalled();
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

test('Test ajax_delete success', () => {
    const callback = jest.fn();
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    const mock_delete_success = jest.fn(mod.ajax_delete(requestURL, callback));
    mock_delete_success();
    expect(mock_delete_success).toHaveBeenCalled();
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

test('Test ajax_post success', () => {
    const callback = jest.fn();
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    const mock_post_success = jest.fn(mod.ajax_post(requestURL, data, callback));
    mock_post_success();
    expect(mock_post_success).toHaveBeenCalled();
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

test('Test ajax_put success', () => {
    const callback = jest.fn();
    const jqXHR = { status: 200, responseText: "" };

    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    const mock_put_success = jest.fn(mod.ajax_put(requestURL, data, callback));
    mock_put_success();
    expect(mock_put_success).toHaveBeenCalled();
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

test('Test ajax_getOKOrFail success', () => {
    const callback = jest.fn();
    const jqXHR = { status: 200, responseText: "" };

    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    const mock_getOKOrFail_success = jest.fn(mod.ajax_getOKOrFail(requestURL, callback));
    mock_getOKOrFail_success();
    expect(mock_getOKOrFail_success).toHaveBeenCalled();
});

test.todo('Test ajax_getWithKeyInfo');
