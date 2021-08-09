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

    const response = await fetch(windowLocationPathname + '/restservices/clds/v2/toscaControlLoop/getToscaInstantiation');

    return response
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

  static async deleteToscaTemplate(name, version, windowLocationPathname) {
    const params = {
      name: name,
      version: version
    }

    const response = await fetch(windowLocationPathname +
      '/restservices/clds/v2/toscaControlLoop/decommissionToscaTemplate' + '?' + (new URLSearchParams(params)),
      {
        method: 'DELETE'
      });

    const data = await response;

    return data;
  }

  static async getToscaControlLoopDefinitions(windowLocationPathname) {

    const response = await fetch(windowLocationPathname +
      '/restservices/clds/v2/toscaControlLoop/getElementDefinitions');

    this.checkResponseForError(response);

    const data = await response;

    return data;
  }

  static async getCommonOrInstanceProperties(name, version, windowLocationPathName, isCommon) {
    const params = {
      name: name,
      version: version,
      common: isCommon
    }

    const response = await fetch(windowLocationPathName +
      '/restservices/clds/v2/toscaControlLoop/getCommonOrInstanceProperties' + '?' + (new URLSearchParams(params)));

    return response;
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
