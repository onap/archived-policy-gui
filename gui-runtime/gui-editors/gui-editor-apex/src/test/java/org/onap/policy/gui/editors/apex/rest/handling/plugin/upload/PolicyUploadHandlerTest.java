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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class PolicyUploadHandlerTest {

    private static final String DEFAULT_UPLOAD_USER_ID = "MyUser";
    private static final String UPLOAD_URL = "http://127.0.0.1";

    @Mock
    private RestTemplate policyUploadRestTemplate;

    @InjectMocks
    private PolicyUploadHandler uploadHandler;

    @Captor
    ArgumentCaptor<HttpEntity<UploadPolicyRequestDto>> dtoEntityCaptor;

    private AxArtifactKey axArtifactKey;
    private String toscaServiceTemplate;

    /**
     * Prepares test environment.
     *
     * @throws IOException where there is problem with reading the file.
     */
    @BeforeEach
    void setUp() throws IOException {
        final var name = "a" + RandomStringUtils.randomAlphabetic(5);
        final var version = "0.0.1";
        axArtifactKey = new AxArtifactKey(name, version);
        final var path = Path.of("src/test/resources/models/", "PolicyModel.json");
        toscaServiceTemplate = Files.readString(path);
        ReflectionTestUtils.setField(uploadHandler, "uploadUrl", UPLOAD_URL);
        ReflectionTestUtils.setField(uploadHandler, "defaultUserId", DEFAULT_UPLOAD_USER_ID);
    }

    @Test
    void testDoUploadNoUrl() {
        ReflectionTestUtils.setField(uploadHandler, "uploadUrl", null);
        final var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");
        assertThat(result.isNok()).isTrue();
        assertThat(result.getMessage()).contains("Model upload is disabled");
    }

    @Test
    void testDoUploadConnectionError() {
        when(policyUploadRestTemplate.postForObject(eq(UPLOAD_URL), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("connection error"));

        final var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");

        assertThat(result.isNok()).isTrue();
        assertThat(result.getMessage()).contains("failed with error");
    }

    @Test
    void testDoResponse() {
        final var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");

        assertThat(result.isOk()).isTrue();
    }

    @Test
    void testDoResponseErrorCode500() {
        when(policyUploadRestTemplate.postForObject(eq(UPLOAD_URL), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        final var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");

        assertThat(result.isNok()).isTrue();
        assertThat(result.getMessage()).contains("failed with status 500");
    }

    @Test
    void testDoUploadUserId() {
        // If uploadUserId is specified, that value should be in DTO.
        var result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "",
            "OverrideUser");
        assertThat(result.isOk()).isTrue();
        Mockito.verify(policyUploadRestTemplate)
            .postForObject(eq(UPLOAD_URL), dtoEntityCaptor.capture(), eq(String.class));
        var dto = dtoEntityCaptor.getValue().getBody();
        assertThat(dto.getUserId()).isEqualTo("OverrideUser");
        Mockito.reset(policyUploadRestTemplate);

        // If uploadUserId is blank, the value from Spring config parameter 'apex-editor.upload-userid' is used.
        result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", "");
        assertThat(result.isOk()).isTrue();
        Mockito.verify(policyUploadRestTemplate)
            .postForObject(eq(UPLOAD_URL), dtoEntityCaptor.capture(), eq(String.class));
        dto = dtoEntityCaptor.getValue().getBody();
        assertThat(dto.getUserId()).isEqualTo(DEFAULT_UPLOAD_USER_ID);
        Mockito.reset(policyUploadRestTemplate);

        // If uploadUserId is null, the value from Spring config parameter 'apex-editor.upload-userid' is used.
        result = uploadHandler.doUpload(toscaServiceTemplate, axArtifactKey, "", null);
        assertThat(result.isOk()).isTrue();
        Mockito.verify(policyUploadRestTemplate)
            .postForObject(eq(UPLOAD_URL), dtoEntityCaptor.capture(), eq(String.class));
        dto = dtoEntityCaptor.getValue().getBody();
        assertThat(dto.getUserId()).isEqualTo(DEFAULT_UPLOAD_USER_ID);
    }
}
