/*
 * -
 *  * ============LICENSE_START=======================================================
 *  *  Copyright (C) 2021 Nordix Foundation.
 *  * ================================================================================
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  * ============LICENSE_END=========================================================
 *
 */
import React from 'react';
import { mount, shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import { act } from "react-dom/test-utils";
import GetLocalToscaFileForUpload from './GetLocalToscaFileForUpload';
import { createMemoryHistory } from 'history';


describe('Verify GetLocalToscaFileForUpload', () => {
  const fs = require('fs');
  let testFile = fs.readFileSync('src/components/dialogs/Policy/toscaData.test.json');
  const file = new Blob([testFile], { type: 'file' });

  beforeEach(() => {
    fetch.resetMocks();
    fetch.mockImplementation(() => {
      return Promise.resolve({
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
          });
        }
      });
    });
  })

  it("renders without crashing", () => {
    shallow(<GetLocalToscaFileForUpload/>);
  });

  it("renders correctly", () => {
    const tree = shallow(<GetLocalToscaFileForUpload/>);
    expect(toJson(tree)).toMatchSnapshot();
  });

  it('should have a UploadToscaFile element', () => {
    const container = shallow(<GetLocalToscaFileForUpload/>)
    expect(container.find('UploadToscaFile').length).toEqual(1);
  });

  it('handleClose called when bottom button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<GetLocalToscaFileForUpload history={ history }/>)
    const logSpy = jest.spyOn(console, 'log');


    act(() => {
      component.find('[variant="secondary"]').simulate('click');
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
  });

  it('handleClose called when top-right button clicked', () => {
    const history = createMemoryHistory();
    const component = mount(<GetLocalToscaFileForUpload history={ history }/>)
    const logSpy = jest.spyOn(console, 'log');


    act(() => {
      component.find('[size="lg"]').get(0).props.onHide();
      expect(logSpy).toHaveBeenCalledWith('handleClose called');
    });
  });

  it('should call fileChangeHandler on change, file undefined', () => {
    const component = mount(<GetLocalToscaFileForUpload/>);
    const logSpy = jest.spyOn(console, 'log');
    const event = {
      preventDefault() {
      },
      currentTarget: { files: [] }
    };

    act(() => {
      component.find('[type="file"]').get(0).props.onChange(event);
      expect(logSpy).toHaveBeenCalledWith('fileChangeHandler called');
    });
  });

  it('should call fileChangeHandler on change, file defined', async () => {
    const component = mount(<GetLocalToscaFileForUpload/>);
    const logSpy = jest.spyOn(console, 'log');
    const event = {
      preventDefault() {
      },
      currentTarget: { files: [file] }
    };

    act(async () => {
      component.find('[type="file"]').get(0).props.onChange(event);
      expect(logSpy).toHaveBeenCalledWith('file defined');
    });
  });
});
