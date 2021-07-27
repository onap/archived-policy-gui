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

import React, { useEffect, useState } from "react";
import Modal from "react-bootstrap/Modal";
import styled from "styled-components";
import Button from "react-bootstrap/Button";
import ControlLoopService from "../../../api/ControlLoopService";
import { JSONEditor } from "@json-editor/json-editor";
import Alert from "react-bootstrap/Alert";

const ModalStyled = styled(Modal)`
  @media (min-width: 800px) {
    .modal-xl {
      max-width: 96%;
    }
  }
  background-color: transparent;
`

const DivWhiteSpaceStyled = styled.div`
  overflow: auto;
  min-width: 100%;
  max-height: 300px;
  padding: 5px 5px 0px 5px;
  text-align: center;
`

const templateName = "ToscaServiceTemplateSimple";
const templateVersion = "1.0.0";
let tempJsonEditor = null;

const InstanceModal = (props) => {
  const [show, setShow] = useState(true);
  const [windowLocationPathname, setWindowLocationPathname] = useState('');
  const [toscaFullTemplate, setToscaFullTemplate] = useState({});
  const [toscaFilteredInitialValues, setToscaFilteredInitialValues] = useState({});
  const [toscaJsonSchema, setToscaJsonSchema] = useState({});
  const [jsonEditor, setJsonEditor] = useState(null);
  const [alertMessage, setAlertMessage] = useState(null);

  useEffect(async () => {
    const toscaInstanceProperties = await ControlLoopService.getInstanceProperties(templateName, templateVersion, windowLocationPathname).catch(error => error.message);

    const toscaSchemaResponse = await ControlLoopService.getToscaTemplate(templateName, templateVersion, windowLocationPathname).catch(error => error.message);

    await parseJsonSchema(toscaSchemaResponse, toscaInstanceProperties);

  }, []);

  const parseJsonSchema = async (fullTemplate, initialProperties) => {

    const fullJsonSchemaTemplate = await fullTemplate.json();
    setToscaFullTemplate(fullJsonSchemaTemplate);

    const filteredInitialStartValues = {};

    const instanceProperties = await initialProperties.json().then(properties => {
      const filteredTemplateObj = {};
      const propertiesTemplateArray = Object.entries(properties);

      propertiesTemplateArray.forEach(([key, value]) => {
        const propertiesObj = {
          properties: value.properties
        }

        const propValues = {};
        filteredTemplateObj[key] = propertiesObj;

        Object.entries(propertiesObj.properties).forEach(([pKey, pValue]) => {
          propValues[pKey] = fullJsonSchemaTemplate.topology_template.node_templates[key].properties[pKey];
        });

        filteredInitialStartValues[key] = propValues;
      });

      setToscaFilteredInitialValues(filteredInitialStartValues);

      return filteredTemplateObj;
    });

    const propertySchema = makeSchemaForInstanceProperties(instanceProperties);
    setToscaJsonSchema(propertySchema);

    tempJsonEditor = createJsonEditor(propertySchema, filteredInitialStartValues);
    setJsonEditor(tempJsonEditor);
  }

  const makeSchemaForInstanceProperties = (instanceProps) => {
    const instancePropsArray = Object.entries(instanceProps);

    const newSchemaObject = {};

    newSchemaObject.title = "InstanceProperties";
    newSchemaObject.type = "object";
    newSchemaObject.properties = {};

    const newSchemaObjectArray = [];
    instancePropsArray.forEach(([key, value]) => {
      const templateObj = {};
      const propertiesObject = {};

      Object.entries(value.properties).forEach(([pKey, pValue]) => {
        propertiesObject[pKey] = {
          type: getType(pValue.type)
        }
      });

      newSchemaObject.properties[key] = {
        properties: propertiesObject
      }
    });

    return newSchemaObject;
  }

  const getType = (pType) => {
    switch (pType) {
      case "string":
        return "string";
      case "integer":
        return "integer";
      case "list":
        return "array";
      case "object":
        return "object";
      default:
        return "string";

    }
  }

  const createJsonEditor = (fullSchema, instanceProperties) => {
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
  }

  const handleClose = () => {
    console.log('handleClose called');
    setShow(false);
    props.history.push('/');
  }

  const updateTemplate = (jsonEditorValues) => {
    const nodeTemplates = toscaFullTemplate.topology_template.node_templates;
    const instanceDataProperties = Object.entries(jsonEditorValues);

    instanceDataProperties.forEach(([key, value]) => {
      Object.entries(value).forEach((pKey, pValue) => {
        nodeTemplates[key].properties[pKey] = pValue;
      });
    });

    toscaFullTemplate.topology_template.node_templates = nodeTemplates;

    setToscaFullTemplate(toscaFullTemplate);

  }

  const handleSave = async () => {
    console.log("handleSave called")

    setWindowLocationPathname(window.location.pathname);

    updateTemplate(jsonEditor.getValue());

    const response = await ControlLoopService.createInstanceProperties(toscaFullTemplate, windowLocationPathname).catch(error => error.message);

    if (response.ok) {
      successAlert();
    } else {
      await errorAlert(response);
    }
  }

  const successAlert = () => {
    console.log("successAlert called");
    setAlertMessage(<Alert variant="success">
      <Alert.Heading>Instantiation Properties Success</Alert.Heading>
      <p>Instance Properties was successfully saved</p>
      <hr/>
    </Alert>);
  }

  const errorAlert = async (response) => {
    console.log("errorAlert called");
    setAlertMessage(<Alert variant="danger">
      <Alert.Heading>Instantiation Properties Failure</Alert.Heading>
      <p>An error occurred while trying to save</p>
      <p>Status code: { await response.status } : { response.statusText }</p>
      <p>Status Text: { await response.text() }</p>
      <hr/>
    </Alert>);
  }

  return (
    <ModalStyled size="xl"
                 show={ show }
                 onHide={ handleClose }
                 backdrop="static"
                 keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>Change Tosca Instance Properties</Modal.Title>
      </Modal.Header>
      <div style={ { padding: '5px 5px 0 5px' } }>
        <Modal.Body>
          <div id="editor"/>
        </Modal.Body>
        <DivWhiteSpaceStyled>
          { alertMessage }
        </DivWhiteSpaceStyled>
      </div>
      <Modal.Footer>
        <Button variant="primary" onClick={ handleSave }>Save</Button>
        <Button variant="secondary" onClick={ handleClose }>Close</Button>
      </Modal.Footer>
    </ModalStyled>
  );
}

export default InstanceModal;
