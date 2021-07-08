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

import React from "react";
import styled from 'styled-components';

import { Accordion, Button, Card } from "react-bootstrap";

const AccordionBody = styled.div`
  margin: 0;
  padding: 0;
  border: 1px solid #7f7f7f;
  border-radius: 0;
`

const CardBody = styled(Card.Body)`
  padding: 0;
  margin: 0;
`

const UninitialisedHeader = styled.div`
  margin: 0;
  padding: 0 0 1px 0;
  border-bottom: 1px solid #7f7f7f;
  background: #cccccc;
  font-weight: normal;
  border-radius: 0;
`

const PassiveHeader = styled.div`
  margin: 0;
  padding: 0 0 1px 0;
  border-bottom: 1px solid #7f7f7f;
  background: #ffe87c;
  font-weight: normal;
  border-radius: 0;
`

const RunningHeader = styled.div`
  margin: 0;
  padding: 0 0 1px 0;
  border-bottom: 1px solid #7f7f7f;
  background: #7ec699;
  font-weight: normal;
  border-radius: 0;
`

const ToggleButton = styled(Button)`
  color: #000000;
  text-decoration: none;

  :hover, :active {
    color: #000000;
    text-decoration: none !important;
  }
`

const InstantiationItem = (props) => {
  const toggleState = () => {
    switch (props.orderedState) {
      case 'UNINITIALISED':
        return renderUninitialisedOrderedState()
      case 'PASSIVE':
        return renderPassiveOrderedState();
      case 'RUNNING':
        return renderRunningOrderedState();
    }
  }

  const renderUninitialisedOrderedState = () => {
    return (
      <UninitialisedHeader className="panel-header">
        <Accordion.Toggle as={ToggleButton} variant="link" eventKey={ props.index.toString() }>
          { props.title }
        </Accordion.Toggle>
      </UninitialisedHeader>
    )
  }

  const renderPassiveOrderedState = () => {
    return (
      <PassiveHeader className="panel-header">
        <Accordion.Toggle as={ToggleButton} variant="link" eventKey={ props.index.toString() }>
          { props.title }
        </Accordion.Toggle>
      </PassiveHeader>
    )
  }

  const renderRunningOrderedState = () => {
    return (
      <RunningHeader className="panel-header">
        <Accordion.Toggle as={ToggleButton} variant="link" eventKey={ props.index.toString() }>
          { props.title }
        </Accordion.Toggle>
      </RunningHeader>
    )
  }

  return (
    <Accordion>
      <AccordionBody>
        { toggleState() }
        <Accordion.Collapse eventKey={ props.index.toString() }>
          <CardBody>{ props.children }</CardBody>
        </Accordion.Collapse>
      </AccordionBody>
    </Accordion>
  );
}

export default InstantiationItem;
