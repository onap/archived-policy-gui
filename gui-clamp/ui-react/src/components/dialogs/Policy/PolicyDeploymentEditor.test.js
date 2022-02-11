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
import PolicyDeploymentEditor from "./PolicyDeploymentEditor";
import fs from "fs";
import LoopService from "../../../api/LoopService";

describe('Verify PolicyDeploymentEditor', () => {
    const toscaPolicyDeploymentEditor = fs.readFileSync('src/components/dialogs/Policy/toscaPolicyDeploymentEditor.test.json', {
        encoding: 'utf8',
        flag: 'r'
    });

    const toscaPolicyDeploymentEditorArray = JSON.parse(toscaPolicyDeploymentEditor);

    const initialStateTrue = [
        {
            "name": "monitoring/xacml",
            "value": true
        }
    ];

    const initialStateFalse = [
        {
            "name": "monitoring/xacml",
            "value": false
        }
    ];

    const logSpy = jest.spyOn(console, 'log');

    it("renders correctly", () => {
        const component = shallow(<PolicyDeploymentEditor policyData={toscaPolicyDeploymentEditorArray}/>);
        expect(toJson(component)).toMatchSnapshot();
    });

    it('Test createPdpGroupOperations', () => {
        const component = shallow(<PolicyDeploymentEditor policyData={toscaPolicyDeploymentEditorArray} />);

        const instance = component.instance();

        instance.createPdpGroupOperations(initialStateTrue, initialStateTrue);

        component.update();

        expect(logSpy).toHaveBeenCalledWith('createPdpGroupOperations called');
    });

    it('Test handleUpdatePdpDeployment', () => {
        LoopService.updatePdpDeployment = jest.fn().mockImplementation(() => {
            return Promise.resolve(undefined);
        });

        const component = shallow(<PolicyDeploymentEditor policyData={toscaPolicyDeploymentEditorArray} />);
        component.setState({checkboxesInitialState: initialStateTrue});
        component.setState({checkboxesState: initialStateFalse});

        const instance = component.instance();
        instance.handleUpdatePdpDeployment();

        expect(component.state('showFailAlert')).toEqual(false);
        expect(component.state('showMessage')).toEqual(undefined);

        component.update();

        component.setState({showFailAlert: true});
        component.setState({showMessage: 'Pdp Deployment update Failure'});

        expect(logSpy).toHaveBeenCalledWith('handleUpdatePdpDeployment called');
        expect(component.state('showFailAlert')).toEqual(true);
        expect(component.state('showMessage')).toEqual('Pdp Deployment update Failure');
    });

});