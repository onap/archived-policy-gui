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

import styled from "styled-components";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import React, { useEffect, useState } from "react";
import InstantiationOrderStateChangeItem from "./InstantiationOrderStateChangeItem";
import ACMService from "../../../api/ACMService";
import { Alert, Container, Dropdown } from "react-bootstrap";

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

const ChangeOrderStateModal = (props) => {
  const [show, setShow] = useState(true);
  const [ACMIdentifierList, setACMIdentifierList] = useState([]);
  const [orderedState, setOrderedState] = useState('');
  const [toscaOrderStateObject, setToscaOrderStateObject] = useState({});
  const [instantiationOrderStateError, setInstantiationOrderStateError] = useState(false);
  const [instantiationOrderStateMsgError, setInstantiationOrderStateMsgError] = useState({});
  const [alertMessage, setAlertMessage] = useState(null);

  useEffect(async () => {

    const instantiationOrderState = await ACMService.getInstanceOrderState(
      props.location.instantiationName,
      props.location.instantiationVersion)
      .catch(error => error.message);

    const orderStateJson = await instantiationOrderState.json();

    console.log(orderStateJson);

    if (!instantiationOrderState.ok || orderStateJson['automationCompositionIdentifierList'].length === 0) {
      setInstantiationOrderStateError(true);
      setInstantiationOrderStateMsgError(orderStateJson);
    } else {
      setACMIdentifierList(orderStateJson['automationCompositionIdentifierList']);
      setOrderedState(orderStateJson['orderedState']);
    }
  }, []);

  const handleDropSelect = (event) => {
    console.log("handleDropDownChange called");

    const stateChangeObject = {
      orderedState: event,
      automationCompositionIdentifierList: ACMIdentifierList
    }
    setToscaOrderStateObject(stateChangeObject);
    setOrderedState(event);
  }

  const handleSave = async () => {
    console.log("handleSave called");

    const response = await ACMService.changeInstanceOrderState(toscaOrderStateObject)
      .catch(error => error.message);

    if (response.ok) {
      successAlert();
    } else {
      await errorAlert(response);
    }
  }

  const handleClose = () => {
    console.log('handleClose called');
    setShow(false);
    props.history.push('/');
  }

  const successAlert = () => {
    console.log("successAlert called");
    setAlertMessage(<Alert variant="success">
      <Alert.Heading>Order State Changed Success</Alert.Heading>
      <p>Order State Changed was successfully changed</p>
      <hr/>
    </Alert>);
  }

  const errorAlert = async (response) => {
    console.log("errorAlert called");
    setAlertMessage(<Alert variant="danger">
      <Alert.Heading>Order State Changed Failure</Alert.Heading>
      <p>An error occurred while trying to change order state</p>
      <p>Status code: { await response.status } : { response.statusText }</p>
      <p>Status Text: { await response.text() }</p>
      <hr/>
    </Alert>);
  }

  return (
    <ModalStyled size="sm"
                 show={ show }
                 onHide={ handleClose }
                 backdrop="static"
                 keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>Manage Instantiation</Modal.Title>
      </Modal.Header>
      <div style={ { padding: '5px 5px 0 5px' } }>
        <Modal.Body>
          <Container>
            <Dropdown onSelect={ handleDropSelect }>
              <Dropdown.Toggle variant="dark" id="dropdown-basic">
                Select Order State
              </Dropdown.Toggle>
              <Dropdown.Menu>
                <Dropdown.Item eventKey="UNINITIALISED">UNINITIALISED</Dropdown.Item>
                <Dropdown.Item eventKey="PASSIVE">PASSIVE</Dropdown.Item>
                <Dropdown.Item eventKey="RUNNING">RUNNING</Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
            {
              ACMIdentifierList.map((clIdList, index) => (
                <InstantiationOrderStateChangeItem title={ clIdList.name } orderState={ orderedState } index={ index } key={ index }/>
              ))
            }
          </Container>
          <AlertStyled show={ instantiationOrderStateError }
                       variant="danger">Can't get instantiation ordered state:<br/>{ JSON.stringify(instantiationOrderStateMsgError, null, 2) }</AlertStyled>
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

export default ChangeOrderStateModal;
