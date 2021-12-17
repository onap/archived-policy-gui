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
 *
 *
 */

import { JSONEditor } from "@json-editor/json-editor";

const InstantiationUtils = {

  parseInstantiationList: (controlLoopList) => {
    const parsedControlLoopList = [];

    controlLoopList.map((instance, index) => {

      const controlLoopObj = {
        index,
        name: instance['name'],
        version: instance['version'],
        orderedState: instance['orderedState'],
        currentState: instance['state'],
        disableDelete: instance['state'] !== 'UNINITIALISED'
      }

      parsedControlLoopList.push(controlLoopObj);
    });

    return parsedControlLoopList;
  },

  makeSchemaForInstanceProperties: (instanceProps) => {
    const instancePropsArray = Object.entries(instanceProps);

    const newSchemaObject = {};

    newSchemaObject.title = "InstanceProperties";
    newSchemaObject.type = "object";
    newSchemaObject.properties = {};

    instancePropsArray.forEach(([key, value]) => {

      const propertiesObject = {};

      Object.entries(value.properties).forEach(([pKey, pValue]) => {
        propertiesObject[pKey] = {
          type: InstantiationUtils.getType(pValue.type)
        }
      });

      newSchemaObject.properties[key] = {
        options: {
          "collapsed": true
        },
        properties: propertiesObject
      }
    });

    return newSchemaObject;
  },

  parseJsonSchema: async (template, instanceProperties) => {
    const fullTemplate = await template.json();

    const filteredInitialValues = {};

    const allInstanceProperties = await instanceProperties.json().then(properties => {
      const filteredTemplateObj = {};
      const propertiesTemplateArray = Object.entries(properties);

      propertiesTemplateArray.forEach(([key, value]) => {
        const propertiesObj = {
          properties: value.properties
        }

        const propValues = {};
        filteredTemplateObj[key] = propertiesObj;

        const jsonNodeSchemaKey = fullTemplate.topology_template.node_templates[key]

        Object.entries(propertiesObj.properties).forEach(([pKey, pValue]) => {
          propValues[pKey] = jsonNodeSchemaKey.properties[pKey];
        });

        filteredInitialValues[key] = propValues;
      });

      return filteredTemplateObj;
    });

    const propertySchema = InstantiationUtils.makeSchemaForInstanceProperties(allInstanceProperties);

    const jsonEditor = InstantiationUtils.createJsonEditor(propertySchema, filteredInitialValues);

    return {
      fullTemplate: fullTemplate,
      jsonEditor: jsonEditor
    }
  },

  getType: (pType) => {
    switch (pType) {
      case "map":
        return "string";
      case "string":
        return "string";
      case "integer":
        return "integer";
      case "list":
        return "array";
      case "object":
        return "object";
      default:
        return "object";
    }
  },

  createJsonEditor: (fullSchema, instanceProperties) => {
    JSONEditor.defaults.options.collapse = true;

    return new JSONEditor(document.getElementById("editor"),
      {
        schema: fullSchema,
        startval: instanceProperties,
        theme: 'bootstrap4',
        iconlib: 'fontawesome5',
        object_layout: 'normal',
        disable_properties: false,
        disable_edit_json: false,
        disable_array_reorder: true,
        disable_array_delete_last_row: true,
        disable_array_delete_all_rows: false,
        array_controls_top: true,
        keep_oneof_values: false,
        collapsed: true,
        show_errors: 'always',
        display_required_only: false,
        show_opt_in: false,
        prompt_before_delete: true,
        required_by_default: false,
      });
  },

  updateTemplate: (jsonEditorValues, fullTemplate) => {
    const nodeTemplates = fullTemplate.topology_template.node_templates;
    const instanceDataProperties = Object.entries(jsonEditorValues);

    instanceDataProperties.forEach(([key, value]) => {
      const nodeTemplatesKey = nodeTemplates[key]
      Object.entries(value).forEach(([pKey, pValue]) => {
        nodeTemplatesKey.properties[pKey] = pValue
      });
    });

    fullTemplate.topology_template.node_templates = nodeTemplates;

    return fullTemplate;
  }
}

export default InstantiationUtils;