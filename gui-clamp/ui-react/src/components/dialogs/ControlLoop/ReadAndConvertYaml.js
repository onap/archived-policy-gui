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

import React, { useState } from "react";
import GetToscaTemplate from "./GetToscaTemplate";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import { Alert } from "react-bootstrap";

import styled from 'styled-components';
import DeleteToscaTemplate from "./DeleteToscaTemplate";

const ModalStyled = styled(Modal)`
  background-color: transparent;
`

const AlertStyled = styled(Alert)`
  margin-top: 10px;
`

const PreStyled = styled.pre`
  color: #7F0055;
  overflow: auto;
  max-height: 70vh;
  margin-top: 10px;
`

const ReadAndConvertYaml = (props) => {
  const [show, setShow] = useState(true);
  const [toscaTemplateData, setToscaTemplateData] = useState();
  const [deleteToscaTemplateData, setDeleteToscaTemplateData] = useState();
  const [responseOk, setResponseOk] = useState(true);
  const [deleteResponseOk, setDeleteResponseOk] = useState(true);
  const name = 'ToscaServiceTemplateSimple';
  const version = '1.0.0';

  const handleClose = () => {
    console.log('handleClose called');
    setShow(false);
    props.history.push('/');
  }

  const getToscaServiceTemplateHandler = async (toscaServiceTemplateResponse) => {
    // console.log('getToscaServiceTemplateHandler called: ' + toscaServiceTemplate);

    if (!toscaServiceTemplateResponse.ok) {
      console.log('Response is not ok');
      setResponseOk(false);
      const toscaData = await toscaServiceTemplateResponse.text();
      setToscaTemplateData(toscaData);
    } else {
      setResponseOk(true);
      const toscaData = await toscaServiceTemplateResponse.json();
      setToscaTemplateData(toscaData);
    }
  }

  const deleteToscaServiceTemplateHandler = async (deleteToscaServiceTemplateResponse) => {

    if (!deleteToscaServiceTemplateResponse.ok) {
      console.log('Delete response not ok');
      setDeleteResponseOk(false);
      const deleteToscaData = await deleteToscaServiceTemplateResponse;
      setDeleteToscaTemplateData(deleteToscaData);
    } else {
      setDeleteResponseOk(true);
      const deleteToscaData = await deleteToscaServiceTemplateResponse.json();
      setDeleteToscaTemplateData(deleteToscaData);
    }
  }

  return (
    <ModalStyled size="xl"
                 show={ show }
                 onHide={ handleClose }
                 backdrop="static"
                 keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>View Tosca Template</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <GetToscaTemplate templateName={ name }
                          templateVersion={ version }
                          onGetToscaServiceTemplate={ getToscaServiceTemplateHandler }/>
        { responseOk && <PreStyled> { JSON.stringify(toscaTemplateData, null, 2) } </PreStyled> }
        <AlertStyled show={ !responseOk }
                     variant="danger">{ toscaTemplateData }</AlertStyled>
        { deleteResponseOk && responseOk && toscaTemplateData != null &&
        <DeleteToscaTemplate templateName={ name }
                             templateVersion={ version }
                             onDeleteToscaServiceTemplate={ deleteToscaServiceTemplateHandler }
        />
        }
        <AlertStyled show={ !deleteResponseOk }
                     variant="danger">{ deleteToscaTemplateData }</AlertStyled>
        <AlertStyled show={ deleteResponseOk && deleteToscaTemplateData != null }
                     variant="success"><h2>Delete Successful</h2><PreStyled>{ JSON.stringify(deleteToscaTemplateData, null, 2) }</PreStyled></AlertStyled>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary"
                type="null"
                onClick={ handleClose }>Close</Button>
      </Modal.Footer>
    </ModalStyled>
  );
}
;

export default ReadAndConvertYaml;
