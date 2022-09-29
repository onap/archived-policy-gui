/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021-2022 Nordix Foundation.
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
      'onap/policy/clamp/acm/v2/instantiation');

    return response
  }

  static async createInstanceProperties(instancePropertiesTemplate) {

    const response = await fetch(window.location.pathname +
      'onap/policy/clamp/acm/v2/instanceProperties', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      credentials: 'same-origin',
      body: JSON.stringify(instancePropertiesTemplate),
    });

    return response
  }

  static async updateInstanceProperties(instanceName, version, instancePropertiesTemplate) {
    const params = {
      name: instanceName,
      version: version
    }

    const response = await fetch(window.location.pathname +
        'onap/policy/clamp/acm/v2/instanceProperties?' + (new URLSearchParams(params)), {
      method: 'PUT',
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
      'onap/policy/clamp/acm/v2/instanceProperties?' + (new URLSearchParams(params)), {
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
      'onap/policy/clamp/acm/v2/instantiationState'+ '?' + (new URLSearchParams(params)));

    const data = await response;

    return data;
  }

  static async changeInstanceOrderState(toscaObject) {
    const response = await fetch(window.location.pathname +
      'onap/policy/clamp/acm/v2/instantiation/command', {
      method: 'PUT',
      headers: {
        "Content-Type": "application/json"
      },
      credentials: 'same-origin',
      body: JSON.stringify(toscaObject)
    });

    return response
  }

  static async getToscaTemplate(name, version, instanceName) {
    const params = instanceName != null ?
      {
        name: name,
        version: version,
        instanceName: instanceName
      } :
      {
        name: name,
        version: version
      }

    const response = await fetch(window.location.pathname +
      'onap/policy/clamp/acm/v2/commission/toscaservicetemplate' + '?' + (new URLSearchParams(params)));

    const data = await response;

    return data;
  }

  static async uploadToscaFile(toscaObject) {
    const response = await fetch(window.location.pathname +
      'onap/policy/clamp/acm/v2/commission', {
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
      'onap/policy/clamp/acm/v2/commission' + '?' + (new URLSearchParams(params)),
      {
        method: 'DELETE'
      });

    const data = await response;

    return data;
  }

  static async getCommonOrInstanceProperties(name, version, instanceName, isCommon) {
    const params = instanceName != null ?
      {
        name: name,
        version: version,
        instanceName: instanceName,
        common: isCommon
      } :
      {
        name: name,
        version: version,
        common: isCommon
      }

    const response = await fetch(window.location.pathname +
      'onap/policy/clamp/acm/v2/commission/getCommonOrInstanceProperties' + '?' + (new URLSearchParams(params)));

    return response;
  }

}
