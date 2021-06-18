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

import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { Button } from "react-bootstrap";

const UninitialisedBox = styled.div`
  margin: 0;
  padding: 0 0 1px 0;
  border-bottom: 1px solid #7f7f7f;
  background: #cccccc;
  font-weight: normal;
  border-radius: 0;
`
const PassiveBox = styled.div`
  margin: 0;
  padding: 0 0 1px 0;
  border-bottom: 1px solid #7f7f7f;
  background: #ffe87c;
  font-weight: normal;
  border-radius: 0;
`
const RunningBox = styled.div`
  margin: 0;
  padding: 0 0 1px 0;
  border-bottom: 1px solid #7f7f7f;
  background: #7ec699;
  font-weight: normal;
  border-radius: 0;
`
const ButtonStyle = styled(Button)`
  margin: 0;
  padding: 5px 12px;
  width: 100%;
  text-align: left;
  background: transparent !important;
  color: #000000 !important;
  text-decoration: none !important;
  border: none;
  border-radius: 0;

  :hover, :active :focus {
    color: #000000 !important;
    outline: 0 !important;
    box-shadow: none !important;
    background: transparent !important;
    text-decoration: none !important;
  }
`

const ControlLoopElementItem = (props) => {
  const [title, setTitle] = useState("");

  useEffect(() => {
    const title = props.title.split(".");
    setTitle(title[4]);
  }, []);

  const toggleState = () => {
    switch (props.orderedState) {
      case 'UNINITIALISED':
        return renderUninitialisedOrderedState();
      case 'PASSIVE':
        return renderPassiveOrderedState();
      case 'RUNNING':
        return renderRunningOrderedState();
    }
  }

  const renderUninitialisedOrderedState = () => {
    return (
      <UninitialisedBox className="panel-header">
        <Button as={ ButtonStyle } variant="link">
          { title }
        </Button>
      </UninitialisedBox>
    )
  }

  const renderPassiveOrderedState = () => {
    return (
      <PassiveBox className="panel-header">
        <Button as={ ButtonStyle } variant="link">
          { title }
        </Button>
      </PassiveBox>
    )
  }

  const renderRunningOrderedState = () => {
    return (
      <RunningBox className="panel-header">
        <Button as={ ButtonStyle } variant="link" active={ true }>
          { title }
        </Button>
      </RunningBox>
    )
  }

  return (
    <React.Fragment>
      { toggleState() }
    </React.Fragment>
  );
}

export default ControlLoopElementItem;
