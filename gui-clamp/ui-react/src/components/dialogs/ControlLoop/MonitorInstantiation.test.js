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

import { mount, shallow } from "enzyme";
import toJson from "enzyme-to-json";
import { createMemoryHistory } from "history";
import React from "react";
import MonitorInstantiation from "./MonitorInstantiation";
import { act } from "react-dom/test-utils";

describe('Verify MonitoringInstantiation', () => {

  it("renders without crashing", () => {
    shallow(<MonitorInstantiation/>);
  });

  it("renders correctly", () => {
    const tree = shallow(<MonitorInstantiation/>);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have a Button element', () => {
    const container = shallow(<MonitorInstantiation/>)
    expect(container.find('Button').length).toEqual(1);
  });

  it('handleClose called when bottom button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<MonitorInstantiation history={ history }/>)
    const logSpy = jest.spyOn(console, 'log');

    act(() => {
      component.find('[variant="secondary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
  });

  it('handleClose called when top-right button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<MonitorInstantiation history={ history }/>)
    const logSpy = jest.spyOn(console, 'log');

    act(() => {
      component.find('[size="xl"]').get(0).props.onHide();
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
  });
});
