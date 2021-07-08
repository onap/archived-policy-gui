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
import React from "react";
import UploadToscaInstantiationFile from "./UploadToscaInstantiationFile";
import toJson from "enzyme-to-json";
import { act } from "react-dom/test-utils";

describe('Verify UploadToscaInstantiationFile', () => {

  it("renders without crashing", () => {
    shallow(<UploadToscaInstantiationFile />);
  });

  it("renders correctly", () => {
    const tree = shallow(<UploadToscaInstantiationFile />);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have a Button element', () => {
    const container = shallow(<UploadToscaInstantiationFile />)
    expect(container.find('Button').length).toEqual(1);
  });

  it('Button should have a specific text', () => {
    const container = shallow(<UploadToscaInstantiationFile />)
    expect(container.find('Button').text()).toBe('Upload Tosca Instantiation');
  });

  it('button should call postToscaInstantiationHandler when clicked', async () => {
    const mockFunction = jest.fn(() => 'default');
    const component = mount(<UploadToscaInstantiationFile onResponseReceived={mockFunction}/>)
    const logSpy = jest.spyOn(console, 'log');
    const event = {
      preventDefault() {
      }
    };

    await act(async () => {
      component.find('[variant="primary"]').get(0).props.onClick(event);
      expect(logSpy).toHaveBeenCalledWith('postToscaInstantiationHandler called');
    });
  });
});
