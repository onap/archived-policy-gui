/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 20XX-20YY Nordix Foundation.
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
import React, { useState } from "react";
import GetToscaTemplate from "../../api/GetToscaTemplate";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";

import styled from 'styled-components';

const ModalStyled = styled(Modal)`
  background-color: transparent;
`

const ErrMsgStyled = styled.div`
  color: red;
`

const PreStyled = styled.pre`
  color: #7F0055;
  overflow: auto;
  max-height: 70vh;
`

const ReadAndConvertYaml = (props) => {
  const [show, setShow] = useState(true);
  const [toscaTemplateData, setToscaTemplateData] = useState();
  const name = 'ToscaServiceTemplateSimple';
  const version = '1.0.0';

  const handleClose = () => {
    setShow(false);
    props.history.push('/');
  }

  const getToscaServiceTemplateHandler = (toscaServiceTemplate) => {
    const toscaData = {
      ...toscaServiceTemplate,
      id: Math.random().toString()
    };
    console.log(toscaData);
    setToscaTemplateData(toscaData);
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
        <PreStyled>{ JSON.stringify(toscaTemplateData, null, 2) }</PreStyled>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary"
                type="null"
                onClick={ handleClose }>Cancel</Button>
      </Modal.Footer>
    </ModalStyled>
  );
}

export default ReadAndConvertYaml;
