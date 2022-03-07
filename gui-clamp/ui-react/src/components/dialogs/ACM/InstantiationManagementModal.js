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
 *
 *
 */

import Modal from "react-bootstrap/Modal";
import { Alert, Container, Table } from "react-bootstrap";
import Button from "react-bootstrap/Button";
import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { Link } from "react-router-dom";
import ACMService from "../../../api/ACMService";
import Row from "react-bootstrap/Row";
import InstantiationUtils from "./utils/InstantiationUtils";

const ModalStyled = styled(Modal)`
  background-color: transparent;
`

const HorizontalSpace = styled.div`
  padding-right: 2px;
  padding-left: 2px;
`;

const DivWhiteSpaceStyled = styled.div`
  overflow: auto;
  min-width: 100%;
  max-height: 300px;
  padding: 5px 5px 0px 5px;
  text-align: center;
`
const InstantiationManagementModal = (props) => {
  const [show, setShow] = useState(true);
  const [instantiationList, setInstantiationList] = useState([]);
  const [alertMessage, setAlertMessage] = useState(null);

  useEffect(async () => {

    const response = await ACMService.getACMInstantiation();

    const instantiationListJson = await response.json();

    const parsedInstantiationList = InstantiationUtils.parseInstantiationList(instantiationListJson['automationCompositionList']);

    setInstantiationList(parsedInstantiationList);
  }, []);

  const getBackgroundColor = (index) => {
    if (index % 2 === 0) {
      return 'Silver';
    }

    return 'White';
  }

  const deleteInstantiationHandler = async (index, instantiation) => {
    console.log("deleteInstantiationHandler called");

    if (instantiation.disableDelete) {
      return;
    }

    const name = instantiation.name;
    const version = instantiation.version;

    const response = await ACMService.deleteInstantiation(name, version);

    updateList(index);

    if (response.ok) {
      successAlert();
    } else {
      await errorAlert(response);
    }
  }

  const updateList = (index) => {
    console.log("updateList called")

    const updatedList = [...instantiationList];
    updatedList.splice(index, 1);

    setInstantiationList(updatedList);
  }

  const handleClose = () => {
    console.log("handleClose called");
    setShow(false);
    props.history.push('/');
  }

  const successAlert = () => {
    console.log("successAlert called");
    setAlertMessage(<Alert variant="success">
      <Alert.Heading>Deletion of Instantiation Success</Alert.Heading>
      <p>Deletion of Instantiation was successful!</p>
      <hr/>
    </Alert>);
  }

  const errorAlert = async (response) => {
    console.log("errorAlert called");
    setAlertMessage(<Alert variant="danger">
      <Alert.Heading>Deletion of Instantiation Failure</Alert.Heading>
      <p>An error occurred while trying to delete instantiation</p>
      <p>Status code: { await response.status } : { response.statusText }</p>
      <p>Status Text: { await response.text() }</p>
      <hr/>
    </Alert>);
  }

  const clearErrors = () => {
    console.log("clearErrors called");
    setAlertMessage(null);
  }

  return (
    <ModalStyled size="xl"
                 show={ show }
                 onHide={ handleClose }
                 backdrop="static"
                 keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>Manage Instances</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Container>
          <Row>
            <Link to={ { pathname: "/editACMInstanceProperties" } }>
              <Button variant="primary" type="null">Create Instance</Button>
            </Link>
            <HorizontalSpace/>
            <Link to={ { pathname: "/monitorInstantiation" } }>
              <Button variant="secondary" type="null">Monitor Instantiations</Button>
            </Link>
          </Row>
        </Container>
        <Table bordered style={ { marginTop: '10px' } }>
          <thead>
          <tr>
            <th>#</th>
            <th style={ { textAlign: "center" } }>Instantiation Name</th>
            <th style={ { textAlign: "center" } }>Edit Instantiation</th>
            <th style={ { textAlign: "center" } }>Delete Instantiation</th>
            <th style={ { textAlign: "center" } }>Change Order State</th>
            <th style={ { textAlign: "center" } }>Instantiation Order State</th>
            <th style={ { textAlign: "center" } }>Instantiation Current State</th>
          </tr>
          </thead>
          <tbody>
          { instantiationList.map((instantiation, index) => {
            return (
              <tr style={ { backgroundColor: getBackgroundColor(index) } } key={ index } className="instantiationList">
                <td>{ index + 1 }</td>
                <td>{ instantiation.name }</td>
                <td style={ { textAlign: "center" } }>
                  <Link to={ {
                    pathname: "editACMInstanceProperties",
                  } } state={ instantiation.name }>
                    <Button variant="outline-success" type="null"
                            disabled={ true }
                            style={ { cursor: "not-allowed" } }>Edit</Button>
                  </Link>
                </td>
                <td style={ { textAlign: "center" } }>
                  <Button variant={ instantiation.disabled ? "outline-danger" : "danger" } type="null"
                          disabled={ instantiation.disableDelete }
                          style={ instantiation.disableDelete ? { cursor: "not-allowed" } : {} }
                          onClick={(e) => deleteInstantiationHandler(index, instantiation)}>Delete</Button>
                </td>
                <td style={ { textAlign: "center" } }>
                  <Link to={ {
                    pathname: "changeOrderState",
                    instantiationName: instantiation.name,
                    instantiationVersion: instantiation.version
                  } }>
                    <Button variant="secondary" type="null">Change</Button>
                  </Link>
                </td>
                <td>{ instantiation.orderedState }</td>
                <td>{ instantiation.currentState }</td>
              </tr>
            )
          }) }
          </tbody>
        </Table>
        <DivWhiteSpaceStyled>
          { alertMessage }
        </DivWhiteSpaceStyled>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" type="null" onClick={ clearErrors }>Clear Error Message</Button>
        <Button variant="secondary" type="null" onClick={ handleClose }>Close</Button>
      </Modal.Footer>
    </ModalStyled>
  );
}

export default InstantiationManagementModal;
