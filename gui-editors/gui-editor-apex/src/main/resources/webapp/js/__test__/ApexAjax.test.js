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

test('Test ajax_get', () => {
    const mockGet = jest.fn(mod.ajax_get).mockImplementation(() => {
        const val = {
            type : 'GET',
            url : 'requestURL',
            dataType : "json", // data type of response
            success : function(data) {
                return 'Success'
            },
            error : function(jqXHR, textStatus, errorThrown) {
                return 'Error'
            }
        }
    });
    mockGet('Called');
    expect(mockGet).toBeCalledTimes(1);
    expect(mockGet).toHaveBeenCalledWith('Called');
});

test('Test ajax_delete', () => {
    const mockDelete = jest.fn(mod.ajax_delete);
    mockDelete();
    expect(mockDelete).toBeCalledTimes(1);
});

test('Test ajax_post', () => {
    const mockAjaxPost = jest.fn(mod.ajax_post);
    mockAjaxPost();
    expect(mockAjaxPost).toBeCalledTimes(1);
});

test('Test ajax_put', () => {
    const mockAjaxPut = jest.fn(mod.ajax_put);
    mockAjaxPut();
    expect(mockAjaxPut).toBeCalledTimes(1);
});

test('Test ajax_getOKOrFail', () => {
    const mockAjaxGetOkOrFail = jest.fn(mod.ajax_getOKOrFail);
    mockAjaxGetOkOrFail();
    expect(mockAjaxGetOkOrFail).toBeCalledTimes(1);
});

test('Test ajax_getWithKeyInfo', () => {
    const mockGetWKey = jest.fn(mod.ajax_getWithKeyInfo);
    const kName = mod.ajax_getWithKeyInfo.keyName;
    expect(kName).not.toBe(null);
});