/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022 Nordix Foundation.
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

package org.onap.policy.gui.editors.apex.rest.handling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.onap.policy.gui.editors.apex.ApexEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Test Apex Editor Rest Resource.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@SpringBootTest(classes = ApexEditor.class)
@AutoConfigureMockMvc
class ApexEditorRestResourceTest {

    private static final String BASE_URL = "/apexservices/editor/{sessionId}";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RestSessionHandler sessionHandler;

    @Test
    void testSessionCreate() throws Exception {
        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -2));
        assertEquals(Result.FAILED, result.getResult());

        result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        assertEquals(Result.SUCCESS, result.getResult());

        final int corruptSessionId = createCorruptSession();

        apexRequest(get(BASE_URL + "/Model/Analyse", corruptSessionId));

        result = apexRequest(get(BASE_URL + "/Model/Analyse", sessionId));
        assertEquals(Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Model/Analyse", -12345));
        assertEquals(Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Model/Analyse", 12345));
        assertEquals(Result.FAILED, result.getResult());

        apexRequest(get(BASE_URL + "/Model/Validate", corruptSessionId));

        result = apexRequest(get(BASE_URL + "/Model/Validate", sessionId));
        assertEquals(Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Model/Validate", -12345));
        assertEquals(Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Model/Validate", 12345));
        assertEquals(Result.FAILED, result.getResult());

        final String modelString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002699\","
            + "\"description\"      : \"A description of the model\"" + "}";
        result = apexRequest(post(BASE_URL + "/Model/Create", -12345)
                .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/Model/Create", -12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/Model/Create", 1234545)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/Model/Create", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(post(BASE_URL + "/Model/Create", corruptSessionId)
            .content(modelString).contentType(APPLICATION_JSON));

        result = apexRequest(put(BASE_URL + "/Model/Update", -12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Update", -12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Update", 1234545)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Update", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(put(BASE_URL + "/Model/Update", corruptSessionId)
            .content(modelString).contentType(APPLICATION_JSON));

        apexRequest(get(BASE_URL + "/Model/GetKey", corruptSessionId));

        result = apexRequest(get(BASE_URL + "/Model/GetKey", sessionId));
        assertEquals(Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Model/GetKey", -12345));
        assertEquals(Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Model/GetKey", 12345));
        assertEquals(Result.FAILED, result.getResult());

        apexRequest(get(BASE_URL + "/Model/Get", corruptSessionId));

        result = apexRequest(get(BASE_URL + "/Model/Get", sessionId));
        assertEquals(Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Model/Get", -12345));
        assertEquals(Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Model/Get", 12345));
        assertEquals(Result.FAILED, result.getResult());

        String resultString = requestString(get(BASE_URL + "/Model/Download", corruptSessionId));
        assertEquals("", resultString);

        resultString = requestString(get(BASE_URL + "/Model/Download", sessionId));
        assertNotNull(resultString);
    }

    @Test
    void testSessionCreateExt() throws Exception {
        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));

        final int sessionId = Integer.parseInt(result.getMessages().get(0));
        apexRequest(get(BASE_URL + "/Session/Create", -1));
        final int corruptSessionId = createCorruptSession();

        String resultString = requestString(get(BASE_URL + "/Model/Download", -12345));
        assertEquals("", resultString);

        resultString = requestString(get(BASE_URL + "/Model/Download", 12345));
        assertEquals("", resultString);

        apexRequest(get(BASE_URL + "/KeyInformation/Get", corruptSessionId));

        result = apexRequest(get(BASE_URL + "/KeyInformation/Get", sessionId));
        assertEquals(Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/KeyInformation/Get", -12345));
        assertEquals(Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/KeyInformation/Get", 12345));
        assertEquals(Result.FAILED, result.getResult());

        apexRequest(delete(BASE_URL + "/Model/Delete", corruptSessionId));

        result = apexRequest(delete(BASE_URL + "/Model/Delete", sessionId));
        assertEquals(Result.SUCCESS, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Model/Delete", -12345));
        assertEquals(Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Model/Delete", 12345));
        assertEquals(Result.FAILED, result.getResult());
    }

    @Test
    void testContextSchema() throws Exception {
        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        final int corruptSessionId = createCorruptSession();

        result = apexRequest(get(BASE_URL + "/Validate/ContextSchema", -12345));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexRequest(get(BASE_URL + "/Validate/ContextSchema", corruptSessionId));

        result = apexRequest(get(BASE_URL + "/Validate/ContextSchema", sessionId));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexRequest(get(BASE_URL + "/Validate/ContextSchema", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexRequest(get(BASE_URL + "/Validate/ContextSchema", sessionId)
            .queryParam("name", "%%%$£")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        String modelString = "Somewhere over the rainbow";
        result = apexRequest(put(BASE_URL + "/Model/Load", -12345)
            .content(modelString)
            .contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", 12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = "";
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/ContextSchema/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(get(BASE_URL + "/ContextSchema/Get", corruptSessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));

        String csString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"schemaFlavour\"    : \"Java\"," + "\"schemaDefinition\" : \"java.lang.String\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/ContextSchema/Create", -12345)
            .content(csString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/ContextSchema/Create", 1234545)
            .content(csString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/ContextSchema/Create", sessionId)
            .content(csString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(post(BASE_URL + "/ContextSchema/Create", corruptSessionId)
            .content(csString).contentType(APPLICATION_JSON));

        csString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"schemaFlavour\"    : \"Java\"," + "\"schemaDefinition\" : \"my.perfect.String\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/ContextSchema/Update", -12345)
            .content(csString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/ContextSchema/Update", 1234545)
            .content(csString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/ContextSchema/Update", sessionId)
            .content(csString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(put(BASE_URL + "/ContextSchema/Update", corruptSessionId)
            .content(csString).contentType(APPLICATION_JSON));

        result = apexRequest(get(BASE_URL + "/ContextSchema/Get", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/ContextSchema/Get", sessionId)
            .queryParam("name", "NonExistant")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexRequest(get(BASE_URL + "/ContextSchema/Get", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/ContextSchema/Get", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexRequest(get(BASE_URL + "/ContextSchema/Get", corruptSessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));

        result = apexRequest(get(BASE_URL + "/Validate/ContextSchema", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(delete(BASE_URL + "/ContextSchema/Delete", corruptSessionId)
            .queryParam("name", "Hello")
            .queryParam("version", "0.0.2"));

        result = apexRequest(delete(BASE_URL + "/ContextSchema/Delete", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/ContextSchema/Delete", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/ContextSchema/Delete", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", "0.0.2"));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    void testContextSchemaExt() throws Exception {
        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        final int sessionId = Integer.parseInt(result.getMessages().get(0));
        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        apexRequest(get(BASE_URL + "/ContextSchema/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));

        result = apexRequest(delete(BASE_URL + "/ContextSchema/Delete", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    void testContextAlbum() throws Exception {
        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = Integer.parseInt(result.getMessages().get(0));
        final int corruptSessionId = createCorruptSession();

        result = apexRequest(get(BASE_URL + "/Validate/ContextAlbum", -12345));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexRequest(get(BASE_URL + "/Validate/ContextAlbum", corruptSessionId));

        result = apexRequest(get(BASE_URL + "/Validate/ContextAlbum", sessionId));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexRequest(get(BASE_URL + "/Validate/ContextAlbum", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexRequest(get(BASE_URL + "/Validate/ContextAlbum", sessionId)
            .queryParam("name", "%%%$£")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        String modelString = "Somewhere over the rainbow";
        result = apexRequest(put(BASE_URL + "/Model/Load", -12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", 12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = "";
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/ContextAlbum/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        String caString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"scope\"            : \"Domain\"," + "\"writeable\"        : false,"
            + "\"itemSchema\"       : {\"name\" : \"StringType\", \"version\" : \"0.0.1\"},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/ContextAlbum/Create", -12345)
            .content(caString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/ContextAlbum/Create", 1234545)
            .content(caString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/ContextAlbum/Create", sessionId)
            .content(caString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(post(BASE_URL + "/ContextAlbum/Create", corruptSessionId)
            .content(caString).contentType(APPLICATION_JSON));

        caString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"scope\"            : \"Global\"," + "\"writeable\"        : false,"
            + "\"itemSchema\"       : {\"name\" : \"StringType\", \"version\" : \"0.0.1\"},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/ContextAlbum/Update", -12345)
            .content(caString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/ContextAlbum/Update", 1234545)
            .content(caString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/ContextAlbum/Update", sessionId)
            .content(caString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(put(BASE_URL + "/ContextAlbum/Update", corruptSessionId)
            .content(caString).contentType(APPLICATION_JSON));

        apexRequest(get(BASE_URL + "/ContextAlbum/Get", corruptSessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));

        result = apexRequest(get(BASE_URL + "/ContextAlbum/Get", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/ContextAlbum/Get", sessionId)
            .queryParam("name", "IDontExist")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexRequest(get(BASE_URL + "/ContextAlbum/Get", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/ContextAlbum/Get", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexRequest(get(BASE_URL + "/Validate/ContextAlbum", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(delete(BASE_URL + "/ContextAlbum/Delete", corruptSessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));

        result = apexRequest(delete(BASE_URL + "/ContextAlbum/Delete", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/ContextAlbum/Delete", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/ContextAlbum/Delete", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", "0.0.2"));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    void testContextAlbumExt() throws Exception {
        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        apexRequest(get(BASE_URL + "/ContextAlbum/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));

        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    void testEvent() throws Exception {
        final int corruptSessionId = createCorruptSession();

        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        result = apexRequest(get(BASE_URL + "/Validate/Event", -12345));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexRequest(get(BASE_URL + "/Validate/Event", sessionId));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        apexRequest(get(BASE_URL + "/Validate/Event", corruptSessionId));

        result = apexRequest(get(BASE_URL + "/Validate/Event", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexRequest(get(BASE_URL + "/Validate/Event", sessionId)
            .queryParam("name", "%%%$£")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        String modelString = "Somewhere over the rainbow";
        result = apexRequest(put(BASE_URL + "/Model/Load", -12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", 12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = "";
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.yaml");
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Event/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
            + "\"target\"           : \"end\"," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Event/Create", -12345)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/Event/Create", 1234545)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/Event/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(post(BASE_URL + "/Event/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        apexRequest(post(BASE_URL + "/Event/Create", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : \"Hiya\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
            + "\"target\"           : \"end\"," + "\"parameters\"       : {},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Event/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
            + "\"target\"           : \"end\","
            + "\"parameters\"       : {\"Par0\" : {\"name\" : \"StringType\", \"version\" : \"0.0.1\", "
            + "\"localName\" : \"Par0\", \"optional\" : false}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Event/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
            + "\"target\"           : \"end\"," + "\"parameters\"       : {\"Par0\" : null},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Event/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
            + "\"target\"           : \"end\","
            + "\"parameters\"       : {\"Par0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\", "
            + "\"localName\" : \"Par0\", \"optional\" : false}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Event/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.someone.elses.rainbow\"," + "\"source\"           : \"start\","
            + "\"target\"           : \"finish\"," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/Event/Update", -12345)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Event/Update", 1234545)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Event/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(put(BASE_URL + "/Event/Update", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : null," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.someone.elses.rainbow\"," + "\"source\"           : \"start\","
            + "\"target\"           : \"finish\"," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/Event/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"NonExistantEvent\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.someone.elses.rainbow\"," + "\"source\"           : \"start\","
            + "\"target\"           : \"finish\"," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/Event/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexRequest(get(BASE_URL + "/Event/Get", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    void testEventExt() throws Exception {
        final int corruptSessionId = createCorruptSession();

        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
            + "\"target\"           : \"end\"," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        apexRequest(post(BASE_URL + "/Event/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : \"Hiya\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
            + "\"target\"           : \"end\"," + "\"parameters\"       : {},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";

        apexRequest(post(BASE_URL + "/Event/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        apexRequest(post(BASE_URL + "/Event/Create", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        apexRequest(put(BASE_URL + "/Event/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        result = apexRequest(get(BASE_URL + "/Event/Get", sessionId)
            .queryParam("name", "IDontExist")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexRequest(get(BASE_URL + "/Event/Get", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Event/Get", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexRequest(get(BASE_URL + "/Event/Get", corruptSessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));

        result = apexRequest(get(BASE_URL + "/Validate/Event", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Validate/Event", -12345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Validate/Event", 12345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexRequest(delete(BASE_URL + "/Event/Delete", corruptSessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));

        result = apexRequest(delete(BASE_URL + "/Event/Delete", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Event/Delete", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Event/Delete", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", "0.0.2"));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Event/Delete", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    void testTask() throws Exception {
        final int corruptSessionId = createCorruptSession();

        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        result = apexRequest(get(BASE_URL + "/Validate/Task", -12345));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexRequest(get(BASE_URL + "/Validate/Task", sessionId));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        try {
            apexRequest(get(BASE_URL + "/Validate/Task", corruptSessionId));
        } catch (final Exception e) {
            assertEquals("HTTP 500 Request failed.", e.getMessage());
        }

        result = apexRequest(get(BASE_URL + "/Validate/Task", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexRequest(get(BASE_URL + "/Validate/Task", sessionId)
            .queryParam("name", "%%%$£")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        String modelString = "Somewhere over the rainbow";
        result = apexRequest(put(BASE_URL + "/Model/Load", -12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", 12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = "";
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Event/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", -12345)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/Task/Create", 1234545)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        apexRequest(post(BASE_URL + "/Task/Create", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : \"Hiya\"," + "\"version\"          : \"0.0.2\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing\"," + "\"version\"          : \"0.0.2\","
            + "\"inputFields\"      : {\"IField0\" : {\"name\" : \"StringType\", \"version\" : \"0.0.1\", "
            + "\"localName\" : \"IField0\", \"optional\" : false}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi\"," + "\"version\"          : \"0.0.2\","
            + "\"inputFields\"      : {\"IField0\" : null},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay\"," + "\"version\"          : \"0.0.2\","
            + "\"inputFields\"      : {\"IField0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\", "
            + "\"localName\" : \"IField0\", \"optional\" : false}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy\"," + "\"version\"          : \"0.0.2\","
            + "\"inputFields\"      : {\"IField0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\", "
            + "\"localName\" : \"NotIField0\", \"optional\" : false}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing2\"," + "\"version\"          : \"0.0.2\","
            + "\"outputFields\"     : {\"OField0\" : {\"name\" : \"StringType\", \"version\" : \"0.0.1\", "
            + "\"localName\" : \"OField0\", \"optional\" : false}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi2\"," + "\"version\"          : \"0.0.2\","
            + "\"outputFields\"     : {\"OField0\" : null},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay2\"," + "\"version\"          : \"0.0.2\","
            + "\"outputFields\"     : {\"OField0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\","
            + " \"localName\" : \"OField0\", \"optional\" : false}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy2\"," + "\"version\"          : \"0.0.2\","
            + "\"outputFields\"     : {\"OField0\" : {\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\", "
            + "\"localName\" : \"NotOField0\", \"optional\" : false}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
    }

    @Test
    void testTaskExt() throws Exception {
        final int corruptSessionId = createCorruptSession();

        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        apexRequest(get(BASE_URL + "/Event/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        apexRequest(post(BASE_URL + "/Task/Create", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : \"HowsItGoing3\"," + "\"version\"          : \"0.0.2\","
            + "\"taskLogic\"        : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons,"
            + " lots of lime\"}," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi3\"," + "\"version\"          : \"0.0.2\","
            + "\"taskLogic\"        : null," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay3\"," + "\"version\"          : \"0.0.2\","
            + "\"namespace\"        : \"somewhere.over.the.rainbow\"," + "\"source\"           : \"beginning\","
            + "\"target\"           : \"end\","
            + "\"taskLogic\"        : {\"logicFlavour\" : \"UNDEFINED\", \"logic\" : \"lots of lemons,"
            + " lots of lime\"}," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy3\"," + "\"version\"          : \"0.0.2\","
            + "\"taskLogic\"        : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : null},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing4\"," + "\"version\"          : \"0.0.2\","
            + "\"parameters\"       : {\"Par0\" : {\"parameterName\" : \"Par0\", "
            + "\"defaultValue\" : \"Parameter Defaultvalue\"}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi4\"," + "\"version\"          : \"0.0.2\","
            + "\"parameters\"       : {\"Par0\" : null},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay4\"," + "\"version\"          : \"0.0.2\","
            + "\"parameters\"       : {\"Par0\" : {\"parameterName\" : \"NotPar0\", \"defaultValue\" : "
            + "\"Parameter Defaultvalue\"}}," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy4\"," + "\"version\"          : \"0.0.2\","
            + "\"parameters\"       : {\"Par0\" : {\"parameterName\" : \"MyParameter\", \"defaultValue\" : null}},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HowsItGoing5\"," + "\"version\"          : \"0.0.2\","
            + "\"contexts\"         : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hi5\"," + "\"version\"          : \"0.0.2\","
            + "\"contexts\"         : []," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"GoodDay5\"," + "\"version\"          : \"0.0.2\","
            + "\"contexts\"         : [{\"name\" : \"NonExistantType\", \"version\" : \"0.0.1\"}],"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Howdy5\"," + "\"version\"          : \"0.0.2\","
            + "\"contexts\"         : [{\"name\" : null, \"version\" : \"0.0.1\"}],"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002799\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/Task/Update", -12345)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Task/Update", 1234545)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Task/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(put(BASE_URL + "/Task/Update", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : null," + "\"version\"          : \"0.0.2\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/Task/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"NonExistantEvent\"," + "\"version\"          : \"0.0.2\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/Task/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexRequest(get(BASE_URL + "/Task/Get", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Task/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Task/Get", sessionId)
            .queryParam("name", "IDontExist")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexRequest(get(BASE_URL + "/Task/Get", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Task/Get", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexRequest(get(BASE_URL + "/Task/Get", corruptSessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));

        result = apexRequest(get(BASE_URL + "/Validate/Event", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Validate/Event", -12345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Validate/Event", 12345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
    }

    @Test
    void testTaskExt_2() throws Exception {
        final int corruptSessionId = createCorruptSession();

        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        apexRequest(get(BASE_URL + "/Event/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";

        apexRequest(post(BASE_URL + "/Task/Create", 1234545)
            .content(entityString).contentType(APPLICATION_JSON));
        apexRequest(post(BASE_URL + "/Task/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        apexRequest(post(BASE_URL + "/Task/Create", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        result = apexRequest(delete(BASE_URL + "/Task/Delete", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Task/Delete", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Task/Delete", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", "0.0.2"));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Task/Delete", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    @Test
    void testPolicy() throws Exception {
        final int corruptSessionId = createCorruptSession();

        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        assertEquals(Result.SUCCESS, result.getResult());
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        result = apexRequest(get(BASE_URL + "/Model/Validate", -12345));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexRequest(get(BASE_URL + "/Model/Validate", sessionId));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexRequest(get(BASE_URL + "/Model/Validate", corruptSessionId));
        result = apexRequest(get(BASE_URL + "/Model/Validate", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        result = apexRequest(get(BASE_URL + "/Model/Validate", sessionId)
            .queryParam("name", "%%%$£")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        String modelString = "Somewhere over the rainbow";
        result = apexRequest(put(BASE_URL + "/Model/Load", -12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", 12345)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = "";
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");
        result = apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Event/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", -12345)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/Policy/Create", 1234545)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_EXISTS, result.getResult());

        apexRequest(post(BASE_URL + "/Policy/Create", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : \"GoodTaSeeYa\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : null," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HelloAnother\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello2\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : null," + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "  \"stateOutputs\"   : {" + "   \"so0\"           : {"
            + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello3\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : null," + "  \"stateOutputs\"   : {" + "   \"so0\"           : {"
            + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello4\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : null,"
            + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello5\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : null" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello6\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"IDontExist\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello7\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : null" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello8\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {"
            + "    \"event\"        : {\"name\" : \"IDontExist\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello9\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : null," + "    \"nextState\"    : null" + "   }"
            + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        System.err.println(result);
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
    }

    @Test
    void testPolicyExt() throws Exception {
        final int corruptSessionId = createCorruptSession();

        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";

        apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        apexRequest(post(BASE_URL + "/Policy/Create", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : \"HelloAnother\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : \"Hello10\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"IDontExist\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"             : \"Hello11\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : null," + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\""
            + "   }" + "  }" + " }" + "}," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello12\"," + "\"version\"              : \"0.0.2\","
            + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
            + "\"states\"               : {" + " \"state\"               : {" + "  \"name\"               : \"state\","
            + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
            + "lots of lime\"}," + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
            + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
            + "   \"tr0\"               : {"
            + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }" + " }"
            + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"          : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello13\"," + "\"version\"              : \"0.0.2\","
            + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
            + "\"states\"               : {" + " \"state\"               : {" + "  \"name\"               : \"state\","
            + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : null},"
            + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
            + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
            + "   \"tr0\"               : {"
            + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }" + " }"
            + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"          : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello14\"," + "\"version\"              : \"0.0.2\","
            + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
            + "\"states\"               : {" + " \"state\"               : {" + "  \"name\"               : \"state\","
            + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
            + "lots of lime\"},"
            + "  \"contexts\"           : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
            + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
            + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
            + "   \"tr0\"               : {"
            + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }" + " }"
            + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"          : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello15\"," + "\"version\"              : \"0.0.2\","
            + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
            + "\"states\"               : {" + " \"state\"               : {" + "  \"name\"               : \"state\","
            + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
            + "lots of lime\"}," + "  \"contexts\"           : [{\"name\" : \"IDontExist\", \"version\" : \"0.0.1\"}],"
            + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
            + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
            + "   \"tr0\"               : {"
            + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }" + " }"
            + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"          : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello16\"," + "\"version\"              : \"0.0.2\","
            + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
            + "\"states\"               : {" + " \"state\"               : {" + "  \"name\"               : \"state\","
            + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
            + "lots of lime\"}," + "  \"contexts\"           : [null]," + "  \"stateOutputs\"       : {"
            + "   \"so0\"               : {"
            + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
            + "   \"tr0\"               : {"
            + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  }" + " }"
            + "}," + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"          : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello17\"," + "\"version\"              : \"0.0.2\","
            + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
            + "\"states\"               : {" + " \"state\"               : {" + "  \"name\"               : \"state\","
            + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
            + "lots of lime\"},"
            + "  \"contexts\"           : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
            + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
            + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
            + "   \"tr0\"               : {"
            + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  },"
            + "  \"finalizers\"         : {"
            + "   \"sf0\"               : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
            + "lots of lime\"}" + "  }" + " }" + "},"
            + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"          : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello18\"," + "\"version\"              : \"0.0.2\","
            + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
            + "\"states\"               : {" + " \"state\"               : {" + "  \"name\"               : \"state\","
            + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
            + "lots of lime\"},"
            + "  \"contexts\"           : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
            + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
            + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
            + "   \"tr0\"               : {"
            + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  },"
            + "  \"finalizers\"         : {" + "   \"sf0\"               : null" + "  }" + " }" + "},"
            + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"          : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"                 : \"Hello19\"," + "\"version\"              : \"0.0.2\","
            + "\"template\"             : \"somewhere.over.the.rainbow\"," + "\"firstState\"           : \"state\","
            + "\"states\"               : {" + " \"state\"               : {" + "  \"name\"               : \"state\","
            + "  \"trigger\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"        : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "  \"taskSelectionLogic\" : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : \"lots of lemons, "
            + "lots of lime\"},"
            + "  \"contexts\"           : [{\"name\" : \"contextAlbum0\", \"version\" : \"0.0.1\"}],"
            + "  \"stateOutputs\"       : {" + "   \"so0\"               : {"
            + "    \"event\"            : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"        : null" + "   }" + "  }," + "  \"tasks\"              : {"
            + "   \"tr0\"               : {"
            + "    \"task\"             : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"       : \"DIRECT\"," + "    \"outputName\"       : \"so0\"" + "   }" + "  },"
            + "  \"finalizers\"         : {"
            + "   \"sf0\"               : {\"logicFlavour\" : \"LemonAndLime\", \"logic\" : null}" + "  }" + " }" + "},"
            + "\"uuid\"                 : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"          : \"A description of hello\"" + "}";
        result = apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"HelloAnother\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A better description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/Policy/Update", -12345)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Policy/Update", 1234545)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(put(BASE_URL + "/Policy/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        result = apexRequest(put(BASE_URL + "/Policy/Update", sessionId)
            .queryParam("firstStatePeriodic", "true")
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());

        apexRequest(put(BASE_URL + "/Policy/Update", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : null," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A better description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/Policy/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        entityString = "{" + "\"name\"             : \"IDontExist\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A better description of hello\"" + "}";
        result = apexRequest(put(BASE_URL + "/Policy/Update", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());

        result = apexRequest(get(BASE_URL + "/Policy/Get", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Policy/Get", sessionId)
            .queryParam("name", "IDontExist")
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, result.getResult());
        result = apexRequest(get(BASE_URL + "/Policy/Get", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Policy/Get", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Policy/Get", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexRequest(get(BASE_URL + "/Policy/Get", corruptSessionId)
            .queryParam("name", "Hello")
            .queryParam("version", (String) null));

        result = apexRequest(get(BASE_URL + "/Validate/Event", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(get(BASE_URL + "/Validate/Event", -12345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(get(BASE_URL + "/Validate/Event", 12345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

        apexRequest(delete(BASE_URL + "/Policy/Delete", corruptSessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));

        result = apexRequest(delete(BASE_URL + "/Policy/Delete", -123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());

    }

    @Test
    void testPolicyExt_2() throws Exception {
        final int corruptSessionId = createCorruptSession();

        ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        final int sessionId = Integer.parseInt(result.getMessages().get(0));

        apexRequest(get(BASE_URL + "/Model/Validate", corruptSessionId));
        apexRequest(get(BASE_URL + "/Model/Validate", sessionId)
            .queryParam("name", "%%%$£")
            .queryParam("version", (String) null));

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        apexRequest(put(BASE_URL + "/Model/Load", sessionId)
            .content(modelString).contentType(APPLICATION_JSON));

        String entityString = "{" + "\"name\"             : \"Hello\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : {" + " \"state\"           : {" + "  \"name\"           : \"state\","
            + "  \"trigger\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "  \"defaultTask\"    : {\"name\" : \"task\", \"version\" : \"0.0.1\"}," + "  \"stateOutputs\"   : {"
            + "   \"so0\"           : {" + "    \"event\"        : {\"name\" : \"inEvent\", \"version\" : \"0.0.1\"},"
            + "    \"nextState\"    : null" + "   }" + "  }," + "  \"tasks\"          : {" + "   \"tr0\"           : {"
            + "    \"task\"         : {\"name\" : \"task\", \"version\" : \"0.0.1\"},"
            + "    \"outputType\"   : \"DIRECT\"," + "    \"outputName\"   : \"so0\"" + "   }" + "  }" + " }" + "},"
            + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));
        apexRequest(post(BASE_URL + "/Policy/Create", corruptSessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        entityString = "{" + "\"name\"             : \"GoodTaSeeYa\"," + "\"version\"          : \"0.0.2\","
            + "\"template\"         : \"somewhere.over.the.rainbow\"," + "\"firstState\"       : \"state\","
            + "\"states\"           : null," + "\"uuid\"             : \"1fa2e430-f2b2-11e6-bc64-92361f002671\","
            + "\"description\"      : \"A description of hello\"" + "}";
        apexRequest(post(BASE_URL + "/Policy/Create", sessionId)
            .content(entityString).contentType(APPLICATION_JSON));

        result = apexRequest(delete(BASE_URL + "/Policy/Delete", 123345)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.FAILED, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Policy/Delete", sessionId)
            .queryParam("name", "Hello")
            .queryParam("version", "0.0.2"));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
        result = apexRequest(delete(BASE_URL + "/Policy/Delete", sessionId)
            .queryParam("name", (String) null)
            .queryParam("version", (String) null));
        assertEquals(ApexApiResult.Result.SUCCESS, result.getResult());
    }

    /*
     * Make a request using MockMvc and return the response body as a string.
     */
    private String requestString(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return mvc.perform(requestBuilder).andReturn().getResponse().getContentAsString();
    }

    /*
     * Make a request using MockMvc and return the decoded JSON response as an ApexApiResult.
     */
    private ApexApiResult apexRequest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        String json = requestString(requestBuilder);
        return new StandardCoder().decode(json, ApexApiResult.class);
    }

    /*
     * This method is used only for testing and is used to cause an exception on calls from unit test to test exception
     * handling.
     */
    private int createCorruptSession() throws Exception {
        final ApexApiResult result = apexRequest(get(BASE_URL + "/Session/Create", -1));
        final int corruptSessionId = Integer.parseInt(result.getMessages().get(0));
        sessionHandler.setCorruptSession(corruptSessionId);
        return corruptSessionId;
    }
}
