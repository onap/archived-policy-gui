/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.gui.editors.apex.rest.handling.plugin.upload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.gui.editors.apex.rest.handling.config.PolicyUploadPluginConfigKey;

@ExtendWith(MockitoExtension.class)
class UploadPluginClientTest {

    private UploadPluginClient uploadPluginClient;

    @Mock
    private Client client;
    @Mock
    private Config config;

    private static final String url = "aUrl";

    @BeforeEach
    void setUp() {
        when(config.getOptionalValue(PolicyUploadPluginConfigKey.URL.getKey(), String.class))
            .thenReturn(Optional.of(url));
        uploadPluginClient = new UploadPluginClient(client, config);
        verify(config).getOptionalValue(PolicyUploadPluginConfigKey.URL.getKey(), String.class);

    }

    @Test
    void upload() {
        final Builder mockBuilder = mock(Builder.class);
        final WebTarget mockWebTarget = mock(WebTarget.class);
        final Response mockResponse = mock(Response.class);
        doReturn(mockWebTarget).when(client).target(url);
        doReturn(mockBuilder).when(mockWebTarget).request(MediaType.APPLICATION_JSON);
        when(mockBuilder.post(any())).thenReturn(mockResponse);
        final Response actualResponse = uploadPluginClient.upload(new UploadPolicyRequestDto());
        assertEquals(mockResponse, actualResponse);
        verify(config).getOptionalValue(PolicyUploadPluginConfigKey.URL.getKey(), String.class);
    }
}