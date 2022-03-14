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
import SvgGenerator from "./SvgGenerator";
import {Router} from "react-router-dom";
import {createMemoryHistory} from "history";
import LoopCache from "../../../api/LoopCache";
import {act} from "react-dom/test-utils";

const logSpy = jest.spyOn(console, 'log')
const history = createMemoryHistory();

describe('Verify SvgGenerator', () => {

    it("renders correctly", () => {
        const component = shallow(<SvgGenerator/>);
        expect(toJson(component)).toMatchSnapshot();
    });

    it("Test renderSvg called", () => {
        shallow(
            <Router history={history}>
                <SvgGenerator loopCache={new LoopCache({})} clickable={ true } generatedFrom='INSTANCE' isBusyLoading={false}/>
            </Router>
        );

        act(async () => {
            expect(logSpy).toHaveBeenCalledWith('renderSvg called');
        });
    });

    it("Test svg click event received", () => {
        const component = shallow(
            <Router history={history}>
                <SvgGenerator loopCache={new LoopCache({})} clickable={ true } generatedFrom='INSTANCE' isBusyLoading={false}/>
            </Router>
        );

        act(async () => {
            component.find('withRouter(SvgGenerator)').simulate('click');
            expect(logSpy).toHaveBeenCalledWith('svg click event received');
        });
    });
});