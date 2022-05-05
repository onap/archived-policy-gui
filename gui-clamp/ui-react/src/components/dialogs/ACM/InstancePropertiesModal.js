/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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
import ACMService from "../../../api/ACMService";
import Alert from "react-bootstrap/Alert";
import * as PropTypes from "prop-types";
import InstantiationUtils from "./utils/InstantiationUtils";
import Row from "react-bootstrap/Row";
import Form from "react-bootstrap/Form";

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

function Fragment(props) {
  return null;
}

const specialCharacter = /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/;

Fragment.propTypes = {children: PropTypes.node};
const InstancePropertiesModal = (props) => {
  const [show, setShow] = useState(true);
  const [toscaFullTemplate, setToscaFullTemplate] = useState({});
  const [jsonEditor, setJsonEditor] = useState(null);
  const [alertMessage, setAlertMessage] = useState(null);
  const [clearButton, setClearButton] = useState(null);
  const [instancePropertiesGlobal, setInstancePropertiesGlobal] = useState({});
  const [serviceTemplateResponseOk, setServiceTemplateResponseOk] = useState(true);
  const [instancePropertiesResponseOk, setInstancePropertiesResponseOk] = useState(true);
  const [instanceName, setInstanceName] = useState('');
  const [instanceVersion, setInstanceVersion] = useState('');
  const [oldInstanceName, setOldInstanceName] = useState('');
  const [tempInstanceName, setTempInstanceName] = useState('');
  const [validated, setValidated] = useState(false);
  const [editMode, setEditMode] = useState(false);

  useEffect(async () => {
    let isEditMode = false;

    if (props.location.instantiationName !== undefined) {
      isEditMode = true;
      setEditMode(true);
      setInstanceName(props.location.instantiationName);
      setInstanceVersion(props.location.instantiationVersion);
    }

    const instantiationName = isEditMode ? props.location.instantiationName : null;
    console.log(instantiationName);

    const toscaTemplateResponse = await ACMService.getToscaTemplate(templateName, templateVersion, instantiationName)
        .catch(error => error.message);

    const toscaInstanceProperties = await ACMService.getCommonOrInstanceProperties(templateName, templateVersion, instantiationName, false)
      .catch(error => error.message);

    if (toscaInstanceProperties == null || toscaTemplateResponse == null) {
      await warningAlert('Tosca service template is empty')
      return;
    }

    if (!toscaInstanceProperties.ok) {
      const errorResponse = await toscaInstanceProperties.json();
      console.log(errorResponse);
      setInstancePropertiesGlobal(errorResponse);
      setInstancePropertiesResponseOk(false);
    }

    if (!toscaTemplateResponse.ok) {
      const errorResponse = await toscaTemplateResponse.json();
      console.log(errorResponse);
      setToscaFullTemplate(errorResponse);
      setServiceTemplateResponseOk(false);
    }

    if (toscaTemplateResponse.ok && toscaInstanceProperties.ok) {
      const renderedJsonSchema = await InstantiationUtils.parseJsonSchema(toscaTemplateResponse, toscaInstanceProperties);
      setToscaFullTemplate(await (renderedJsonSchema).fullTemplate);
      setJsonEditor(await (renderedJsonSchema).jsonEditor);
    }

  }, []);

  useEffect(async () => {

    await validateInstanceName();

  }, [instanceName])

  const validateInstanceName = async () => {
    console.log('validateInstanceName called');

    if (specialCharacter.test(instanceName)) {
      await warningAlert('Instance name cannot contain special characters');
    } else if (alertMessage) {
      clearWarning();
    }

    if (instanceName.length > 2 && !specialCharacter.test(instanceName)) {
      console.log('validated');
      setOldInstanceName(tempInstanceName);
      setValidated(true);
    } else {
      setValidated(false);
    }
  }

  const handleClose = () => {
    console.log('handleClose called');
    setShow(false);
    props.history.push('/');
  }

  const handleInstanceName = (event) => {
    console.log('handleInstanceName called');

    setTempInstanceName(instanceName);
    setInstanceName(event.target.value);

    validateInstanceName().then(() => {
    });
  }

  const handleSave = async () => {
    if (instanceName !== '' || instanceName !== undefined || instanceName.length > 0) {
      console.log("handleSave called");

      console.log("instanceName to be saved is: " + instanceName);

      if (jsonEditor != null) {
        setToscaFullTemplate(InstantiationUtils.updateTemplate(instanceName, jsonEditor.getValue(), toscaFullTemplate));
      }

      let response = null;

      if (editMode) {
        response = await ACMService.updateInstanceProperties(oldInstanceName, instanceVersion, toscaFullTemplate)
            .catch(error => error.message);
      } else {
        response = await ACMService.createInstanceProperties(toscaFullTemplate)
            .catch(error => error.message);
      }

      if (response.ok) {
        successAlert();
      } else {
        await errorAlert(response);
      }
    } else {
      await warningAlert('Instance name cannot be empty');
    }
  }

  const warningAlert = async (message) => {
    console.log("warningAlert called");

    setAlertMessage(<Alert variant="warning">
      <Alert.Heading>Instantiation Properties Warning</Alert.Heading>
      <p>{message}</p>
      <hr/>
    </Alert>);
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

  const clearWarning = () => {
    console.log("clearWarning called");

    if (specialCharacter.test(instanceName)) {
      return;
    }

    setClearButton(null);
    setAlertMessage(null);
  }

  return (
    <ModalStyled size="xl"
                 show={ show }
                 onHide={ handleClose }
                 backdrop="static"
                 keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>{ editMode ? 'Edit' : 'Create' } Tosca Instance Properties</Modal.Title>
      </Modal.Header>
      <div style={ { padding: '5px 5px 0 5px' } }>
        <Modal.Body>
          <Form.Group as={ Row } controlId="formPlaintextEmail">
            <Form.Label column sm="2">Instance Name:</Form.Label>
            <input sm="5" type="text" style={ { width: '50%' } }
                   value={ instanceName }
                   onChange={ handleInstanceName }
            />
          </Form.Group>
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
        { clearButton }
        <Button variant="primary" disabled={ !validated } onClick={ handleSave } style={validated ? {} : { cursor: "not-allowed" }}>Save</Button>
        <Button variant="secondary" onClick={ handleClose }>Close</Button>
      </Modal.Footer>
    </ModalStyled>
  );
}

export default InstancePropertiesModal;
