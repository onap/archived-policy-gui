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
import UploadToscaFile from './UploadToscaFile';
import toJson from "enzyme-to-json";
import { act } from "react-dom/test-utils";

describe('Verify UploadToscaFile', () => {

  it("renders without crashing", () => {
    shallow(<UploadToscaFile/>);
  });

  it("renders correctly", () => {
    const tree = shallow(<UploadToscaFile/>);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have a Button element', () => {
    const container = shallow(<UploadToscaFile/>)
    expect(container.find('Button').length).toEqual(1);
  });

  it('button should call postServiceTemplateHandler when clicked', async () => {
    const component = mount(<UploadToscaFile/>)
    const logSpy = jest.spyOn(console, 'log');
    const event = {
      preventDefault() {
      }
    };

    act(async () => {
      component.find('[variant="primary"]').get(0).props.onClick(event);
      expect(logSpy).toHaveBeenCalledWith('postServiceTemplateHandler called');
    })

  });

  it('should have a Button element with specified text', () => {
    const container = shallow(<UploadToscaFile/>)
    expect(container.find('Button').text()).toBe('Upload Tosca Service Template');
  });
});
