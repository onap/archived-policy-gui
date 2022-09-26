/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022 Nordix Foundation.
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

package org.onap.policy.gui.editors.apex.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.onap.policy.gui.editors.apex.ApexEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * The RestInterface Test.
 */
@SpringBootTest(classes = ApexEditor.class)
@AutoConfigureMockMvc
class RestInterfaceTest {

    @Autowired
    private MockMvc mvc;

    private static final String TESTMODELFILE = "models/PolicyModel.yaml";

    private static String localModelString = null;

    /**
     * Sets up the tests.
     *
     */
    @BeforeAll
    static void setUp() {
        // load a test model locally
        localModelString = ResourceUtils.getResourceAsString(TESTMODELFILE);
    }

    /**
     * Test to see that the message create Model with model id -1 .
     */
    @Test
    void createSession() throws Exception {
        createNewSession();
    }

    /**
     * Helper method to invoke rest call using mock mvc, and return ApexApiResult.
     */
    private ApexApiResult apexRest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        var response = mvc.perform(requestBuilder).andReturn().getResponse();
        return new StandardCoder().decode(response.getContentAsString(), ApexApiResult.class);
    }

    /**
     * Creates a new session.
     *
     * @return the session ID
     */
    private int createNewSession() throws Exception {
        final ApexApiResult responseMsg = apexRest(get("/policy/gui/v1/apex/editor/-1/Session/Create"));
        assertEquals(ApexApiResult.Result.SUCCESS, responseMsg.getResult());
        assertEquals(1, responseMsg.getMessages().size());
        return Integer.parseInt(responseMsg.getMessages().get(0));
    }

    /**
     * Upload policy.
     *
     * @param sessionId         the session ID
     * @param modelAsJsonString the model as json string
     */
    private void uploadPolicy(final int sessionId, final String modelAsJsonString) throws Exception {
        final ApexApiResult responseMsg = apexRest(put("/policy/gui/v1/apex/editor/" + sessionId + "/Model/Load")
            .content(modelAsJsonString).contentType(APPLICATION_JSON));
        assertTrue(responseMsg.isOk());
    }

    /**
    * Create a new session, Upload a test policy model, then get a policy, parse it, and compare it to the same policy
     * in the original model.
     *
     * @throws ApexException if there is an Apex Error
     **/
    @Test
    void testUploadThenGet() throws Exception {

        final int sessionId = createNewSession();

        uploadPolicy(sessionId, localModelString);

        final ApexApiResult responseMsg = apexRest(get("/policy/gui/v1/apex/editor/" + sessionId + "/Policy/Get")
            .queryParam("name", "policy").queryParam("version", "0.0.1"));
        assertTrue(responseMsg.isOk());

        // The string in responseMsg.Messages[0] is a JSON representation of a AxPolicy
        // object. Lets parse it
        final String returnedPolicyAsString = responseMsg.getMessages().get(0);
        ApexModelReader<AxPolicy> apexPolicyReader = new ApexModelReader<>(AxPolicy.class, false);
        final AxPolicy returnedPolicy = apexPolicyReader.read(returnedPolicyAsString);

        assertNotNull(returnedPolicy);
        assertEquals("state", returnedPolicy.getFirstState());
        assertEquals(1, returnedPolicy.getStateMap().size());
    }
}
