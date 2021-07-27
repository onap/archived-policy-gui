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

  static async getControlLoopInstantiation(windowLocationPathname) {

    return await fetch(windowLocationPathname + '/restservices/clds/v2/toscaControlLoop/getToscaInstantiation', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'same-origin',
    }).then(response => {
      console.log("fetchControlLoopInstantiation received " + response.status);

      if (response.ok) {
        console.info("fetchControlLoopInstantiation query successful");
        return response.json();
      } else {
        return response.text().then(responseBody => {
          throw Error("HTTP " + response.status + "," + responseBody);
        });
      }
    }).catch(error => {
      console.error("fetchControlLoopInstantiation error occurred ", error);
      alert("fetchControlLoopInstantiation error occurred " + error);
      return undefined;
    });
  }

  static async getInstanceProperties(name, version, windowLocationPathname) {
    const params = {
      name: name,
      version: version,
      common: "false"
    }

    const response = await fetch(windowLocationPathname + '/restservices/clds/v2/toscaControlLoop/getCommonOrInstanceProperties' + '?' + (new URLSearchParams(params)));

    this.checkResponseForError(response);

    return response;
  }

  static async createInstanceProperties(instancePropertiesTemplate, windowLocationPathname) {

    const response = await fetch(windowLocationPathname +
      '/restservices/clds/v2/toscaControlLoop/postToscaInstanceProperties', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      credentials: 'same-origin',
      body: JSON.stringify(instancePropertiesTemplate),
    });

    return response
  }

  static async getToscaTemplate(name, version, windowLocationPathname) {
    const params = {
      name: name,
      version: version
    }

    const response = await fetch(windowLocationPathname +
      '/restservices/clds/v2/toscaControlLoop/getToscaTemplate' + '?' + (new URLSearchParams(params)));

    if (!response.ok) {
      const message = `An error has occurred: ${ response.status }`;
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

  static async getToscaControlLoopDefinitions(windowLocationPathname) {

    const response = await fetch(windowLocationPathname +
      '/restservices/clds/v2/toscaControlLoop/getElementDefinitions');

    this.checkResponseForError(response);

    const data = await response;

    return data;
  }

  static async getToscaServiceTemplateSchema(section, windowLocationPathName) {

    const params = {
      section: section
    }

    const response = await fetch(windowLocationPathName +
      '/restservices/clds/v2/toscaControlLoop/getJsonSchema' + '?' + (new URLSearchParams(params)));

    this.checkResponseForError(response);

    return response;
  }

  static checkResponseForError(response) {
    if (!response.ok) {
      const message = `An error has occurred: ${ response.status }`;
      throw new Error(message);
    }
  }

}
