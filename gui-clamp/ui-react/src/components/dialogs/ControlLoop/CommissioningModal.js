/*
 * ============LICENSE_START=======================================================
 * Copyright (C) 2021 Nordix Foundation.
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
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { JSONEditor } from "@json-editor/json-editor";
import ControlLoopService from "../../../api/ControlLoopService";
import OnapConstant from "../../../utils/OnapConstants";

const ModalStyled = styled(Modal)`
  @media (min-width: 800px) {
    .modal-xl {
      max-width: 96%;
    }
  }
  background-color: transparent;
`

const StyledMessagesDiv = styled.div`
  overflow: auto;
  min-width: 100%;
  max-height: 300px;
  padding: 5px 5px 0px 5px;
  text-align: center;
`

const CommissioningModal = (props) => {
  const [windowLocationPathName, setWindowLocationPathName] = useState('');
  const [toscaInitialValues, setToscaInitialValues] = useState({});
  const [toscaJsonSchema, setToscaJsonSchema] = useState({});
  const [jsonEditor, setJsonEditor] = useState({});
  const [show, setShow] = useState(true);

  const handleClose = () => {
    console.log('handleClose called');
    setShow(false);
    props.history.push('/');
  }

  const getToscaServiceTemplateHandler = async (toscaServiceTemplateResponse) => {

    if (!toscaServiceTemplateResponse.ok) {
      const toscaData = await toscaServiceTemplateResponse;
      setToscaInitialValues(toscaData);
    } else {
      const toscaData = await toscaServiceTemplateResponse.json();
      setToscaInitialValues(toscaData);
    }
  }

  const getToscaSchemaHandler = async (toscaSchemaResponse) => {

    if (!toscaSchemaResponse.ok) {
      const toscaSchemaData = await toscaSchemaResponse;
      setToscaJsonSchema(toscaSchemaData);
    } else {
      const toscaSchemaData = await toscaSchemaResponse.json();
      setToscaJsonSchema(toscaSchemaData);
    }
  }

  useEffect(async () => {
    setWindowLocationPathName(window.location.pathname);

    const toscaTemplateResponse = await ControlLoopService.getToscaControlLoopDefinitions(windowLocationPathName)
      .catch(error => error.message);

    const toscaSchemaResponse = await ControlLoopService.getToscaServiceTemplateSchema('node_templates', windowLocationPathName);

    createJsonEditor(await toscaSchemaResponse.json(), await toscaTemplateResponse.json());

    }, []);

  // TODO Need to Hook this up to a new camel endpoint to get it working
  const createJsonEditor = (toscaModel, editorData) => {
    JSONEditor.defaults.options.collapse = true;

    return new JSONEditor(document.getElementById("editor"),
      {
        schema: toscaModel,
        startval: editorData,
        theme: 'bootstrap4',
        iconlib: 'fontawesome5',
        object_layout: 'normal',
        disable_properties: false,
        disable_edit_json: false,
        disable_array_reorder: true,
        disable_array_delete_last_row: true,
        disable_array_delete_all_rows: false,
        array_controls_top: true,
        keep_oneof_values: false,
        collapsed: true,
        show_errors: 'always',
        display_required_only: false,
        show_opt_in: false,
        prompt_before_delete: true,
        required_by_default: false,
      })
  }

  return (
    <ModalStyled size="xl"
                 show={ show }
                 onHide={ handleClose }
                 backdrop="static"
                 keyboard={ false }>
      <Modal.Header closeButton>
        <Modal.Title>Edit Control Loop Parameters</Modal.Title>
      </Modal.Header>
      <br/>
      <div style={ { padding: '5px 5px 0px 5px' } }>
        <Modal.Body>
          <div id="editor"/>
        </Modal.Body>
      </div>
      <Modal.Footer>
        <Button variant="primary"
                >Submit</Button>
        <Button variant="secondary"
                onClick={ handleClose }>Close</Button>
      </Modal.Footer>
    </ModalStyled>
  );
}

export default CommissioningModal;
