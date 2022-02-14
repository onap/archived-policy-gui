/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2022 Nordix Foundation
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.gui.editors.apex.rest.ApexEditorMain;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Handles the Policy Model upload.
 */
public class PolicyUploadHandler {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(PolicyUploadHandler.class);

    // Recurring string constants
    private static final String MODEL_UPLOAD_NOT_OK = "Model/Upload: NOT OK";

    /**
     * Handles the policy model upload converting it to TOSCA with given template files.
     *
     * @param toscaServiceTemplate the TOSCA service template
     * @param policyModelKey       the key of the policy model
     * @param policyModelUuid      the UUID of the policy model
     * @param uploadUserId         the userId to use for upload. If blank, the commandline
     *                             parameter "upload-userid" is used.
     * @return the result of the upload process
     */
    public ApexApiResult doUpload(final String toscaServiceTemplate, final AxArtifactKey policyModelKey,
        final String policyModelUuid, String uploadUserId) {
        LOGGER.entry();

        final String uploadUrl = ApexEditorMain.getParameters().getUploadUrl();
        if (StringUtils.isBlank(uploadUrl)) {
            final var apexApiResult = new ApexApiResult(Result.FAILED);
            apexApiResult.addMessage("Model upload is disabled, parameter upload-url is not set on server");
            LOGGER.exit(MODEL_UPLOAD_NOT_OK);
            return apexApiResult;
        }

        if (StringUtils.isBlank(uploadUserId)) {
            uploadUserId = ApexEditorMain.getParameters().getUploadUserid();
        }

        final var uploadPolicyRequestDto = new UploadPolicyRequestDto();
        uploadPolicyRequestDto.setUserId(uploadUserId);
        uploadPolicyRequestDto
            .setFileData(Base64.getEncoder().encodeToString(toscaServiceTemplate.getBytes(StandardCharsets.UTF_8)));
        uploadPolicyRequestDto.setFilename(
            String.format("%s.%s.%s", policyModelUuid, policyModelKey.getName(), policyModelKey.getVersion()));

        try {
            final var response = ClientBuilder.newClient().target(uploadUrl)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(uploadPolicyRequestDto, MediaType.APPLICATION_JSON));

            if (response.getStatus() == 201) {
                final var apexApiResult = new ApexApiResult(Result.SUCCESS);
                apexApiResult.addMessage(
                    String.format("uploading Policy '%s' to URL '%s' with userId '%s' was successful",
                        policyModelKey.getId(), uploadUrl, uploadUserId));
                LOGGER.exit("Model/Upload: OK");
                return apexApiResult;
            } else {
                final var apexApiResult = new ApexApiResult(Result.FAILED);
                apexApiResult.addMessage(
                    String.format("uploading Policy '%s' to URL '%s' with userId '%s' failed with status %s",
                        policyModelKey.getId(), uploadUrl, uploadUserId, response.getStatus()));
                LOGGER.exit(MODEL_UPLOAD_NOT_OK);
                return apexApiResult;
            }
        } catch (Exception e) {
            final var apexApiResult = new ApexApiResult(Result.FAILED);
            apexApiResult
                .addMessage(String.format("uploading Policy '%s' to URL '%s' with userId '%s' failed with error %s",
                    policyModelKey.getId(), uploadUrl, uploadUserId, e.getMessage()));
            LOGGER.exit(MODEL_UPLOAD_NOT_OK);
            return apexApiResult;
        }
    }
}
