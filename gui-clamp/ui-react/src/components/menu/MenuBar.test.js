/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2022 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */
import React from 'react';
import { shallow } from 'enzyme';
import MenuBar from './MenuBar';
import DropdownItem from "react-bootstrap/DropdownItem";

describe('Verify MenuBar', () => {

  it('Test the render method', () => {
    const component = shallow(<MenuBar/>)

    expect(component).toMatchSnapshot();
  });

  it('Update loopName', () => {
    const component = shallow(<MenuBar/>)
    component.setProps({ loopName: "newLoop" });
    expect(component.state('disabled')).toBe(false);
  });

  it('Default loopName', () => {
    const component = shallow(<MenuBar/>)
    component.setProps({ loopName: "Empty (NO loop loaded yet)" });
    expect(component.state('disabled')).toBe(true);
  });

  it('Find 20 DropdownItem', () => {
    const component = shallow(<MenuBar />)
    expect(component.find('DropdownItem').length).toEqual(20);
  });

  it('Finds POLICY Framework Menu', () => {
    const component = shallow(<MenuBar />).childAt(0).dive();
    expect(component.find({ tile: 'POLICY Framework' }));
  });

  it('Finds POLICY Framework Menu', () => {
    const component = shallow(<MenuBar />).childAt(1).dive();
    expect(component.find({ tile: 'CLAMP Options' }));
  });

  it('Finds POLICY Framework Menu', () => {
    const component = shallow(<MenuBar />).childAt(2).dive();
    expect(component.find({ tile: 'LOOP Instance' }));
  });

  it('Finds POLICY Framework Menu', () => {
    const component = shallow(<MenuBar />).childAt(3).dive();
    expect(component.find({ tile: 'LOOP Operations' }));
  });

  it('Finds POLICY Framework Menu', () => {
    const component = shallow(<MenuBar />).childAt(4).dive();
    expect(component.find({ tile: 'TOSCA Automation Composition' }));
  });

  it('Finds POLICY Framework Menu', () => {
    const component = shallow(<MenuBar />).childAt(5).dive();
    expect(component.find({ tile: 'Help' }));
  });

  it('Finds StyledNavLink', () => {
    const component = shallow(<MenuBar />);
    expect(component.find('Styled(NavLink)').length).toEqual(2);
  });

  it('Finds StyledNavDropdown', () => {
    const component = shallow(<MenuBar />);
    expect(component.find('Styled(NavDropdown)').length).toEqual(6);
  });
});
