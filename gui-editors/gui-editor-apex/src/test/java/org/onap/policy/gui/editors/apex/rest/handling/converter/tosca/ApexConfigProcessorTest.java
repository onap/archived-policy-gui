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
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ApexConfigProcessor.ErrorMessage.INVALID_APEX_CONFIG;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ApexConfigProcessor.ErrorMessage.INVALID_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ApexConfigProcessor.ErrorMessage.MISSING_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.ENGINE_SERVICE_PARAMETERS;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import org.onap.policy.common.utils.coder.StandardCoder;

public class ApexConfigProcessorTest {

    private final ApexConfigProcessor apexConfigProcessor = new ApexConfigProcessor(new StandardCoder());
    private final Path testResourcesPath = Paths.get("src", "test", "resources", "processor");

    @Test
    public void testProcessSuccess() throws IOException {
        final String fileName = "ApexConfig.json";
        final ProcessedTemplate process;
        try (final FileInputStream fileInputStream = readFileAsStream(fileName)) {
            process = apexConfigProcessor.process(fileInputStream);
        }
        assertThat("Template should be valid", process.isValid(), is(true));
        final String expectedContent = readFileAsString(fileName);
        assertThat("Content should be the same", process.getContent(), is(expectedContent));
    }

    @Test
    public void testProcessMissingPoliciesEntry() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream =
            readFileAsStream("ApexConfig-missing-engineServiceParameters.json")) {
            processedTemplate = apexConfigProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(MISSING_ENTRY.getMessage(ENGINE_SERVICE_PARAMETERS.getKey())));
    }

    @Test
    public void testProcessInvalidToscaTemplate() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream = readFileAsStream("ApexConfig-invalid.json")) {
            processedTemplate = apexConfigProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(INVALID_APEX_CONFIG.getMessage()));
    }

    @Test
    public void testProcessInvalidEngineServiceParameters() throws IOException {
        final ProcessedTemplate processedTemplate;
        try (final FileInputStream fileInputStream =
            readFileAsStream("ApexConfig-invalid-engineServiceParameters.json")) {
            processedTemplate = apexConfigProcessor.process(fileInputStream);
        }
        assertProcessedTemplate(processedTemplate, false,
            List.of(INVALID_ENTRY.getMessage(ENGINE_SERVICE_PARAMETERS.getKey())));
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