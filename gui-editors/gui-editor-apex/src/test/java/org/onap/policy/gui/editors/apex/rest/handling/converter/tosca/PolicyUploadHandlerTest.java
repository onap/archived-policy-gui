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

package org.onap.policy.gui.editors.apex.rest.handling.converter.tosca;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.gui.editors.apex.rest.handling.PolicyUploadHandler;
import org.onap.policy.gui.editors.apex.rest.handling.config.PolicyUploadPluginConfigKey;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.exception.PolicyToscaConverterException;
import org.onap.policy.gui.editors.apex.rest.handling.plugin.upload.UploadPluginClient;
import org.onap.policy.gui.editors.apex.rest.handling.plugin.upload.UploadPolicyRequestDto;

class PolicyUploadHandlerTest {

    private static final String USER_ID = "cs0008";
    @Mock
    private PolicyToscaConverter policyToscaConverter;
    @Mock
    private ToscaTemplateProcessor toscaTemplateProcessor;
    @Mock
    private ApexConfigProcessor apexConfigProcessor;
    @Mock
    private UploadPluginClient uploadPluginClient;
    @Mock
    private Config config;

    @InjectMocks
    private PolicyUploadHandler policyUploadHandler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        when(config.getOptionalValue(eq(PolicyUploadPluginConfigKey.ENABLE.getKey()), eq(Boolean.class)))
            .thenReturn(Optional.of(true));
    }

    @Test
    void doUploadResponseSuccessAndFail() throws PolicyToscaConverterException {
        final ApexModel apexModel = mockApexModel();
        final ProcessedTemplate processedToscaTemplate = new ProcessedTemplate();
        processedToscaTemplate.setContent("tosca");
        final ProcessedTemplate processedApexConfig = new ProcessedTemplate();
        processedApexConfig.setContent("apexConfig");
        when(toscaTemplateProcessor.process(any(InputStream.class))).thenReturn(processedToscaTemplate);
        when(apexConfigProcessor.process(any(InputStream.class))).thenReturn(processedApexConfig);
        when(policyToscaConverter.convert(eq("policy\n"), eq("apexConfig"), eq("tosca")))
            .thenReturn(Optional.of("test"));
        when(uploadPluginClient.upload(any(UploadPolicyRequestDto.class)))
            .thenReturn(Response.ok().status(201).build());

        ApexApiResult apexApiResult = policyUploadHandler
            .doUpload(apexModel, mock(InputStream.class), mock(InputStream.class), USER_ID);

        assertThat("Response should be ok", apexApiResult.isOk(), is(true));
        String expectedSuccessMsg =
            String.format("Policy '%s' uploaded successfully", apexModel.getPolicyModel().getId());
        assertThat("Response message should be as expected",
            apexApiResult.getMessage(), is(expectedSuccessMsg + "\n"));

        when(uploadPluginClient.upload(any(UploadPolicyRequestDto.class)))
            .thenReturn(Response.serverError().build());

        apexApiResult = policyUploadHandler
            .doUpload(apexModel, mock(InputStream.class), mock(InputStream.class), USER_ID);

        assertThat("Response should not be ok", apexApiResult.isNok(), is(true));
        expectedSuccessMsg =
            String.format("An error has occurred while uploading the Policy '%s'. Status was %s",
                apexModel.getPolicyModel().getId(), 500);
        assertThat("Response message should be as expected",
            apexApiResult.getMessage(), is(expectedSuccessMsg + "\n"));
    }

    @Test
    void doUploadPluginDisabled() {
        when(config.getOptionalValue(eq(PolicyUploadPluginConfigKey.ENABLE.getKey()), eq(Boolean.class)))
            .thenReturn(Optional.of(false));

        final ProcessedTemplate processedToscaTemplate = new ProcessedTemplate();
        final ProcessedTemplate processedApexConfig = new ProcessedTemplate();
        when(toscaTemplateProcessor.process(any(InputStream.class))).thenReturn(processedToscaTemplate);
        when(apexConfigProcessor.process(any(InputStream.class))).thenReturn(processedApexConfig);
        final ApexApiResult apexApiResult = policyUploadHandler
            .doUpload(mock(ApexModel.class), mock(InputStream.class), mock(InputStream.class), USER_ID);

        assertThat("Response should not be ok", apexApiResult.isNok(), is(true));
        assertThat("Response message should be as expected",
            apexApiResult.getMessage(), is("Upload feature is disabled\n"));
    }

    @Test
    void doUploadInvalidToscaTemplate() {
        when(config.getOptionalValue(eq(PolicyUploadPluginConfigKey.ENABLE.getKey()), eq(Boolean.class)))
            .thenReturn(Optional.of(false));

        final ProcessedTemplate processedToscaTemplate = new ProcessedTemplate();
        final String errorMsg = "an error";
        processedToscaTemplate.addToErrors(Collections.singleton(errorMsg));
        when(toscaTemplateProcessor.process(any(InputStream.class))).thenReturn(processedToscaTemplate);
        final ApexApiResult apexApiResult = policyUploadHandler
            .doUpload(mock(ApexModel.class), mock(InputStream.class), mock(InputStream.class), USER_ID);

        assertThat("Response should not be ok", apexApiResult.isNok(), is(true));
        assertThat("Response message should be as expected",
            apexApiResult.getMessage(), is(errorMsg + "\n"));
    }

    @Test
    void doUploadInvalidApexConfigTemplate() {
        when(config.getOptionalValue(eq(PolicyUploadPluginConfigKey.ENABLE.getKey()), eq(Boolean.class)))
            .thenReturn(Optional.of(false));

        when(toscaTemplateProcessor.process(any(InputStream.class))).thenReturn(new ProcessedTemplate());
        final ProcessedTemplate processedApexConfig = new ProcessedTemplate();
        final String errorMsg = "an error";
        processedApexConfig.addToErrors(Collections.singleton(errorMsg));
        when(apexConfigProcessor.process(any(InputStream.class))).thenReturn(processedApexConfig);
        final ApexApiResult apexApiResult = policyUploadHandler
            .doUpload(mock(ApexModel.class), mock(InputStream.class), mock(InputStream.class), USER_ID);

        assertThat("Response should not be ok", apexApiResult.isNok(), is(true));
        assertThat("Response message should be as expected",
            apexApiResult.getMessage(), is(errorMsg + "\n"));
    }

    @Test
    void doUploadConversionFailed() throws PolicyToscaConverterException {
        final ApexModel apexModel = mockApexModel();
        final ProcessedTemplate processedToscaTemplate = new ProcessedTemplate();
        processedToscaTemplate.setContent("tosca");
        final ProcessedTemplate processedApexConfig = new ProcessedTemplate();
        processedApexConfig.setContent("apexConfig");
        when(toscaTemplateProcessor.process(any(InputStream.class))).thenReturn(processedToscaTemplate);
        when(apexConfigProcessor.process(any(InputStream.class))).thenReturn(processedApexConfig);
        when(policyToscaConverter.convert(eq("policy\n"), eq("apexConfig"), eq("tosca")))
            .thenThrow(PolicyToscaConverterException.class);
        when(uploadPluginClient.upload(any(UploadPolicyRequestDto.class)))
            .thenReturn(Response.ok().status(201).build());

        final ApexApiResult apexApiResult = policyUploadHandler
            .doUpload(apexModel, mock(InputStream.class), mock(InputStream.class), USER_ID);

        assertThat("Response should not be ok", apexApiResult.isNok(), is(true));
        final String expectedErrorMsg = String
            .format("An error has occurred while uploading the converting the Policy '%s' to YAML.",
                apexModel.getPolicyModel().getId());
        assertThat("Response message should be as expected",
            apexApiResult.getMessage(), is(expectedErrorMsg + "\n"));
    }

    private ApexModel mockApexModel() {
        final ApexModel apexModel = mock(ApexModel.class);
        final ApexApiResult listModelApexApiResult = new ApexApiResult();
        listModelApexApiResult.addMessage("policy");
        when(apexModel.listModel()).thenReturn(listModelApexApiResult);
        final AxPolicyModel axPolicyModel = new AxPolicyModel();
        final AxArtifactKey axArtifactKey = new AxArtifactKey("policyKey", "1.0.0");
        final Map<AxArtifactKey, AxKeyInfo> keyInfoMap = new HashMap<>();
        keyInfoMap.put(axArtifactKey, new AxKeyInfo(axArtifactKey));
        final AxKeyInformation axKeyInformation = new AxKeyInformation(axArtifactKey, keyInfoMap);
        axPolicyModel.setKeyInformation(axKeyInformation);
        when(apexModel.getPolicyModel()).thenReturn(axPolicyModel);
        return apexModel;
    }
}
