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
import static org.junit.jupiter.api.Assertions.fail;
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
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ToscaTemplateProcessorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToscaTemplateProcessorTest.class);

    private final ToscaTemplateProcessor toscaTemplateProcessor = new ToscaTemplateProcessor();
    private final Path testResourcesPath = Paths.get("src", "test", "resources", "processor");

    @Test
    void processSuccess() {
        final String fileName = "ToscaTemplate.json";
        final FileInputStream fileInputStream = readFileAsStream(fileName);
        final ProcessedTemplate process = toscaTemplateProcessor.process(fileInputStream);
        assertThat("Template should be valid", process.isValid(), is(true));
        final String expectedContent = readFileAsString(fileName);
        assertThat("Content should be the same", process.getContent(), is(expectedContent));
    }

    @Test
    void processMissingPoliciesEntry() throws IOException {
        final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-missing-policies.json");
        final ProcessedTemplate processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        assertProcessedTemplate(processedTemplate, false, List.of(MISSING_POLICIES_ENTRY.getMessage()));
    }

    @Test
    void processMissingTopologyTemplate() throws IOException {
        final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-missing-topology-template.json");
        final ProcessedTemplate processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        assertProcessedTemplate(processedTemplate, false, List.of(MISSING_TOPOLOGY_TEMPLATE_ENTRY.getMessage()));
    }

    @Test
    void processMissingPolicy() throws IOException {
        final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-missing-policy.json");
        final ProcessedTemplate processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        assertProcessedTemplate(processedTemplate, false, List.of(MISSING_POLICY.getMessage()));
    }

    @Test
    void processMissingToscaDefinitionsVersion() throws IOException {
        final FileInputStream fileInputStream =
            readFileAsStream("ToscaTemplate-missing-tosca-definitions-version.json");
        final ProcessedTemplate processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        assertProcessedTemplate(processedTemplate, false,
            List.of(MISSING_TOSCA_DEFINITION_VERSION_ENTRY.getMessage()));
    }

    @Test
    void processMissingProperties() throws IOException {
        final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-missing-properties.json");
        final ProcessedTemplate processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        assertProcessedTemplate(processedTemplate, false,
            List.of(MISSING_PROPERTIES_ENTRY.getMessage("onap.policies.native.apex.Grpc")));
    }

    @Test
    void processMoreThanOnePolicy() throws IOException {
        final FileInputStream fileInputStream = readFileAsStream("ToscaTemplate-more-than-one-policy.json");
        final ProcessedTemplate processedTemplate = toscaTemplateProcessor.process(fileInputStream);
        assertProcessedTemplate(processedTemplate, false,
            List.of(ONLY_ONE_POLICY_ALLOWED.getMessage()));
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

    private FileInputStream readFileAsStream(final String fileName) {
        final Path path = Paths.get(testResourcesPath.toString(), fileName);
        final String errorMsg = String.format("Could not load file '%s'", fileName);
        try {
            return new FileInputStream(path.toFile());
        } catch (final FileNotFoundException e) {
            LOGGER.error(errorMsg, e);
            fail(errorMsg);
        }
        return null;
    }

    private String readFileAsString(final String fileName) {
        final Path path = Paths.get(testResourcesPath.toString(), fileName);
        final String errorMsg = String.format("Could not load file '%s'", fileName);
        try {
            return Files.readString(path);
        } catch (final IOException e) {
            LOGGER.error(errorMsg, e);
            fail(errorMsg);
        }
        return null;
    }

}