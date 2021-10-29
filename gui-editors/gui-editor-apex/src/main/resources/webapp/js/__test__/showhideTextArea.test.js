/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

const mod = require('../showhideTextarea');

test('Test showHideTextarea_display_hide', () => {
   let documentSpy = jest.spyOn(document, 'getElementById');
   let element_text = document.createElement("text_textarea");
   let element_showHide = document.createElement("text_showhide");
   documentSpy.mockReturnValue(element_text);
   documentSpy.mockReturnValue(element_showHide);
   const mock_activate = jest.fn(mod.showHideTextarea_display_hide);
   mock_activate('text');
   expect(mock_activate).toBeCalled();
});

test('Test showHideTextarea_display_show', () => {
   let documentSpy = jest.spyOn(document, 'getElementById');
   let element_text = document.createElement("text_textarea");
   let element_showHide = document.createElement("text_showhide");
   documentSpy.mockReturnValue(element_text);
   documentSpy.mockReturnValue(element_showHide);
   const mock_activate = jest.fn(mod.showHideTextarea_display_show);
   mock_activate('text');
   expect(mock_activate).toBeCalled();
});





