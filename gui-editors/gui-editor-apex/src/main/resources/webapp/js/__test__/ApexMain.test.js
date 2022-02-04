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
const $ = require('jquery');

test('Test main_getRestRootURL', () => {
    jest.spyOn(window.localStorage.__proto__, 'getItem');
    window.localStorage.__proto__.getItem = jest.fn(() => true);
    const mock_main_getRestRootURL = jest.fn(mod.main_getRestRootURL);
    mock_main_getRestRootURL();
    expect(mock_main_getRestRootURL).toBeCalled();
});

test('Test clearLocalStorage', () => {
    const mock_clearLocalStorage = jest.fn(mod.clearLocalStorage);
    mock_clearLocalStorage();
    expect(mock_clearLocalStorage).toBeCalled();
});

test('test ready', () => {
    document.documentElement.innerHTML = '<html><head></head><body><ul id="menu li"><li><div>menu</div><ul><li><div>FileNew</div></li></ul></li></ul></body></html>';
    window.$ = $;
    $("#menu li").click();
    let h1 = document.querySelector('ul');
    console.log(document.documentElement.innerHTML);
    expect(h1.textContent).toEqual('menuFileNew');
});