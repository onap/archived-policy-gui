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

import React from 'react';
import { mount, shallow } from 'enzyme';
import toJson from "enzyme-to-json";
import DeleteToscaTemplate from "./DeleteToscaTemplate";

describe('Verify DeleteToscaTemplate', () => {

  it("renders without crashing", () => {
    shallow(<DeleteToscaTemplate/>);
  });

  it("renders correctly", () => {
    const tree = shallow(<DeleteToscaTemplate/>);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have a Button element', () => {
    const container = shallow(<DeleteToscaTemplate/>)
    expect(container.find('Button').length).toEqual(1);
  });

  it('button should call deleteTemplateHandler when clicked', async () => {
    const component = mount(<DeleteToscaTemplate/>)
    const logSpy = jest.spyOn(console, 'log');

    component.find('[variant="danger"]').simulate('click');
    expect(logSpy).toHaveBeenCalledWith('deleteTemplateHandler called');
  });

  it('should have a Button element with specified text', () => {
    const container = shallow(<DeleteToscaTemplate/>)
    expect(container.find('Button').text()).toBe('Delete Tosca Service Template');
  });
});
