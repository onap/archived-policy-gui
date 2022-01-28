/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2022 Nordix Foundation
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

const mod = require('../ApexFiles');
const resultForm_activate = require('../ApexResultForm');

let data = {
    messages: [
       '{"apexContextSchema": {"key":{"name": "name1", "version": "version1"}}, "apexTask":{"key":{"name": "name1", "version": "version1"}},' +
       '"apexContextAlbum":{"key":{"name": "name1", "version": "version1"},"itemSchema":{}},"apexEvent":{"key":{"name": "name1", "version": "version1"}},' +
       '"apexPolicy":{"policyKey":{"name": "name1", "version": "version1"}}, "apexKeyInfo":{"key":{"name": "name1", "version": "version1"}}}'
    ],
    result: 'SUCCESS'
 };

test('test files_open', () => {
    const open = jest.fn(mod.files_fileOpen);
    open();
    expect(open).toBeCalledTimes(1);
});

test('test files_download', () => {
    const download = jest.fn(mod.files_fileDownload);
    download();
    expect(download).toHaveBeenCalledTimes(1);
});

test('Test files_upload', () => {
    global.window.restRootURL = () => 'http://localhost'
    const jqXHR = { status: 200, responseText: "" };
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    jest.spyOn(resultForm_activate, 'resultForm_activate').mockReturnValueOnce(null);
    const upload = jest.fn(mod.files_fileUpload);
    upload();
    expect(upload).toBeCalled();
});
