/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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

import InstantiationUtils from "./InstantiationUtils";
import instanceProps from "../testFiles/instanceProps.json";
import fullTemp from "../testFiles/fullTemplate.json";

const instanceProperties = JSON.parse(JSON.stringify(instanceProps))
const fullTemplate = JSON.parse(JSON.stringify(fullTemp))

describe('Verify InstantiationUtils', () => {

    const fullTemplatePromise = {
        ok: true,
        status: 200,
        text: () => "OK",
        json: () => {
            return Promise.resolve(fullTemplate)
        }
    }

    const instancePropertiesPromise = {
        ok: true,
        status: 200,
        text: () => "OK",
        json: () => {
            return Promise.resolve(instanceProperties)
        }
    }

    it('test parseJsonSchema output is correct', async () => {
        // Have to mock "editor" dom element for json editor to work in testing
        document.body.innerHTML = '<div id="editor"></div>';

        await expect((await InstantiationUtils.parseJsonSchema(fullTemplatePromise, instancePropertiesPromise)).jsonEditor).toBeTruthy()
        await expect((await InstantiationUtils.parseJsonSchema(fullTemplatePromise, instancePropertiesPromise)).fullTemplate).toBeTruthy()
    })
});