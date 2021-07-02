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

export default class ControlLoopService {

  static async getControlLoopList(windowLocationPathname) {

    return await fetch(windowLocationPathname + '/restservices/clds/v2/toscaControlLoop/getToscaInstantiation', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'same-origin',
    }).then(response => {
      console.log("getControlLoopList received " + response.status);

      if (response.ok) {
        console.info("getControlLoopList query successful");
        return response.json();
      } else {
        return response.text().then(responseBody => {
          throw Error("HTTP " + response.status + "," + responseBody);
        });
      }
    }).catch(error => {
      console.error("getControlLoopList error occurred ", error);
      alert("getControlLoopList error occurred " + error);
      return undefined;
    });
  }

  static async getToscaTemplate(name, version, windowLocationPathname) {
    const params = {
      name: name,
      version: version
    }

    const response = await fetch(windowLocationPathname +
      '/restservices/clds/v2/toscaControlLoop/getToscaTemplate' + '?' + (new URLSearchParams(params)));

    if (!response.ok) {
      const message = `An error has occurred: ${response.status}`;
      throw new Error(message);
    }

    const data = await response;

    return data;
  }

  static async uploadToscaFile(toscaObject, windowLocationPathName) {
    const response = await fetch(windowLocationPathName +
      '/restservices/clds/v2/toscaControlLoop/commissionToscaTemplate', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      credentials: 'same-origin',
      body: JSON.stringify(toscaObject),
    });

    return response

  }
}
