/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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

package org.onap.policy.gui.server.rest;

import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/apex-editor/policy/gui/v*/apex/editor")
public class ApexEditorRestController {

    /**
     * Strip /apex-editor prefix from Apex Editor rest calls.
     */
    @SuppressWarnings("java:S3752") // Suppress warning about RequestMapping without HTTP method.
    @RequestMapping("/**")
    public ModelAndView forwardApexEditorRest(ModelMap model, HttpServletRequest request) {
        String targetUrl = request.getRequestURI().replaceFirst("^/apex-editor", "");
        return new ModelAndView("forward:" + targetUrl, model);
    }
}
