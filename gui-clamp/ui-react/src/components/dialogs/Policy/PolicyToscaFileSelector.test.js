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
 */

import {shallow} from "enzyme";
import toJson from "enzyme-to-json";
import React from "react";
import PolicyToscaFileSelector from "./PolicyToscaFileSelector";
import PolicyService from "../../../api/PolicyService";
import Alert from "react-bootstrap/Alert";
import PolicyToscaService from "../../../api/PolicyToscaService";
import fs from "fs";

describe('Verify PolicyToscaFileSelector', () => {

    let toscaPoliciesList = fs.readFileSync('src/components/dialogs/Policy/toscaPoliciesList.test.json', {
        encoding: 'utf8',
        flag: 'r'
    });

    const uploadFile = JSON.stringify(
        {
            name: 'test-file.yaml',
            lastModified: 1639414348371,
            lastModifiedDate: 'Sat Jan 1 2022 00:00:01 GMT+0000',
            size: 32880,
            type: ''
        });

    const file = new Blob([uploadFile], {type: 'file'})
    const logSpy = jest.spyOn(console, 'log');

    const alertMessages = [(
        <Alert variant="success"><Alert.Heading>{file.name}</Alert.Heading><p>Policy Tosca Model Creation Test</p>
            <hr/>
            <p>Type: {file.type}</p><p>Size: {file.size}</p></Alert>)];

    it("renders correctly", () => {
        const component = shallow(<PolicyToscaFileSelector/>);
        expect(toJson(component)).toMatchSnapshot();
    });

    it('Test handleClose', async () => {
        const flushPromises = () => new Promise(setImmediate);
        const showFileSelectorMock = jest.fn();
        const showFileSelector = showFileSelectorMock.bind({
            showFileSelector: false
        });
        const handleClose = jest.spyOn(PolicyToscaFileSelector.prototype, 'handleClose');
        const component = shallow(<PolicyToscaFileSelector disableFunction={showFileSelector} show={false}/>);

        component.find('[variant="secondary"]').get(0).props.onClick();
        await flushPromises();
        component.update();

        expect(handleClose).toHaveBeenCalledTimes(1);
        expect(component.state('show')).toEqual(false);

        handleClose.mockClear();
    });

    it('handleClose called when top-right button clicked', async () => {
        const flushPromises = () => new Promise(setImmediate);
        const showFileSelectorMock = jest.fn();
        const showFileSelector = showFileSelectorMock.bind({
            showFileSelector: false
        });
        const handleClose = jest.spyOn(PolicyToscaFileSelector.prototype, 'handleClose');
        const component = shallow(<PolicyToscaFileSelector disableFunction={showFileSelector} show={false}/>);

        component.find('[size="lg"]').get(0).props.onHide();
        await flushPromises();
        component.update();

        expect(handleClose).toHaveBeenCalledTimes(1);
        expect(component.state('show')).toEqual(false);

        handleClose.mockClear();
    });

    it("onFileChange called when upload button clicked", () => {
        PolicyService.sendNewPolicyModel = jest.fn().mockImplementation(() => {
            return Promise.resolve({});
        });

        const component = shallow(<PolicyToscaFileSelector/>);
        const instance = component.instance();
        const target = {
            currentTarget: {files: [file]}
        }

        instance.onFileChange(target);

        component.find('[type="file"]').get(0).props.onChange(target);
        expect(logSpy).toHaveBeenCalledWith('onFileChange target');
    });

    it("setAlertMessage state", () => {
        PolicyToscaService.getToscaPolicyModels = jest.fn().mockImplementation(() => {
            return Promise.resolve(toscaPoliciesList);
        });
        const getAllToscaModelsMock = jest.fn();
        const getAllToscaModels = getAllToscaModelsMock.bind({
            toscaModelsListData: toscaPoliciesList,
            toscaModelsListDataFiltered: toscaPoliciesList
        });

        const component = shallow(<PolicyToscaFileSelector toscaTableUpdateFunction={getAllToscaModels}/>);
        component.setState({alertMessages: alertMessages});

        expect(component.state('alertMessages')).toEqual(alertMessages);
    });
});