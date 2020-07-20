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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.POLICIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.PROPERTIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.TOPOLOGY_TEMPLATE;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.TOSCA_DEFINITIONS_VERSION;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.INVALID_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.INVALID_POLICY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.INVALID_TOSCA_TEMPLATE;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_POLICIES_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_POLICY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_PROPERTIES_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_TOPOLOGY_TEMPLATE_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_TOSCA_DEFINITION_VERSION_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.ONLY_ONE_POLICY_ALLOWED;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import org.onap.policy.common.utils.coder.StandardCoder;

public class ToscaTemplateProcessorTest {

    private final ToscaTemplateProcessor toscaTemplateProcessor = new ToscaTemplateProcessor(new StandardCoder());
    private final Path testResourcesPath = Paths.get("src", "test", "resources", "processor");

    @Test
    public void testProcessSuccess() throws IOException {
        final String fileName = "ToscaTemplate.json";
        final ProcessedTemplate process;
        try (final FileInputStream fileInputStream = readFileAsStream(fileName)) {
            process = toscaTemplateProcessor.process(fileInputStream);
        }
        assertThat("Template should be valid", process.isValid(), is(true));
        final String expectedContent = readFileAsString(fileName);
        assertThat("Content should be the same", process.getContent(), is(expectedContent));
    }

    @Test
    public void testProcessMissingPoliciesEntry() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-missing-policies.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false, List.of(MISSING_POLICIES_ENTRY.getMessage()));
    }

    @Test
    public void testProcessMissingTopologyTemplate() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-missing-topology-template.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false, List.of(MISSING_TOPOLOGY_TEMPLATE_ENTRY.getMessage()));
    }

    @Test
    public void testProcessMissingPolicy() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-missing-policy.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false, List.of(MISSING_POLICY.getMessage()));
    }

    @Test
    public void testProcessMissingToscaDefinitionsVersion() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream =
            readFileAsStream("ToscaTemplate-missing-tosca-definitions-version.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(MISSING_TOSCA_DEFINITION_VERSION_ENTRY.getMessage()));
    }

    @Test
    public void testProcessMissingProperties() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-missing-properties.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(MISSING_PROPERTIES_ENTRY.getMessage("onap.policies.native.apex.Grpc")));
    }

    @Test
    public void testProcessMoreThanOnePolicy() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-more-than-one-policy.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(ONLY_ONE_POLICY_ALLOWED.getMessage()));
    }

    @Test
    public void testProcessInvalidToscaTemplate() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-invalid.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(INVALID_TOSCA_TEMPLATE.getMessage()));
    }

    @Test
    public void testProcessInvalidEntryToscaDefinitionsVersion() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream =
            readFileAsStream("ToscaTemplate-invalid-toscaDefinitions.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(INVALID_ENTRY.getMessage(TOSCA_DEFINITIONS_VERSION.getKey())));
    }

    @Test
    public void testProcessInvalidEntryTopologyTemplate() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream =
            readFileAsStream("ToscaTemplate-invalidEntry-topologyTemplate.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(INVALID_ENTRY.getMessage(TOPOLOGY_TEMPLATE.getKey())));
    }

    @Test
    public void testProcessInvalidEntryPolicies() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream =
            readFileAsStream("ToscaTemplate-invalidEntry-policies.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(INVALID_ENTRY.getMessage(POLICIES.getKey())));
    }

    @Test
    public void testProcessInvalidPolicy() throws IOException {
        ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-invalidPolicy1.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(INVALID_POLICY.getMessage()));

        try (final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-invalidPolicy2.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(INVALID_POLICY.getMessage()));
    }

    @Test
    public void testProcessInvalidEntryProperties() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream =
            readFileAsStream("ToscaTemplate-invalidEntry-properties.json")) {
            processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(INVALID_ENTRY.getMessage(PROPERTIES.getKey())));
    }


    private void assertProcessedTemplate(final ProcessedTemplate process, boolean isValid,
                                         final List<String> expectedErrorList) {
        assertThat("Template should be valid", process.isValid(), is(isValid));
        if (isValid || expectedErrorList == null) {
            return;
        }
        assertThat("Should contains the expected quantity of errors",
            process.getErrorSet().size(), is(expectedErrorList.size()));
        expectedErrorList
            .forEach(errorMsg -> assertThat("Should contains a specific error message", process.getErrorSet(),
                contains(errorMsg)));
    }

    private FileInputStream readFileAsStream(final String fileName) throws FileNotFoundException {
        final Path path = Paths.get(testResourcesPath.toString(), fileName);
        return new FileInputStream(path.toFile());
    }

    private String readFileAsString(final String fileName) throws IOException {
        final Path path = Paths.get(testResourcesPath.toString(), fileName);
        return Files.readString(path);
    }

}