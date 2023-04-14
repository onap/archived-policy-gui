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
const apexPageControl = require('../ApexPageControl');
const apexContextSchemaTab = require('../ApexContextSchemaTab');
const apexEventTab = require('../ApexEventTab');
const apexAlbumTab = require('../ApexContextAlbumTab');
const apexTaskTab = require('../ApexTaskTab');
const apexPolicyTab = require('../ApexPolicyTab');
const keyInformationTab = require('../ApexKeyInformationTab');

require('../../dist/js/jquery-ui-1.12.1/jquery-ui.js');

const data = {
    messages: [
        JSON.stringify({ key: { name: "name1", version: "version1" }})
    ],
    result: 'SUCCESS'
};

const jqXHR = { status: 200, responseText: "" };

test('Test main_getRestRootURL', () => {
    document.documentElement.innerHTML = '<html><head></head><body>' +
    '<div id="mainTabs"><ul><li><a href="#mainTabs1">Tab 1</a></li></ul></div>' +
    '</body></html>';
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    jest.spyOn(window.localStorage.__proto__, 'getItem');
    window.localStorage.__proto__.getItem = jest.fn(() => true);
    jest.spyOn(apexPageControl, 'pageControl_modelMode').mockReturnValueOnce(null);
    jest.spyOn(apexContextSchemaTab, 'contextSchemaTab_activate').mockReturnValueOnce(null);
    jest.spyOn(apexEventTab, 'eventTab_activate').mockReturnValueOnce(null);
    jest.spyOn(apexAlbumTab, 'contextAlbumTab_activate').mockReturnValueOnce(null);
    jest.spyOn(apexTaskTab, 'taskTab_activate').mockReturnValueOnce(null);
    jest.spyOn(apexPolicyTab, 'policyTab_activate').mockReturnValueOnce(null);
    jest.spyOn(keyInformationTab, 'keyInformationTab_activate').mockReturnValueOnce(null);
    const mock_main_getRestRootURL = jest.fn(mod.main_getRestRootURL);
    mock_main_getRestRootURL();
    const expected = '<head></head><body>'+
    '<div id="mainTabs" class="ui-tabs ui-corner-all ui-widget ui-widget-content"><ul role="tablist" class="ui-tabs-nav ui-corner-all ui-helper-reset ui-helper-clearfix ui-widget-header">' +
    '<li role="tab" tabindex="0" class="ui-tabs-tab ui-corner-top ui-state-default ui-tab ui-tabs-active ui-state-active" aria-controls="mainTabs1" aria-labelledby="ui-id-1" aria-selected="true" aria-expanded="true">' +
    '<a href="#mainTabs1" role="presentation" tabindex="-1" class="ui-tabs-anchor" id="ui-id-1">Tab 1</a></li></ul></div></body>'
    expect(mock_main_getRestRootURL).toBeCalled();
    expect(document.documentElement.innerHTML).toEqual(expected);
});

test('Test main_getRestRootURL false', () => {
    document.documentElement.innerHTML = '<html><head></head><body><div class="ebInlineMessage-description" id="statusMessageTable"></div></body></html>';
    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, jqXHR);
    });
    jest.spyOn(window.localStorage.__proto__, 'getItem');
    window.localStorage.__proto__.getItem = jest.fn(() => false);
    const mock_main_getRestRootURL = jest.fn(mod.main_getRestRootURL);
    mock_main_getRestRootURL();
    expect(mock_main_getRestRootURL).toBeCalled();
    expect(document.documentElement.innerHTML).toEqual('<head></head><body><div class="ebInlineMessage-description" id="statusMessageTable"><tr><td> REST root URL set to: http://localhost/policy/gui/v1/apex/editor/false</td></tr></div></body>');
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
    expect(h1.textContent).toEqual('menuFileNew');
});
