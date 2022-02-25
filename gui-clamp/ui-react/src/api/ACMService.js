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

export default class ACMService {

  static async getACMInstantiation() {

    const response = await fetch(window.location.pathname +
      'restservices/clds/v2/acm/getToscaInstantiation');

    return response
  }

  static async createInstanceProperties(instanceName, instancePropertiesTemplate) {

    const response = await fetch(window.location.pathname +
      'restservices/clds/v2/acm/postToscaInstanceProperties', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      credentials: 'same-origin',
      body: JSON.stringify(instancePropertiesTemplate),
    });

    return response
  }

  static async deleteInstantiation(name, version) {
    const params = {
      name: name,
      version: version
    }

    const response = await fetch(window.location.pathname +
      'restservices/clds/v2/acm/deleteToscaInstanceProperties?' + (new URLSearchParams(params)), {
      method: 'DELETE',
      credentials: 'same-origin',
    });

    const data = await response;

    return data;
  }

  static async getInstanceOrderState(name, version) {
    const params = {
      name: name,
      version: version
    }
    const response = await fetch(window.location.pathname +
      'restservices/clds/v2/acm/getInstantiationOrderState'+ '?' + (new URLSearchParams(params)));

    const data = await response;

    return data;
  }

  static async changeInstanceOrderState(toscaObject) {
    const response = await fetch(window.location.pathname +
      'restservices/clds/v2/acm/putToscaInstantiationStateChange', {
      method: 'PUT',
      headers: {
        "Content-Type": "application/json"
      },
      credentials: 'same-origin',
      body: JSON.stringify(toscaObject)
    });

    return response
  }

  static async getToscaTemplate(name, version) {
    const params = {
      name: name,
      version: version
    }

    const response = await fetch(window.location.pathname +
      'restservices/clds/v2/acm/getToscaTemplate' + '?' + (new URLSearchParams(params)));

    const data = await response;

    return data;
  }

  static async uploadToscaFile(toscaObject) {
    const response = await fetch(window.location.pathname +
      'restservices/clds/v2/acm/commissionToscaTemplate', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      credentials: 'same-origin',
      body: JSON.stringify(toscaObject),
    });

    return response

  }

  static async deleteToscaTemplate(name, version) {
    const params = {
      name: name,
      version: version
    }

    const response = await fetch(window.location.pathname +
      'restservices/clds/v2/acm/decommissionToscaTemplate' + '?' + (new URLSearchParams(params)),
      {
        method: 'DELETE'
      });

    const data = await response;

    return data;
  }

  static async getCommonOrInstanceProperties(name, version, isCommon) {
    const params = {
      name: name,
      version: version,
      common: isCommon
    }

    const response = await fetch(window.location.pathname +
      'restservices/clds/v2/acm/getCommonOrInstanceProperties' + '?' + (new URLSearchParams(params)));

    return response;
  }

}
