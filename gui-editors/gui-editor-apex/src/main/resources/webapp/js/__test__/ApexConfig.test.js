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

const apexConfig = require('../ApexConfig');

test('configObj called successfully', () => {
    expect(apexConfig.configObj.getConfig()).not.toBeNull();
    expect(apexConfig.configObj.setConfig()).not.toBeNull();
    expect(apexConfig.configObj).toHaveProperty('configMap');
});

test('load called successfully', () => {
    expect(apexConfig.rootUrl).not.toBe(null);
    const mock = jest.fn(apexConfig.load).mockImplementation(() => {
         function test (data) {
             for (let i = 0; i < data.messages.message.length; i++) {
                const configEntry = JSON.parse(data.messages.message[i]);
                Object.keys(configEntry).forEach(key => {
                    configObj.setConfig(key, configEntry[key]);
                });
            }
            configObj.readySignal();
         };
    });
    mock();
    expect(mock).toHaveBeenCalledTimes(1);
});