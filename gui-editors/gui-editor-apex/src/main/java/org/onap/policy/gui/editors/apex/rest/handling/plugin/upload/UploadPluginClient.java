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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.gui.editors.apex.rest.UploadPluginConfigParameters;

/**
 * Client for the Policy Model upload endpoint.
 */
public class UploadPluginClient {

    private final Client client;
    private final UploadPluginConfigParameters uploadConfigParam;

    /**
     * Create a upload plugin client.
     */
    public UploadPluginClient() {
        this(ClientBuilder.newClient(), ParameterService.get(UploadPluginConfigParameters.GROUP_NAME));
    }

    /**
     * Create a upload plugin client.
     * @param client the http client
     * @param uploadConfigParam the upload configuration parameters
     */
    UploadPluginClient(final Client client,
                       final UploadPluginConfigParameters uploadConfigParam) {
        this.client = client;
        this.uploadConfigParam = uploadConfigParam;
    }

    /**
     * Uploads the policy to the configured endpoint.
     *
     * @param uploadPolicyRequestDto the policy DTO to upload
     * @return the request response
     */
    public Response upload(final UploadPolicyRequestDto uploadPolicyRequestDto) {
        return client
            .target(uploadConfigParam.getUrl())
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(uploadPolicyRequestDto, MediaType.APPLICATION_JSON));
    }

}
