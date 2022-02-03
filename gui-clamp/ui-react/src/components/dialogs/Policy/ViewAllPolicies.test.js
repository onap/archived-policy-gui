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
import React from "react";
import ViewAllPolicies from "./ViewAllPolicies";
import fs from "fs";
import PolicyToscaService from "../../../api/PolicyToscaService";
import PolicyService from "../../../api/PolicyService";
import CreateLoopModal from "../Loop/CreateLoopModal";
import toJson from "enzyme-to-json";

describe('Verify ViewAllPolicies', () => {
    let toscaPolicyModels = fs.readFileSync('src/components/dialogs/Policy/toscaData.test.json', {
        encoding: 'utf8',
        flag: 'r'
    });
    let toscaPoliciesList = fs.readFileSync('src/components/dialogs/Policy/toscaPoliciesList.test.json', {
        encoding: 'utf8',
        flag: 'r'
    });

    it("renders correctly", () => {
        const component = shallow(<ViewAllPolicies />);
        expect(toJson(component)).toMatchSnapshot();
    });

    it('Test handleClose', () => {
        const historyMock = {push: jest.fn()};
        const handleClose = jest.spyOn(ViewAllPolicies.prototype, 'handleClose');
        const component = shallow(<ViewAllPolicies history={historyMock}/>);

        component.find('[variant="secondary"]').prop('onClick')();

        expect(handleClose).toHaveBeenCalledTimes(1);
        expect(component.state('show')).toEqual(false);
        expect(historyMock.push.mock.calls[0]).toEqual(['/']);

        handleClose.mockClear();
    });

    it('handleClose called when top-right button clicked', () => {
        const historyMock = {push: jest.fn()};
        const handleClose = jest.spyOn(ViewAllPolicies.prototype, 'handleClose');
        const component = shallow(<ViewAllPolicies history={historyMock}/>);

        component.find('[size="xl"]').get(0).props.onHide();

        expect(handleClose).toHaveBeenCalledTimes(1);
        expect(component.state('show')).toEqual(false);
        expect(historyMock.push.mock.calls[0]).toEqual(['/']);

        handleClose.mockClear();
    });

    it('Test getAllToscaModels', () => {
        PolicyToscaService.getToscaPolicyModels = jest.fn().mockImplementation(() => {
            return Promise.resolve(toscaPolicyModels);
        });

        const getAllToscaModels = jest.spyOn(ViewAllPolicies.prototype, 'getAllToscaModels');
        shallow(<ViewAllPolicies/>);

        expect(getAllToscaModels).toHaveBeenCalledTimes(1);
    });

    it('Test setToscaModelsListData', async () => {
        PolicyToscaService.getToscaPolicyModel = jest.fn().mockImplementation(() => {
            return Promise.resolve(toscaPolicyModels);
        });

        const component = shallow(<ViewAllPolicies/>);
        component.setState({toscaModelsListData: toscaPolicyModels})
        expect(component.state('toscaModelsListData')).toEqual(toscaPolicyModels);
    });

    it('Test getAllPolicies', () => {
        PolicyService.getPoliciesList = jest.fn().mockImplementation(() => {
            return Promise.resolve(toscaPoliciesList);
        });

        const getAllPolicies = jest.spyOn(ViewAllPolicies.prototype, 'getAllPolicies');
        shallow(<ViewAllPolicies/>);

        expect(getAllPolicies).toHaveBeenCalledTimes(1);
    });

    it('Test setPoliciesListData', async () => {
        PolicyService.getPoliciesList = jest.fn().mockImplementation(() => {
            return Promise.resolve(toscaPoliciesList);
        });

        const component = shallow(<ViewAllPolicies/>);
        component.setState({policiesListData: toscaPoliciesList});
        component.setState({policiesListDataFiltered: toscaPoliciesList});

        expect(component.state('policiesListData')).toEqual(toscaPoliciesList);
        expect(component.state('policiesListDataFiltered')).toEqual(toscaPoliciesList);
    });

    it('Test handleDeletePolicy event rowdata', async () => {
        const rowData = {
            type: 'onap.policies.monitoring.tcagen2',
            type_version: '1.0.0',
            name: 'MICROSERVICE_vLoadBalancerMS_v1_0_tcagen2_1_0_0_AV0',
            version: '1.0.0'
        }

        PolicyService.deletePolicy = await jest.fn().mockImplementation(() => {
            return Promise.resolve({});
        });

        const event = { target: {value: 'event'}}
        const component = shallow(<CreateLoopModal/>);

        component.setState({showSuccessAlert: true});
        component.setState({showMessage: 'Policy successfully Deleted'});

        component.find('input').simulate('click', event, rowData);
        component.update();

        expect(component.state('showSuccessAlert')).toEqual(true);
        expect(component.state('showMessage')).toEqual('Policy successfully Deleted');
    });
});
