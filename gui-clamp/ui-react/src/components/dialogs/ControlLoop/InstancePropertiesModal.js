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
import * as PropTypes from "prop-types";
import Form from "react-bootstrap/Form";
import Spinner from "react-bootstrap/Spinner";

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

const AlertStyled = styled(Alert)`
  margin-top: 10px;
`

const templateName = "ToscaServiceTemplateSimple";
const templateVersion = "1.0.0";
let tempJsonEditor = null;

function Fragment(props) {
  return null;
}

Fragment.propTypes = { children: PropTypes.node };
const InstancePropertiesModal = (props) => {
  const [show, setShow] = useState(true);
  const [toscaFullTemplate, setToscaFullTemplate] = useState({});
  const [jsonEditor, setJsonEditor] = useState(null);
  const [alertMessage, setAlertMessage] = useState(null);
  const [instancePropertiesGlobal, setInstancePropertiesGlobal] = useState({});
  const [serviceTemplateResponseOk, setServiceTemplateResponseOk] = useState(true);
  const [instancePropertiesResponseOk, setInstancePropertiesResponseOk] = useState(true);
  const [instanceName, setInstanceName] = useState('')
  const [isLoading, setIsLoading] = useState(true);

  useEffect(async () => {
    const toscaInstanceProperties = await ControlLoopService.getCommonOrInstanceProperties(templateName, templateVersion, false)
      .catch(error => error.message);

    const toscaTemplateResponse = await ControlLoopService.getToscaTemplate(templateName, templateVersion)
      .catch(error => error.message);

    if (!toscaInstanceProperties.ok) {
      const errorResponse = await toscaInstanceProperties.json()
      console.log(errorResponse)
      setInstancePropertiesGlobal(errorResponse);
      setInstancePropertiesResponseOk(false);
    }

    if (!toscaTemplateResponse.ok) {
      const errorResponse = await toscaTemplateResponse.json()
      console.log(errorResponse)
      setToscaFullTemplate(errorResponse)
      setServiceTemplateResponseOk(false);
    }

    if (toscaTemplateResponse.ok && toscaInstanceProperties.ok) {
      await parseJsonSchema(toscaTemplateResponse, toscaInstanceProperties);
    }

  }, []);

  const parseJsonSchema = async (fullTemplate, initialProperties) => {

    const fullJsonSchemaTemplate = await fullTemplate.json();
    setToscaFullTemplate(fullJsonSchemaTemplate);

    console.log(fullJsonSchemaTemplate);

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

        const jsonNodeSchemaKey = fullJsonSchemaTemplate.topology_template.node_templates[key]

        Object.entries(propertiesObj.properties).forEach(([pKey, pValue]) => {
          propValues[pKey] = jsonNodeSchemaKey.properties[pKey];
        });

        filteredInitialStartValues[key] = propValues;
      });

      return filteredTemplateObj;
    });

    const propertySchema = makeSchemaForInstanceProperties(instanceProperties);

    tempJsonEditor = createJsonEditor(propertySchema, filteredInitialStartValues);
    setJsonEditor(tempJsonEditor);
  }

  const makeSchemaForInstanceProperties = (instanceProps) => {
    const instancePropsArray = Object.entries(instanceProps);

    const newSchemaObject = {};

    newSchemaObject.title = "InstanceProperties";
    newSchemaObject.type = "object";
    newSchemaObject.properties = {};

    instancePropsArray.forEach(([key, value]) => {

      const propertiesObject = {};

      Object.entries(value.properties).forEach(([pKey, pValue]) => {
        propertiesObject[pKey] = {
          type: getType(pValue.type)
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
  }

  const getType = (pType) => {
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
  }

  const createJsonEditor = (fullSchema, instanceProperties) => {
    console.log(props.location.instanceName)
    setIsLoading(false)
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
      const nodeTemplatesKey = nodeTemplates[key]
      Object.entries(value).forEach(([pKey, pValue]) => {
        nodeTemplatesKey.properties[pKey] = pValue
      });
    });

    toscaFullTemplate.topology_template.node_templates = nodeTemplates;

    setToscaFullTemplate(toscaFullTemplate);

  }

  const handleSave = async () => {
    console.log("handleSave called")

    console.log("instanceName to be saved is: " + instanceName)

    updateTemplate(jsonEditor.getValue());

    const response = await ControlLoopService.createInstanceProperties(instanceName, toscaFullTemplate)
      .catch(error => error.message);

    if (response.ok) {
      successAlert();
    } else {
      await errorAlert(response);
    }
  }

  const handleNameChange = (e) => {
    setInstanceName(e.target.value)
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
        <Modal.Title>Create Tosca Instance Properties</Modal.Title>
      </Modal.Header>
      <div style={ { padding: '5px 5px 0 5px' } }>
        <Modal.Body>
          <div id="editor"/>
          <AlertStyled show={ !serviceTemplateResponseOk }
                       variant="danger">Can't get service template:<br/>{ JSON.stringify(toscaFullTemplate, null, 2) }</AlertStyled>
          <AlertStyled show={ !instancePropertiesResponseOk }
                       variant="danger">Can't get instance properties:<br/>{ JSON.stringify(instancePropertiesGlobal, null, 2) }</AlertStyled>
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

export default InstancePropertiesModal;
