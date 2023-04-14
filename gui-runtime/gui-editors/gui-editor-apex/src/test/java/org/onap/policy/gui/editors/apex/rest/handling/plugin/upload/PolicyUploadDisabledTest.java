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

package org.onap.policy.gui.editors.apex.rest.handling.plugin.upload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.onap.policy.gui.editors.apex.ApexEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest(classes = ApexEditor.class,
                properties = "apex-editor.upload-url=")
@AutoConfigureMockMvc
class PolicyUploadDisabledTest {

    @Autowired
    private MockMvc mvc;

    private int sessionId;

    @BeforeEach
    void setUp() throws Exception {
        createSession();
        loadModel();
    }

    @Test
    void testModelUploadDisabled() throws Exception {
        ApexApiResult result = apexRequest(get("/policy/gui/v1/apex/editor/" + sessionId + "/Model/Upload")
            .queryParam("userId", "MyUser"));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        assertThat(result.getMessage()).contains("Model upload is disabled");
    }

    private ApexApiResult apexRequest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        var response = mvc.perform(requestBuilder).andReturn().getResponse();
        return new StandardCoder().decode(response.getContentAsString(), ApexApiResult.class);
    }

    private void createSession() throws Exception {
        ApexApiResult result = apexRequest(get("/policy/gui/v1/apex/editor/-1/Session/Create"));
        sessionId = Integer.parseInt(result.getMessages().get(0));
    }

    private void loadModel() throws Exception {
        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");
        apexRequest(put("/policy/gui/v1/apex/editor/" + sessionId + "/Model/Load")
            .content(modelString).contentType(APPLICATION_JSON));
    }
}
