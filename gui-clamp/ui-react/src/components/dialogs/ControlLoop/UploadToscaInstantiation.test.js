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
import toJson from "enzyme-to-json";
import UploadToscaInstantiation from "./UploadToscaInstantiation";
import { createMemoryHistory } from "history";
import { act } from "react-dom/test-utils";

describe("Verify UploadToscaInstantiation", () => {
  const fs = require("fs");
  const jsonFile = fs.readFileSync("src/components/dialogs/Policy/toscaInstantiationData.test.json");
  const file = new Blob([jsonFile], { type: 'file' });

  it("renders without crashing", () => {
    shallow(<UploadToscaInstantiation/>);
  });

  it("renders correctly", () => {
    const tree = shallow(<UploadToscaInstantiation/>);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have a Button element', () => {
    const container = shallow(<UploadToscaInstantiation/>)
    expect(container.find('Button').length).toEqual(1);
  });

  it('handleClose called when bottom button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<UploadToscaInstantiation history={ history }/>)
    const logSpy = jest.spyOn(console, 'log');


    act(() => {
      component.find('[variant="secondary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
  });

  it('handleClose called when top-right button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<UploadToscaInstantiation history={ history }/>)
    const logSpy = jest.spyOn(console, 'log');


    act(() => {
      component.find('[size="lg"]').get(0).props.onHide();
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
  });

  it('fileUploadHandler called when uploading a defined file', () => {
    const component = mount(<UploadToscaInstantiation/>)
    const logSpy = jest.spyOn(console, 'log');

    const event = {
      preventDefault() {
      },
      currentTarget: { files: [file] }
    };

    act(() => {
      component.find('[type="file"]').get(0).props.onChange(event);
      expect(logSpy).toHaveBeenCalledWith('fileUploadHandler called');
      expect(logSpy).toHaveBeenCalledWith('fileDefined called');
    });
  });

  it('fileUploadHandler called when uploading a undefined file', () => {
    const component = mount(<UploadToscaInstantiation/>)
    const logSpy = jest.spyOn(console, 'log');

    const event = {
      preventDefault() {
      },
      currentTarget: { files: [] }
    };

    act(() => {
      component.find('[type="file"]').get(0).props.onChange(event);
      expect(logSpy).toHaveBeenCalledWith('fileUndefined called');
    });
  });
});