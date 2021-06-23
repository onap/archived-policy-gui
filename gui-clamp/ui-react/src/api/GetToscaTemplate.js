/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */
import React, { useState } from "react";
import Button from "react-bootstrap/Button";

const GetToscaTemplate = (props) => {

  const [windowLocationPathName, setWindowLocationPathname] = useState('');

  const getTemplateHandler = async () => {
    console.log('getTemplateHandler called')
    setWindowLocationPathname(window.location.pathname);

    const params = {
      name: props.templateName,
      version: props.templateVersion
    }

    const response = await fetch(windowLocationPathName +
      '/restservices/clds/v2/toscaControlLoop/getToscaTemplate' + '?' + (new URLSearchParams(params)));

    const data = await response.json();

    props.onGetToscaServiceTemplate(data);

  }

  return (
    <React.Fragment>
      <Button variant="primary"
              type="submit"
              onClick={ getTemplateHandler }>Get Tosca Service Template</Button>
    </React.Fragment>
  );


}

export default GetToscaTemplate;
