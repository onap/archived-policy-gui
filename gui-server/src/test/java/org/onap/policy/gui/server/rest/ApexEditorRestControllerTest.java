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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {
        "clamp.url=https://clamp-backend:8443/",
        "clamp.disable-ssl-validation=true"
    })
@AutoConfigureMockMvc
class ApexEditorRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testStaticContentUrls() throws Exception {
        mvc.perform(get("/apex-editor/"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/apex-editor/index.html"));

        mvc.perform(get("/apex-editor"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/apex-editor/"));
    }

    @Test
    void testApexEditorRestForwarding() throws Exception {
        mvc.perform(get("/apex-editor/policy/gui/v1/apex/editor/-1/Session/Create"))
            .andExpect(forwardedUrl("/policy/gui/v1/apex/editor/-1/Session/Create"));
    }
}
