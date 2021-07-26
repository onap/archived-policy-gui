/*
 * ============LICENSE_START=======================================================
 * Copyright (C) 2021 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { JSONEditor } from "@json-editor/json-editor";
import ControlLoopService from "../../../api/ControlLoopService";
import OnapConstant from "../../../utils/OnapConstants";
import { Alert } from "react-bootstrap";

const ModalStyled = styled(Modal)`
  @media (min-width: 800px) {
    .modal-xl {
      max-width: 96%;
    }
  }
  background-color: transparent;
`

const StyledMessagesDiv = styled.div`
  overflow: auto;
  min-width: 100%;
  max-height: 300px;
  padding: 5px 5px 0px 5px;
  text-align: center;
`

const CommissioningModal = (props) => {
  const [windowLocationPathName, setWindowLocationPathName] = useState('');
  const [fullToscaTemplate, setFullToscaTemplate] = useState({});
  const [toscaInitialValues, setToscaInitialValues] = useState({});
  const [commonProperties, setCommonProperties] = useState({})
  const [toscaJsonSchema, setToscaJsonSchema] = useState({});
  const [jsonEditor, setJsonEditor] = useState(null);
  const [show, setShow] = useState(true);
  const [alertMessages, setAlertMessages] = useState();
  const name = 'ToscaServiceTemplateSimple';
  const version = '1.0.0';
  let editorTemp = null

  useEffect(async () => {
    const toscaTemplateResponse = await ControlLoopService.getToscaTemplate(name, version, windowLocationPathName)
      .catch(error => error.message);
    const toscaCommonProperties = await ControlLoopService.getCommonProperties(name, version, windowLocationPathName)
      .catch(error => error.message);

    await renderJsonEditor(toscaTemplateResponse, toscaCommonProperties)

  }, []);

  const handleClose = () => {
    console.log('handleClose called');
    setShow(false);
    props.history.push('/');
  }

  const handleSave = () => {
    updateTemplate(jsonEditor.getValue())
  }



  const handleCommission = async () => {
    setWindowLocationPathName(window.location.pathname);

    const decommissioningResponse = await ControlLoopService.deleteToscaTemplate('ToscaServiceTemplateSimple', "1.0.0", windowLocationPathName)
      .catch(error => error.message)

    const recommissioningResponse = await ControlLoopService.uploadToscaFile(fullToscaTemplate, windowLocationPathName)
      .catch(error => error.message)

    await receiveResponseFromCommissioning(recommissioningResponse)
  }

  const receiveResponseFromCommissioning = async (response) => {

    if (await response.ok) {
      setAlertMessages(<Alert variant="success">
        <Alert.Heading>Commissioning Success</Alert.Heading>
        <p>Altered Template was Successfully Uploaded</p>
        <hr/>
      </Alert>);
    }
    else {
      setAlertMessages(<Alert variant="danger">
        <Alert.Heading>Commissioning Failure</Alert.Heading>
        <p>Updated Template Failed to Upload</p>
        <p>Status code: { await response.status }: { response.statusText }</p>
        <p>Response Text: { await response.text() }</p>
        <hr/>
      </Alert>);
    }
  };

  const renderJsonEditor = async (template, commonProps) => {

    const fullTemplate = await template.json()

    setFullToscaTemplate(fullTemplate)
    const shortenedNodeTemplatesObjectStartValues = {}
    // Get the common properties to construct a schema
    // Get valid start values at the same time
    const commonNodeTemplatesJson = await commonProps.json().then(data => {
      const nodeTemplatesArray = Object.entries(data)
      const shortenedNodeTemplatesObject = {}
      nodeTemplatesArray.forEach(([key, template]) => {
        const propertiesObject = {
          properties: template.properties
        }

        shortenedNodeTemplatesObject[key] = propertiesObject

        const propertiesStartValues = {}

        // Get all the existing start values to populate the properties in the schema
        Object.entries(propertiesObject.properties).forEach(([propKey, prop]) => {
          propertiesStartValues[propKey] = fullTemplate.topology_template.node_templates[key].properties[propKey]
        })

        shortenedNodeTemplatesObjectStartValues[key] = propertiesStartValues

      })

      setToscaInitialValues(shortenedNodeTemplatesObjectStartValues)
      return shortenedNodeTemplatesObject;
    })

    const propertySchema = makeSchemaForCommonProperties(commonNodeTemplatesJson)
    setToscaJsonSchema(propertySchema)

    editorTemp = createJsonEditor(propertySchema, shortenedNodeTemplatesObjectStartValues);
    setJsonEditor(editorTemp)
  }

  const updateTemplate = (userAddedCommonPropValues) => {
    const nodeTemplates = fullToscaTemplate.topology_template.node_templates
    const commonPropertyDataEntries = Object.entries(userAddedCommonPropValues)

    commonPropertyDataEntries.forEach(([templateKey, values]) => {
      Object.entries(values).forEach(([propKey, propVal]) => {
        nodeTemplates[templateKey].properties[propKey] = propVal
      })
    })

    fullToscaTemplate.topology_template.node_templates = nodeTemplates

    setFullToscaTemplate(fullToscaTemplate)
    alert('Changes saved. Commission When Ready...')
  }

  const makeSchemaForCommonProperties = (commonProps) => {
    const commonPropsArr = Object.entries(commonProps)

    const newSchemaObject = {}

    newSchemaObject.title = "CommonProperties"
    newSchemaObject.type = "object"
    newSchemaObject.properties = {}

    const newSchemaObjectArray = [];
    commonPropsArr.forEach(([templateKey, template]) => {
      const templateObj = {}

      const propertiesObject = {}
      Object.entries(template.properties).forEach(([propKey, prop]) => {
        // const singlePropObject = {}

        propertiesObject[propKey] = {
          type: getType(prop.type)
        }

      })
      newSchemaObject.properties[templateKey] = {
        properties: propertiesObject
      }
    })

    return newSchemaObject

  }

  const getType = (propertyType) => {
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
        return "string"
    }
  }

  // TODO Need to Hook this up to a new camel endpoint to get it working
  const createJsonEditor = (toscaModel, editorData) => {
    JSONEditor.defaults.options.collapse = false;

    return new JSONEditor(document.getElementById("editor"),
      {
        schema: toscaModel,
        startval: editorData,
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
      })
  }

  return (
    <ModalStyled size="xl"
                 show={ show }
                 onHide={ handleClose }
                 backdrop="static"
                 keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>Edit Common Properties</Modal.Title>
      </Modal.Header>
      <br/>
      <div style={ { padding: '5px 5px 0px 5px' } }>
        <Modal.Body>
          <div id="editor"/>
        </Modal.Body>
      </div>
      <StyledMessagesDiv>
        { alertMessages }
      </StyledMessagesDiv>
      <Modal.Footer>
        <Button
          variant="primary"
          onClick={ handleSave }
        >Save</Button>
        <Button variant="success"
                onClick={ handleCommission }>Commission</Button>
        <Button variant="secondary"
                onClick={ handleClose }>Close</Button>
      </Modal.Footer>
    </ModalStyled>
  );
}

export default CommissioningModal;
