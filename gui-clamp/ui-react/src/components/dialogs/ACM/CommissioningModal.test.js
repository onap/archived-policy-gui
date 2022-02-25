/*
 * ============LICENSE_START=======================================================
 * Copyright (C) 2022 Nordix Foundation.
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
import { act } from "react-dom/test-utils";
import { createMemoryHistory } from "history";
import CommissioningModal from "./CommissioningModal";
import commonProps from "./testFiles/commonProps.json";
import fullTemp from "./testFiles/fullTemplate.json";
import ACMService from "../../../api/ACMService";

let logSpy = jest.spyOn(console, 'log')
const commonProperties = JSON.parse(JSON.stringify(commonProps))
const fullTemplate = JSON.parse(JSON.stringify(fullTemp))
describe('Verify CommissioningModal', () => {

  const unmockedFetch = global.fetch
  beforeAll(() => {
    global.fetch = () =>
      Promise.resolve({
        status: 200,
        text: () => "OK",
        json: () => "{GlobalFetch}"
      })
  })

  afterAll(() => {
    global.fetch = unmockedFetch
  })

  beforeEach(() => {
    logSpy.mockClear()
  })

  it("renders without crashing", () => {
    shallow(<CommissioningModal/>);
  });

  it("renders correctly", () => {
    const tree = shallow(<CommissioningModal/>);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have three Button elements', () => {
    const container = shallow(<CommissioningModal/>)
    expect(container.find('Button').length).toEqual(3);
  });

  it('handleClose called when bottom button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<CommissioningModal history={ history }/>)

    act(() => {
      component.find('[variant="secondary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });

    component.unmount();
  });

  it('handleClose called when top-right button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<CommissioningModal history={ history }/>)

    act(() => {
      component.find('[size="xl"]').get(0).props.onHide();
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });

    component.unmount();
  });

  it('handleSave called when save button clicked', () => {
    const component = shallow(<CommissioningModal/>)
    act(() => {
      component.find('[variant="primary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith("handleSave called");
    });
  });

  it('getToscaTemplate gets called in useEffect with error',  async() => {
    const fetchMock = jest.spyOn(ACMService, 'getToscaTemplate').mockImplementation(() => Promise.resolve({
      ok: false,
      status: 200,
      text: () => "OK",
      json: () => fullTemplate
    }))

    mount(<CommissioningModal/>)
    await act(async () => {
      expect(fetchMock).toHaveBeenCalled();
    });
  });

  it('getCommonProperties gets called in useEffect with error',  async() => {
    const fetchMock = jest.spyOn(ACMService, 'getToscaTemplate').mockImplementation(() => Promise.resolve({
      ok: false,
      status: 200,
      text: () => "OK",
      json: () => commonProperties
    }))

    mount(<CommissioningModal/>)
    await act(async () => {
      expect(fetchMock).toHaveBeenCalled();
    });
  });

  it('useState gets called in useEffect with error',  async() => {
    const useStateSpy = jest.spyOn(React, 'useState')
    jest
      .spyOn(global, 'fetch')
      .mockImplementation(() =>
        Promise.resolve({
          ok: false,
          status: 200,
          text: () => "OK",
          json: () => "{useState}"
        })
      )

    mount(<CommissioningModal/>)
    await act(async () => {
      expect(useStateSpy).toHaveBeenCalledTimes(6);
    });
  });

  it('set state gets called for setFullToscaTemplate',  () => {
    const setFullToscaTemplate = jest.fn();
    const history = createMemoryHistory();
    jest
      .spyOn(global, 'fetch')
      .mockImplementation(() =>
        Promise.resolve({
          ok: true,
          status: 200,
          text: () => "OK",
          json: () => fullTemplate
        })
      )

    mount(<CommissioningModal history={ history }/>)
    act(async () => {
      // expect(renderJsonEditor).toHaveBeenCalled();
      expect(setFullToscaTemplate).toHaveBeenCalledTimes(1);
    });
  });

  it('set state gets called for setToscaJsonSchema useEffect on success',  () => {
    const setToscaJsonEditor = jest.fn();
    const history = createMemoryHistory();
    jest
      .spyOn(global, 'fetch')
      .mockImplementation(() =>
        Promise.resolve({
          ok: true,
          status: 200,
          text: () => "OK",
          json: () => fullTemplate
        })
      )

    mount(<CommissioningModal history={ history }/>)
    act(async () => {
      expect(setToscaJsonEditor).toHaveBeenCalledTimes(1);
    });
  });

  it('Check useEffect is being called', async () => {
    const useEffect = jest.spyOn(React, "useEffect");
    mount(<CommissioningModal/>)
    await act(async () => {
      expect(useEffect).toHaveBeenCalled();
    })
  });

  it('test handleCommission called on click', async () => {
    const deleteToscaTemplateSpy = jest.spyOn(ACMService, 'deleteToscaTemplate').mockImplementation(() => {
      Promise.resolve({
        ok: true,
        status: 200,
        text: () => "OK",
        json: () => "{handleCommissioning}"
      })
    })
    const uploadToscaTemplateSpy = jest.spyOn(ACMService, 'uploadToscaFile').mockImplementation(() => {
      Promise.resolve({
        ok: true,
        status: 200,
        text: () => "OK",
        json: () => "{uploadToscaFile}"
      })
    })


    const useStateSpy = jest.spyOn(React, 'useState')

    const component = shallow(<CommissioningModal/>)
    component.find('[variant="success mr-auto"]').simulate('click');

    await act( async () => {
      expect(logSpy).toHaveBeenCalledWith("handleCommission called")
      expect(await deleteToscaTemplateSpy).toHaveBeenCalled()
      expect(await uploadToscaTemplateSpy).toHaveBeenCalled()
      expect(logSpy).toHaveBeenCalledWith("receiveResponseFromCommissioning called")
      expect(useStateSpy).toHaveBeenCalled()
    })
  })
});
