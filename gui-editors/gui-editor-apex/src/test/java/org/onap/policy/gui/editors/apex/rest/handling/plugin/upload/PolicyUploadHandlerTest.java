/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021-2022 Nordix Foundation.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.gui.editors.apex.rest.ApexEditorMain;

public class PolicyUploadHandlerTest {

    private static final String CMDLINE_UPLOAD_USERID = "MyUser";
    private PolicyUploadHandler uploadHandler;
    private AxArtifactKey axArtifactKey;
    private String toscaServiceTemplate;
    private MockedStatic<ClientBuilder> clientBuilderMockedStatic;
    private ArgumentCaptor<Entity<UploadPolicyRequestDto>> dtoEntityCaptor;

    /**
     * Prepares test environment.
     *
     * @throws IOException where there is problem with reading the file.
     */
    @Before
    public void setUp() throws IOException {
        uploadHandler = new PolicyUploadHandler();
        final var name = "a" + RandomStringUtils.randomAlphabetic(5);
        final var version = "0.0.1";
        axArtifactKey = new AxArtifactKey(name, version);
        final var path = Path.of("src/test/resources/models/", "PolicyModel.json");
        toscaServiceTemplate = Files.readString(path);
    }

    /**
     * Cleaning up after the test.
     */
    @After
    public void tearDown() {
        if (clientBuilderMockedStatic != null) {
            clientBuilderMockedStatic.close();
        }
    }

    @Test
    public void testDoUploadNoUrl() {
        final String[] args = {"--upload-userid", CMDLINE_UPLOAD_USERID};
        final var outBaStream = new ByteArrayOutputStream();
        final var outStream = new PrintStream(outBaStream);
        new ApexEditorMain(args, outStream);

        final var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");
        assertThat(result.isNok()).isTrue();
        assertThat(result.getMessage()).contains("Model upload is disable");
    }

    @Test
    public void testDoUploadConnectionError() {
        final var response = Mockito.mock(Response.class);
        mockRsHttpClient(response);
        Mockito.doThrow(ResponseProcessingException.class).when(response).getStatus();

        prepareApexEditorMain();

        final var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");

        assertThat(result.isNok()).isTrue();
        assertThat(result.getMessage()).contains("failed with error");
    }

    @Test
    public void testDoResponse() {
        final var response = Mockito.mock(Response.class);
        mockRsHttpClient(response);

        Mockito.doReturn(201).when(response).getStatus();

        prepareApexEditorMain();

        final var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");

        assertThat(result.isOk()).isTrue();
    }

    @Test
    public void testDoResponseErrorCode500() {
        final var response = Mockito.mock(Response.class);
        mockRsHttpClient(response);

        Mockito.doReturn(500).when(response).getStatus();

        prepareApexEditorMain();

        final var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");

        assertThat(result.isNok()).isTrue();
        assertThat(result.getMessage()).contains("failed with status 500");
    }

    @Test
    public void testDoUploadUserId() {
        final var response = Mockito.mock(Response.class);
        mockRsHttpClient(response);

        Mockito.doReturn(201).when(response).getStatus();

        prepareApexEditorMain();

        // If uploadUserId is specified, that value should be in DTO.
        var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "",
            "OverrideUser");
        assertThat(result.isOk()).isTrue();
        var dto = dtoEntityCaptor.getValue().getEntity();
        assertThat(dto.getUserId()).isEqualTo("OverrideUser");

        // If uploadUserId is blank, the value from command line parameter 'upload-userid' is used.
        result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");
        assertThat(result.isOk()).isTrue();
        dto = dtoEntityCaptor.getValue().getEntity();
        assertThat(dto.getUserId()).isEqualTo(CMDLINE_UPLOAD_USERID);

        // If uploadUserId is null, the value from command line parameter 'upload-userid' is used.
        result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", null);
        assertThat(result.isOk()).isTrue();
        dto = dtoEntityCaptor.getValue().getEntity();
        assertThat(dto.getUserId()).isEqualTo(CMDLINE_UPLOAD_USERID);
    }

    private void mockRsHttpClient(Response response) {
        final var webTarget = Mockito.mock(WebTarget.class);
        final var client = Mockito.mock(Client.class);
        final var invocationBuilder = Mockito.mock(Invocation.Builder.class);


        clientBuilderMockedStatic = Mockito.mockStatic(ClientBuilder.class);

        dtoEntityCaptor = ArgumentCaptor.forClass(Entity.class);

        Mockito.when(ClientBuilder.newClient()).thenReturn(client);
        Mockito.when(client.target(ArgumentMatchers.anyString())).thenReturn(webTarget);
        Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(invocationBuilder);
        Mockito.when(invocationBuilder.post(dtoEntityCaptor.capture())).thenReturn(response);
    }

    private void prepareApexEditorMain() {
        final String[] args = {"--upload-userid", CMDLINE_UPLOAD_USERID, "--upload-url", "http://127.0.0.1"};
        final var outBaStream = new ByteArrayOutputStream();
        final var outStream = new PrintStream(outBaStream);
        new ApexEditorMain(args, outStream);
    }
}
