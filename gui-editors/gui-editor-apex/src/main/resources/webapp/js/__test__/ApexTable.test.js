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

const mod = require('../ApexTable');

let wrapper = document.createElement("example");
wrapper.setAttribute("id", "engineSummary_wrapper");
wrapper.setAttribute("class", "wrapper_borderless");

test('call createTable', () => {
    const createTable = mod.createTable('01');
    expect(createTable.getAttribute('id')).toBeDefined();
    expect(createTable.getAttribute('class')).toBeDefined();
    expect(createTable.getAttribute('id').valueOf()).toBe('01');
});

test('test setRowHover', () => {
    const mock = jest.fn(mod.setRowHover(wrapper));
    mock();
    expect(mock).toBeCalledTimes(1);
})