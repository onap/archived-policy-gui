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
import InstancePropertiesModal from "./InstancePropertiesModal";
import toJson from "enzyme-to-json";
import { createMemoryHistory } from "history";
import { act } from "react-dom/test-utils";
import ACMService from "../../../api/ACMService";
import fullTemp from "./testFiles/fullTemplate.json";
import instanceProps from "./testFiles/instanceProps.json";

const instanceProperties = JSON.parse(JSON.stringify(instanceProps))
const fullTemplate = JSON.parse(JSON.stringify(fullTemp))
let logSpy = jest.spyOn(console, 'log');

describe('Verify InstancePropertiesModal', () => {

  const unmockedFetch = global.fetch
    beforeAll(() => {
      global.fetch = () =>
          Promise.resolve({
            status: 200,
            text: () => "OK",
            json: () => "{GlobalFetch}"
          });
    });

  afterAll(() => {
    global.fetch = unmockedFetch
  });

  beforeEach(() => {
    logSpy.mockClear()
  });

  it("renders without crashing", () => {
    shallow(<InstancePropertiesModal />);
  });

  it("renders correctly", () => {
    const tree = shallow(<InstancePropertiesModal />);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have save button element', () => {
    const container = shallow(<InstancePropertiesModal/>)
    expect(container.find('[variant="primary"]').length).toEqual(1);
  });

  it('should have close button element', () => {
    const container = shallow(<InstancePropertiesModal/>)
    expect(container.find('[variant="secondary"]').length).toEqual(1);
  });

  it('handleCreateUpdateToscaInstanceProperties called when save button clicked', () => {
    const component = shallow(<InstancePropertiesModal />);

    const instanceNameState = jest.spyOn(React, "useState");
    instanceNameState.mockImplementation(instanceName => [instanceName, 'Test']);

    act(() => {
      component.find('[variant="primary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleSave called');
    });
  });

  it('handleClose called when close button clicked', () => {
    const history = createMemoryHistory();
    const component = shallow(<InstancePropertiesModal history={ history }/>)

    act(() => {
      component.find('[variant="secondary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
  });

  it('handleSave called when save button clicked', () => {
    const component = shallow(<InstancePropertiesModal />)

    act(() => {
      component.find('[variant="primary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleSave called');
    });
  });

  it('Check useEffect is being called', async () => {
    const useEffect = jest.spyOn(React, "useEffect");
    const component = shallow(<InstancePropertiesModal />)
    act(async () => {
      expect(useEffect).toHaveBeenCalled();
      component.update()
    });
  });

  it('Check useEffect is being called', async () => {
    const useEffect = jest.spyOn(React, "useEffect");
    const component = shallow(<InstancePropertiesModal />)
    act(async () => {
      expect(useEffect).toHaveBeenCalled();
      component.update();
    });
  });

  it('getToscaTemplate gets called in useEffect with error',  async() => {
    const fetchMock = jest.spyOn(ACMService, 'getToscaTemplate').mockImplementation(() => Promise.resolve({
      ok: false,
      status: 200,
      text: () => "OK",
      json: () => fullTemplate
    }))

    const component = shallow(<InstancePropertiesModal/>)
    act(async () => {
      expect(fetchMock).toHaveBeenCalled();
      component.update();
    });
  });

  it('getCommonOrInstanceProperties gets called in useEffect with error',  async() => {
    const fetchMock = jest.spyOn(ACMService, 'getCommonOrInstanceProperties').mockImplementation(() => Promise.resolve({
      ok: false,
      status: 200,
      text: () => "OK",
      json: () => instanceProperties
    }))

    const component = shallow(<InstancePropertiesModal/>)
    act(async () => {
      expect(fetchMock).toHaveBeenCalled();
      component.update();
    });
  });

});
