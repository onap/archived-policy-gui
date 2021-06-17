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
import Button from "react-bootstrap/Button";
import React, { useState } from "react";

const UploadToscaFile = (props) => {
  const [windowLocationPathName, setWindowLocationPathname] = useState('');

  const postServiceTemplateHandler = async (event) => {

    event.preventDefault();
    setWindowLocationPathname(window.location.pathname);

    const response = await fetch(windowLocationPathName +
      '/restservices/clds/v2/toscaControlLoop/commissionToscaTemplate', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      credentials: 'same-origin',
      body: JSON.stringify(props.toscaObject),
    });

    const responseObj = await response;
    const responseMessage = await response.text();

    props.onResponseReceived(responseObj, responseMessage);

  }

  return (
    <React.Fragment>
      <Button variant="primary"
              block={ true }
              type="submit"
              onClick={ postServiceTemplateHandler }>
        Upload Tosca Service Template
      </Button>
    </React.Fragment>
  );

};

export default UploadToscaFile;
