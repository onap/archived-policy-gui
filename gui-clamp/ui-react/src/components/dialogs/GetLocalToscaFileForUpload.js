/*
 * -
 *  * ============LICENSE_START=======================================================
 *  *  Copyright (C) 2021 Nordix Foundation.
 *  * ================================================================================
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  * ============LICENSE_END=========================================================
 *
 */

import React, { useState } from 'react';
import Button from "react-bootstrap/Button";
import Modal from 'react-bootstrap/Modal';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import styled from 'styled-components';
import Alert from 'react-bootstrap/Alert';
import * as yaml from "js-yaml";
import UploadToscaFile from "../../api/UploadToscaFile";

const ModalStyled = styled(Modal)`
  background-color: transparent;
`

const StyledMessagesDiv = styled.div`
  overflow: auto;
  min-width: 100%;
  max-height: 300px;
  padding: 5px 5px 0px 5px;
  text-align: center;
`

const GetLocalToscaFileForUpload = (props) => {
  const [selectedFile, setSelectedFile] = useState();
  const [fileIsSelected, setFileIsSelected] = useState(false);
  const [toscaJsonObject, setToscaJsonObject] = useState({});
  const [show, setShow] = useState(true);
  const [alertMessages, setAlertMessages] = useState();

  const handleClose = () => {
    setShow(false);
    props.history.push('/');
  }

  const fileChangeHandler = (event) => {
    event.preventDefault();

    if (event.currentTarget.files[0] !== undefined) {
      setSelectedFile(event.currentTarget.files[0]);
      setFileIsSelected(true);

      const file = event.currentTarget.files[0];

      setAlertMessages([]);

      const fileReader = new FileReader();

      fileReader.onload = () => {
        const jsonObj = yaml.load(fileReader.result, 'utf8');
        setToscaJsonObject(jsonObj);
      }

      fileReader.readAsText(file);

    } else {
      return;
    }
  };

  const receiveResponseFromUpload = (response, responseMessage) => {

    if (response.ok) {
      setAlertMessages(<Alert variant="success">
        <Alert.Heading>Upload Success</Alert.Heading>
        <p>Tosca Service Template from { selectedFile.name } was Successfully Uploaded</p>
        <hr/>
        <p>Type: { selectedFile.type }</p><p>Size: { +selectedFile.size / 1000 }Kb</p>
      </Alert>);
    }
    else {
      setAlertMessages(<Alert variant="danger">
        <Alert.Heading>Upload Failure</Alert.Heading>
        <p>Tosca Service Template from { selectedFile.name } failed to upload</p>
        <p>Status code: { response.status }: { response.statusText }</p>
        <p>Response from CLAMP: { responseMessage }</p>
        <hr/>
        <p>Type: { selectedFile.type }</p><p>Size: { +selectedFile.size / 1000 }Kb</p>
      </Alert>);
    }
  };

  return (
    <ModalStyled size="lg"
                 show={ show }
                 onHide={ handleClose }
                 backdrop="static"
                 keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>Upload Tosca to Commissioning API</Modal.Title>
      </Modal.Header>
      <br/>
      <div style={ { padding: '5px 5px 0px 5px' } }>
        <Modal.Body>
          <Form style={ { paddingTop: '20px' } }>
            <Form.Group as={ Row }>
              <Form.File
                type="file"
                className="custom-file-label"
                id="inputGroupFile01"
                onChange={ fileChangeHandler }
                custom
                accept=".yaml,.yml,.json"
                label={ fileIsSelected ? selectedFile.name : 'Please select a file' }
              >
              </Form.File>
              <Form.Text>Only .yaml, .yml and .json files are supported</Form.Text>
            </Form.Group>
            <Form.Group as={ Row }>
              <UploadToscaFile toscaObject={ toscaJsonObject }
                               onResponseReceived={ receiveResponseFromUpload }/>
            </Form.Group>
            <Form.Group as={ Row }>
              <StyledMessagesDiv>
                { alertMessages }
              </StyledMessagesDiv>
            </Form.Group>
          </Form>
        </Modal.Body>
      </div>
      <Modal.Footer>
        <Button variant="secondary"
                onClick={ handleClose }>Close</Button>
      </Modal.Footer>
    </ModalStyled>
  );
}

export default GetLocalToscaFileForUpload;
