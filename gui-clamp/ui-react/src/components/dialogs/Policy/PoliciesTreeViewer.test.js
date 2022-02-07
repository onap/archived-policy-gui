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
import PoliciesTreeViewer from "./PoliciesTreeViewer";
import fs from "fs";
import toJson from "enzyme-to-json";

describe('Verify PoliciesTreeViewer', () => {

    let toscaPoliciesData = fs.readFileSync('src/components/dialogs/Policy/toscaPoliciesData.test.json', {
        encoding: 'utf8',
        flag: 'r'
    });
    const toscaPoliciesDataArray = JSON.parse(toscaPoliciesData);

    const logSpy = jest.spyOn(console, 'log');


    it("renders correctly", () => {
        const component = shallow(<PoliciesTreeViewer policiesData={'[]'} />);

        expect(toJson(component)).toMatchSnapshot();
    });

    it("tests createPoliciesTree handler", () => {
        const component = shallow(<PoliciesTreeViewer policiesData={ toscaPoliciesDataArray } valueForTreeCreation="name" />);
        component.setState({ policiesTreeData: toscaPoliciesDataArray });

        const instance = component.instance();
        instance.createPoliciesTree(toscaPoliciesDataArray);

        expect(logSpy).toHaveBeenCalledWith('createPoliciesTree called');
        expect(component.state('policiesTreeData')).toEqual(toscaPoliciesDataArray);
    });

});