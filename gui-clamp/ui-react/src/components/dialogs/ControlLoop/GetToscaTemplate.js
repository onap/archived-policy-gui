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
import Button from "react-bootstrap/Button";
import ControlLoopService from "../../../api/ControlLoopService";

const GetToscaTemplate = (props) => {

  const getTemplateHandler = async () => {

    const response = await ControlLoopService.getToscaTemplate(props.templateName, props.templateVersion)
      .catch(error => error.message);

    if(!response.ok) {
      console.log('getToscaServiceTemplateHandler called with error')

    } else
    {
      console.log('getToscaServiceTemplateHandler called')
    }

    props.onGetToscaServiceTemplate(response);

  }

  return (
    <React.Fragment>
      <Button variant="primary"
              type="submit"
              onClick={ getTemplateHandler }>Pull Tosca Service Template</Button>
    </React.Fragment>
  );


}

export default GetToscaTemplate;
