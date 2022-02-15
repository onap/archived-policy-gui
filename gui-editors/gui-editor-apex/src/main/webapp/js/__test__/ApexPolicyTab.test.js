/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation
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

const mod = require('../ApexPolicyTab');
const ApexUtils = require('../ApexUtils');

afterEach(() => {
    document.body.innerHTML = '';
});

test('Test policyTab_activate', () => {
    document.body.innerHTML = '<div id="policiesTab"></div>';

    const data = {
        useHttps: 'useHttps',
        hostname: 'hostname',
        port: 'port',
        username: 'username',
        password: 'password',
        messages: {
            message: [
                '{' +
                '    "apexPolicy": {' +
                '        "policyKey": {' +
                '            "name": "name1",' +
                '            "version": "version1"' +
                '        },' +
                '        "template": "template1",' +
                '        "firstState": "key11",' +
                '        "state": {' +
                '            "entry": [' +
                '                {' +
                '                    "key": "key1",' +
                '                    "value": {' +
                '                        "trigger": {' +
                '                            "name": "name2",' +
                '                            "version": "version2"' +
                '                        },' +
                '                        "taskReferences": {' +
                '                            "entry": [' +
                '                                {' +
                '                                    "key": {' +
                '                                        "name": "name3",' +
                '                                        "version": "version3"' +
                '                                    },' +
                '                                    "version": "version2",' +
                '                                    "value": {' +
                '                                        "outputType": "outputType1",' +
                '                                        "output": {' +
                '                                            "localName": "localName1"' +
                '                                        }' +
                '                                    }' +
                '                                }' +
                '                            ]' +
                '                        },' +
                '                        "defaultTask": {' +
                '                            "name": "name4",' +
                '                            "version": "version4"' +
                '                        },' +
                '                        "taskSelectionLogic": {' +
                '                            "logicFlavour": "logicFlavour1"' +
                '                        },' +
                '                        "stateOutputs": {' +
                '                            "entry": [' +
                '                                {' +
                '                                    "key": "key2",' +
                '                                    "value": {' +
                '                                        "nextState": {' +
                '                                            "localName": "localName2"' +
                '                                        },' +
                '                                        "outgoingEvent": {' +
                '                                            "name": "name4",' +
                '                                            "version": "version4"' +
                '                                        }' +
                '                                    }' +
                '                                }' +
                '                            ]' +
                '                        },' +
                '                        "stateFinalizerLogicMap": {' +
                '                            "entry": [' +
                '                                {' +
                '                                    "key": "key3",' +
                '                                    "value": {' +
                '                                        "logicFlavour": "logicFlavour2"' +
                '                                    }' +
                '                                }' +
                '                            ]' +
                '                        },' +
                '                        "contextAlbumReference": [' +
                '                            {' +
                '                                "name": "name5",' +
                '                                "version": "version5"' +
                '                            }' +
                '                        ]' +
                '                    }' +
                '                },' +
                '                {' +
                '                    "key": "key11",' +
                '                    "value": {' +
                '                        "trigger": {' +
                '                            "name": "name12",' +
                '                            "version": "version12"' +
                '                        },' +
                '                        "taskReferences": {' +
                '                            "entry": [' +
                '                                {' +
                '                                    "key": {' +
                '                                        "name": "name13",' +
                '                                        "version": "version13"' +
                '                                    },' +
                '                                    "version": "version12",' +
                '                                    "value": {' +
                '                                        "outputType": "outputType11",' +
                '                                        "output": {' +
                '                                            "localName": "localName11"' +
                '                                        }' +
                '                                    }' +
                '                                }' +
                '                            ]' +
                '                        },' +
                '                        "defaultTask": {' +
                '                            "name": "name14",' +
                '                            "version": "version14"' +
                '                        },' +
                '                        "taskSelectionLogic": {' +
                '                            "logicFlavour": "logicFlavour11"' +
                '                        },' +
                '                        "stateOutputs": {' +
                '                            "entry": [' +
                '                                {' +
                '                                    "key": "key12",' +
                '                                    "value": {' +
                '                                        "nextState": {' +
                '                                            "localName": "localName12"' +
                '                                        },' +
                '                                        "outgoingEvent": {' +
                '                                            "name": "name14",' +
                '                                            "version": "version14"' +
                '                                        }' +
                '                                    }' +
                '                                }' +
                '                            ]' +
                '                        },' +
                '                        "stateFinalizerLogicMap": {' +
                '                            "entry": [' +
                '                                {' +
                '                                    "key": "key13",' +
                '                                    "value": {' +
                '                                        "logicFlavour": "logicFlavour12"' +
                '                                    }' +
                '                                }' +
                '                            ]' +
                '                        },' +
                '                        "contextAlbumReference": [' +
                '                            {' +
                '                                "name": "name15",' +
                '                                "version": "version15"' +
                '                            }' +
                '                        ]' +
                '                    }' +
                '                }' +
                '            ]' +
                '        }' +
                '    }' +
                '}'
            ]
        },
        content: ['01', '02'],
        result: 'ok',
        ok: true
    };

    $.ajax = jest.fn().mockImplementation((args) => {
        args.success(data, null, null);
    });

    const expected = '<div id="policiesTab"><policytabcontent id="policyTabContent">' +
        '<table id="policyTableBody" class="apexTable ebTable elTablelib-Table-table ebTable_striped">' +
        '<thead id="policyTableHeader"><tr id="policyTableHeaderRow">' +
        '<th id="policyTableKeyHeader">Policy</th>' +
        '<th id="policyTableTemplateHeader">Template</th>' +
        '<th id="policyTableFirstStateHeader">First State</th>' +
        '<th id="policyTableStatesHeader">States</th>' +
        '</tr></thead><tbody><tr>' +
        '<td>name1:version1</td>' +
        '<td>template1</td>' +
        '<td>key11</td>' +
        '<td><table class="ebTable"><thead><tr class="headerRow">' +
        '<th>State</th>' +
        '<th>Trigger</th>' +
        '<th>Referenced Tasks</th>' +
        '<th>Default Task</th>' +
        '<th>TSL</th>' +
        '<th>State Outputs</th>' +
        '<th>State Finsalizer Logic</th>' +
        '<th>Context Album References</th>' +
        '</tr></thead><tbody><tr>' +
        '<td>key11</td>' +
        '<td>name12:version12</td>' +
        '<td><table class="ebTable"><thead><tr class="headerRow">' +
        '<th>Task Reference</th>' +
        '<th>Output Type</th>' +
        '<th>Output</th>' +
        '</tr></thead><tbody><tr>' +
        '<td>name13:version13</td>' +
        '<td>outputType11</td>' +
        '<td>localName11</td>' +
        '</tr></tbody></table></td>' +
        '<td>name14:version14</td>' +
        '<td>logicFlavour11</td>' +
        '<td><table class="ebTable"><thead><tr class="headerRow">' +
        '<th>Name</th>' +
        '<th>Next State</th>' +
        '<th>Event</th>' +
        '</tr></thead><tbody><tr>' +
        '<td>key12</td>' +
        '<td>localName12</td>' +
        '<td>name14:version14</td>' +
        '</tr></tbody></table></td>' +
        '<td><table class="ebTable"><thead><tr class="headerRow"><th>Name</th><th>Type</th></tr></thead><tbody>' +
        '<tr><td>key13</td><td>logicFlavour12</td></tr></tbody></table></td>' +
        '<td><table class="ebTable"><tbody><tr><td>name15:version15</td></tr></tbody></table></td>' +
        '</tr><tr>' +
        '<td>key1</td>' +
        '<td>name2:version2</td>' +
        '<td><table class="ebTable"><thead><tr class="headerRow">' +
        '<th>Task Reference</th>' +
        '<th>Output Type</th>' +
        '<th>Output</th>' +
        '</tr></thead><tbody><tr>' +
        '<td>name3:version3</td>' +
        '<td>outputType1</td>' +
        '<td>localName1</td>' +
        '</tr></tbody></table></td>' +
        '<td>name4:version4</td>' +
        '<td>logicFlavour1</td>' +
        '<td><table class="ebTable"><thead><tr class="headerRow"><th>Name</th><th>Next State</th><th>Event</th></tr></thead>' +
        '<tbody><tr>' +
        '<td>key2</td>' +
        '<td>localName2</td>' +
        '<td>name4:version4</td>' +
        '</tr></tbody></table></td>' +
        '<td><table class="ebTable"><thead><tr class="headerRow"><th>Name</th><th>Type</th></tr></thead>' +
        '<tbody><tr><td>key3</td><td>logicFlavour2</td></tr></tbody></table></td>' +
        '<td><table class="ebTable"><tbody><tr><td>name5:version5</td></tr></tbody></table></td>' +
        '</tr></tbody></table></td></tr></tbody></table></policytabcontent></div>';

    mod.policyTab_activate();
    expect(document.body.innerHTML).toBe(expected);
});

test('Test policyTab_deactivate', (done) => {
    ApexUtils.apexUtils_removeElement = jest.fn(id => {
        expect(id).toBe('policyTabContent');
        done();
    });

    mod.policyTab_deactivate();
});

test('Test policyTab_create policyTabContent exists', () => {
    document.body.innerHTML = '<div id="policiesTab"><div id="policyTabContent"></div></div>';

    mod.policyTab_create();
    expect(document.body.innerHTML).toBe('<div id="policiesTab"><div id="policyTabContent"></div></div>');
});

test('Test policyTab_create policiesTab does not exists', () => {
    document.body.innerHTML = '<div></div>';

    mod.policyTab_create();
    expect(document.body.innerHTML).toBe('<div></div>');
});

test('Test policyTab_create', () => {
    document.body.innerHTML = '<div id="policiesTab"></div>';

    const expected = '<div id="policiesTab"><policytabcontent id="policyTabContent">' +
        '<table id="policyTableBody" class="apexTable ebTable elTablelib-Table-table ebTable_striped">' +
        '<thead id="policyTableHeader"><tr id="policyTableHeaderRow">' +
        '<th id="policyTableKeyHeader">Policy</th>' +
        '<th id="policyTableTemplateHeader">Template</th>' +
        '<th id="policyTableFirstStateHeader">First State</th>' +
        '<th id="policyTableStatesHeader">States</th>' +
        '</tr></thead><tbody></tbody></table></policytabcontent></div>';

    mod.policyTab_create();
    expect(document.body.innerHTML).toBe(expected);
});
