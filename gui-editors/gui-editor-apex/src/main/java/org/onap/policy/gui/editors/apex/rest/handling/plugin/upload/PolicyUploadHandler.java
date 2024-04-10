/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2022, 2024 Nordix Foundation
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
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * Handles the Policy Model upload.
 */
@Service
public class PolicyUploadHandler {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(PolicyUploadHandler.class);

    @Value("${apex-editor.upload-url:}")
    private String uploadUrl;

    @Value("${apex-editor.upload-userid:}")
    private String defaultUserId;

    private final RestTemplate policyUploadRestTemplate;

    // Recurring string constants
    private static final String MODEL_UPLOAD_OK = "Model/Upload: OK";
    private static final String MODEL_UPLOAD_NOT_OK = "Model/Upload: NOT OK";

    @Autowired
    public PolicyUploadHandler(RestTemplate policyUploadRestTemplate) {
        this.policyUploadRestTemplate = policyUploadRestTemplate;
    }

    /**
     * Handles the policy model upload converting it to TOSCA with given template files.
     *
     * @param toscaServiceTemplate the TOSCA service template
     * @param policyModelKey       the key of the policy model
     * @param policyModelUuid      the UUID of the policy model
     * @param uploadUserId         the userId to use for upload. If blank, the Spring
     *                             config parameter "apex-editor.upload-userid" is used.
     * @return the result of the upload process
     */
    public ApexApiResult doUpload(final String toscaServiceTemplate, final AxArtifactKey policyModelKey,
                                  final String policyModelUuid, String uploadUserId) {
        LOGGER.entry();

        if (StringUtils.isBlank(uploadUrl)) {
            final var apexApiResult = new ApexApiResult(Result.FAILED);
            apexApiResult.addMessage("Model upload is disabled, parameter upload-url is not set on server");
            LOGGER.exit(MODEL_UPLOAD_NOT_OK);
            return apexApiResult;
        }

        if (StringUtils.isBlank(uploadUserId)) {
            uploadUserId = defaultUserId;
        }

        final var uploadPolicyRequestDto = new UploadPolicyRequestDto();
        uploadPolicyRequestDto.setUserId(uploadUserId);
        uploadPolicyRequestDto
            .setFileData(Base64.getEncoder().encodeToString(toscaServiceTemplate.getBytes(StandardCharsets.UTF_8)));
        uploadPolicyRequestDto.setFilename(
            String.format("%s.%s.%s", policyModelUuid, policyModelKey.getName(), policyModelKey.getVersion()));

        try {
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            var request = new HttpEntity<>(uploadPolicyRequestDto, headers);
            policyUploadRestTemplate.postForObject(uploadUrl, request, String.class);

            final var apexApiResult = new ApexApiResult(Result.SUCCESS);
            apexApiResult.addMessage(
                String.format("uploading Policy '%s' to URL '%s' with userId '%s' was successful",
                    policyModelKey.getId(), uploadUrl, uploadUserId));
            LOGGER.exit(MODEL_UPLOAD_OK);
            return apexApiResult;

        } catch (HttpStatusCodeException e) {
            final var apexApiResult = new ApexApiResult(Result.FAILED);
            apexApiResult.addMessage(
                String.format("uploading Policy '%s' to URL '%s' with userId '%s' failed with status %d",
                    policyModelKey.getId(), uploadUrl, uploadUserId, e.getStatusCode().value()));
            LOGGER.exit(MODEL_UPLOAD_NOT_OK);
            return apexApiResult;

        } catch (Exception e) {
            final var apexApiResult = new ApexApiResult(Result.FAILED);
            apexApiResult.addMessage(
                String.format("uploading Policy '%s' to URL '%s' with userId '%s' failed with error %s",
                    policyModelKey.getId(), uploadUrl, uploadUserId, e.getMessage()));
            LOGGER.exit(MODEL_UPLOAD_NOT_OK);
            return apexApiResult;
        }
    }
}
