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

import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ApexConfigProcessor.ErrorMessage.INVALID_APEX_CONFIG;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ApexConfigProcessor.ErrorMessage.INVALID_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.ApexConfigProcessor.ErrorMessage.MISSING_ENTRY;
import static org.onap.policy.gui.editors.apex.rest.handling.converter.tosca.PolicyToscaConverter.ToscaKey.ENGINE_SERVICE_PARAMETERS;

import com.google.gson.JsonObject;
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
 * Process the Apex Config JSON template file.
 */
public class ApexConfigProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexConfigProcessor.class);

    private final StandardCoder standardCoder;

    public ApexConfigProcessor(final StandardCoder standardCoder) {
        this.standardCoder = standardCoder;
    }

    /**
     * Process the Apex Config JSON template file.
     *
     * @param apexConfigInputStream the input stream for the Apex Config JSON template
     * @return the result of the processing with the read JSON and its errors.
     */
    public ProcessedTemplate process(final InputStream apexConfigInputStream) throws IOException {
        final ProcessedTemplate processedTemplate = new ProcessedTemplate();
        final String templateAsString;
        try (final InputStream inputStream = apexConfigInputStream) {
            templateAsString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
        final Set<String> errorSet = validate(templateAsString);
        processedTemplate.setContent(templateAsString);
        processedTemplate.addToErrors(errorSet);

        return processedTemplate;
    }

    private Set<String> validate(final String apexConfig) {
        final Set<String> errorSet = new HashSet<>();
        final JsonObject apexConfigJson;
        try {
            apexConfigJson = standardCoder.decode(apexConfig, JsonObject.class);
        } catch (final CoderException e) {
            LOGGER.debug(INVALID_APEX_CONFIG.getMessage(), e);
            errorSet.add(INVALID_APEX_CONFIG.getMessage());
            return errorSet;
        }

        final JsonObject topologyTemplate;
        try {
            topologyTemplate = apexConfigJson.getAsJsonObject(ENGINE_SERVICE_PARAMETERS.getKey());
        } catch (final Exception e) {
            final String errorMsg = INVALID_ENTRY.getMessage(ENGINE_SERVICE_PARAMETERS.getKey());
            LOGGER.debug(errorMsg, e);
            errorSet.add(errorMsg);
            return errorSet;
        }

        if (topologyTemplate == null) {
            errorSet.add(MISSING_ENTRY.getMessage(ENGINE_SERVICE_PARAMETERS.getKey()));
            return errorSet;
        }

        return errorSet;
    }

    /**
     * Stores the possible error messages for the Apex Config template validation process.
     */
    @AllArgsConstructor
    public enum ErrorMessage {
        MISSING_ENTRY("Missing '%s' entry"),
        INVALID_ENTRY("Invalid entry '%s' provided"),
        INVALID_APEX_CONFIG("Invalid apex config provided");

        private final String messageFormat;

        public String getMessage(final String... params) {
            return String.format(this.messageFormat, params);
        }
    }

}
