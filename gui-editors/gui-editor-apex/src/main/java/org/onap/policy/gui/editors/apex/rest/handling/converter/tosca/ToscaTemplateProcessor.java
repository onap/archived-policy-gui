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
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.MISSING_POLICY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ToscaTemplateProcessor.ErrorMessage.ONLY_ONE_POLICY_ALLOWED;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey;
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

        final Optional<JsonPrimitive> toscaDefinitionVersionOpt =
            readJsonEntry(TOSCA_DEFINITIONS_VERSION, toscaTemplateJson::getAsJsonPrimitive, errorSet);
        if (toscaDefinitionVersionOpt.isEmpty()) {
            return errorSet;
        }

        final Optional<JsonObject> topologyTemplate =
            readJsonEntry(TOPOLOGY_TEMPLATE, toscaTemplateJson::getAsJsonObject, errorSet);
        if (topologyTemplate.isEmpty()) {
            return errorSet;
        }

        final Optional<JsonArray> policiesOpt =
            readJsonEntry(POLICIES, topologyTemplate.get()::getAsJsonArray, errorSet);
        if (policiesOpt.isEmpty()) {
            return errorSet;
        }
        final JsonArray policies = policiesOpt.get();

        if (policies.size() == 0) {
            errorSet.add(MISSING_POLICY.getMessage());
            return errorSet;
        }

        if (policies.size() > 1) {
            errorSet.add(ONLY_ONE_POLICY_ALLOWED.getMessage());
            return errorSet;
        }

        final JsonObject firstPolicy;
        try {
            final JsonObject firstPolicyObj = policies.get(0).getAsJsonObject();
            firstPolicy = firstPolicyObj.entrySet().iterator().next().getValue().getAsJsonObject();
        } catch (final Exception e) {
            final String errorMsg = INVALID_POLICY.getMessage();
            LOGGER.debug(errorMsg, e);
            errorSet.add(errorMsg);
            return errorSet;
        }

        readJsonEntry(PROPERTIES, firstPolicy::getAsJsonObject, errorSet);

        return errorSet;
    }

    private <T> Optional<T> readJsonEntry(final ToscaKey toscaKey,
                                          final Function<String, T> jsonFunction, final Set<String> errorSet) {
        try {
            final T json = jsonFunction.apply(toscaKey.getKey());
            if (json == null) {
                errorSet.add(MISSING_ENTRY.getMessage(toscaKey.getKey()));
            }
            return Optional.ofNullable(json);
        } catch (final Exception e) {
            final String errorMsg = INVALID_ENTRY.getMessage(toscaKey.getKey());
            LOGGER.debug(errorMsg, e);
            errorSet.add(errorMsg);
            return Optional.empty();
        }

    }

    /**
     * Stores the possible error messages for the Tosca template validation process.
     */
    @AllArgsConstructor
    public enum ErrorMessage {
        MISSING_ENTRY("Missing '%s' entry"),
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
