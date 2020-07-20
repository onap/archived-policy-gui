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

import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ApexConfigProcessor.ErrorMessage.MISSING_ENGINE_SERVICE_PARAMETERS_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.ENGINE_SERVICE_PARAMETERS;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

/**
 * Process the Apex Config JSON template file.
 */
public class ApexConfigProcessor {

    /**
     * Process the Apex Config JSON template file.
     *
     * @param apexConfigInputStream the input stream for the Apex Config JSON template
     * @return the result of the processing with the read JSON and its errors.
     */
    public ProcessedTemplate process(final InputStream apexConfigInputStream) {
        final ProcessedTemplate processedTemplate = new ProcessedTemplate();

        final String templateAsString = readAsString(apexConfigInputStream);
        final Set<String> errorSet = validate(templateAsString);
        processedTemplate.setContent(templateAsString);
        processedTemplate.addToErrors(errorSet);

        return processedTemplate;
    }

    private String readAsString(final InputStream fileInputStream) {
        return new BufferedReader(
            new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    private Set<String> validate(final String apexConfig) {
        final Set<String> errorSet = new HashSet<>();
        final JsonObject apexConfigJson = JsonParser.parseString(apexConfig).getAsJsonObject();


        final JsonObject topologyTemplate = apexConfigJson.getAsJsonObject(ENGINE_SERVICE_PARAMETERS.getKey());
        if (topologyTemplate == null) {
            errorSet.add(MISSING_ENGINE_SERVICE_PARAMETERS_ENTRY.getMessage());
            return errorSet;
        }

        return errorSet;
    }

    /**
     * Stores the possible error messages for the Apex Config template validation process.
     */
    @AllArgsConstructor
    public enum ErrorMessage {
        MISSING_ENGINE_SERVICE_PARAMETERS_ENTRY("Missing 'engineServiceParameters' entry");

        private final String messageFormat;

        public String getMessage(final String... params) {
            return String.format(this.messageFormat, params);
        }
    }

}
