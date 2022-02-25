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

  useEffect(async () => {
    const toscaTemplateResponse = await ACMService.getToscaTemplate(templateName, templateVersion)
        .catch(error => error.message);

    const toscaInstanceProperties = await ACMService.getCommonOrInstanceProperties(templateName, templateVersion, false)
      .catch(error => error.message);

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

  const handleClose = () => {
    console.log('handleClose called');
    setShow(false);
    props.history.push('/');
  }

  const handleSave = async () => {
    console.log("handleSave called");

    setInstanceName(instanceName);

    console.log("instanceName to be saved is: " + instanceName);

    if (jsonEditor != null) {
      setToscaFullTemplate(InstantiationUtils.updateTemplate(jsonEditor.getValue(), toscaFullTemplate));
    }

    const response = await ACMService.createInstanceProperties(instanceName, toscaFullTemplate)
      .catch(error => error.message);

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
