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

package org.onap.policy.gui.editors.apex.rest.handling;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import javax.ws.rs.core.Response;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.gui.editors.apex.rest.UploadPluginConfigParameters;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ApexConfigProcessor;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ProcessedTemplate;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.exception.PolicyToscaConverterException;
import org.onap.policy.gui.editors.apex.rest.handling.plugin.upload.UploadPluginClient;
import org.onap.policy.gui.editors.apex.rest.handling.plugin.upload.UploadPolicyRequestDto;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Handles the Policy Model upload.
 */
public class PolicyUploadHandler {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(PolicyUploadHandler.class);
    private final PolicyToscaConverter policyToscaConverter;
    private final ToscaTemplateProcessor toscaTemplateProcessor;
    private final ApexConfigProcessor apexConfigProcessor;
    private final UploadPluginClient uploadPluginClient;
    private final UploadPluginConfigParameters uploadPluginConfigParameters;

    /**
     * Creates the upload handler with its necessary dependencies.
     *
     * @param uploadPluginClient the UploadPluginClient instance
     * @param policyToscaConverter the PolicyToscaConverter instance
     * @param toscaTemplateProcessor the ToscaTemplateProcessor instance
     * @param apexConfigProcessor the ApexConfigProcessor instance
     * @param uploadPluginConfigParameters the Config instance
     */
    public PolicyUploadHandler(final UploadPluginClient uploadPluginClient,
                               final PolicyToscaConverter policyToscaConverter,
                               final ToscaTemplateProcessor toscaTemplateProcessor,
                               final ApexConfigProcessor apexConfigProcessor,
                               final UploadPluginConfigParameters uploadPluginConfigParameters) {
        this.uploadPluginClient = uploadPluginClient;
        this.policyToscaConverter = policyToscaConverter;
        this.toscaTemplateProcessor = toscaTemplateProcessor;
        this.apexConfigProcessor = apexConfigProcessor;
        this.uploadPluginConfigParameters = uploadPluginConfigParameters;
    }

    /**
     * Handles the policy model upload converting it to TOSCA with given template files.
     *
     * @param apexModel the apex model that contains the policy model
     * @param toscaTemplateInputStream the tosca template input stream
     * @param apexConfigInputStream the apex config input stream
     * @return the result of the upload process
     */
    public ApexApiResult doUpload(final ApexModel apexModel, final InputStream toscaTemplateInputStream,
                                  final InputStream apexConfigInputStream) {
        final ProcessedTemplate processedToscaTemplate;
        try {
            processedToscaTemplate = toscaTemplateProcessor.process(toscaTemplateInputStream);
        } catch (IOException e) {
            final ApexApiResult result = new ApexApiResult(Result.FAILED);
            result.addThrowable(e);
            final String errorMsg = "Could not process the tosca template file";
            result.addMessage(errorMsg);
            LOGGER.error(errorMsg, e);
            return result;
        }
        if (!processedToscaTemplate.isValid()) {
            return buildResponse(processedToscaTemplate);
        }

        final ProcessedTemplate processedApexConfig;
        try {
            processedApexConfig = apexConfigProcessor.process(apexConfigInputStream);
        } catch (final IOException e) {
            final ApexApiResult result = new ApexApiResult(Result.FAILED);
            result.addThrowable(e);
            final String errorMsg = "Could not process the apex config file";
            result.addMessage(errorMsg);
            LOGGER.error(errorMsg, e);
            return result;
        }
        if (!processedApexConfig.isValid()) {
            return buildResponse(processedApexConfig);
        }
        return doUpload(apexModel, processedToscaTemplate.getContent(), processedApexConfig.getContent());
    }

    private ApexApiResult doUpload(final ApexModel apexModel, final String toscaTemplate, final String apexConfig) {
        LOGGER.entry();
        if (!isUploadPluginEnabled()) {
            final ApexApiResult apexApiResult = new ApexApiResult(Result.FAILED);
            apexApiResult.addMessage("Upload feature is disabled");
            return apexApiResult;
        }
        final AxPolicyModel policyModel = apexModel.getPolicyModel();
        final ApexApiResult result = apexModel.listModel();
        final UploadPolicyRequestDto uploadPolicyRequestDto = new UploadPolicyRequestDto();
        final AxArtifactKey policyKey = policyModel.getKeyInformation().getKey();
        final java.util.UUID uuid = policyModel.getKeyInformation().get(policyKey).getUuid();
        uploadPolicyRequestDto
            .setFilename(String.format("%s.%s.%s", uuid, policyKey.getName(), policyKey.getVersion()));
        final String apexPolicy = convert(result.getMessage(), toscaTemplate, apexConfig).orElse(null);
        if (apexPolicy == null) {
            final ApexApiResult apexApiResult = new ApexApiResult(Result.FAILED);
            apexApiResult.addMessage(
                String.format("An error has occurred while uploading the converting the Policy '%s' to YAML.",
                    policyModel.getId()));
            LOGGER.exit("Model/Upload: NOT OK");
            return apexApiResult;
        }
        uploadPolicyRequestDto.setFileData(
            Base64.getEncoder().encodeToString(apexPolicy.getBytes(StandardCharsets.UTF_8)));
        final Response response = uploadPluginClient.upload(uploadPolicyRequestDto);
        if (response.getStatus() == 201) {
            final ApexApiResult apexApiResult = new ApexApiResult(Result.SUCCESS);
            apexApiResult.addMessage(String.format("Policy '%s' uploaded successfully", policyModel.getId()));
            LOGGER.exit("Model/Upload: OK");
            return apexApiResult;
        } else {
            final ApexApiResult apexApiResult = new ApexApiResult(Result.FAILED);
            apexApiResult.addMessage(
                String.format("An error has occurred while uploading the Policy '%s'. Status was %s",
                    policyModel.getId(), response.getStatus()));
            LOGGER.exit("Model/Upload: NOT OK");
            return apexApiResult;
        }
    }

    private ApexApiResult buildResponse(final ProcessedTemplate processedTemplate) {
        final ApexApiResult result = new ApexApiResult(Result.SUCCESS);
        if (!processedTemplate.isValid()) {
            result.setResult(Result.OTHER_ERROR);
            processedTemplate.getErrorSet().forEach(result::addMessage);
        }
        return result;
    }

    private boolean isUploadPluginEnabled() {
        return uploadPluginConfigParameters.isEnabled();
    }

    private Optional<String> convert(final String apexPolicy, final String toscaTemplate, final String apexConfig) {
        try {
            return policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate);
        } catch (final PolicyToscaConverterException e) {
            LOGGER.error("Could not convert policy to TOSCA", e);
        }

        return Optional.empty();
    }
}
