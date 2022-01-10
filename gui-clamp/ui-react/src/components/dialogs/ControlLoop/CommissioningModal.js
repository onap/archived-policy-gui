/*
 * ============LICENSE_START=======================================================
 * Copyright (C) 2022 Nordix Foundation.
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
import ControlLoopService from "../../../api/ControlLoopService";
import { Alert } from "react-bootstrap";
import CommissioningUtils from "./utils/CommissioningUtils";

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

const AlertStyled = styled(Alert)`
  margin-top: 10px;
`

const CommissioningModal = (props) => {
  const [fullToscaTemplate, setFullToscaTemplate] = useState({});
  const [toscaInitialValues, setToscaInitialValues] = useState({});
  const [commonProperties, setCommonProperties] = useState({})
  const [toscaJsonSchema, setToscaJsonSchema] = useState({});
  const [jsonEditor, setJsonEditor] = useState(null);
  const [show, setShow] = useState(true);
  const [alertMessages, setAlertMessages] = useState();
  const [commonPropertiesResponseOk, setCommonPropertiesResponseOk] = useState(true);
  const [serviceTemplateResponseOk, setServiceTemplateResponseOk] = useState(true);
  const name = 'ToscaServiceTemplateSimple';
  const version = '1.0.0';

  useEffect(async () => {
    const toscaTemplateResponse = await ControlLoopService.getToscaTemplate(name, version)
      .catch(error => error.message);
    const toscaCommonProperties = await ControlLoopService.getCommonOrInstanceProperties(name, version, true)
      .catch(error => error.message);

    if (!toscaCommonProperties.ok) {
      const errorResponse = await toscaCommonProperties.json()
      console.log(errorResponse)
      setCommonProperties(errorResponse)
      setCommonPropertiesResponseOk(false);
    }

    if (!toscaTemplateResponse.ok) {
      const errorResponse = await toscaTemplateResponse.json()
      console.log(errorResponse)
      setFullToscaTemplate(errorResponse)
      setServiceTemplateResponseOk(false);
    }

    if (toscaTemplateResponse.ok && toscaCommonProperties.ok) {
      const renderedEditorObjects = CommissioningUtils.renderJsonEditor(toscaTemplateResponse, toscaCommonProperties)
      setFullToscaTemplate((await renderedEditorObjects).fullTemplate)
      setToscaJsonSchema((await renderedEditorObjects).propertySchema)
      setJsonEditor((await renderedEditorObjects).editorTemp)
      setToscaInitialValues((await renderedEditorObjects).toscaInitialValues)

    }

  }, []);

  const handleClose = () => {
    console.log('handleClose called');
    setShow(false);
    props.history.push('/');
  }

  const handleSave = async () => {
    console.log("handleSave called")
    if (jsonEditor != null) {
      setFullToscaTemplate(await CommissioningUtils.updateTemplate(jsonEditor.getValue(), fullToscaTemplate))
    }
  }



  const handleCommission = async () => {

    console.log("handleCommission called")

    await ControlLoopService.deleteToscaTemplate('ToscaServiceTemplateSimple', "1.0.0")

    const recommissioningResponse = await ControlLoopService.uploadToscaFile(fullToscaTemplate)

    await receiveResponseFromCommissioning(recommissioningResponse)
  }

  const receiveResponseFromCommissioning = async (response) => {
    console.log("receiveResponseFromCommissioning called")
    setAlertMessages(await CommissioningUtils.getAlertMessages(response));
  };

  return (
    <ModalStyled size="xl"
                 show={ show }
                 onHide={ handleClose }
                 backdrop="static"
                 keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>Change Control Loop Common Properties</Modal.Title>
      </Modal.Header>
      <br/>
      <div style={ { padding: '5px 5px 0px 5px' } }>
        <Modal.Body>
          <div id="editor"/>
          <AlertStyled show={ !serviceTemplateResponseOk }
                       variant="danger">Can't get service template:<br/>{ JSON.stringify(fullToscaTemplate, null, 2) }</AlertStyled>
          <AlertStyled show={ !commonPropertiesResponseOk }
                       variant="danger">Can't get common properties:<br/>{ JSON.stringify(commonProperties, null, 2) }</AlertStyled>
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
        <Button variant="success mr-auto"
                onClick={ handleCommission }>Commission</Button>
        <Button variant="secondary"
                onClick={ handleClose }>Close</Button>
      </Modal.Footer>
    </ModalStyled>
  );
}

export default CommissioningModal;
