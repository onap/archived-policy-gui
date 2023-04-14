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

const ApexUtils = require('../ApexUtils');

afterEach(() => {
    delete global.confirm;

    document.body.innerHTML = '';
});

test('test apexUtils_areYouSure', () => {
    const errorMsg = 'My message';
    const returned = {};
    global.confirm = jest
        .fn()
        .mockImplementation((message) => {
            expect(message).toBe(errorMsg);
            return returned;
        });

    const actual = ApexUtils.apexUtils_areYouSure(errorMsg);
    expect(actual).toBe(returned);
});

test('apexUtils_emptyElement found', () => {
    document.body.innerHTML =
        '<div id="tested">' +
        '  <span id="username"></span>' +
        '  <button id="button"></button>' +
        '</div>';

    ApexUtils.apexUtils_emptyElement('tested');

    expect(document.body.innerHTML).toBe('<div id="tested"></div>')
});

test('apexUtils_emptyElement found', () => {
    const text =
        '<div id="other">' +
        '  <span id="username"></span>' +
        '  <button id="button"></button>' +
        '</div>';
    document.body.innerHTML = text;
    ApexUtils.apexUtils_emptyElement('tested');

    expect(document.body.innerHTML).toBe(text)
});

test('apexUtils_removeElement not found', () => {
    const expected = /<div>\s*<button id="button"><\/button>\s*<\/div>/;

    document.body.innerHTML =
        '<div>' +
        '  <span id="tested"></span>' +
        '  <button id="button"></button>' +
        '</div>';

    ApexUtils.apexUtils_removeElement('tested');
    expect(document.body.innerHTML).toMatch(expected);
});

test('apexUtils_escapeHtml', () => {
    const actual = ApexUtils.apexUtils_escapeHtml('&<ab>"\'/`=\n\t d');
    expect(actual).toBe('&amp;&lt;ab&gt;&quot;&#39;&#x2F;`=<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;d')
});

test('createAddFormButton no text', () => {
    const expected = document.createElement('div');
    expected.setAttribute('class','add-field');
    expected.innerHTML = '<i class="form-add-icon ebIcon ebIcon_add"></i><span class="form-add-text">Add</span>';
    const actual = ApexUtils.createAddFormButton();
    expect(actual).toEqual(expected);
});

test('createAddFormButton with text', () => {
    const expected = document.createElement('div');
    expected.setAttribute('class','add-field');
    expected.innerHTML = '<i class="form-add-icon ebIcon ebIcon_add"></i><span class="form-add-text">My_text</span>';
    const actual = ApexUtils.createAddFormButton('My_text');
    expect(actual).toEqual(expected);
});

test('test EMPTY createEditArea', () => {
    const mock_activate = jest.fn(ApexUtils.createEditArea);
    mock_activate('id', 'options', '');
    expect(mock_activate).toBeCalled();
});

test('test getHomepageURL', () => {
    const mock_activate = jest.fn(ApexUtils.getHomepageURL);
    mock_activate();
    expect(mock_activate).toBeCalled();
});

test('test isFirefox', () => {
    const mock_activate = jest.fn(ApexUtils.isFirefox);
    mock_activate();
    expect(mock_activate).toBeCalled();
});