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
import AccordionHeader from "./AccordionHeader";

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

const InstantiationItem = (props) => {

  return (
    <Accordion>
      <AccordionBody>
        <AccordionHeader title={ props.title } orderedState={ props.orderedState } index={ props.index } key={ props.index } />
        <Accordion.Collapse eventKey={ props.index.toString() }>
          <CardBody>{ props.children }</CardBody>
        </Accordion.Collapse>
      </AccordionBody>
    </Accordion>
  );
}

export default InstantiationItem;
