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
import toJson from "enzyme-to-json";
import ChangeOrderStateModal from "./ChangeOrderStateModal";
import { createMemoryHistory } from "history";
import { act } from "react-dom/test-utils";
import clLoopList from "./testFiles/acmList.json";
import orderedStateJson from "./testFiles/orderedStateJson.json";
import ACMService from "../../../api/ACMService";

let logSpy = jest.spyOn(console, 'log')
const oldWindowLocation = window.location
describe('Verify ChangeOrderStateModal', () => {
  const flushPromises = () => new Promise(setImmediate);

  beforeEach(() => {
    logSpy.mockClear()
  })

  afterAll(() => {
    window.location = oldWindowLocation
  })

  beforeAll(() => {
    jest
      .spyOn(global, 'fetch')
      .mockImplementation(() => {
          return {
            ok: true,
            status: 200,
            text: () => "OK",
            json: () => {
              return Promise.resolve(orderedStateJson)
            }
          }
        }
      )


    delete window.location

    // create a new `window.location` object that's *almost*
    // like the real thing
    window.location = Object.defineProperties(
      // start with an empty object on which to define properties
      {},
      {
        // grab all of the property descriptors for the
        // `jsdom` `Location` object
        ...Object.getOwnPropertyDescriptors(oldWindowLocation),
        instantiationName: {
          configurable: true,
          value: "PMSH_Instance1",
        },
        instantiationVersion: {
          configurable: true,
          value: "2.3.1",
        }
      },
    )
  })

  it("renders without crashing", () => {
    act(() => {
      shallow(<ChangeOrderStateModal location={window.location}/>);
    })
  });

  it("renders correctly", () => {
    act(() => {
      const tree = shallow(<ChangeOrderStateModal location={window.location}/>);
      expect(toJson(tree)).toMatchSnapshot();
    })
  });

  it('should have two Button elements', () => {
    act(() => {
      const container = shallow(<ChangeOrderStateModal location={window.location}/>)
      expect(container.find('Button').length).toEqual(2);
    })
  });

  it('should have one dropdown element', () => {
    act(() => {
      const container = shallow(<ChangeOrderStateModal location={ window.location }/>)
      expect(container.find('Dropdown').length).toEqual(1);
    });
  });

  it('handleDropSelect called when dropdown clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<ChangeOrderStateModal history={ history } location={window.location}/>)

    act(() => {
      component.find('Dropdown').get(0).props.onSelect();
      expect(logSpy).toHaveBeenCalledWith('handleDropDownChange called');
    });

    component.unmount();
  });

  it('handleClose called when bottom button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<ChangeOrderStateModal history={ history } location={window.location}/>)

    act(() => {
      component.find('[variant="secondary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });

    component.unmount();
  });

  it('handleClose called when top-right button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<ChangeOrderStateModal history={ history } location={window.location}/>)

    act(() => {
      component.find('[size="sm"]').get(0).props.onHide();
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });

    component.unmount();
  });

  it('handleSave called when save button clicked and response is ok', async () => {
    jest.resetAllMocks()
    const getInstanceOrderStateSpy = jest.spyOn(ACMService, 'getInstanceOrderState')
      .mockImplementationOnce(() => {
        return Promise.resolve({
          ok: true,
          status: 200,
          text: () => "OK",
          json: () => {
            return Promise.resolve(orderedStateJson)
          }
        })
      }
    )

    const changeInstanceOrderStateSpy = jest.spyOn(ACMService, 'changeInstanceOrderState')
      .mockImplementationOnce(() => {
        return Promise.resolve({
          ok: true,
          status: 200,
          text: () => "OK",
          json: () => {
            return Promise.resolve(clLoopList)
          }
        })
      }
    )

    const component = mount(<ChangeOrderStateModal location={window.location}/>)

    await act( async () => {
      component.find('[variant="primary"]').simulate('click');
      await expect(getInstanceOrderStateSpy).toHaveBeenCalled()
      await expect(changeInstanceOrderStateSpy).toHaveBeenCalled()
      expect(logSpy).toHaveBeenNthCalledWith(1,"handleSave called");
      expect(logSpy).toHaveBeenNthCalledWith(2,"successAlert called");
    });
  });

  it('handleSave called when save button clicked and response is not ok', async () => {
    jest.resetAllMocks()
    const getInstanceOrderStateSpy = jest.spyOn(ACMService, 'getInstanceOrderState')
      .mockImplementationOnce(() => {
          return Promise.resolve({
            ok: true,
            status: 200,
            text: () => "OK",
            json: () => {
              return Promise.resolve(orderedStateJson)
            }
          })
        }
      )

    const changeInstanceOrderStateSpy = jest.spyOn(ACMService, 'changeInstanceOrderState')
      .mockImplementationOnce(() => {
          return Promise.resolve({
            ok: false,
            status: 200,
            text: () => "OK",
            json: () => {
              return Promise.resolve(clLoopList)
            }
          })
        }
      )

    const component = mount(<ChangeOrderStateModal location={window.location}/>)

    await act( async () => {
      component.find('[variant="primary"]').simulate('click');
      await expect(getInstanceOrderStateSpy).toHaveBeenCalled()
      await expect(changeInstanceOrderStateSpy).toHaveBeenCalled()
      expect(logSpy).toHaveBeenNthCalledWith(1,"handleSave called");
      expect(logSpy).toHaveBeenNthCalledWith(2,"errorAlert called");
    });
  });

  it('Check useEffect is being called', async () => {
    jest.resetAllMocks()
    jest.spyOn(ACMService, 'getInstanceOrderState')
      .mockImplementationOnce(() => {
          return Promise.resolve({
            ok: true,
            status: 200,
            text: () => "OK",
            json: () => {
              return Promise.resolve(orderedStateJson)
            }
          })
        }
      )

    jest.spyOn(ACMService, 'changeInstanceOrderState')
      .mockImplementationOnce(() => {
          return Promise.resolve({
            ok: true,
            status: 200,
            text: () => "OK",
            json: () => {
              return Promise.resolve(clLoopList)
            }
          })
        }
      )

    const component = mount(<ChangeOrderStateModal location={window.location}/>)

    const useEffect = jest.spyOn(React, "useEffect");
    await act(async () => {
      await flushPromises()
      component.update()
      await expect(useEffect).toHaveBeenCalled();
    })
    component.unmount();
  });
});
