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
 *
 *
 */

import {mount, shallow} from "enzyme";
import React from "react";
import toJson from "enzyme-to-json";
import InstantiationManagementModal from "./InstantiationManagementModal";
import {act} from "react-dom/test-utils";
import {createMemoryHistory} from "history";
import ACMService from "../../../api/ACMService";
import acmLoopList from "./testFiles/acmList.json";
import {BrowserRouter} from "react-router-dom";

const logSpy = jest.spyOn(console, 'log')
const history = createMemoryHistory();

describe('Verify Instantiation Management', () => {
    const flushPromises = () => new Promise(setImmediate);

    beforeEach(() => {
        logSpy.mockClear();
    });

    it("renders without crashing", () => {
        shallow(<InstantiationManagementModal/>);
    });
    it("renders correctly", () => {
        const tree = shallow(<InstantiationManagementModal/>);
        expect(toJson(tree)).toMatchSnapshot();
    });

    it('should have a close Button element', () => {
        const container = shallow(<InstantiationManagementModal/>);
        const button = container.find('[variant="secondary"]').at(2);

        expect(button.text()).toEqual("Close");
    });

    it('should have a Create Instance Button element', () => {
        const container = shallow(<InstantiationManagementModal/>);
        const button = container.find('[variant="primary"]').at(0);

        expect(button.text()).toEqual("Create Instance");
    });

    it('should have a Monitor Instantiations Button element', () => {
        const container = shallow(<InstantiationManagementModal/>);
        const button = container.find('[variant="secondary"]').at(0);

        expect(button.text()).toEqual("Monitor Instantiations");
    });

    it('handleClose called when bottom button clicked', () => {
        const container = shallow(<InstantiationManagementModal history={history}/>);
        const button = container.find('[variant="secondary"]').at(2);

        act(() => {
            button.simulate('click');
            expect(logSpy).toHaveBeenCalledWith('handleClose called');
        });
    });

    it('handleClose called when top-right button clicked', () => {
        const container = shallow(<InstantiationManagementModal history={history}/>);

        act(() => {
            container.find('[size="xl"]').get(0).props.onHide();
            expect(logSpy).toHaveBeenCalledWith('handleClose called');
        });
    });

    it('clearErrors called when clear error message button clicked', () => {
        const container = shallow(<InstantiationManagementModal history={history}/>);
        const button = container.find('[variant="secondary"]').at(1);

        act(() => {
            button.simulate('click');
            expect(logSpy).toHaveBeenCalledWith('clearErrors called');
        });
    });

    it('Check useEffect is being called', async () => {
        jest.resetAllMocks();
        jest.spyOn(ACMService, 'getACMInstantiation')
            .mockImplementationOnce(async () => {
                return Promise.resolve({
                    ok: true,
                    status: 200,
                    text: () => "OK",
                    json: () => {
                        return Promise.resolve(acmLoopList);
                    }
                });
            });

        const component = mount(
            <BrowserRouter>
                <InstantiationManagementModal/>
            </BrowserRouter>
        );
        const useEffect = jest.spyOn(React, "useEffect");

        await act(async () => {
            await flushPromises()
            component.update();
            await expect(useEffect).toHaveBeenCalled();

        });
        component.unmount();
    });

    it('set state gets called for setInstantiationList useEffect on success', async () => {
        const setInstantiationList = jest.fn();
        const setDeleteInstantiation = true;
        const history = createMemoryHistory();
        jest
            .spyOn(global, 'fetch')
            .mockImplementation(() =>
                Promise.resolve({
                    ok: true,
                    status: 200,
                    text: () => "OK",
                    json: () => acmLoopList
                })
            )

        mount(
            <BrowserRouter>
                <InstantiationManagementModal history={history}/>
            </BrowserRouter>
        );
        act(async () => {
            expect(setInstantiationList).toHaveBeenCalledTimes(1);
            expect(setDeleteInstantiation).toHaveBeenCalledTimes(1);
        });
    });
});
