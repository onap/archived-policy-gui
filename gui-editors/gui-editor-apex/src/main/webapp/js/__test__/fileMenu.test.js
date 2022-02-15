/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation
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

const mod = require('../../dist/js/fileMenu');

test('test hideMenu', () => {
    document.documentElement.innerHTML = '<html><head></head><body><ul id="menu">Hello world!</ul></body></html>';
    $ = require('jquery');
    window.$ = $;
    $('#menu').fileMenu();
    $('#menu').click();
    let h1 = document.querySelector('ul');
    expect(h1.textContent).toEqual('Hello world!');
});
