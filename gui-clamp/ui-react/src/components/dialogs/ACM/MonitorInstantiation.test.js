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

import React from "react";
import { mount, shallow } from "enzyme";
import toJson from "enzyme-to-json";
import { act } from "react-dom/test-utils";
import { createMemoryHistory } from "history";
import MonitorInstantiation from "./MonitorInstantiation";
import ACMService from "../../../api/ACMService";
import clLoopList from "./testFiles/acmList.json";

const logSpy = jest.spyOn(console, 'log')
const history = createMemoryHistory();

describe('Verify MonitorInstantiation', () => {
  const flushPromises = () => new Promise(setImmediate);

  beforeEach(() => {
    logSpy.mockClear();
  });

  it("renders correctly", () => {
    const container = shallow(<MonitorInstantiation />);
    expect(toJson(container)).toMatchSnapshot();
  });

  it('should have a Button element', () => {
    const container = shallow(<MonitorInstantiation />);
    expect(container.find('Button').length).toEqual(1);
  });

  it('handleClose called when bottom button clicked', () => {
    const container = shallow(<MonitorInstantiation history={ history } />);
    const logSpy = jest.spyOn(console, 'log');

    act(() => {
      container.find('[variant="secondary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
  });

  it('handleClose called when top-right button clicked', () => {
    const container = shallow(<MonitorInstantiation history={ history } />);
    const logSpy = jest.spyOn(console, 'log');

    act(() => {
      container.find('[size="xl"]').get(0).props.onHide();
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
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
            return Promise.resolve(clLoopList);
          }
        });
      });

    const component = mount(<MonitorInstantiation />);
    const useEffect = jest.spyOn(React, "useEffect");

    await act(async () => {
      await flushPromises()
      component.update();
      await expect(useEffect).toHaveBeenCalled();
    });
    component.unmount();
  });
});
