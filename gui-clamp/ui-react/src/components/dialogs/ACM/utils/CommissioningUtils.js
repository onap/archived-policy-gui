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

import { JSONEditor } from "@json-editor/json-editor";
import { Alert } from "react-bootstrap";
import React from "react";

const CommissioningUtils = {
  makeSchemaForCommonProperties: (commonProps) => {
    const commonPropsArr = Object.entries(commonProps)

    const newSchemaObject = {}

    newSchemaObject.title = "CommonProperties"
    newSchemaObject.type = "object"
    newSchemaObject.properties = {}

    commonPropsArr.forEach(([templateKey, template]) => {

      const propertiesObject = {}
      Object.entries(template.properties).forEach(([propKey, prop]) => {

        propertiesObject[propKey] = {
          type: CommissioningUtils.getType(prop.type)
        }

      })
      newSchemaObject.properties[templateKey] = {
        options: {
          "collapsed": true
        },
        properties: propertiesObject
      }
    })

    return newSchemaObject
  },
  getType: (propertyType) => {
    switch (propertyType)
    {
      case "string":
        return "string"
      case "integer":
        return "integer"
      case "list":
        return "array"
      case "object":
        return "object"
      default:
        return "object"
    }
  },
  createJsonEditor: (toscaModel, editorData) => {
    JSONEditor.defaults.options.collapse = false;

    return new JSONEditor(document.getElementById("editor"),
      {
        schema: toscaModel,
        startval: editorData,
        theme: 'bootstrap4',
        iconlib: 'fontawesome5',
        object_layout: 'normal',
        disable_properties: false,
        disable_edit_json: true,
        disable_array_reorder: true,
        disable_array_delete_last_row: true,
        disable_array_delete_all_rows: false,
        array_controls_top: true,
        keep_oneof_values: false,
        collapsed: false,
        show_errors: 'always',
        display_required_only: false,
        show_opt_in: false,
        prompt_before_delete: true,
        required_by_default: false,
      })
  },
  renderJsonEditor: async (template, commonProps) => {

    const fullTemplate = await template.json()

    let toscaInitialValues
    const allNodeTemplates = fullTemplate.topology_template.node_templates
    const shortenedNodeTemplatesObjectStartValues = {}
    // Get the common properties to construct a schema
    // Get valid start values at the same time
    const commonNodeTemplatesJson = await commonProps.json().then(data => {
      const nodeTemplatesArray = Object.entries(data)
      const shortenedNodeTemplatesObject = {}
      nodeTemplatesArray.forEach(([key, temp]) => {
        const currentNodeTemplate = allNodeTemplates[key]
        const propertiesObject = {
          properties: temp.properties
        }

        shortenedNodeTemplatesObject[key] = propertiesObject

        const propertiesStartValues = {}

        // Get all the existing start values to populate the properties in the schema
        Object.entries(propertiesObject.properties).forEach(([propKey, prop]) => {
          propertiesStartValues[propKey] = currentNodeTemplate.properties[propKey]
        })

        shortenedNodeTemplatesObjectStartValues[key] = propertiesStartValues

      })

      toscaInitialValues = shortenedNodeTemplatesObjectStartValues;
      return shortenedNodeTemplatesObject;
    })

    const propertySchema = CommissioningUtils.makeSchemaForCommonProperties(commonNodeTemplatesJson);

    const editorTemp = CommissioningUtils.createJsonEditor(propertySchema, shortenedNodeTemplatesObjectStartValues);

    return {
      fullTemplate: fullTemplate,
      propertySchema: propertySchema,
      editorTemp: editorTemp,
      toscaInitialValues: toscaInitialValues
    }
  },
  getAlertMessages: async (response) => {
    if (response.ok) {
      return(<Alert variant="success">
        <Alert.Heading>Commissioning Success</Alert.Heading>
        <p>Altered Template was Successfully Uploaded</p>
        <hr/>
      </Alert>);
    }
    else {
      return(<Alert variant="danger">
        <Alert.Heading>Commissioning Failure</Alert.Heading>
        <p>Updated Template Failed to Upload</p>
        <p>Status code: { await response.status }: { response.statusText }</p>
        <p>Response Text: { await response.text() }</p>
        <hr/>
      </Alert>);
    }
  },

  updateTemplate: async (userAddedCommonPropValues, fullToscaTemplate) => {
    const nodeTemplates = fullToscaTemplate.topology_template.node_templates
    const commonPropertyDataEntries = Object.entries(userAddedCommonPropValues)

    // This replaces the values for properties in the full tosca template
    // that will be sent up to the api with the entries the user provided.
    commonPropertyDataEntries.forEach(([templateKey, values]) => {
      Object.entries(values).forEach(([propKey, propVal]) => {
        nodeTemplates[templateKey].properties[propKey] = propVal
      })
    })

    fullToscaTemplate.topology_template.node_templates = nodeTemplates

    alert('Changes saved. Commission When Ready...')
    return fullToscaTemplate
  }
}

export default CommissioningUtils;
