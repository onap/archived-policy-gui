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

import React from 'react';
import { mount, shallow } from 'enzyme';
import GetToscaTemplate from './GetToscaTemplate';
import toJson from "enzyme-to-json";
import { act } from "react-dom/test-utils";

describe('Verify GetToscaTemplate', () => {

  const flushPromises = () => new Promise(setImmediate);

  it("renders without crashing", () => {
    shallow(<GetToscaTemplate/>);
  });

  it("renders correctly", () => {
    const tree = shallow(<GetToscaTemplate/>);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have a Button element', () => {
    const container = shallow(<GetToscaTemplate/>)
    expect(container.find('Button').length).toEqual(1);
  });

  it('button should call getTemplateHandler when clicked when response is error', async () => {

    const onGetToscaServiceTemplate = jest.fn()
    jest
      .spyOn(global, 'fetch')
      .mockImplementationOnce(async () =>
        Promise.resolve({
            ok: false,
            status: 200,
            json: () => {
              return Promise.resolve({
                "tosca_definitions_version": "tosca_simple_yaml_1_1_0",
                "data_types": {},
                "policy_types": {},
                "topology_template": {},
                "name": "ToscaServiceTemplateSimple",
                "version": "1.0.0",
                "metadata": {},
                "id": "0.19518677404255147"
              })
            }
          }
        )
      )

    const component = mount(<GetToscaTemplate onGetToscaServiceTemplate={onGetToscaServiceTemplate}/>)
    const logSpy = jest.spyOn(console, 'log');

    await act(async () => {
      component.find('[variant="primary"]').simulate('click');
      await flushPromises()
      component.update()
      expect(logSpy).toHaveBeenCalledWith('getToscaServiceTemplateHandler called with error');
    });
    component.unmount();
  });

  it('should have a Button element with specified text', () => {
    const container = shallow(<GetToscaTemplate/>)
    expect(container.find('Button').text()).toBe('Pull Tosca Service Template');
  });
});
