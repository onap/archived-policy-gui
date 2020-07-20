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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.ENGINE_SERVICE_PARAMETERS;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.POLICIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.POLICY_TYPE_IMPL;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.PROPERTIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.TOPOLOGY_TEMPLATE;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.TOSCA_DEFINITIONS_VERSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.exception.PolicyToscaConverterException;

class PolicyToscaConverterTest {

    @Spy
    private final StandardCoder standardCoder = new StandardCoder();

    @InjectMocks
    private PolicyToscaConverter policyToscaConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void convertSuccess() throws IOException, PolicyToscaConverterException {
        final String apexConfig = readResourceFileToString(Paths.get("converter", "ApexConfig.json"));
        final String apexPolicy = readResourceFileToString(Paths.get("converter", "APEXgRPCPolicy.json"));
        final String toscaTemplate = readResourceFileToString(Paths.get("converter", "ToscaTemplate.json"));
        final Optional<String> convert = policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate);
        assertTrue(convert.isPresent());
        final String convertedYaml = convert.get();
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        @SuppressWarnings("unchecked") final Map<String, Object> yamlAsMap = yamlReader
            .readValue(convertedYaml, Map.class);
        assertTrue(yamlAsMap.containsKey(TOSCA_DEFINITIONS_VERSION.getKey()));
        assertTrue(yamlAsMap.containsKey(TOPOLOGY_TEMPLATE.getKey()));
        @SuppressWarnings("unchecked") final Map<String, Object> topology_template =
            (Map<String, Object>) yamlAsMap.get(TOPOLOGY_TEMPLATE.getKey());
        assertTrue(topology_template.containsKey(POLICIES.getKey()));
        @SuppressWarnings("unchecked") final List<Object> policies =
            (List<Object>) topology_template.get(POLICIES.getKey());
        assertEquals(1, policies.size());
        @SuppressWarnings("unchecked") final Map<String, Object> firstPolicyMap = (Map<String, Object>) policies.get(0);
        assertEquals(1, firstPolicyMap.keySet().size());
        @SuppressWarnings("unchecked") final Map<String, Object> firstPolicy =
            (Map<String, Object>) firstPolicyMap.get(firstPolicyMap.keySet().iterator().next());
        assertTrue(firstPolicy.containsKey(PROPERTIES.getKey()));
        @SuppressWarnings("unchecked") final Map<String, Object> propertiesMap =
            (Map<String, Object>) firstPolicy.get(PROPERTIES.getKey());
        assertTrue(propertiesMap.containsKey(ENGINE_SERVICE_PARAMETERS.getKey()));
        @SuppressWarnings("unchecked") final Map<String, Object> engineServiceParametersProperty =
            (Map<String, Object>) propertiesMap.get(ENGINE_SERVICE_PARAMETERS.getKey());
        assertTrue(engineServiceParametersProperty.containsKey(POLICY_TYPE_IMPL.getKey()));
        System.out.println(convertedYaml);
    }

    @Test
    void convertInvalidJsonDecode() throws CoderException {
        final String invalidJson = "this is an invalid JSON";
        doThrow(CoderException.class).when(standardCoder).decode(eq(invalidJson), eq(JsonObject.class));

        final PolicyToscaConverterException actualException =
            assertThrows(PolicyToscaConverterException.class,
                () -> policyToscaConverter.convert(invalidJson, invalidJson, invalidJson));
        final String expectedMsg = String.format("Could not convert JSON string to JSON:\n%s", invalidJson);
        assertEquals(expectedMsg, actualException.getMessage());
    }

    @Test
    void convertInvalidJsonEncodeToString() throws CoderException, IOException {
        final String apexConfig = readResourceFileToString(Paths.get("converter", "ApexConfig.json"));
        final String apexPolicy = readResourceFileToString(Paths.get("converter", "APEXgRPCPolicy.json"));
        final String toscaTemplate = readResourceFileToString(Paths.get("converter", "ToscaTemplate.json"));

        doThrow(CoderException.class).when(standardCoder).encode(any(JsonObject.class));

        final PolicyToscaConverterException actualException =
            assertThrows(PolicyToscaConverterException.class,
                () -> policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate));
        assertTrue(actualException.getMessage().contains("Could not convert JSON to JSON string:"));
    }

    @Test
    void convertJsonToYamlFailure() throws CoderException, IOException {
        final String apexConfig = readResourceFileToString(Paths.get("converter", "ApexConfig.json"));
        final String apexPolicy = readResourceFileToString(Paths.get("converter", "APEXgRPCPolicy.json"));
        final String toscaTemplate = readResourceFileToString(Paths.get("converter", "ToscaTemplate.json"));
        final String invalidJson = "an invalid JSON";

        doReturn(invalidJson).when(standardCoder).encode(any(JsonObject.class));

        final PolicyToscaConverterException actualException =
            assertThrows(PolicyToscaConverterException.class,
                () -> policyToscaConverter.convert(apexPolicy, apexConfig, toscaTemplate));
        final String errorMsg = String.format("Could not convert JSON string to YAML:\n%s", invalidJson);
        assertEquals(errorMsg, actualException.getMessage());
    }

    private String readResourceFileToString(final Path filePathFromResources) throws IOException {
        final Path resourceDirectory = Paths.get("src", "test", "resources");
        final Path converter = Paths.get(resourceDirectory.toString(), filePathFromResources.toString());
        return Files.readString(converter);
    }

}