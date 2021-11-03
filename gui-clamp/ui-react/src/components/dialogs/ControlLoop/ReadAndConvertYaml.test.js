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
import ReadAndConvertYaml from './ReadAndConvertYaml';
import GetToscaTemplate from "./GetToscaTemplate";
import toJson from "enzyme-to-json";
import { act } from "react-dom/test-utils";
import { createMemoryHistory } from "history";

let logSpy = jest.spyOn(console, 'log')
describe('Verify ReadAndConvertYaml', () => {

  const unmockedFetch = global.fetch

  const flushPromises = () => new Promise(setImmediate);

  beforeAll(() => {
    global.fetch = () =>
      Promise.resolve({
        ok: true,
        status: 200,
        text: () => "OK",
        json: () => Promise.resolve({
          "tosca_definitions_version": "tosca_simple_yaml_1_1_0",
          "data_types": {},
          "policy_types": {},
          "topology_template": {},
          "name": "ToscaServiceTemplateSimple",
          "version": "1.0.0",
          "metadata": {},
          "id": "0.19518677404255147"
        })
      })
  })

  beforeEach(() => {
    logSpy.mockClear()
  })

  afterAll(() => {
    global.fetch = unmockedFetch
  })

  it("renders without crashing", () => {
    shallow(<ReadAndConvertYaml/>);
  });

  it("renders correctly", () => {
    const tree = shallow(<ReadAndConvertYaml/>);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have a GetToscaTemplate element', () => {
    const container = shallow(<ReadAndConvertYaml/>)
    expect(container.find('GetToscaTemplate').length).toEqual(1);
  });

  it('should call getToscaServiceTemplateHandler on click', async () => {
    const component = mount(<ReadAndConvertYaml/>);

    await act(async () => {
      component.find('GetToscaTemplate').simulate('click');
      await flushPromises()
      component.update()
      await expect(logSpy).toHaveBeenCalledWith('getToscaServiceTemplateHandler called');
    });
    component.unmount()
  });

  it('should make unsuccessful call getToscaServiceTemplateHandler on click', async () => {
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
    const component = mount(<ReadAndConvertYaml/>);

    await act(async () => {
      component.find('GetToscaTemplate').simulate('click');
      await flushPromises()
      component.update()
      expect(logSpy).toHaveBeenCalledWith('getToscaServiceTemplateHandler called with error');
    });
    component.unmount();
  });

  it('should make unsuccessful call deleteToscaServiceTemplateHandler on click', async () => {
    jest
      .spyOn(global, 'fetch')
      .mockImplementationOnce(() =>
        Promise.resolve({
            ok: true,
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
    const component = mount(<ReadAndConvertYaml/>);

    await act(async () => {
      component.find('GetToscaTemplate').simulate('click');
      await flushPromises()
      component.update()
      component.find('DeleteToscaTemplate').simulate('click');
      await flushPromises()
      component.update()
      expect(logSpy).toHaveBeenCalledWith('deleteTemplateHandler called');
    });

    component.unmount()
  });

  it('should make unsuccessful call deleteToscaServiceTemplateHandler on click', async () => {
    const realUseState = React.useState
    const stubInitialState = [true]

    const useStateSpy = jest.spyOn(React, 'useState')
    useStateSpy.mockImplementationOnce(() => realUseState(stubInitialState));

    jest
      .spyOn(global, 'fetch')
      .mockImplementationOnce(() =>
        Promise.resolve({
            ok: true,
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
    const component = mount(<ReadAndConvertYaml/>);


    await act(async () => {
      component.find('GetToscaTemplate').simulate('click');
      await flushPromises()
      component.update()
    });

    jest
      .spyOn(global, 'fetch')
      .mockImplementationOnce(() =>
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

    await act(async () => {
      component.find('DeleteToscaTemplate').simulate('click');
      await flushPromises()
      component.update()
      expect(logSpy).toHaveBeenCalledWith('deleteTemplateHandler called with error');
    });
    component.unmount()
  });

  it('handleClose called when bottom button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<ReadAndConvertYaml history={ history }/>)


    act(() => {
      component.find('[variant="secondary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
    component.unmount()
  });

  it('handleClose called when top-right button clicked', async () => {
    const history = createMemoryHistory();
    const component = mount(<ReadAndConvertYaml history={ history }/>)
    const logSpy = jest.spyOn(console, 'log');


    await act(async () => {
      component.find('[size="xl"]').get(0).props.onHide();
      await flushPromises()
      component.update()
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
    component.unmount()
  });
});
