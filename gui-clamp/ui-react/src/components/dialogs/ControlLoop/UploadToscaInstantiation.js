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
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import styled from "styled-components";
import Row from "react-bootstrap/Row";
import Form from "react-bootstrap/Form";
import UploadToscaInstantiationFile from "./UploadToscaInstantiationFile";
import jsYaml from "js-yaml";
import Alert from "react-bootstrap/Alert";

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

const UploadToscaInstantiation = (props) => {
  const [show, setShow] = useState(true);
  const [fileIsSelected, setFileIsSelected] = useState(false);
  const [selectedFile, setSelectedFile] = useState();
  const [jsonObject, setJsonObject] = useState([]);
  const [alertMessages, setAlertMessages] = useState();

  const fileUploadHandler = (event) => {
    event.preventDefault();
    console.log('fileUploadHandler called');

    const file = event.currentTarget.files[0];

    if (file !== undefined) {
      console.log('fileDefined called');
      setSelectedFile(file);
      setFileIsSelected(true);

      const fileReader = new FileReader();
      fileReader.onload = () => {
        const jsonFile = jsYaml.load(fileReader.result, 'utf8')
        setJsonObject(jsonFile)
      }

      fileReader.readAsText(file);
    } else {
      console.log('fileUndefined called');
    }
  }

  const onResponseReceivedHandler = async (response) => {
    console.log('onResponseReceivedHandler called');

    if (await response.ok) {
      setAlertMessages(<Alert variant="success">
        <Alert.Heading>Upload Success</Alert.Heading>
        <p>Tosca Instantiation from { selectedFile.name } was Successfully Uploaded</p>
        <hr/>
        <p>Type: { selectedFile.type }</p><p>Size: { +selectedFile.size / 1000 }Kb</p>
      </Alert>);
    } else {
      setAlertMessages(<Alert variant="danger">
        <Alert.Heading>Upload Failure</Alert.Heading>
        <p>Tosca Instantiation from { selectedFile.name } failed to upload</p>
        <p>Status code: { await response.status }: { response.statusText }</p>
        <p>Response Text: { await response.text() }</p>
        <hr/>
        <p>Type: { selectedFile.type }</p><p>Size: { +selectedFile.size / 1000 }Kb</p>
      </Alert>);
    }
  }

  const handleClose = () => {
    console.log('handleClose called');
    setShow(false);
    props.history.push('/');
  }

  return (
    <ModalStyled size="lg" show={ show } onHide={ handleClose } backdrop="static" keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>Tosca Control Loop - Create Instantiation</Modal.Title>
      </Modal.Header>
      <div style={ { padding: '5px 5px 0px 5px' } }>
        <Modal.Body>
          <Form style={ { paddingTop: '20px' } }>
            <Form.Group as={ Row }>
              <Form.File
                type="file"
                className="custom-file-label"
                id="inputGroupFile01"
                onChange={ fileUploadHandler }
                custom
                accept=".yaml,.yml,.json"
                label={ fileIsSelected ? selectedFile.name : 'Please select a file' }></Form.File>
              <UploadToscaInstantiationFile
                jsonObject={jsonObject}
                onResponseReceived={onResponseReceivedHandler}/>
              <Form.Text>Only .yaml, .yml and .json files are supported</Form.Text>
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
        <Button variant="secondary" type="null" onClick={ handleClose }>Close</Button>
      </Modal.Footer>
    </ModalStyled>
  )
}

export default UploadToscaInstantiation;
