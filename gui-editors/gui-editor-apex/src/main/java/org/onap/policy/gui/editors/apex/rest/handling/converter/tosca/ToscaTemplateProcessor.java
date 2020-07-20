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
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.INVALID_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.INVALID_POLICY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.INVALID_TOSCA_TEMPLATE;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_POLICIES_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_POLICY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_PROPERTIES_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_TOPOLOGY_TEMPLATE_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_TOSCA_DEFINITION_VERSION_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.ONLY_ONE_POLICY_ALLOWED;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process the TOSCA JSON template file, base for the Policy Model TOSCA conversion.
 */
public class ToscaTemplateProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToscaTemplateProcessor.class);

    private final StandardCoder jsonCoder;

    public ToscaTemplateProcessor(final StandardCoder jsonCoder) {
        this.jsonCoder = jsonCoder;
    }

    /**
     * Process the TOSCA JSON template file.
     *
     * @param toscaTemplateInputStream the input stream for the TOSCA JSON template file
     * @return the result of the processing with the read JSON and its errors.
     */
    public ProcessedTemplate process(final InputStream toscaTemplateInputStream) throws IOException {
        final ProcessedTemplate processedTemplate = new ProcessedTemplate();

        final String templateAsString;
        try (final InputStream inputStream = toscaTemplateInputStream) {
            templateAsString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }

        final Set<String> errorSet = validate(templateAsString);
        processedTemplate.setContent(templateAsString);
        processedTemplate.addToErrors(errorSet);

        return processedTemplate;
    }

    private Set<String> validate(final String toscaTemplate) {
        final Set<String> errorSet = new HashSet<>();
        final JsonObject toscaTemplateJson;
        try {
            toscaTemplateJson = jsonCoder.decode(toscaTemplate, JsonObject.class);
        } catch (final CoderException e) {
            LOGGER.debug(INVALID_TOSCA_TEMPLATE.getMessage(), e);
            errorSet.add(INVALID_TOSCA_TEMPLATE.getMessage());
            return errorSet;
        }
        final JsonPrimitive toscaDefinitionVersion;
        try {
            toscaDefinitionVersion = toscaTemplateJson.getAsJsonPrimitive(TOSCA_DEFINITIONS_VERSION.getKey());
        } catch (final Exception e) {
            final String errorMsg = INVALID_ENTRY.getMessage(TOSCA_DEFINITIONS_VERSION.getKey());
            LOGGER.debug(errorMsg, e);
            errorSet.add(errorMsg);
            return errorSet;
        }

        if (toscaDefinitionVersion == null) {
            errorSet.add(MISSING_TOSCA_DEFINITION_VERSION_ENTRY.getMessage());
        }

        final JsonObject topologyTemplate;
        try {
            topologyTemplate = toscaTemplateJson.getAsJsonObject(TOPOLOGY_TEMPLATE.getKey());
        } catch (final Exception e) {
            final String errorMsg = INVALID_ENTRY.getMessage(TOPOLOGY_TEMPLATE.getKey());
            LOGGER.debug(errorMsg, e);
            errorSet.add(errorMsg);
            return errorSet;
        }
        if (topologyTemplate == null) {
            errorSet.add(MISSING_TOPOLOGY_TEMPLATE_ENTRY.getMessage());
            return errorSet;
        }

        final JsonArray policies;
        try {
            policies = topologyTemplate.getAsJsonArray(POLICIES.getKey());
        } catch (final Exception e) {
            final String errorMsg = INVALID_ENTRY.getMessage(POLICIES.getKey());
            LOGGER.debug(errorMsg, e);
            errorSet.add(errorMsg);
            return errorSet;
        }

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

        final JsonObject firstPolicy;
        final String firstPolicyKey;
        try {
            final JsonObject firstPolicyObj = policies.get(0).getAsJsonObject();
            firstPolicyKey = firstPolicyObj.keySet().iterator().next();
            firstPolicy = firstPolicyObj.get(firstPolicyKey).getAsJsonObject();
        } catch (final Exception e) {
            final String errorMsg = INVALID_POLICY.getMessage();
            LOGGER.debug(errorMsg, e);
            errorSet.add(errorMsg);
            return errorSet;
        }

        final JsonObject properties;
        try {
            properties = firstPolicy.getAsJsonObject(PROPERTIES.getKey());
        } catch (final Exception e) {
            final String errorMsg = INVALID_ENTRY.getMessage(PROPERTIES.getKey());
            LOGGER.debug(errorMsg, e);
            errorSet.add(errorMsg);
            return errorSet;
        }

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
        INVALID_POLICY("Invalid policy was provided in the 'policies' list"),
        ONLY_ONE_POLICY_ALLOWED("Only one policy entry is allowed in the 'policies' list"),
        INVALID_TOSCA_TEMPLATE("Invalid tosca template provided"),
        INVALID_ENTRY("Invalid entry '%s' provided"),
        MISSING_PROPERTIES_ENTRY("Missing properties entry in '%s'");

        private final String messageFormat;

        public String getMessage(final String... params) {
            return String.format(this.messageFormat, params);
        }
    }


}
