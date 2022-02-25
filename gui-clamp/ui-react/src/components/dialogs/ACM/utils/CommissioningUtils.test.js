/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

import commonProps from "../testFiles/commonProps.json";
import fullTemp from "../testFiles/fullTemplate.json";
import CommissioningUtils from "./CommissioningUtils";
import React from "react";

const commonProperties = JSON.parse(JSON.stringify(commonProps))
const fullTemplate = JSON.parse(JSON.stringify(fullTemp))

describe('Verify CommissioningUtils', () => {

  const fullTemplatePromise = {
    ok: true,
    status: 200,
    text: () => "OK",
    json: () => {
      return Promise.resolve(fullTemplate)
    }
  }

  const commonPropertiesPromise = {
    ok: true,
    status: 200,
    text: () => "OK",
    json: () => {
      return Promise.resolve(commonProperties)
    }
  }

  it('test renderJsonEditor output is correct', async () => {
    // Have to mock "editor" dom element for json editor to work in testing
    document.body.innerHTML = '<div id="editor"></div>';

    await expect((await CommissioningUtils.renderJsonEditor(fullTemplatePromise, commonPropertiesPromise)).editorTemp).toBeTruthy()
    await expect((await CommissioningUtils.renderJsonEditor(fullTemplatePromise, commonPropertiesPromise)).fullTemplate).toBeTruthy()
    await expect((await CommissioningUtils.renderJsonEditor(fullTemplatePromise, commonPropertiesPromise)).propertySchema).toBeTruthy()
    await expect((await CommissioningUtils.renderJsonEditor(fullTemplatePromise, commonPropertiesPromise)).toscaInitialValues).toBeTruthy()
  })

  it('test the getType method object type', () => {
    expect(CommissioningUtils.getType("object")).toBe("object")
  })

  it('test getAlertMessages with response ok', async () => {
    const response = {
      ok: true,
      status: 200,
      text: () => {
        return Promise.resolve("OK")
      },
      json: () => {
        return Promise.resolve("{}")
      }
    }

    await expect(JSON.stringify(await CommissioningUtils.getAlertMessages(response))).toContain("Commissioning Success")
  })

  it('test getAlertMessages with response not ok', async () => {
    const response = {
      ok: false,
      status: 200,
      text: () => {
        return Promise.resolve("Error")
      },
      json: () => {
        return Promise.resolve("{}")
      }
    }

    await expect(JSON.stringify(await CommissioningUtils.getAlertMessages(response))).toContain("Commissioning Failure")
  })


  }
)
