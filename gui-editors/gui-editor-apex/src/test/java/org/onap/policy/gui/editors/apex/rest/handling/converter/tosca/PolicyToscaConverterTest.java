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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.ENGINE_SERVICE_PARAMETERS;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.POLICIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.POLICY_TYPE_IMPL;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.PROPERTIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.TOPOLOGY_TEMPLATE;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.TOSCA_DEFINITIONS_VERSION;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.coder.StandardYamlCoder;
import org.onap.policy.common.utils.coder.YamlJsonTranslator;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.exception.PolicyToscaConverterException;

public class PolicyToscaConverterTest {

    @Spy
    private final StandardCoder standardCoder = new StandardCoder();
    @Spy
    private final YamlJsonTranslator yamlJsonTranslator = new YamlJsonTranslator();
    @InjectMocks
    private PolicyToscaConverter policyToscaConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConvertSuccess() throws IOException, PolicyToscaConverterException, CoderException {
        final String apexConfig = readResourceFileToString(Paths.get("converter", "ApexConfig.json"));
        final String apexPolicy = readResourceFileToString(Paths.get("converter", "APEXgRPCPolicy.json"));
        final String toscaTemplate = readResourceFileToString(Paths.get("converter", "ToscaTemplate.json"));
        final Optional<String> convert = policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate);
        assertTrue(convert.isPresent());
        final String convertedYaml = convert.get();
        final StandardYamlCoder standardYamlCoder = new StandardYamlCoder();
        @SuppressWarnings("unchecked") final Map<String, Object> yamlAsMap =
            standardYamlCoder.decode(convertedYaml, Map.class);
        assertThat(yamlAsMap).containsKeys(TOSCA_DEFINITIONS_VERSION.getKey(), TOPOLOGY_TEMPLATE.getKey());
        @SuppressWarnings("unchecked") final Map<String, Object> topology_template =
            (Map<String, Object>) yamlAsMap.get(TOPOLOGY_TEMPLATE.getKey());
        assertThat(topology_template).containsKey(POLICIES.getKey());
        @SuppressWarnings("unchecked") final List<Object> policies =
            (List<Object>) topology_template.get(POLICIES.getKey());
        assertEquals(1, policies.size());
        @SuppressWarnings("unchecked") final Map<String, Object> firstPolicyMap = (Map<String, Object>) policies.get(0);
        assertEquals(1, firstPolicyMap.keySet().size());
        @SuppressWarnings("unchecked") final Map<String, Object> firstPolicy =
            (Map<String, Object>) firstPolicyMap.get(firstPolicyMap.keySet().iterator().next());
        assertThat(firstPolicy).containsKey(PROPERTIES.getKey());
        @SuppressWarnings("unchecked") final Map<String, Object> propertiesMap =
            (Map<String, Object>) firstPolicy.get(PROPERTIES.getKey());
        assertThat(propertiesMap).containsKey(ENGINE_SERVICE_PARAMETERS.getKey());
        @SuppressWarnings("unchecked") final Map<String, Object> engineServiceParametersProperty =
            (Map<String, Object>) propertiesMap.get(ENGINE_SERVICE_PARAMETERS.getKey());
        assertThat(engineServiceParametersProperty).containsKey(POLICY_TYPE_IMPL.getKey());
    }

    @Test
    public void testConvertInvalidJsonDecode() throws CoderException {
        final String invalidJson = "this is an invalid JSON";
        doThrow(CoderException.class).when(standardCoder).decode(eq(invalidJson), eq(JsonObject.class));

        final String expectedMsg = String.format("Could not convert JSON string to JSON:\n%s", invalidJson);
        assertThatThrownBy(() -> policyToscaConverter.convert(invalidJson, invalidJson, invalidJson))
            .isInstanceOf(PolicyToscaConverterException.class)
            .hasMessage(expectedMsg);
    }

    @Test
    public void testConvertInvalidJsonEncodeToString() throws IOException {
        final String apexConfig = readResourceFileToString(Paths.get("converter", "ApexConfig.json"));
        final String apexPolicy = readResourceFileToString(Paths.get("converter", "APEXgRPCPolicy.json"));
        final String toscaTemplate = readResourceFileToString(Paths.get("converter", "ToscaTemplate.json"));

        doThrow(RuntimeException.class).when(yamlJsonTranslator).toYaml(any(JsonObject.class));

        assertThatThrownBy(() -> policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate))
            .isInstanceOf(PolicyToscaConverterException.class)
            .hasMessageContaining("Could not convert JSON Object to YAML:");
    }

    @Test
    public void testCouldNotReadFirstPolicy() throws IOException {
        final String apexConfig = readResourceFileToString(Paths.get("converter", "ApexConfig.json"));
        final String apexPolicy = readResourceFileToString(Paths.get("converter", "APEXgRPCPolicy.json"));
        final String toscaTemplate1 =
            readResourceFileToString(Paths.get("processor", "ToscaTemplate-missing-policies.json"));
        final String expectedError =
            String.format("Could not read the first policy in the '%s' entry under '%s'",
                POLICIES.getKey(), TOPOLOGY_TEMPLATE.getKey());

        assertThatThrownBy(() -> policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate1))
            .isInstanceOf(PolicyToscaConverterException.class)
            .hasMessage(expectedError);
        final String toscaTemplate2 =
            readResourceFileToString(Paths.get("processor", "ToscaTemplate-missing-policy.json"));

        assertThatThrownBy(() -> policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate2))
            .isInstanceOf(PolicyToscaConverterException.class)
            .hasMessage(expectedError);
    }

    @Test
    public void testCouldNotReadPolicyProperties() throws IOException {
        final String apexConfig = readResourceFileToString(Paths.get("converter", "ApexConfig.json"));
        final String apexPolicy = readResourceFileToString(Paths.get("converter", "APEXgRPCPolicy.json"));
        final String toscaTemplate =
            readResourceFileToString(Paths.get("processor", "ToscaTemplate-missing-properties.json"));
        assertThatThrownBy(() -> policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate))
            .isInstanceOf(PolicyToscaConverterException.class)
            .hasMessage(String.format("Could not read the policy '%s' entry", PROPERTIES.getKey()));
    }

    @Test
    public void testCouldNotReadEngineServiceParameters() throws IOException {
        final String apexConfig =
            readResourceFileToString(Paths.get("converter", "ApexConfig-engineServiceParameters-notAnObject.json"));
        final String apexPolicy = readResourceFileToString(Paths.get("converter", "APEXgRPCPolicy.json"));
        final String toscaTemplate =
            readResourceFileToString(Paths.get("converter", "ToscaTemplate.json"));
        assertThatThrownBy(() -> policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate))
            .isInstanceOf(PolicyToscaConverterException.class)
            .hasMessage(
                String.format("Could not read the '%s' in the Apex Config", ENGINE_SERVICE_PARAMETERS.getKey()));
    }

    @Test
    public void testCouldNotReadTopologyTemplate() throws IOException {
        final String apexConfig = readResourceFileToString(Paths.get("converter", "ApexConfig.json"));
        final String apexPolicy = readResourceFileToString(Paths.get("converter", "APEXgRPCPolicy.json"));
        final String toscaTemplate =
            readResourceFileToString(Paths.get("processor", "ToscaTemplate-missing-topology-template.json"));
        assertThatThrownBy(() -> policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate))
            .isInstanceOf(PolicyToscaConverterException.class)
            .hasMessage(
                String.format("Could not read the '%s' entry in the Tosca Template", TOPOLOGY_TEMPLATE.getKey()));
    }

    private String readResourceFileToString(final Path filePathFromResources) throws IOException {
        final Path resourceDirectory = Paths.get("src", "test", "resources");
        final Path converter = Paths.get(resourceDirectory.toString(), filePathFromResources.toString());
        return Files.readString(converter);
    }

}