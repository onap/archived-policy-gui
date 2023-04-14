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

const mod = require('../dropdownList');

let selectedOption = {
    displaytext : 'displaytext'
};

const page = '<html><head></head><body>' +
'<div id="divName_display"><ul><li><a href="divName">Delete this</a><li>Second</li></li></ul></div>' +
'<div id="divName"><ul><li><a href="divName">Just a Div</a><li>Second</li></li></ul></div>' +
'<div id="divName_options"><ul><li><a href="dropdownList_show">Show</a><li>Second</li></li></ul></div>' +
'</body></html>';

test('test dropdownList option select', () => {
    document.documentElement.innerHTML = page;
    mod.dropdownList_option_select('divName',selectedOption, false);
    let expected = '<head></head><body>' +
    '<div id="divName_display">displaytext</div>' +
    '<div id="divName"><ul><li><a href="divName">Just a Div</a></li><li>Second</li></ul></div>' +
    '<div id="divName_options"><ul><li><a href="dropdownList_show">Show</a></li><li>Second</li></ul></div>' +
    '</body>';
    expect(document.documentElement.innerHTML).toBe(expected);
});

test('test dropdownList display click', () => {
    document.documentElement.innerHTML = page;
    mod.dropdownList_display_click('divName',selectedOption, false, false);
    const expected = '<head></head><body>' +
    '<div id=\"divName_display\"><ul><li><a href=\"divName\">Delete this</a></li><li>Second</li></ul></div>' +
    '<div id=\"divName\"><ul><li><a href=\"divName\">Just a Div</a></li><li>Second</li></ul></div>' +
    '<div id=\"divName_options\" class=\"dropdownList_show dropdownList_display_clicked\"><ul><li><a href=\"dropdownList_show\">Show</a></li><li>Second</li></ul></div></body>';
    expect(document.documentElement.innerHTML).toBe(expected);
    mod.dropdownList_display_click('divName',selectedOption, true, false);
});

test('test dropdownList filter', () => {
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<p id="optionDiv_search">tosearch</p>' +
    '<div id="optionDiv_options_list_ul"><div id="#divName"><li>test</li></div></div>' +
    '</body></html>';
    let documentSpy = jest.spyOn(document, 'getElementById');
    let elementMock = document.createElement("optionDiv_search");
    elementMock.value = '1'
    elementMock.id = 'divName'
    documentSpy.mockReturnValue(elementMock);
    mod.dropdownList_filter('optionDiv','optionUl');
    const expected = '<head></head><body><p id=\"optionDiv_search\">tosearch</p><div id=\"optionDiv_options_list_ul\"><div id=\"#divName\"><li>test</li></div></div></body>';
    expect(document.documentElement.innerHTML).toBe(expected);
});