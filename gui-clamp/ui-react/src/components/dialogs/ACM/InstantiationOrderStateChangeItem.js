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
import styled from "styled-components";

const UninitialisedBox = styled.div`
  margin: 2px -15px;
  padding: 8px;
  outline: none;
  font-size: 16px;
  font-weight: normal;
  background: #cccccc;
  border-radius: 8px;
  border: 1px solid #7f7f7f;

  &:focus, &:active, &:after {
    outline: none;
    border-radius: 8px;
  }
`

const PassiveBox = styled.div`
  margin: 2px -15px;
  padding: 8px;
  outline: none;
  font-size: 16px;
  font-weight: normal;
  background: #ffe87c;
  border-radius: 8px;
  border: 1px solid #7f7f7f;

  &:focus, &:active, &:after {
    outline: none;
    border-radius: 8px;
  }
`

const RunningBox = styled.div`
  margin: 2px -15px;
  padding: 8px;
  outline: none;
  font-size: 16px;
  font-weight: normal;
  background: #7ec699;
  border-radius: 8px;
  border: 1px solid #7f7f7f;

  &:focus, &:active, &:after {
    outline: none;
    border-radius: 8px;
  }
`

const InstantiationOrderStateChangeItem = (props) => {

  const renderOrderStateItem = () => {
    console.log("renderOrderStateItem called");
    switch (props.orderState) {
      case 'UNINITIALISED':
        console.log("called UNINITIALISED");
        return renderUninitialisedOrderedState();
      case 'PASSIVE':
        console.log("called PASSIVE");
        return renderPassiveOrderedState();
      case 'RUNNING':
        console.log("called RUNNING");
        return renderRunningOrderedState();
    }
  }

  const renderUninitialisedOrderedState = () => {
    return (
      <UninitialisedBox>{ props.title }</UninitialisedBox>
    )
  }

  const renderPassiveOrderedState = () => {
    return (
      <PassiveBox>{ props.title }</PassiveBox>
    )
  }

  const renderRunningOrderedState = () => {
    return (
      <RunningBox>{ props.title }</RunningBox>
    )
  }

  return (
    <React.Fragment>
      { renderOrderStateItem() }
    </React.Fragment>
  );
}

export default InstantiationOrderStateChangeItem;
