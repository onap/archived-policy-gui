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
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ApexConfigProcessor.ErrorMessage.MISSING_ENGINE_SERVICE_PARAMETERS_ENTRY;

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

class ApexConfigProcessorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexConfigProcessorTest.class);

    private final ApexConfigProcessor apexConfigProcessor = new ApexConfigProcessor();
    private final Path testResourcesPath = Paths.get("src", "test", "resources", "processor");

    @Test
    void processSuccess() {
        final String fileName = "ApexConfig.json";
        final FileInputStream fileInputStream = readFileAsStream(fileName);
        final ProcessedTemplate process = apexConfigProcessor.process(fileInputStream);
        assertThat("Template should be valid", process.isValid(), is(true));
        final String expectedContent = readFileAsString(fileName);
        assertThat("Content should be the same", process.getContent(), is(expectedContent));
    }

    @Test
    void processMissingPoliciesEntry() {
        final FileInputStream fileInputStream =
            readFileAsStream("ApexConfig-missing-engineServiceParameters.json");
        final ProcessedTemplate processedTemplate = apexConfigProcessor.process(fileInputStream);
        assertProcessedTemplate(processedTemplate, false,
            List.of(MISSING_ENGINE_SERVICE_PARAMETERS_ENTRY.getMessage()));
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