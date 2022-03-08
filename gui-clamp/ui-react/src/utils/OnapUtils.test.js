/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2022 Nordix Intellectual Property. All rights
 *                             reserved.
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

import OnapUtils from './OnapUtils'

describe('Onap Utils Error Formatting', () => {

    var error = {
       message: "Please populate the required property Threshold",
       path: "root.signatures.0",
       property: "required"
    };

    var error2 = {
        message: "Invalid data type Threshold",
     };

    var errorArray = [error, error2];
    it('Test array formatting', () => {
        var utils = new OnapUtils();
        expect(utils.clickBlocked).toBeFalsy();
        var expectedResult = "Please populate the required property Threshold" + '\n'
            +"Invalid data type Threshold";
        expect(OnapUtils.jsonEditorErrorFormatter(errorArray)).toEqual(expectedResult);
    });

    it('Test error not array formatting', () => {
        let spy = {};
        spy.console = jest.spyOn(console, 'error').mockImplementation(() => {});
        OnapUtils.jsonEditorErrorFormatter(error);
        expect(console.error).toHaveBeenCalled();
        expect(spy.console.mock.calls[0][0]).toContain('jsoneEditorErrorFormatter was passed a non-array argument');
        spy.console.mockRestore();
    });
});