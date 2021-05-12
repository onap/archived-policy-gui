/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import {ajax_getOKOrFail, ajax_get} from "./ApexAjax";
import {resultForm_activate} from "./ApexResultForm";

function modelHandling_analyse() {
    var requestURL = window.restRootURL + "/Model/Analyse";

    ajax_get(requestURL, function(data) {
        resultForm_activate(document.getElementById("mainArea"), "Model Analysis Result", data.messages.message[0]);
    });
}

function modelHandling_validate() {
    var requestURL = window.restRootURL + "/Model/Validate";

    ajax_getOKOrFail(requestURL, function(data) {
        var validationResultString = "";
        for (let msg of data.messages.message) {
            validationResultString += (msg + "\n");
        }
        resultForm_activate(document.getElementById("mainArea"), "Model Validation Result", validationResultString);
    });
}

export {
    modelHandling_analyse,
    modelHandling_validate
}
