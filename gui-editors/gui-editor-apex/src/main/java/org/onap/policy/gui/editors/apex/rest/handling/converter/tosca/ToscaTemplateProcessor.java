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

import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.POLICIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.PROPERTIES;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.TOPOLOGY_TEMPLATE;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.TOSCA_DEFINITIONS_VERSION;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_POLICIES_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_POLICY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_PROPERTIES_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_TOPOLOGY_TEMPLATE_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_TOSCA_DEFINITION_VERSION_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.ONLY_ONE_POLICY_ALLOWED;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

/**
 * Process the TOSCA JSON template file, base for the Policy Model TOSCA conversion.
 */
public class ToscaTemplateProcessor {

    /**
     * Process the TOSCA JSON template file.
     *
     * @param toscaTemplateInputStream the input stream for the TOSCA JSON template file
     * @return the result of the processing with the read JSON and its errors.
     */
    public ProcessedTemplate process(final InputStream toscaTemplateInputStream) {
        final ProcessedTemplate processedTemplate = new ProcessedTemplate();

        final String templateAsString = readAsString(toscaTemplateInputStream);
        final Set<String> errorSet = validate(templateAsString);
        processedTemplate.setContent(templateAsString);
        processedTemplate.addToErrors(errorSet);

        return processedTemplate;
    }

    private String readAsString(final InputStream toscaTemplateInputStream) {
        return new BufferedReader(
            new InputStreamReader(toscaTemplateInputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    private Set<String> validate(final String toscaTemplate) {
        final Set<String> errorSet = new HashSet<>();
        final JsonObject toscaTemplateJson = JsonParser.parseString(toscaTemplate).getAsJsonObject();

        final JsonPrimitive toscaDefinitionVersion =
            toscaTemplateJson.getAsJsonPrimitive(TOSCA_DEFINITIONS_VERSION.getKey());
        if (toscaDefinitionVersion == null) {
            errorSet.add(MISSING_TOSCA_DEFINITION_VERSION_ENTRY.getMessage());
        }

        final JsonObject topologyTemplate = toscaTemplateJson.getAsJsonObject(TOPOLOGY_TEMPLATE.getKey());
        if (topologyTemplate == null) {
            errorSet.add(MISSING_TOPOLOGY_TEMPLATE_ENTRY.getMessage());
            return errorSet;
        }

        final JsonArray policies = topologyTemplate.getAsJsonArray(POLICIES.getKey());

        if (policies == null) {
            errorSet.add(MISSING_POLICIES_ENTRY.getMessage());
            return errorSet;
        }

        if (policies.size() == 0) {
            errorSet.add(MISSING_POLICY.getMessage());
            return errorSet;
        }

        if (policies.size() > 1) {
            errorSet.add(ONLY_ONE_POLICY_ALLOWED.getMessage());
            return errorSet;
        }

        final JsonObject firstPolicyObj = policies.get(0).getAsJsonObject();
        final String firstPolicyKey = firstPolicyObj.keySet().toArray()[0].toString();
        final JsonObject firstPolicy = firstPolicyObj.get(firstPolicyKey).getAsJsonObject();
        final JsonObject properties = firstPolicy.getAsJsonObject(PROPERTIES.getKey());
        if (properties == null) {
            errorSet.add(MISSING_PROPERTIES_ENTRY.getMessage(firstPolicyKey));
            return errorSet;
        }

        return errorSet;
    }

    /**
     * Stores the possible error messages for the Tosca template validation process.
     */
    @AllArgsConstructor
    public enum ErrorMessage {
        MISSING_TOSCA_DEFINITION_VERSION_ENTRY("Missing 'tosca_definitions_version' entry"),
        MISSING_TOPOLOGY_TEMPLATE_ENTRY("Missing 'engineServiceParameters' entry"),
        MISSING_POLICIES_ENTRY("Missing 'topology_template->policies' entry"),
        MISSING_POLICY("No policy was provided in the 'policies' list"),
        ONLY_ONE_POLICY_ALLOWED("Only one policy entry is allowed in the 'policies' list"),
        MISSING_PROPERTIES_ENTRY("Missing properties entry in '%s'");

        private final String messageFormat;

        public String getMessage(final String... params) {
            return String.format(this.messageFormat, params);
        }
    }


}
